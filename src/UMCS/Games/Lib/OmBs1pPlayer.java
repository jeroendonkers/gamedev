package UMCS.Games.Lib;

/*************************************************
 * 1 pass Bounded-sum pruning Opponent-Model Search 
 * (Iida / Uitetwijk / vd Herik / Donkers)
 *************************************************/


public class OmBs1pPlayer extends Player {

   public OmBs1pPlayer(Game g, Evaluator op, int md, int b) 
   { super(g); 
     maxDepth = md;
     eval = g.getDefaultEvaluator();
     evalop = op;
     bound = b;
   }

   public OmBs1pPlayer(Game g, Evaluator e, Evaluator op, int md, int b) 
   { super(g); 
     maxDepth = md;
     eval = e; evalop = op;
     bound = b;
   }

   public String getName() { return "1-pass Boundedsum OM-search Player"; }

   public void setEval(Evaluator e) { eval = e; }
   public void setEvalOp(Evaluator o) { evalop = o; }

   public SearchResult nextMove(Position pos) {
      if (amLogging()) log("OM BS1p, depth="+maxDepth);
      clearEvals();
      Node m = new Node(pos); m.setType(Node.MAX); 
      storedBestResult = null;
      try {
        return omSearch(m, maxDepth,MININF,MININF,POSINF,POSINF,"");
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

   private omSearchResult omSearch(Node m, int depth, short a0, short aopp, short b0, short bopp, String pre) 
    throws OutOfTimeException {

      if (amLogging()) log(pre+"OMsearch "+a0+" "+aopp+" "+b0+" "+bopp);

      if (m.isTerminal()) {
          short v = eval(m,depth);
          return new omSearchResult(v,v,NOMOVE,null);
      }

      if (depth==0) {
         short vop=0, v=0;
         vop = evalop(m,depth);
         if (vop<=bopp) v = eval(m,depth);
         return new omSearchResult(v,vop,NOMOVE,null);
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

         bestVal = a0; 
         bestValop = a0;
         short beta = b0;
         if (a0 < b0) { bestValop = decBound(a0); beta = incBound(b0); }

         while (move!=NOMOVE) {
 
            if (depth==maxDepth) storedBestResult = bestResult;
            checkTime();
     
            if (amLogging()) log(pre+"try(MAX) "+getGame().moveString(move));
            Node mm = getGame().doMove(m,move);
            omSearchResult r = omSearch(mm,depth-1,bestVal,decBound(bestValop),incBound(bopp),beta,pre+"  ");
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

            if (bestValop>=bopp) move = NOMOVE; 
            else move = enum.nextMove();
         }     

      } else {

         // minimize only on opponent value 
         bestValop = b0;
         short alpha = a0;
         bestVal = 0;
         if (a0 < b0) {
             bestValop = incBound(b0);
             alpha = decBound(a0);
         }

//        if (amLogging()) log(pre+"MIN start: "+bestValop);

         while (move!=NOMOVE) {

            Node mm = getGame().doMove(m,move);

            omSearchResult r = null;
            short vop = 0, v = 0;

            if (depth==1) {

               if (amLogging()) log(pre+"eval(MIN) "+getGame().moveString(move));
               vop = evalop(mm,depth);   // only evaluate opponent !!!
               if (amLogging()) log(pre+"eval(MIN) valueopp="+vop);

            } else {
                if (amLogging()) log(pre+"try(MIN) "+getGame().moveString(move));
                r = omSearch(mm,depth-1,decBound(a0),alpha,b0,bestValop,pre+"  ");
                vop = r.valueop;
                v = r.value;
            }

            getGame().undoMove(mm,move);
            if (amLogging()) log(pre+"undo(MIN) "+getGame().moveString(move)+": valueop="+vop);

            if (vop < bestValop) {
               if (amLogging()) log(pre+"valueopp < bestvalopp = "+bestValop);
                bestResult = r;
                bestValop = vop;
                bestVal = v;
                bestMove = move;
            }
            if (bestValop<=aopp) {
                 if (amLogging()) log(pre+"PRUNE: bestvalopp < Alphaopp: "+bestValop+" < "+aopp);
                 move=NOMOVE; 
            }
            else move = enum.nextMove();
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


   private short incBound(short v) {
     if (v+bound >= POSINF) return POSINF; else return (short)(v+bound);
   }

   private short decBound(short v) {
     if (v-bound <= MININF) return MININF; else return (short)(v-bound);
   }


   Evaluator eval, evalop;
   int maxDepth, bound;
   SearchResult storedBestResult;
}
