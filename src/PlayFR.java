import  UMCS.Games.FourRow.*; 
import  UMCS.Games.Lib.*; 
import  UMCS.iotools.*; 

public class PlayFR
{
  public static void main(String[] args) {

    int depth = 4;

    try {
        if (args[0].length()>0) {
           depth = Integer.parseInt(args[0]);
        } 
    } catch (Exception e) { }

    FourRow k = new FourRow();
    AlphaBetaPlayer p = new AlphaBetaPlayer(k,depth);

    Position m= k.getStartPosition();
    k.drawPosition(m);
    short move=Player.NOMOVE;
    do {
       move=Player.NOMOVE;
       do {
          if (!readMove()) return;
          if (k.canMove(m,col)) move=(short)col;
       } while (move==Player.NOMOVE);

       System.out.println("Move :"+k.moveString(move));             
       m = k.setMove(m,move); k.drawPosition(m);


       if (!m.isEnded()) {
         p.clearEvals();
         SearchResult r = p.nextMove(m);
         System.out.println("Move :"+k.moveString(r.move));
         m = k.setMove(m,r.move);  k.drawPosition(m);
       } 
    } while (!m.isEnded());

  }

  static boolean readMove() {
    String s = Console.input("Your move ['1-8']> ")+" ";
    if (s.charAt(0)=='q') return false;
    col = s.charAt(0)-'1';
    return true;
  }

  static int col=0;

}
