package UMCS.Games.Lib;

/*************************************************
 * Pure Opponent-Model Search with beta-pruning and AB probe
 * (Iida / Uitetwijk / vd Herik / Donkers)
 *************************************************/


public class OmPureTestPlayer extends AlphaBetaPlayer {

   public OmPureTestPlayer(Game g, Evaluator op, int md) 
   { super(g,md); 
     eval = g.getDefaultEvaluator();
     evalop = op;
   }

   public OmPureTestPlayer(Game g, Evaluator e, Evaluator op, int md) 
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

   private short evalop(Node m, int depth) {
      incEvals(); incPartialEvals();
      return evalop.evaluate(m,maxDepth-depth);
   }

   private short eval(Node m, int depth) {
      incEvals();
      return eval.evaluate(m,maxDepth-depth);
   }

   private omSearchResult omSearch(Node m, int depth, short beta, String pre) 
    throws OutOfTimeException {

      if (amLogging()) log(pre+"OMsearch Ab ["+beta+"]");

      if (m.isTerminal()) {
          short v = eval(m,depth);
          return new omSearchResult(v,v,NOMOVE,null);
      }

      if (depth==0) {
         short vop=0, v=0;
         vop = evalop(m,depth);
         v = eval(m,depth);
         return new omSearchResult(v,vop,NOMOVE,null);
      }  

      boolean isMax = (m.getType() == Node.MAX);

      short bestVal; 
      short bestValop;
      short bestMove = NOMOVE;
      omSearchResult bestResult=null;

      if (isMax) {   // we maximize on player's value but also on opponent value

         bestVal = MININF; 
         bestValop = MININF;

         MoveEnumerator enum = getGame().generateMoves(m.getPosition());
         short move = enum.getMove();
         if (move==NOMOVE) 
            return new omSearchResult();  // illegal situation! m should be terminal!
         bestMove = move;

         while (move!=NOMOVE) {
 
            if (depth==maxDepth) storedBestResult = bestResult;
            checkTime();
     
            if (amLogging()) log(pre+"try(MAX) "+getGame().moveString(move));
            Node mm = getGame().doMove(m,move);
            omSearchResult r = omSearch(mm,depth-1,beta,pre+"  ");
            getGame().undoMove(mm,move);
            if (amLogging()) log(pre+"undo(MAX) "+getGame().moveString(move)+": value="+r.value);

            if (r.value>bestVal) {
                bestVal = r.value;
                bestResult = r;
                bestMove = move;
            }

            if (r.valueop>bestValop) {
               bestValop = r.valueop;
            }

            move = enum.nextMove();
         }     

      } else {

       SearchResult r = alphabeta(m,depth,MININF,beta,pre);  // AB PROBE
       bestValop = r.value;
       bestMove = r.move;
       bestVal = POSINF;

       if (amLogging()) log(pre+"try(MIN) final "+getGame().moveString(bestMove));
         Node mm = getGame().doMove(m,bestMove);
        if (depth==1) 
           bestVal = eval(mm,depth-1); 
        else {
           // lookout: open beta-window somewhat!
           bestResult = omSearch(mm,depth-1,(short)(bestValop+1),pre+"  ");
           bestVal = bestResult.value; 
        }
       getGame().undoMove(mm,bestMove);
       if (amLogging()) log(pre+"OPT(comp max) "+getGame().moveString(bestMove)+": value="+bestVal);

      } // end MIN node

      if (amLogging()) log(pre+"OPT "+getGame().moveString(bestMove)+" value="+bestVal+", valueop="+bestValop);
      return new omSearchResult(bestVal, bestValop, bestMove, bestResult);

   }

   class omSearchResult extends SearchResult {
     omSearchResult() { 
        super(); valueop=(short)0; 
     }

     omSearchResult(short v, short vop, short m, SearchResult n) { 
        super(v,m,n); valueop=vop; 
     }
     short valueop;
   }


   Evaluator eval, evalop;
 
}
