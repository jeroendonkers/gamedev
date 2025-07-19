package UMCS.Games.Lib;

public class AbTTPlayerNoOrdering extends Player {

   public  AbTTPlayerNoOrdering(Game g, int md) 
   { super(g); 
     eval = g.getDefaultEvaluator();
     hhtable = getGame().getNewHHTable();
     maxDepth = md;
   }

   public  AbTTPlayerNoOrdering(Game g, Evaluator e, int md) 
   { super(g); 
     eval = e;
     hhtable = getGame().getNewHHTable();
     maxDepth = md;
   }

   public String getName() { return "AlphaBeta Player TT but no ordering"; }


   public void setEvaluator(Evaluator e) {eval = e;}
   public Evaluator getEvaluator() {return eval;}


   public void setMaxDepth(int md) { 
      maxDepth=md; 
   }

   public void donotIterate() { iterate=false; }

   public SearchResult nextMove(Position pos) {
      if (amLogging()) log("------ next move ---------");

      killers = new short[maxDepth];
      for (int i=0; i<maxDepth; i++) killers[i] = NOMOVE;

      if (hasTTable()) getTTable().clear(); else iterate=false;
      clearEvals(); clearSearchResults();
      Node m = new Node(Node.MAX,pos.clonePosition()); 
      if (hhtable!=null) hhtable.refresh();
      try {
         int minDepth=2;
         if (!iterate) minDepth=maxDepth;
         SearchResult r=null;
         for (searchDepth=minDepth; searchDepth<=maxDepth; searchDepth++) {
             r=alphabeta(m, searchDepth,MININF, POSINF, "");
             addSearchResult(r);
         }
         return r;
      } catch (OutOfTimeException e) {
         System.out.println("OUT OF TIME! Searched to depth "+searchDepth );
         return storedBestResult;
      }
   }


   short eval(Node m, long hash, int depth, short score, byte flag, int ply) {
       if (ply>=depth && flag==TTable.EXACT) return score;
       incEvals();
       short d = eval.evaluate(m,searchDepth - depth);
       if (ply>=depth) {
         if (flag==TTable.UPPER && d>score) return score;
         if (flag==TTable.LOWER && d<score) return score;
       } 
       if (ply<0 && hasTTable()) {
         getTTable().putEntry(hash,NOMOVE,d,TTable.EXACT,(byte)0);
       }
       return d;
   }

