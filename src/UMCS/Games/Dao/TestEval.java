package UMCS.Games.Dao; 
import  UMCS.Games.*; 
import  UMCS.Games.Lib.*; 
import  UMCS.iotools.*; 
import  UMCS.stat.*;

public class TestEval
{
  public static void main(String[] args) {

    Histogram hwin = new Histogram(600,1,-300);
	Histogram hloss = new Histogram(600,1,-300);
	Histogram hdraw = new Histogram(600,1,-300);			
	Dao k = new Dao();
	for (int i=0; i<Dao.ClassCount; i++) {
		int ev = k.eval[i];
		if (ev==10000 || ev==-10000) {} 
		else {
	      int l=Dao.solLabel[i];		
		  if (l==1) hwin.add(ev);
		  if (l==0) hdraw.add(ev);
		  if (l==-1) hloss.add(ev);		  		   	 
		}
	}
	System.out.println(hwin.summary());
	System.out.println(hdraw.summary());	
	System.out.println(hloss.summary());	
  }

}
