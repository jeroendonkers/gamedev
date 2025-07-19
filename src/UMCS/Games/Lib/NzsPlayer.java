package UMCS.Games.Lib;

/**
 * @author Jeroen
 *
 *  simple nonzerosum player: no pruning, just maximize (negamax-like)
 *  SIMPLEMODE: in case of tie-break: take first move.
 * 
 */
public class NzsPlayer extends Player {

   public static final int SIMPLEMODE = 0;
   public static final int COOPERATIVEMODE = 1;
   public static final int COMPETITIVEMODE = 2;
   public static final int ALTRUISTICMODE = 3;
   public static final int COLLECTALL = 100;
   
   


   class dummyEvaluator implements Evaluator {
      public short evaluate(Node m, int depth) {
         return 0;
      }
   }

   public NzsPlayer(Game g, int md) 
   { super(g); 
     evalv = g.getDefaultEvaluator();
     evalc = new dummyEvaluator();
     maxDepth = md;
   }

   public NzsPlayer(Game g, Evaluator ev, Evaluator ec, int md) 
   { super(g); 
     evalv = ev;
     evalc = ec;
     maxDepth = md;
   }

   public void setMode(int m) {mode=m;}

   public String getName() { return "Nzs plain Player ("+mode+")"; }

   public void setEvaluator(Evaluator e) {evalv = e;}

   public void setEvaluatorV(Evaluator e) {evalv = e;}
   public void setEvaluatorC(Evaluator e) {evalc = e;}

   public void setMaxDepth(int md) { 
      maxDepth=md; 
   }

   public SearchResult nextMove(Position pos) {
      if (amLogging()) log("------ next move ---------");
      clearEvals();
      Node m = new Node(Node.MAX,pos); 
      return minimax(m, maxDepth);
   }

   NzsSearchResult minimax(Node m, int depth) {

      if (m.isTerminal() || depth==0) {
         incEvals();
         short v = evalv.evaluate(m,maxDepth-depth);
         short c = evalc.evaluate(m,maxDepth-depth);
         // we have to flip sign of v at uneven depth!
         if (((maxDepth - depth) & 1) !=0) v = (short)-v;  
         return new NzsSearchResult(c,v,NOMOVE,null);
      }

      MoveEnumerator enum = getGame().generateMoves(m.getPosition());
      short move = enum.getMove();
      if (move==NOMOVE) 
        return new NzsSearchResult();  // illegal situation! m should be terminal!

      NzsSearchResult bestResult=null;
      short bestVal = MININF, bestopVal=MININF, bestv=0, bestc=0, bestMove=NOMOVE;
      

      while (move!=NOMOVE) {
         short d,e,c,v;
         Node mm = getGame().doMove(m,move);
         NzsSearchResult r = minimax(mm,depth-1);
         v = (short)-r.vvalue; 
         c = r.cvalue; 
         d = (short)(c+v);
         e = (short)(c-v);

         getGame().undoMove(mm,move);

         if (mode==SIMPLEMODE) {
            if (d>bestVal) { // select first best move
                bestVal = d; bestv=v; bestc=c;
                bestMove = move; 
                bestResult = r;
            }
         } else if (mode==COOPERATIVEMODE) {
			if (d>bestVal) { 
			    bestVal = d; bestv=v; bestc=c;
			    bestopVal = c;
				bestMove = move; 
				bestResult = r;
			} else if  (d == bestVal)  {
			  
			  if (c>bestopVal) {
		    	 bestopVal = c; bestv=v; bestc=c;
		         bestMove = move; 
			     bestResult = r;
			} }
         } else if (mode==COMPETITIVEMODE) {
		    if (d>bestVal) { 
   				bestVal = d; bestv=v; bestc=c;
				bestopVal = e;
				bestMove = move; 
				bestResult = r;
			} else if  (d == bestVal && e<bestopVal) { 
		 		bestopVal = d; bestv=v; bestc=c;
		  		bestMove = move; 
		  		bestResult = r;
			}
         } else if (mode==ALTRUISTICMODE) {
			if (d>bestVal) { 
				bestVal = d; bestv=v; bestc=c;
				bestopVal = e;
				bestMove = move; 
				bestResult = r;
			} else if  (d == bestVal)  {
			  
		  	if (e>bestopVal) {
				 bestopVal = d; bestv=v; bestc=c;
			 	bestMove = move; 
			 	bestResult = r;
			} }
	    } 
         
         
         
          move = enum.nextMove();
      }     

      return new NzsSearchResult(bestc,bestv,bestMove,bestResult);   
   }


   Evaluator evalv, evalc;
   int maxDepth;
   int mode = SIMPLEMODE;
}

