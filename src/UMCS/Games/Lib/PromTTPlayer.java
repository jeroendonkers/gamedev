package UMCS.Games.Lib;
import java.util.*;

/**
* PROM search with Alphabetaprobes (Half-window)
* Beta-pruning (-passing) 
* and transposition tables
**/


public class PromTTPlayer extends AbTTPlayer {

   public PromTTPlayer(Game g, Evaluator[] op, double[] pr, int md) {
      super(g,md); 
      opponent = op;
      nOpponent = opponent.length;
      probs = pr;
   }

   public PromTTPlayer(Game g, Evaluator[] op, int md) 
   { super(g,md); 
     setOpponent(op);
     maxDepth=md;
   }

   public void setOppTTables(TTable[] tt) {
     opptt=tt;
   }

   public void setOpponent(Evaluator[] o) { 
     opponent = o;
     nOpponent = opponent.length;
     probs = new double[nOpponent];
     for (int i=0; i<nOpponent; i++) probs[i] = 1.0/nOpponent;
   }

  
   // set opponent probabilites
   public void setProbs(double[] p) { 
       probs=p; 
   }

   public String getName() { 
      return "Probabilistic OM-Search Player with TT's"; 
   }

   public String toString() { return "PromPlayer(depth="+maxDepth+",nopp="+nOpponent+")"; }


   /**
    * Generate the answer on Node m, using promSearch.
    **/
   public SearchResult nextMove(Position pos) {

      if (amLogging()) log("Prom Move, depth="+maxDepth);
      clearEvals();  clearSearchResults();
      Node m =  new Node(pos); m.setType(Node.MAX);  

      try {

        // initialize opponent data
        BitSet b = new BitSet(nOpponent);  
        short[] beta = new short[nOpponent];
        for (int i=0; i<nOpponent; i++) { 
           if (probs[i]>0) {
                b.set(i);  // only use opponents with positive probabilities
                if (opptt!=null && opptt[i]!=null) opptt[i].clear();
           }
           beta[i]=POSINF;
        }

        int minDepth=2;
        if (!iterate) minDepth=maxDepth;
        SearchResult r=null;
        for (searchDepth=minDepth; searchDepth<=maxDepth; searchDepth++) {
           for (int i=0; i<nOpponent; i++)  beta[i]=POSINF;
           r = promSearch(m, searchDepth, b, beta,"");
           addSearchResult(r);
        } 
        return r;

      } catch (OutOfTimeException e) {

         System.out.println("OUT OF TIME!");
         return storedBestResult;

      }
   }

