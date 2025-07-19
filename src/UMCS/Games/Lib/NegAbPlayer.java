package UMCS.Games.Lib;

public class NegAbPlayer extends Player {

   public NegAbPlayer(Game g, int md) 
   { super(g); 
     eval = g.getDefaultEvaluator();
     maxDepth = md;
   }

   public NegAbPlayer(Game g, Evaluator e, int md) 
   { super(g); 
     eval = e;
     maxDepth = md;
   }

   public String getName() { return "Neg-AlphaBeta Player"; }

   public void setEvaluator(Evaluator e) {eval = e;}

   public void setMaxDepth(int md) { 
      maxDepth=md; 
   }

   public SearchResult nextMove(Position pos) {
      if (amLogging()) log("------ next move ---------");
      clearEvals();
      Node m = new Node(Node.MAX,pos); 
      try {    
         return negab(m, maxDepth,MININF, POSINF, "");
      } catch (OutOfTimeException e) {
         System.out.println("OUT OF TIME!");
         return storedBestResult;
      }
   }



   SearchResult negab(Node m, int depth, short alpha, short beta, String pre) 
    throws OutOfTimeException {
      if (amLogging()) log(pre+"alpha "+alpha+" beta "+beta);

      if (m.isTerminal() || depth==0) {
         incEvals();
         short d = eval.evaluate(m,maxDepth-depth);
         // we have to flip sign at uneven depth because of absolute evalution values!
         if (((maxDepth - depth) & 1) !=0) d = (short)-d;  
         return new SearchResult(d,NOMOVE,null);
      }

      MoveEnumerator enum = getGame().generateMoves(m.getPosition());
      short move = enum.getMove();
      if (move==NOMOVE) 
        return new SearchResult();  // illegal situation! m should be terminal!

      short bestVal = alpha;
      short bestMove = move;
      SearchResult bestResult=null;

      while (move!=NOMOVE) {

         if (depth == maxDepth-1) storedBestResult = bestResult;
         checkTime();
 
         if (amLogging()) log(pre+"try "+getGame().moveString(move));
         Node mm = getGame().doMove(m,move);

         SearchResult r = negab(mm,depth-1,(short)-beta,(short)-bestVal, pre+"  ");
         short d = (short)-r.value; 

         getGame().undoMove(mm,move);
         if (amLogging()) log(pre+"undo "+getGame().moveString(move)+": value="+d);

         if (d>bestVal) {
            bestVal = d;
            bestMove = move; 
            bestResult = r;
         }

         if (bestVal>=beta) {
           if (amLogging()) log(pre+bestVal+" >= "+beta);
            move = NOMOVE;  // Alphabeta pruning !!!
         } else {
            move = enum.nextMove();
         }
      }     
      if (amLogging()) log(pre + "OPT "+getGame().moveString(bestMove)+": value="+bestVal);

       return new SearchResult(bestVal,bestMove,bestResult);   
   }


   Evaluator eval;
   int maxDepth;
   SearchResult storedBestResult;
}
