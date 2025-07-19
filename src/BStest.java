import  UMCS.Games.*; 
import  UMCS.Games.Lib.*; 
import java.util.*;

public class BStest 
{
	public static void main(String[] args) {
        BStest t = new BStest();
        t.run(); 

	}


    void run() {
    	
    	 int bound = 10;
         System.out.println(" Start");    	
    	
	     BDRandomGame g = new BDRandomGame(4,4,2,bound);
	     CheapBDRandomGame h = new CheapBDRandomGame(4,4,2,bound);
         Evaluator[] e = new Evaluator[2];
         Evaluator[] f = new Evaluator[2];
         for (int i=0; i<2; i++) e[i] = g.getEvaluator(i);
         for (int i=0; i<2; i++) f[i] = h.getEvaluator(i);         
         double[] prob = new double[2];
         prob[0] = 0.3; 
         prob[1] = 0.7;
         
         SPPromPlayer p = new SPPromPlayer(g,e,prob,bound,8);         
         SPPromPlayer ph = new SPPromPlayer(h,f,prob,bound,8);         
         PromPurePlayer q = new PromPurePlayer(g,e,prob,8);
         PromPurePlayer qh = new PromPurePlayer(h,f,prob,8);
         
         Position start = g.getStartPosition();
         long seed = g.getSeed();    
         SearchResult rq = q.nextMove(start);
         System.out.println("pure g: "+rq.value+" "+q.getNumEvals());         
         
         h.setSeed(seed);
         start = h.getStartPosition();
         SearchResult rh = qh.nextMove(start);         
         
         System.out.println("pure h: "+rh.value+" "+qh.getNumEvals());         
         
         g.setSeed(seed);
         start = g.getStartPosition();
         SearchResult rp = p.nextMove(start);         

         System.out.println("sim g: "+rp.value+" "+p.getNumEvals());         

         h.setSeed(seed);
         start = h.getStartPosition();
         SearchResult rph = ph.nextMove(start);         
         
         System.out.println("sim h: "+rph.value+" "+ph.getNumEvals());         

              	
    }

}  