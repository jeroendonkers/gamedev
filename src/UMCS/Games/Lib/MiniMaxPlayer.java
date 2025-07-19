package UMCS.Games.Lib;

public class MiniMaxPlayer extends Player {

   public MiniMaxPlayer(Game g, int md) 
   { super(g); 
     eval = g.getDefaultEvaluator();
     maxDepth = md;
   }

   public MiniMaxPlayer(Game g, Evaluator e, int md) 
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
         return minimax(m, maxDepth, "");
      } catch (OutOfTimeException e) {
         System.out.println("OUT OF TIME!");
         return storedBestResult;
      }
   }



   SearchResult minimax(Node m, int depth, String pre) 
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

      short bestVal = MININF;
      short bestMove = move;
      SearchResult bestResult=null;

      while (move!=NOMOVE) {

         if (depth==maxDepth) storedBestResult = bestResult;
         checkTime();
 
         if (amLogging()) log(pre+"try "+getGame().moveString(move));
         Node mm = getGame().doMove(m,move);

         SearchResult r = minimax(mm,depth-1,pre+"  ");
         short d = r.value; 

         getGame().undoMove(mm,move);
         if (amLogging()) log(pre+"undo "+getGame().moveString(move)+": value="+d);

         if (!isMax) d = (short)-d; // flip sign for minimax

         if (d>bestVal) {
            bestVal = d;
            bestMove = move; 
            bestResult = r;
         }
         move = enum.nextMove();
      }     

      if (!isMax) bestVal = (short)-bestVal; // flip sign back minimax

      return new SearchResult(bestVal,bestMove,bestResult);   
   }

   Evaluator eval;
   int maxDepth;
   SearchResult storedBestResult;
}