   SearchResult alphabeta(Node m, int depth, short alpha, short beta, String pre) 
    throws OutOfTimeException {

      if (amLogging()) log(pre+"Alphabeta ["+alpha+","+beta+"]");

      short oldalpha = alpha, oldbeta = beta; 
      long hash=0; 
      short ttply = -1; 
      short ttmove = NOMOVE;
      short ttscore = 0;
      byte ttflag = 0;

      if (m.isTerminal()) {
         if (amLogging()) log(pre+"terminal");
         incEvals();
         return new SearchResult(eval.evaluate(m,searchDepth-depth),NOMOVE,null);
      }

      boolean isMax = (m.getType() == Node.MAX);

      if (hasTTable()) {
         hash=m.getPosition().getHashValue();
         int index = getTTable().getEntry(hash);
         if (index>=0) {
			 TTent++;
             ttply = getTTable().getPly(index);
             ttmove = getTTable().getMove(index);
             if (ttply>=depth) {
                ttflag = getTTable().getFlag(index);
                ttscore = getTTable().getScore(index);
                switch (ttflag) {
                case TTable.EXACT:
                   TThit++;
                   if (amLogging()) log(pre+"TT exact hit: "+getGame().moveString(ttmove)+" "+ttscore +" (ply "+ttply+")");
                   return new SearchResult(ttscore,ttmove,null);                
                case TTable.LOWER: if (ttscore>alpha) alpha=ttscore; break;
                case TTable.UPPER: if (ttscore<beta) beta=ttscore;   break;
                }               
               if (alpha>=beta) {
                  TThit++;
                  if (amLogging()) log(pre+"TT window hit: "+getGame().moveString(ttmove)+" "+ttscore +" (ply "+ttply+")");
                  return new SearchResult(ttscore,ttmove,null);                
               }
               if (amLogging()) log(pre+"TT adjust window ["+alpha+","+beta+"]");
               TTuse++;
            }
         }
      }

      if (depth==0) {
         if (amLogging()) log(pre+"Leaf node");
         return new SearchResult(eval(m,hash,depth,ttscore,ttflag,ttply),NOMOVE,null);
      }  


      boolean dosearch = true;
      short bestVal = (isMax ? alpha : (short)-beta);
      short bestMove = NOMOVE;
      SearchResult bestResult=null;
       
      // NO ORDERING: we DO NOT try ttmove first
/*
      if (ttply>0 && ttmove!=NOMOVE) {
         bestMove=ttmove;
         if (amLogging()) log(pre+"try(TT) "+getGame().moveString(ttmove));
         Node mm = getGame().doMove(m,ttmove);
         bestResult = alphabeta(mm,depth-1,alpha,beta,pre+"  ");
         getGame().undoMove(mm,ttmove);
         if (amLogging()) log(pre+"undo "+getGame().moveString(ttmove)+": value="+bestResult.value);
         bestVal = bestResult.value; 
         if (isMax) {
            if (bestVal>=beta) dosearch=false;
         } else {
            if (bestVal<=alpha) dosearch=false; else bestVal = (short)-bestVal;
         }  
      }

*/
     if (dosearch) {

      MoveEnumerator enum;
      if (hhtable==null) enum = getGame().generateMoves(m.getPosition());
      else enum = getGame().generateMoves(m.getPosition(), hhtable);

      short move = enum.getMove();
      if (move==NOMOVE) {
        if (amLogging()) log(pre+"NO MOVE FOUND!");
        return new SearchResult();  // illegal situation! m should be terminal!
      }

      if (bestMove==NOMOVE) bestMove=move;

      while (move!=NOMOVE) if (move==ttmove) move = enum.nextMove();
      else {
 
           if (amLogging()) log(pre+"try "+getGame().moveString(move));
           Node mm = getGame().doMove(m,move);

           SearchResult r=null;
           if (isMax)
               r = alphabeta(mm,depth-1,bestVal,beta,pre+"  ");
           else
               r = alphabeta(mm,depth-1,alpha,(short)-bestVal,pre+"  ");
           short d= r.value; 
           if (!isMax) d = (short)-d; // flip sign for minimax

           getGame().undoMove(mm,move);
           if (amLogging()) log(pre+"undo "+getGame().moveString(move)+": value="+r.value);

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


           if (depth == searchDepth) {
             storedBestResult = new SearchResult(bestVal,bestMove,bestResult);
             if (amLogging() && bestResult!=null) log(pre+"STORE "+getGame().moveString(bestMove));            
           }
           checkTime();

        }  // while



        if (!isMax) bestVal = (short)-bestVal;

      }  // end dosearch;

      if (amLogging()) log(pre + "OPT "+getGame().moveString(bestMove)+": value="+bestVal);

      if (hasTTable()) {
         byte flag;
         if (isMax) {         
            if (bestVal <= oldalpha) flag = TTable.UPPER;
            else if (bestVal >= beta) flag = TTable.LOWER;
            else flag = TTable.EXACT;
         } else {
            if (bestVal >= oldbeta) flag = TTable.LOWER;
            else if (bestVal <= alpha) flag = TTable.UPPER;
            else flag = TTable.EXACT;
         }
         getTTable().putEntry(hash,bestMove,bestVal,flag,(byte)depth);
      }


     // history heuristic:
     if (hhtable!=null) hhtable.addMove(bestMove,searchDepth-depth);

      return new SearchResult(bestVal,bestMove,bestResult);   
   }


   Evaluator eval;
   int maxDepth, searchDepth;
   SearchResult storedBestResult;
   boolean iterate=true;
   HHTable hhtable = null;


   public long TTent=0, TThit=0, TTuse=0;
   private short[] killers;
}
