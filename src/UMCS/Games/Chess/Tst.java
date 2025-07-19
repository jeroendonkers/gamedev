package UMCS.Games.Chess;
import  UMCS.Games.Lib.*; 
import  UMCS.iotools.*; 

public class Tst {
   public static void main(String[] args) {


      int depth = 100;
      int maxtime = 60;

       try {
        if (args[0].length()>0) {
           maxtime = Integer.parseInt(args[0]);
        } 
       } catch (Exception e) { }


       Chess k = new Chess();
       Evaluator ev = new MaterialEvaluator();
       AbTTPlayer p = new AbTTPlayer(k,ev,depth);
       p.setTTable(new SimpleTT(18));
       // p.setLog("chess.txt");

       String s = Console.input("Start position> ");
       Position m = k.getStartPosition(), nm;
       if (s.length()>0) try {
          m = k.getStartPosition(s);
       } catch (Exception e) {
         System.out.println(e);
         return;
       }
       k.drawPosition(m);
       short move=Player.NOMOVE; 

       do {
         do {
            try {
              s = Console.input("Your move> ");
              if (s.length()==0) return;
              move = Board.parseMove(s); 
              System.out.println("Setting "+Board.moveStr(move));
              nm = k.setMove(m,move); 
            } catch (Exception e) {
              System.out.println(e);
              nm=null;
            }
         } while (nm==null);
         m = nm; k.drawPosition(m);

         if (!m.isEnded()) {
           k.clearCount();
           p.clearEvals();
//           p.startLog();
           p.startTimer(maxtime * 1000);
           SearchResult r = p.nextMove(m);
           long wait = p.stopTimer();
//           p.stopLog();
           System.out.println("Move :"+k.moveString(r.move));
           nm = k.setMove(m,r.move); 
           if (nm==null) System.exit(0); else m=nm;
           k.drawPosition(m);
           System.out.println(k.getCount() +" nodes generated and "+p.getNumEvals()+","+p.getNumPartialEvals()+" nodes evaluated. v="+r.value+" ("+(wait/1000)+" sec.)");
           System.out.println( (1000.0*k.getCount() / wait) +" nodes per second.");
         } 
      } while (!m.isEnded());
   }
}
