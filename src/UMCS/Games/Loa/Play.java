package  UMCS.Games.Loa; 
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

    Loa k = new Loa();
    Evaluator ev = k.getDefaultEvaluator();
    AlphaBetaPlayer p = new AlphaBetaPlayer(k,ev,depth);

    Position m= k.getSpecialPosition(0);
    k.drawPosition(m);
    short move=Player.NOMOVE;
    do {
       do {
          if (!readStone()) return;
          move = k.canMove(m,row,col,dir);
       } while (move==Player.NOMOVE);

       System.out.println("Move :"+k.moveString(move));             
       m = k.setMove(m,move); k.drawPosition(m);

       if (!m.isEnded()) {
         k.clearCount();
         p.clearEvals();
         SearchResult r = p.nextMove(m);
         System.out.println("Move :"+k.moveString(r.move));
         m = k.setMove(m,r.move);  k.drawPosition(m);
         System.out.println(k.getCount() +" nodes generated and "+p.getNumEvals()+","+p.getNumPartialEvals()+" nodes evaluated. v="+r.value);
       } 
    } while (!m.isEnded());

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
