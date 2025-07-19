package UMCS.Games.Lib;

public class NegaMaxPlayer extends Player {

   public NegaMaxPlayer(Game g, int md) 
   { super(g); 
     eval = g.getDefaultEvaluator();
     maxDepth = md;
   }

   public NegaMaxPlayer(Game g, Evaluator e, int md) 
   { super(g); 
     eval = e;
     maxDepth = md;
   }

   public String getName() { return "MiniMax Player"; }


   public void setEvaluator(Evaluator e) {eval = e;}

   public void setMaxDepth(int md) { 
      maxDepth=md; 
   }

   public SearchResult nextMove(Position pos) {
      if (amLogging()) log("------ next move ---------");
      clearEvals();
      Node m = new Node(Node.MAX,pos); 
      try {    
         return negamax(m, maxDepth, "");
      } catch (OutOfTimeException e) {
         System.out.println("OUT OF TIME!");
         return storedBestResult;
      }
   }


   SearchResult negamax(Node m, int depth, String pre) 
    throws OutOfTimeException {

      if (m.isTerminal() || depth==0) {
         incEvals();
         short d = eval.evaluate(m,maxDepth-depth);
         // we have to flip sign at uneven depth too!
         if (((maxDepth - depth) & 1) !=0) d = (short)-d;  
         return new SearchResult(d,NOMOVE,null);
      }

      MoveEnumerator enum = getGame().generateMoves(m.getPosition());
      short move = enum.getMove();
      if (move==NOMOVE) 
        return new SearchResult();  // illegal situation! m should be terminal!

      short bestVal = MININF;
      short bestMove = move;
      SearchResult bestResult=null;

      while (move!=NOMOVE) {

         if (depth==maxDepth) storedBestResult = bestResult;
         checkTime();
 
         if (amLogging()) log(pre+"try "+getGame().moveString(move));
         Node mm = getGame().doMove(m,move);

         SearchResult r = negamax(mm,depth-1,pre+"  ");
         short d = (short)-r.value;

         getGame().undoMove(mm,move);
         if (amLogging()) log(pre+"undo "+getGame().moveString(move)+": value="+d);

         if (d>bestVal) {
            bestVal = d;
            bestMove = move; 
            bestResult = r;
         }
         move = enum.nextMove();
      }     

      return new SearchResult(bestVal,bestMove,bestResult);   
   }

   Evaluator eval;
   int maxDepth;
   SearchResult storedBestResult;
}
