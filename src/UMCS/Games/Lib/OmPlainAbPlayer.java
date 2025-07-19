package UMCS.Games.Lib;

/*************************************************
 * Plain Opponent-Model Search with ab probe
 * (Iida / Uitetwijk / vd Herik / Donkers)
 *************************************************/


public class OmPlainAbPlayer extends AlphaBetaPlayer {

   public OmPlainAbPlayer(Game g, Evaluator op, int md) 
   { super(g,md); 
     eval = g.getDefaultEvaluator();
     evalop = op;
   }

   public OmPlainAbPlayer(Game g, Evaluator e, Evaluator op, int md) 
   { super(g,md); 
     eval = e; evalop = op;
   }

   public String getName() { return "Plain OM-search Player with ab probe"; }

   public void setEval(Evaluator e) { eval = e; }
   public void setEvalOp(Evaluator o) { evalop = o; }

   public SearchResult nextMove(Position pos) {
      if (amLogging()) log("OM Plain ab, depth="+maxDepth);
      clearEvals();
      Node m = new Node(pos); m.setType(Node.MAX); 
      storedBestResult = null;
      try {
        setEvaluator(evalop);
        return omSearch(m, maxDepth,"");
      } catch (OutOfTimeException e) {
         System.out.println("OUT OF TIME!");
         return storedBestResult;
      }

   }

   private SearchResult omSearch(Node m, int depth, String pre) 
    throws OutOfTimeException {

      if (amLogging()) log(pre+"Plain OMsearch with Ab");

      if (m.isTerminal() || depth==0) {
          incEvals();
          short v = eval.evaluate(m,maxDepth-depth);
          return new SearchResult(v,NOMOVE,null);
      }

      boolean isMax = (m.getType() == Node.MAX);

      short bestVal; 
      short bestMove = NOMOVE;
      SearchResult bestResult=null;

      if (isMax) {   // we maximize on player's value but also on opponent value

         bestVal = MININF; 

         MoveEnumerator enum = getGame().generateMoves(m.getPosition());
         short move = enum.getMove();
         if (move==NOMOVE) 
            return new SearchResult();  // illegal situation! m should be terminal!
         bestMove = move;

         while (move!=NOMOVE) {
 
            if (depth==maxDepth) storedBestResult = bestResult;
            checkTime();
     
            if (amLogging()) log(pre+"try(MAX) "+getGame().moveString(move));
            Node mm = getGame().doMove(m,move);
            SearchResult r = omSearch(mm,depth-1,pre+"  ");
            getGame().undoMove(mm,move);
            if (amLogging()) log(pre+"undo(MAX) "+getGame().moveString(move)+": value="+r.value);

            if (r.value>bestVal) {
                bestVal = r.value;
                bestResult = r;
                bestMove = move;
            }

            move = enum.nextMove();
         }     

      } else {

       SearchResult r = alphabeta(m,depth,MININF,POSINF,pre);  // AB PROBE
       short bestValop = r.value;
       bestMove = r.move;
 
       if (amLogging()) log(pre+"try(MIN) final "+getGame().moveString(bestMove));
       Node mm = getGame().doMove(m,bestMove);
       bestResult = omSearch(mm,depth-1,pre+"  ");
       bestVal = bestResult.value; 
       getGame().undoMove(mm,bestMove);
       if (amLogging()) log(pre+"OPT(comp max) "+getGame().moveString(bestMove)+": value="+bestVal);

      } // end MIN node

      if (amLogging()) log(pre+"OPT "+getGame().moveString(bestMove)+" value="+bestVal);
      return new SearchResult(bestVal, bestMove, bestResult);

   }


   Evaluator eval, evalop;
} 
