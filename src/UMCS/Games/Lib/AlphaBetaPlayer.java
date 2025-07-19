package UMCS.Games.Lib;

public class AlphaBetaPlayer extends Player {

   public AlphaBetaPlayer(Game g, int md) 
   { super(g); 
     eval = g.getDefaultEvaluator();
     maxDepth = md;
   }

   public AlphaBetaPlayer(Game g, Evaluator e, int md) 
   { super(g); 
     eval = e;
     maxDepth = md;
   }

   public String getName() { return "AlphaBeta Player"; }

   public String toString() { return "AlphaBetaPlayer(depth="+maxDepth+")"; }

   public void setEvaluator(Evaluator e) {eval = e;}

   public void setMaxDepth(int md) { 
      maxDepth=md; 
   }

   public SearchResult nextMove(Position pos) {
      if (amLogging()) log("------ next move ---------");
      clearEvals();
      Node m = new Node(Node.MAX,pos); 
      try {    
         return alphabeta(m, maxDepth,MININF, POSINF, "");
      } catch (OutOfTimeException e) {
         System.out.println("OUT OF TIME!");
         return storedBestResult;
      }
   }



   SearchResult alphabeta(Node m, int depth, short alpha, short beta, String pre) 
    throws OutOfTimeException {

      if (m.isTerminal() || depth==0) {
         incEvals();
         return new SearchResult(eval.evaluate(m,maxDepth-depth),NOMOVE,null);
      }

      boolean isMax = (m.getType() == Node.MAX);

      MoveEnumerator enum = getGame().generateMoves(m.getPosition());
      short move = enum.getMove();
      if (move==NOMOVE) 
        return new SearchResult();  // illegal situation! m should be terminal!

      short bestVal;
      if (isMax) bestVal=alpha; else bestVal=(short)-beta;
      short bestMove = move;
      SearchResult bestResult=null;

      while (move!=NOMOVE) {

         if (depth == maxDepth-1) storedBestResult = bestResult;
         checkTime();
 
         short d;

         if (amLogging()) log(pre+"try "+getGame().moveString(move));
         Node mm = getGame().doMove(m,move);

         SearchResult r=null;
         if (isMax)
             r = alphabeta(mm,depth-1,bestVal,beta,pre+"  ");
         else
             r = alphabeta(mm,depth-1,alpha,(short)-bestVal,pre+"  ");
         d = r.value; 

         getGame().undoMove(mm,move);
         if (amLogging()) log(pre+"undo "+getGame().moveString(move)+": value="+d);

         if (!isMax) d = (short)-d; // flip sign for minimax

         if (d>bestVal) {
            bestVal = d;
            bestMove = move; 
            bestResult = r;
         }

         if ((isMax && bestVal>=beta) || (!isMax && -bestVal<=alpha)) {
            move = NOMOVE;  // Alphabeta pruning !!!
         } else {
            move = enum.nextMove();
         }
      }     

      if (!isMax) bestVal = (short)-bestVal;

      if (amLogging()) log(pre + "OPT "+getGame().moveString(bestMove)+": value="+bestVal);

      return new SearchResult(bestVal,bestMove,bestResult);   
   }


   Evaluator eval;
   int maxDepth;
   SearchResult storedBestResult;
}
