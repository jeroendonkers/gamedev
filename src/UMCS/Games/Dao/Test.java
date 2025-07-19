package UMCS.Games.Dao; 
import  UMCS.Games.*; 
import  UMCS.Games.Lib.*; 
import  UMCS.iotools.*; 

public class Test
{
	
  
  static long r1, r2;
  	
  public static void main(String[] args) {
  	
	long[] res1 = new long[22];
	long[] res2 = new long[22];
	Dao k = new Dao();

	java.util.Random rnd = new java.util.Random();
		
	for (int run=1; run<=100; run++) {
		System.out.print(run+" ");
		for (int i=0; i<=21; i++) {
			  testa(k,i);
			  res1[i]=r1;
			  res2[i]=r2;
			  System.out.print(".");	
		}
		System.out.println();			
		for (int i=0; i<=21; i++) 
			System.out.print(res1[i]+" ");
		for (int i=0; i<=21; i++) 
					System.out.print(res2[i]+" ");
		System.out.println();
		k.computeHash(rnd.nextInt());								
	}
    	
    
			
  }

  public static void testa(Dao k, int size) {
  	
	int depth = 8;
	k.clearVisited();
		
	
	AbTTPlayer p = new AbTTPlayer(k,depth);
//	AbTTPlayerNoOrdering p = new AbTTPlayerNoOrdering(k,depth);
//	MiniMaxPlayer p = new MiniMaxPlayer(k,depth);
	//p.donotIterate();
	
	SimpleTT tt = null;
	if (size>0) {
		tt = new SimpleTT(size,TTable.RS_DEEPER);
	    p.setTTable(tt);
	}    
 
	Position m= k.getStartPosition();
	p.clearEvals();
	SearchResult r = p.nextMove(m);
	//System.out.println("Value :"+r.value);
	//System.out.println("TTent "+p.TTent);
	//System.out.println("TTuse "+p.TTuse);
	//System.out.println("TThit "+p.TThit);
	//long h=0;
	//if (size>0) h = tt.filled;
	//System.out.println("TTfill "+h);
	   

	int  v=0, vw = 0, vb= 0, ec=0;
	long cv=0, cvw = 0, cvb = 0, cec = 0;
	for (int i=0; i<Dao.ClassCount; i++) {
	  if (k.visitedWhite[i]>0 || k.visitedBlack[i]>0) v++; 
	  if (k.visitedWhite[i]>0) vw++;
	  if (k.visitedBlack[i]>0) vb++;
	  if (k.evaluated[i]>0) ec++;	
	  cvw += k.visitedWhite[i];
	  cvb += k.visitedBlack[i];
	  cv +=  k.visitedBlack[i] + k.visitedWhite[i];
	  cec += k.evaluated[i];
	}   
    
	//System.out.println("Nodes visited "+v+" avg "+ 1.0*cv/v);
	//System.out.println("White nodes visited "+vw+" avg "+ 1.0*cvw/vw);
	//System.out.println("Black nodes visited "+vb+" avg "+ 1.0*cvb/vb);
	//System.out.println("Nodes evaluated "+ec+" avg "+ 1.0*cec/ec+" leafs: "+p.getNumEvals());
	  	
	//System.out.println(r.value+" "+p.TTent+" "+p.TTuse+" "+
	//p.TThit+" "+ h + " "+ v+" "+ 1.0*cv/v +" "+vw+" "+ 1.0*cvw/vw+" "
	//+vb+" "+ 1.0*cvb/vb+" "+ec+" "+ 1.0*cec/ec+" "+p.getNumEvals());	  	
  	r1 = cv;
  	r2 = p.getNumEvals();
  }

}
