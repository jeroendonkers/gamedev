package UMCS.Games.Lib;

/*************************************************
 * Pure Opponent-Model Search with beta-pruning and AB probe
 * (Iida / Uitetwijk / vd Herik / Donkers)
 *************************************************/


public class OmPurePlayer extends AlphaBetaPlayer {

   public OmPurePlayer(Game g, Evaluator op, int md) 
   { super(g,md); 
     eval = g.getDefaultEvaluator();
     evalop = op;
   }

   public OmPurePlayer(Game g, Evaluator e, Evaluator op, int md) 
   { super(g,md); 
     eval = e; evalop = op;
   }

   public String getName() { return "Pure OM-search Player AB-scout"; }

   public void setEval(Evaluator e) { eval = e; }
   public void setEvalOp(Evaluator o) { evalop = o; }

   public SearchResult nextMove(Position pos) {
      if (amLogging()) log("OM Pure, depth="+maxDepth);
      clearEvals();
      Node m = new Node(pos); m.setType(Node.MAX); 
      storedBestResult = null;
      try {
        setEvaluator(evalop); // AB must use MIN's evaluator
        return omSearch(m, maxDepth,POSINF,"");
      } catch (OutOfTimeException e) {
         System.out.println("OUT OF TIME!");
         return storedBestResult;
      }

   }

   private SearchResult omSearch(Node m, int depth, short beta, String pre) 
    throws OutOfTimeException {

      if (amLogging()) log(pre+"OMsearch Ab ["+beta+"]");

      if (m.isTerminal() || depth==0) {
          incEvals();
          short v = eval.evaluate(m,maxDepth-depth);
          if (amLogging()) log(pre+"leaf: "+v+" hash="+m.getPosition().getHashValue());
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
     
            Node mm = getGame().doMove(m,move);
            if (amLogging()) log(pre+"try(MAX)"+getGame().moveString(bestMove)
               +" hash1="+m.getPosition().getHashValue()
               +" hash2="+mm.getPosition().getHashValue());
            SearchResult r = omSearch(mm,depth-1,beta,pre+"  ");
            if (amLogging()) log(pre+"undo(MAX) "+getGame().moveString(move)+": value="+r.value);
            getGame().undoMove(mm,move);


            if (r.value>bestVal) {
                bestVal = r.value;
                bestResult = r;
                bestMove = move;
            }

            move = enum.nextMove();
         }     

      } else {

       SearchResult r = alphabeta(m,depth,MININF,beta,pre);  // AB PROBE
       short bestValop = r.value;
       bestMove = r.move;
        if (getGame().isRealValue(r.value)) {
         bestResult = r;
         bestVal = r.value;
       } else {
         Node mm = getGame().doMove(m,bestMove);
         if (amLogging()) log(pre+"try(MIN) final "+getGame().moveString(bestMove)
               +" hash1="+m.getPosition().getHashValue()
               +" hash2="+mm.getPosition().getHashValue());
         bestResult = omSearch(mm,depth-1,(short)(bestValop+1),pre+"  ");
         bestVal = bestResult.value; 
         if (amLogging()) log(pre+"OPT(comp max) "+getGame().moveString(bestMove)+": value="+bestVal);
         getGame().undoMove(mm,bestMove);
       }

      } // end MIN node

      if (amLogging()) log(pre+"OPT "+getGame().moveString(bestMove)+" value="+bestVal);
      return new SearchResult(bestVal, bestMove, bestResult);

   }

   Evaluator eval, evalop;
 
}
