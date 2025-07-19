import  UMCS.Games.*; 
import  UMCS.Games.Lib.*; 
import java.util.*;

public class RGtest 
{
	public static void main(String[] args) {
        RGtest t = new RGtest();
        t.run(); 

	}


    void printTree(Game g, Node n, int depth, String pre) {
    	System.out.println(pre+n.getPosition());
    	if (depth>0) for (short i=(short)0; i<(short)4; i++) 
    	    printTree(g,g.doMove(n,i),depth-1,pre+"  ");	
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
         
         Position gstart = g.getStartPosition();
         Position hstart = h.getStartPosition();         
         
         long seed = g.getSeed();    
         Node m =  new Node(gstart); m.setType(Node.MAX);  
         printTree(g,m,3,"");
         
         h.setSeed(seed);
         m =  new Node(hstart); m.setType(Node.MAX);  
         printTree(h,m,3,"");              	
    }

}  