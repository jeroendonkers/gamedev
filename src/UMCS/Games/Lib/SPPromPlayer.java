package UMCS.Games.Lib;
import java.util.*;

/**
* PROM search with Alphabetaprobes
* and Similarity Pruning
**/

public class SPPromPlayer extends AlphaBetaPlayer {

   public SPPromPlayer(Game g, Evaluator[] op, double[] pr, int bnd, int md) {
      super(g,md); 
      opponent = op;
      nOpponent = opponent.length;
      probs = pr;
      bound = bnd;
   }

   public SPPromPlayer(Game g, Evaluator[] op, int bnd, int md) 
   { super(g,md); 
     setOpponent(op);
     maxDepth=md;
     bound = bnd;
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


   // set bound
   public void setBound(int bnd) { 
       bound = bnd; 
   }



   public String getName() { 
      return "Probabilistic OM-Search Player with Sim-pruning"; 
   }

   public String toString() { return "SPPromPlayer(depth="+maxDepth+",nopp="+nOpponent+",bnd="+bound+")"; }



    class Moveandres {
    	short move;
    	SearchResult result; 
    	Moveandres(short m, SearchResult r) {
    		move = m; result = r;
    	}
    }


    

    
    public short getABValue() { return storedBestResult.abvalue; }
    public short getABMove() { return storedBestResult.abmove; }    

   /**
    * Generate the answer on Node m, using promSearch.
    **/
   public SearchResult nextMove(Position pos) {

      if (amLogging()) log("Prom Move, depth="+maxDepth);
      clearEvals(); 
      
      //if (probs[0]==0) {
      //	return new SearchResult((short)0,NOMOVE,null);
      //}
      
      
      Node m =  new Node(pos); m.setType(Node.MAX);  

      try {

        // initialize opponent data
        BitSet b = new BitSet(nOpponent);  
        short[] beta = new short[nOpponent];
        for (int i=0; i<nOpponent; i++) { 
           if (probs[i]>0) b.set(i);  // only use opponents with positive probabilities
           beta[i]=POSINF;
        }

        storedBestResult = promSearch(m, maxDepth, b, beta,"");
        return storedBestResult;

      } catch (OutOfTimeException e) {

         System.out.println("OUT OF TIME!");
         return storedBestResult;

      }
   }
   
   
   

   /**
    * Recursive PromSearch algorithm with similarity pruning.
    * @param m Node to generate answer for. 
    * @param depth Maximal depth to expend. 
    * @param b Set of opponents to take into account 
    * @param beta Beta values for all opponents.
    * @param pre log prefix (increases with depth).
    **/
   private promSearchResult promSearch(Node m, int depth, BitSet b, short[] beta, String pre) 
      throws OutOfTimeException {
  
      if (amLogging()) log(pre+"SPPrOMsearch ");

      if (m.isTerminal() || depth<=0) {
         incEvals();
         short v = opponent[0].evaluate(m,maxDepth-depth);
         return new promSearchResult(v,v,NOMOVE,NOMOVE,null);
      }  

      short underbound = (short)Math.floor((1-probs[0])*bound*(depth-1));
      short bestabVal; 
      promSearchResult bestResult = null;
      short bestabMove;
      
      short[] moves = new short[nOpponent];
      short[] valop = new short[nOpponent];
      

      // MAX Node
      // first perform alphabeta for type 0 only


       MoveEnumerator myenum = getGame().generateMoves(m.getPosition());
       short move = myenum.getMove();
       if (move==NOMOVE) 
           return new promSearchResult();  // illegal situation! m should be terminal!


       Vector<Moveandres> movestocheck = new Vector<Moveandres>();
       bestabVal = MININF;
       bestabMove = move;
       setEvaluator(opponent[0]); 
       while (move != NOMOVE) {

            if (depth==maxDepth) storedBestResult = bestResult;
            checkTime();


            if (amLogging()) log(pre+"try(MAX) "+getGame().moveString(move));
            Node mm = getGame().doMove(m,move);
            
            // allow alpha pruning here
            
            short alfa = bestabVal;
            if (alfa>MININF) alfa -= underbound;
            SearchResult r = alphabeta(mm,depth-1,alfa,beta[0],pre);  // AB PROBE
            getGame().undoMove(mm,move);
                        

            if (amLogging()) log(pre+"undo(abMAX) "+getGame().moveString(move)+": value="+r.value);
   
             // maximize for node value (ma).

            if (r.value > bestabVal) {
               bestabVal = r.value;
               bestabMove = move;
            }
       
            movestocheck.addElement(new Moveandres(move,r));
            move = myenum.nextMove();
           
       }
       
       if (depth==1) {  // uneven depth, 
       	    return new promSearchResult(bestabVal,bestabVal,bestabMove,bestabMove,null);
       }
       
       
       // check results for moves to be removed
       
       int k=0;
       while (k<movestocheck.size()) {
       	   if (movestocheck.elementAt(k).result.value<bestabVal - underbound)
       	      movestocheck.removeElementAt(k);
           else k++;        	

       }
       

         // initialize maximization variables
       short bestVal = MININF;
       short bestMove = move;

         // Child Node loop.
       for (int mi=0; mi<movestocheck.size(); mi++) {
       	    move = movestocheck.elementAt(mi).move;
       	    SearchResult r = movestocheck.elementAt(mi).result;

            if (depth==maxDepth) storedBestResult = bestResult;
            checkTime();

            if (amLogging()) log(pre+"try(abMAX) "+getGame().moveString(move));
            Node mm = getGame().doMove(m,move);
          

         // MIN Node 
         // use AB probing !!!

           short val0 = 0;
           short bestminVal = POSINF; 
           short bestminMove = NOMOVE;
           boolean dopromsearch = true;
       
             // first look at opponent type 0 (already preprocessed)
         
           valop[0] = r.value;
           moves[0] = r.move;     

           if (getGame().isRealValue(r.value)) { 
               // if alphabeta produced real value, promsearch is not needed
               dopromsearch = false; bestminVal = r.value; bestminMove = moves[0];

           } else if (r.value < bestVal - underbound) {
               // if value of v0 is too small, promsearch value will also be
               // too small...
               dopromsearch = false; bestminVal = r.value; bestminMove = moves[0];
           }

 
           if (dopromsearch) {
        	
             for (int i=1; i<nOpponent; i++) if (b.get(i)) {
                if (amLogging()) log(pre+"(MIN) probe opponent "+i + "(beta="+beta[i]+")");
                setEvaluator(opponent[i]); 
                // use valop[0] to bound other probes...
                short palpha = (short)(Math.floor(valop[0]-bound));
                short pbeta = beta[i];
                short bb = (short)(Math.ceil(valop[0]+bound)+1);
                if (bb<pbeta) pbeta=bb;
                r = alphabeta(mm,depth-1,palpha,pbeta,pre);  // AB PROBE
                valop[i] = r.value;
                moves[i] = r.move;     
             } else moves[i] = NOMOVE;


              // collect bestmoves (including the move of opponent 0)
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
             short[] newbeta = new short[nOpponent];
       
             for (int j=0; j<cnt; j++) {
                for (int i=0; i<nOpponent; i++) if (b.get(i)) {
                   if (inverse[i]==j) newbeta[i] = (short)(valop[i]+1);
                   else newbeta[i]=POSINF;
                 }
                 if (amLogging()) log(pre+"try(MIN) final "+getGame().moveString(moves[j]));
                 Node mmm = getGame().doMove(mm,moves[j]);
                 promSearchResult pr = promSearch(mmm,depth-2,b,newbeta,pre+"  ");
                 maxval[j] = pr.value; 
                 getGame().undoMove(mmm,moves[j]);
                 if (amLogging()) log(pre+"OPT(comp max) "+getGame().moveString(moves[j])+": value="+bestVal);
             }

             double val = 0;
             for (int i=0; i<nOpponent; i++) if (b.get(i)) 
                val += probs[i] * maxval[inverse[i]];
          
             bestminVal = (short)(val);

           } // end if dopromsearch

// ----------------------- finished min node ------------------------

        getGame().undoMove(mm,move);
        if (amLogging()) log(pre+"undo(MAX) "+getGame().moveString(move)+": value="+bestminVal);
   
        // maximize for node value (ma).
  
        if (bestminVal > bestVal) {
               //bestResult = r;
             bestVal = bestminVal;
             bestMove = move;
         }
            
         move = myenum.nextMove();

       } // end of child for loop


      // End of MAX Node

      return new promSearchResult(bestVal, bestabVal, bestMove, bestabMove, bestResult);

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
     promSearchResult(short v, short abv, short m, short abm, SearchResult n) { 
        super(v,m,n); 
        abvalue = abv;
        abmove = abm;
        
     }
     short abvalue = 0;
     short abmove = 0;
   }

   private Evaluator[] opponent;
   private double[] probs;
   private int nOpponent;
   private int bound;
   private promSearchResult storedBestResult;
 
}
