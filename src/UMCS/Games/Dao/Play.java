package UMCS.Games.Dao; 
import  UMCS.Games.*; 
import  UMCS.Games.Lib.*; 
import  UMCS.iotools.*; 

public class Play
{
  public static void main(String[] args) {

	int depth = 4;

	try {
		if (args[0].length()>0) {
		   depth = Integer.parseInt(args[0]);
		} 
	} catch (Exception e) { }

	Dao k = new Dao();
	AbTTPlayer p = new AbTTPlayer(k,depth);
	p.setTTable(new SimpleTT(8));
 
	Position m= k.getStartPosition();
	k.drawPosition(m);
	short move=Player.NOMOVE;
	do {
	   move=readMove(m);
	   if (move==Player.NOMOVE) return;

	   System.out.println("Move :"+k.moveString(move));             
	   Dao.setMove(m,move); k.drawPosition(m);
 
	   if (!m.isEnded()) {
		 p.clearEvals();
		 SearchResult r = p.nextMove(m);
		 System.out.println("Move :"+k.moveString(r.move));
		 Dao.setMove(m,r.move);  k.drawPosition(m);
	   } 
	} while (!m.isEnded());
	System.out.println("Game Over");

  }

  static short readMove(Position pos) {
	
   do {
	String s = Console.input("Your move> ")+" ";
	if (s.length()==0) return Player.NOMOVE;
	if (s.charAt(0)=='?') {               
		System.out.println("q: quit, a1-c4: move");
		continue;
	}
	if (s.charAt(0)=='q') return Player.NOMOVE;
	if (s.length()<5) continue;
	int c1=(s.charAt(0)-'a');
	int c2=(s.charAt(3)-'a');
	int r1=(3 - (s.charAt(1)-'1'));
	int r2=(3 - (s.charAt(4)-'1'));
	if (r1<0 || r1>3 || r2<0 || r2>3 ||
		c1<0 || c1>3 || c2<0 || c2>3) {
	   System.out.println("Use move format cr-cr");
	   continue;
	 }
	int f = r1*4 + c1; 
	int t = r2*4 + c2;  
	return Dao.findMove(pos,Dao.WhiteMask[f] | Dao.WhiteMask[t]);
	
} while (true);

  }

  static int col=0;

}
