package UMCS.Games.Chess;
import  UMCS.Games.Lib.*; 
import  UMCS.iotools.*; 

public class Play {
   public static void main(String[] args) {


      int depth = 4;

       try {
        if (args[0].length()>0) {
           depth = Integer.parseInt(args[0]);
        } 
       } catch (Exception e) { }


       Chess k = new Chess();
       Evaluator ev = new MaterialEvaluator();
       AlphaBetaPlayer p = new AlphaBetaPlayer(k,ev,depth);
       Position m = k.getStartPosition(), nm;
       k.drawPosition(m);
       short move=Player.NOMOVE; 
       do {
         do {
            try {
              String s = Console.input("Your move> ");
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
           SearchResult r = p.nextMove(m);
           System.out.println("Move :"+k.moveString(r.move));
           nm = k.setMove(m,r.move); 
           if (nm==null) System.exit(0); else m=nm;
           k.drawPosition(m);
           System.out.println(k.getCount() +" nodes generated and "+p.getNumEvals()+","+p.getNumPartialEvals()+" nodes evaluated. v="+r.value);
         } 
      } while (!m.isEnded());
   }
}
