package UMCS.Games.Lib;
import java.util.*;


/**
* One-pass PROM search
* no pruning 
**/


public class PromPlainNabPlayer extends Player {

   public PromPlainNabPlayer(Game g, Evaluator[] op, double[] pr, int md) {
      super(g); 
      opponent = op;
      nOpponent = opponent.length;
      probs = pr;
      maxDepth = md;
   }

   public PromPlainNabPlayer(Game g, Evaluator[] op, int md) 
   { super(g); 
     setOpponent(op);
     maxDepth=md;
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
      return "Probabilistic OM-Search Player (no beta-pruning, no probing)"; 
   }

   public String toString() { return "PromPlainNabPlayer(depth="+maxDepth+",nopp="+nOpponent+")"; }


   /**
    * Generate the answer on Node m, using promSearch.
    **/
   public SearchResult nextMove(Position pos) {

      if (amLogging()) log("Prom Move, depth="+maxDepth);
      clearEvals(); 
      Node m =  new Node(pos); m.setType(Node.MAX);  

      try {

        // initialize opponent data
        BitSet b = new BitSet(nOpponent);  
        for (int i=0; i<nOpponent; i++) { 
           if (probs[i]>0) b.set(i);  // only use opponents with positive probabilities
        }

        return promSearch(m, maxDepth, b, "");

      } catch (OutOfTimeException e) {

         System.out.println("OUT OF TIME!");
         return storedBestResult;

      }
   }

   /**
    * Recursive PromSearch algorithm with opponent beta-pruning.
    * @param m Node to generate answer for. 
    * @param depth Maximal depth to expend. 
    * @param b Set of opponents to take into account (others are already pruned).
    * @param beta Beta values for all opponents.
    * @param pre log prefix (increases with depth).
    **/
   private promSearchResult promSearch(Node m, int depth, BitSet b, String pre) 
      throws OutOfTimeException {
  
      if (amLogging()) log(pre+"PrOMsearch ");

      if (m.isTerminal()) {
          incEvals();
          short v = opponent[0].evaluate(m,depth);
          short[] vv = new short[nOpponent];
          for (int i=0; i<nOpponent; i++) vv[i]=v;
          return new promSearchResult(v,vv,1,NOMOVE,null);
      }

      if (depth==0) {
         short[] vv = new short[nOpponent];
         for (int i = 0; i<nOpponent; i++) 
              if (i==0 || b.get(i)) { 
                 vv[i] = opponent[i].evaluate(m,depth); 
                 incEvals();
              }
         return new promSearchResult(vv[0],vv,1,NOMOVE,null);
      }  

      boolean isMax = (m.getType() == Node.MAX);

      MoveEnumerator enum = getGame().generateMoves(m.getPosition());
      short move = enum.getMove();
      if (move==NOMOVE) 
         return new promSearchResult();  // illegal situation! m should be terminal!
      short bestVal; 
      short[] bestValop = new short[nOpponent];
      promSearchResult bestResult = null;
      short bestMove = move;

      if (isMax) {  

        // MAX Node
        // We maximize on ma value for the node's value.
        // For every opponent we also maximize.
        // Apply beta-pruning for opponents

         // initialize maximization variables
         bestVal = MININF;
         bestMove = move;
         for (int i = 0; i<nOpponent; i++) {
           bestValop[i] = MININF;
         }

           // Child Node loop.
         while (move != NOMOVE) {

            if (depth==maxDepth) storedBestResult = bestResult;
            checkTime();

            if (amLogging()) log(pre+"try(MAX) "+getGame().moveString(move));
            Node mm = getGame().doMove(m,move);
            promSearchResult r = promSearch(mm,depth-1,b,pre+"  ");
            getGame().undoMove(mm,move);
            if (amLogging()) log(pre+"undo(MAX) "+getGame().moveString(move)+": value="+r.value);
   
            // maximize for all opponents in b.
            for (int i=0; i<nOpponent; i++) 
               if ((b.get(i)) && r.valueop[i]>bestValop[i]) {
                  bestValop[i] = r.valueop[i];
            }   

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

         short[] maxval = new short[nOpponent];
         bestVal = POSINF;
         for (int i = 0; i<nOpponent; i++) {
           bestValop[i] = POSINF;
         }

         while (move != NOMOVE) {
       
            if (amLogging()) log(pre+"try(MIN) "+getGame().moveString(move));
            Node mm = getGame().doMove(m,move);
            promSearchResult r = promSearch(mm,depth-1,b,pre+"  ");
            getGame().undoMove(mm,move);
            if (amLogging()) log(pre+"undo(MIN) "+getGame().moveString(move)+": value="+r.value);

            for (int i=0; i<nOpponent; i++) if (b.get(i)) {
               if (r.valueop[i] < bestValop[i]) {
                  bestValop[i] = r.valueop[i];
                  maxval[i] = r.value;
               }  
            }

            move = enum.nextMove();
          } // end of child loop


        double val = 0;
        for (int i=0; i<nOpponent; i++) if (b.get(i)) 
           val += probs[i] * maxval[i];
        
        bestVal = (short)(val);

      } // end MIN node

      return new promSearchResult(bestVal, bestValop, 1, bestMove, bestResult);

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
     promSearchResult(short v, short[] op, double pr, short m, SearchResult n) { 
        super(v,m,n); 
        valueop = op;
        movepr = pr;
     }
     short[] valueop = null;    // opponent value
 
     double movepr = 0;
   }

   private Evaluator[] opponent;
   private double[] probs;
   private int nOpponent;
   private int maxDepth;
   private SearchResult storedBestResult; 
}
