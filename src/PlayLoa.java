import  UMCS.Games.*; 
import  UMCS.Games.Loa.*; 
import  UMCS.Games.Lib.*; 
import  UMCS.iotools.*; 

public class PlayLoa
{
  public static void main(String[] args) {

    int depth = 4;

    try {
        if (args[0].length()>0) {
           depth = Integer.parseInt(args[0]);
        } 
    } catch (Exception e) { }

    Loa k = new Loa();
    k.fullReportOn();
    k.checkOn();
    Evaluator ed = k.getDefaultEvaluator();
    Evaluator en = k.getNetEvaluator("LoaNet1");

//      MiniMaxPlayer p = new MiniMaxPlayer(k,e,depth);
//      NegaMaxPlayer p = new NegaMaxPlayer(k,e,depth);
//    AlphaBetaPlayer p = new AlphaBetaPlayer(k,e,depth);
//    NegAbPlayer p = new NegAbPlayer(k,e,depth);
//    OmPurePlayer p = new OmPurePlayer(k,ed,ed,depth);
    AbTTPlayer p = new AbTTPlayer(k,ed,depth);
//    NegTTPlayer p = new NegTTPlayer(k,e,depth);
//    OmTTPlayer p = new OmTTPlayer(k,ed,ed,depth);

    p.setTTable(new SimpleTT(19));
//    p.setOmTTable(new ScoreOnlyTT(18));
    k.setSeed(2);

//    p.setLog("loa.log");

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
//         System.out.println(p.TThit+" TThits "+p.TTuse+" TTuses");
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
