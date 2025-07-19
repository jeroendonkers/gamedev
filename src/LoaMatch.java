 
import  UMCS.Games.Loa.*; 
import  UMCS.Games.Lib.*; 
import  UMCS.iotools.*; 

public class LoaMatch
{
  public static void main(String[] args) {

    int depth = 3;

    try {
        if (args[0].length()>0) {
           depth = Integer.parseInt(args[0]);
        } 
    } catch (Exception e) { }

    Loa k = new Loa();

    Log l;
    try {
     l = new Log(args[1]);
    } catch (Exception e) { l = new Log(); }

    AbTTPlayer[] pl = new AbTTPlayer[5];

    pl[0] = new AbTTPlayer(k,k.getNetEvaluator("LoaNet1"),depth);
    pl[1] = new AbTTPlayer(k,k.getNetEvaluator("LoaNet2"),depth);
    pl[2] = new AbTTPlayer(k,k.getNetEvaluator("LoaNet3"),depth);
    pl[3] = new AbTTPlayer(k,k.getNetEvaluator("LoaNet4"),depth);
    pl[4] = new AbTTPlayer(k,k.getNetEvaluator("LoaNet5"),depth);

    TTable tp = new SimpleTT(18);
    TTable tq = new SimpleTT(18);


    for (int p1=0; p1<4; p1++) for (int p2=p1+1; p2<5; p2++) for (int turn=0; turn<2; turn++) {
    	AbTTPlayer p,q;
    	if (turn==0) {
    		  p = pl[p1]; q= pl[p2];
        	l.println("Player "+p1+" against Player "+p2);    		  
      } else {
      	   p = pl[p2]; q = pl[p1];
        	l.println("Player "+p2+" against Player "+p1);    		        	   
      }
    	p.setTTable(tp); q.setTTable(tq);
    	
    
    	l.println("===============================");
    	
      Position m= k.getSpecialPosition(0);
      k.drawPosition(m);
      int move=-1;
      do {

          k.clearCount();
          p.clearEvals();
          SearchResult r = p.nextMove(m);
          System.out.println("BLACK Moves: "+k.moveString(r.move));
          l.println("BLACK Moves: "+k.moveString(r.move)+" - "+k.getCount() +" nodes generated and "+p.getNumEvals()+" nodes evaluated. v="+r.value);
          System.out.println(k.getCount() +" nodes generated and "+p.getNumEvals()+" nodes evaluated. v="+r.value);
          m = k.setMove(m,r.move);  k.drawPosition(m);

        if (!m.isEnded()) {
          k.clearCount();
          q.clearEvals();
          r = q.nextMove(m);
          System.out.println("WHITE Moves: "+k.moveString(r.move));
          l.println("WHITE Moves: "+k.moveString(r.move)+" - "+k.getCount() +" nodes generated and "+q.getNumEvals()+" nodes evaluated. v="+r.value);
          System.out.println(k.getCount() +" nodes generated and "+q.getNumEvals()+" nodes evaluated. v="+r.value);
          m = k.setMove(m,r.move);  k.drawPosition(m);
       } 
    } while (!m.isEnded());
      l.println();
   }

  }

  static boolean readStone() {
    String s = Console.input("Your move ['a-h''1-8''A-H']> ")+" ";
    if (s.charAt(0)=='q') return false;
    col = s.charAt(0)-'a';
    row = s.charAt(1)-'1';
    dir = s.charAt(2)-'A';
    return true;
  }

  static int row=0, col=0, dir=0;

}