   /**
    * Recursive PromSearch algorithm with opponent beta-pruning.
    * @param m Node to generate answer for. 
    * @param depth Maximal depth to expend. 
    * @param b Set of opponents to take into account 
    * @param beta Beta values for all opponents.
    * @param pre log prefix (increases with depth).
    **/
   private promSearchResult promSearch(Node m, int depth, BitSet b, short[] beta, String pre) 
      throws OutOfTimeException {
  
      if (amLogging()) log(pre+"PrOMsearch ");

      if (m.isTerminal() || depth==0) {
         incEvals();
         short v = opponent[0].evaluate(m,searchDepth-depth);
         return new promSearchResult(v,1,NOMOVE,null);
      }  

      boolean isMax = (m.getType() == Node.MAX);

      short bestVal; 
      promSearchResult bestResult = null;
      short bestMove;

      if (isMax) {  

        // MAX Node
        // We maximize on ma value for the node's value.

         MoveEnumerator enum = getGame().generateMoves(m.getPosition());
         short move = enum.getMove();
         if (move==NOMOVE) 
           return new promSearchResult();  // illegal situation! m should be terminal!

         // initialize maximization variables
         bestVal = MININF;
         bestMove = move;

         // Child Node loop.
         while (move != NOMOVE) {

            if (depth==maxDepth) storedBestResult = bestResult;
            checkTime();

            if (amLogging()) log(pre+"try(MAX) "+getGame().moveString(move));
            Node mm = getGame().doMove(m,move);
            promSearchResult r = promSearch(mm,depth-1,b,beta,pre+"  ");
            getGame().undoMove(mm,move);
            if (amLogging()) log(pre+"undo(MAX) "+getGame().moveString(move)+": value="+r.value);
   
            // maximize for node value (ma).

            if (r.value > bestVal) {
               bestResult = r;
               bestVal = r.value;
               bestMove = move;
            }
            
            move = enum.nextMove();

         } // end of child loop


         // End of MAX Node

      } else {   

         // MIN Node 
         // use AB probing !!!

         short[] moves = new short[nOpponent];
         short[] valop = new short[nOpponent];

         bestVal = POSINF; bestMove=NOMOVE;
         boolean realValue = false;

         for (int i=0; i<nOpponent; i++) if (b.get(i)) {
            if (amLogging()) log(pre+"(MIN) probe opponent "+i + "(beta="+beta[i]+")");
            setEvaluator(opponent[i]);
            if (opptt!=null && opptt[i]!=null) setTTable(opptt[i]);
            SearchResult r = alphabeta(m,depth,MININF,beta[i],pre);  // AB PROBE
            valop[i] = r.value;
            moves[i] = r.move;               
            if (getGame().isRealValue(r.value)) { 
               if (amLogging()) log(pre+"REAL VALUE!");               
               realValue = true; bestVal = r.value; bestMove = r.move;
               break;
             }
         } else moves[i] = NOMOVE;

        if (!realValue) {        
         // collect bestmoves
         int[] inverse = new int[nOpponent];
         int cnt=0;
         for (int i=0; i<nOpponent; i++) if (b.get(i)) {
            if (cnt==0)  {
                moves[cnt]=moves[i]; 
                inverse[i]=cnt;
                cnt++;
            } else {
               int fnd=-1;
               for (int j=0; j<cnt; j++) 
                    if (moves[j]==moves[i]) { fnd=j; break; }
               if (fnd==-1) {
                  moves[cnt]=moves[i]; 
                  inverse[i]=cnt;
                  cnt++;
               } else inverse[i]=fnd;
            }
          }

       
          // perform promSearch recursion on all moves that have optima
          short[] maxval = new short[cnt];
          beta = new short[nOpponent];
       
          for (int j=0; j<cnt; j++) {
             for (int i=0; i<nOpponent; i++) if (b.get(i)) {
                 if (inverse[i]==j) beta[i] = (short)(valop[i]+1);
                 else beta[i]=POSINF;
             }
             if (amLogging()) log(pre+"try(MIN) final "+getGame().moveString(moves[j]));
             Node mm = getGame().doMove(m,moves[j]);
             promSearchResult r = promSearch(mm,depth-1,b,beta,pre+"  ");
             maxval[j] = r.value; 
             getGame().undoMove(mm,moves[j]);
             if (amLogging()) log(pre+"OPT(comp max) "+getGame().moveString(moves[j])+": value="+bestVal);
          }

          double val = 0;
          for (int i=0; i<nOpponent; i++) if (b.get(i)) 
             val += probs[i] * maxval[inverse[i]];
          
          bestVal = (short)(val);

        } // end if realValue
      } // end MIN node

      return new promSearchResult(bestVal, 1, bestMove, bestResult);

   } // end of PromSearch


   /**
    * Check wether a bitset is empty.
    **/
   private boolean empty(BitSet b) {
      return b.equals(zero); 
   }

   // Auxillary bitset for method empty()
   private static BitSet zero = new BitSet();

   class promSearchResult extends SearchResult {
     promSearchResult() { }
     promSearchResult(short v, double pr, short m, SearchResult n) { 
        super(v,m,n); 
        movepr = pr;
     }
     double movepr = 0;
   }

   private Evaluator[] opponent;
   private double[] probs;
   private int nOpponent;
 

   private TTable[] opptt;
}
