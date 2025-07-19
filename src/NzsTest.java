import  UMCS.Games.*; 
import  UMCS.Games.Lib.*; 
import  UMCS.Games.Lib.Nzs.*;
import java.util.*;

public class NzsTest 
{
	public static void main(String[] args) {
        NzsTest t = new NzsTest();
        t.run(); 

	}


	RandomNzsGame g;
    Evaluator ev,ec,evv; 

    NzsEquiSet Collect(Node m, int depth) {
    	int sign = 1; if (m.getType()==Node.MIN) sign=-1;
		NzsEquiSet e = new NzsEquiSet();
		
		if (m.isTerminal() || depth==0) {
    		e.add(new NzsEquilibrium(ec.evaluate(m,depth),
    		      (short)(ev.evaluate(m,depth)))) ;
    		return e;
    	}
		MoveEnumerator enumr = g.generateMoves(m.getPosition());
		short move = enumr.getMove();
		if (move==Player.NOMOVE) 
		   return new NzsEquiSet();  // illegal situation! m should be terminal!
		   
		Vector v = new Vector();   
	  
 		while (move!=Player.NOMOVE) {
	  	    Node mm = g.doMove(m,move);
	  	    v.add(Collect(mm,depth-1));
			g.undoMove(mm,move);
			move = enumr.nextMove();
		}     

        int n=v.size();
        int [] mins = new int[n];
        for (int i=0; i<n; i++)
           mins[i]=((NzsEquiSet)v.elementAt(i)).minimum(sign);
        
		for (int i=0; i<n; i++) {
			NzsEquiSet es = (NzsEquiSet)v.elementAt(i);
			for (int j=0; j<es.size(); j++) {
				NzsEquilibrium q = es.getEquiAt(j);
				boolean add=true;
				short val = q.V(sign);
				for (int k=0; k<n; k++) if (val<mins[k]) {
					add=false;
					break;
				}
		      	if (add && !e.contains(q)) e.add(q);	   
			}
		}
        

		return e;   
    	
    	
    }

	NzsEquiSet CollectAll(Position p, int depth) {
		Node m = new Node(Node.MAX,p); 
		return Collect(m, depth);	
    	
	}

	void run() {
		
		int depth = 6;
	 	g = new RandomNzsGame(8,8);
	 	NzsPlayer p = new NzsPlayer(g,depth);
	 	AlphaBetaPlayer q = new AlphaBetaPlayer(g,depth);
	 	
	 	
		double facv=10;
		double facc=5;
		
	 	ev = g.getVEvaluator(facv);
		ec = g.getCEvaluator(facc);
		evv = g.getEvaluator(facc,facv);
		
		p.setEvaluatorV(ev);
		p.setEvaluatorC(ec);
		q.setEvaluator(evv);
		
		
		Position m= g.getStartPosition();
		SearchResult s = q.nextMove(m);
		System.out.println("MINIAX: "+s.move+": "+s.value);
		
		
		
		NzsEquiSet e = CollectAll(m,depth);
		System.out.print(e.size()+": ");
		int minc=0, minv=0, maxc=0, maxv=0;
		
		for (int i=0; i<e.size(); i++) {
		   NzsEquilibrium qq = e.getEquiAt(i);
		   System.out.print(qq);
		   if (i==0) {
		   	  minc=qq.cvalue;
			  maxc=qq.cvalue;
			  minv=qq.vvalue;
			  maxv=qq.vvalue;
		   } 
		   else if (qq.cvalue<minc) minc=qq.cvalue;
		   else if (qq.cvalue>maxc) maxc=qq.cvalue;
		   else if (qq.vvalue<minv) minv=qq.vvalue;	
		   else if (qq.vvalue>maxv) maxv=qq.vvalue;
		   
		}    
		System.out.println();
		System.out.println("C: "+minc+" - "+maxc+", V: "+minv+" - "+maxv);
		
		
		p.setMode(NzsPlayer.COOPERATIVEMODE);
		NzsSearchResult r = (NzsSearchResult)p.nextMove(m);
		System.out.println("COOPERATIVE: "+r.move+": "+r.value+" ("+r.cvalue+","+r.vvalue+")");
		
		p.setMode(NzsPlayer.SIMPLEMODE);
		r = (NzsSearchResult)p.nextMove(m);
		System.out.println("SIMPLE: "+r.move+": "+r.value+" ("+r.cvalue+","+r.vvalue+")");
		
		p.setMode(NzsPlayer.COMPETITIVEMODE);
		r = (NzsSearchResult)p.nextMove(m);
		System.out.println("COMPETITIVE: "+r.move+": "+r.value+" ("+r.cvalue+","+r.vvalue+")");
						
		p.setMode(NzsPlayer.ALTRUISTICMODE);
		r = (NzsSearchResult)p.nextMove(m);
		System.out.println("ALTRUISTIC: "+r.move+": "+r.value+" ("+r.cvalue+","+r.vvalue+")");
	
		
								
	}
	
	
	
	
} 