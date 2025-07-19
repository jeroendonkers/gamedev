import  UMCS.Games.*; 
import  UMCS.Games.Loa.*; 
import  UMCS.Games.Lib.*; 
import  UMCS.iotools.*; 

public class ManualLoa
{
  public static void main(String[] args) {

    int type = 0;

    try {
        if (args[0].length()>0) {
           type = Integer.parseInt(args[0]);
        } 
    } catch (Exception e) { }

    Loa k = new Loa();
    Position m= k.getSpecialPosition(type);
    k.drawPosition(m);
    int move=-1;
    do {
       do {
          if (!readStone()) return;
          move = k.canMove(m,row,col,dir);
       } while (move<0);

       System.out.println("Move :"+k.moveString((short)move));             
       m = k.setMove(m,(short)move); k.drawPosition(m);
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
