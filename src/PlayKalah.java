import  UMCS.Games.Kalah.*; 
import  UMCS.Games.Lib.*; 
import  UMCS.iotools.*; 

public class PlayKalah
{
  public static void main(String[] args) {

    int depth = 4;
    int seeds = 4;
    int holes = 6;

    try {
        if (args[0].length()>0) {
           depth = Integer.parseInt(args[0]);
        } 
        if (args[1].length()>0) {
           seeds = Integer.parseInt(args[1]);
        } 
        if (args[2].length()>0) {
           holes = Integer.parseInt(args[2]);
        } 
    } catch (Exception e) { }
    
    Kalah2 k = new Kalah2(holes,seeds);
    Player p = (Player)(new AlphaBetaPlayer(k,depth));

    Position m =  k.getStartPosition();
    k.drawPosition(m);

    do {
       do {
           String s = Console.input("Your move [1-6]> ")+" ";
           if (s.charAt(0)=='q') return;
           int choice = s.charAt(0)-'1'; 
           m = k.doMove(m,choice);
           k.drawPosition(m);
       } while (!m.isEnded() && k.isKalahMove(m));

       if (!m.isEnded()) do {
          SearchResult r = p.nextMove(m);
          m = k.doMove(m,r.move);
          k.drawPosition(m);
          System.out.println("(my value="+r.value+")");
          System.out.println("");
       } while (!m.isEnded() && k.isKalahMove(m));

    } while (!m.isEnded());
  }


}
