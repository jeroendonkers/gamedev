import  UMCS.Games.*; 
import  UMCS.Games.Lib.*; 

public class PlayPerfect
{
  public static void main(String[] args) {

    OrderedGame g = new OrderedGame(8);
    Player p = (Player)(new AlphaBetaPlayer(g,6));
    Player q = (Player)(new MiniMaxPlayer(g,6));

    Position start =  g.getStartPosition();
    SearchResult r = p.nextMove(start);
    System.out.println(r.value+" "+r.move);
    r = q.nextMove(start);
    System.out.println(r.value+" "+r.move);

  }


}
