package UMCS.Games.Lib;

/*************************************************
 * Plain Opponent-Model Search 
 * (Iida / Uitetwijk / vd Herik / Donkers)
 *************************************************/


public class OmPlainPlayer extends Player {

   public OmPlainPlayer(Game g, Evaluator op, int md) 
   { super(g); 
     maxDepth = md;
     eval = g.getDefaultEvaluator();
     evalop = op;
   }

   public OmPlainPlayer(Game g, Evaluator e, Evaluator op, int md) 
   { super(g); 
     maxDepth = md;
     eval = e; evalop = op;
   }

   public String getName() { return "Plain OM-search Player"; }

   public void setEval(Evaluator e) { eval = e; }
   public void setEvalOp(Evaluator o) { evalop = o; }

   public SearchResult nextMove(Position pos) {
      if (amLogging()) log("OM Pure, depth="+maxDepth);
      clearEvals();
      Node m = new Node(pos); m.setType(Node.MAX); 
      storedBestResult = null;
      try {
        return omSearch(m, maxDepth,"");
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

   private omSearchResult omSearch(Node m, int depth, String pre) 
    throws OutOfTimeException {

      if (amLogging()) log(pre+"OMsearch");

      if (m.isTerminal()) {
          short v = eval(m,depth);
          return new omSearchResult(v,v,NOMOVE,null);
      }

      if (depth==0) {
         return new omSearchResult(eval(m,depth),evalop(m,depth),NOMOVE,null);
      }  

      boolean isMax = (m.getType() == Node.MAX);
      MoveEnumerator enum = getGame().generateMoves(m.getPosition());

      short move = enum.getMove();
      if (move==NOMOVE) 
        return new omSearchResult();  // illegal situation! m should be terminal!

      short bestVal; 
      short bestValop;
      short bestMove = move;
      omSearchResult bestResult=null;

      if (isMax) {   // we maximize on player's value but also on opponent value

         bestVal = MININF; 
         bestValop = MININF;

         while (move!=NOMOVE) {
 
            if (depth==maxDepth) storedBestResult = bestResult;
            checkTime();
     
            if (amLogging()) log(pre+"try(MAX) "+getGame().moveString(move));
            Node mm = getGame().doMove(m,move);
            omSearchResult r = omSearch(mm,depth-1,pre+"  ");
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

         // minimize only on opponent value 
         bestValop = POSINF;
         bestVal = POSINF;

         while (move!=NOMOVE) {

            Node mm = getGame().doMove(m,move);

            omSearchResult r = null;
            short vop = 0, v = 0;

            if (depth==1) {

               if (amLogging()) log(pre+"eval(MIN) "+getGame().moveString(move));
               vop = evalop(mm,depth);   // only evaluate opponent !!!

            } else {
                if (amLogging()) log(pre+"try(MIN) "+getGame().moveString(move));
                r = omSearch(mm,depth-1,pre+"  ");
                vop = r.valueop;
                v = r.value;
            }

            getGame().undoMove(mm,move);
            if (amLogging()) log(pre+"undo(MIN) "+getGame().moveString(move)+": valueop="+vop);

            if (vop < bestValop) {
                bestResult = r;
                bestValop = vop;
                bestVal = v;
                bestMove = move;
            }

            move = enum.nextMove();
        } // while     

       if (depth==1) {  // now evaluate for bestMove!!!
           Node mm = getGame().doMove(m,bestMove);
           bestVal = eval(mm,depth); 
           getGame().undoMove(mm,bestMove);
           if (amLogging()) log(pre+"OPT(comp max) "+getGame().moveString(bestMove)+": value="+bestVal);
       }

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
   int maxDepth;
   SearchResult storedBestResult;
}
