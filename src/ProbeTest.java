import  UMCS.Games.*; 
import  UMCS.Games.Lib.*; 

public class ProbeTest
{
  public static void main(String[] args) {

    int md = 8; int opp=1;
    RandomGame g = new RandomGame(3,3,2);

    OmPurePlayerProbeTst p = new OmPurePlayerProbeTst(g,g.getEvaluator(0),g.getEvaluator(opp),md);
    OmPurePlayer q = new OmPurePlayer(g,g.getEvaluator(0),g.getEvaluator(opp),md);
    OmPlainPlayer r = new OmPlainPlayer(g,g.getEvaluator(0),g.getEvaluator(opp),md);

    for (int i=0; i<100; i++) {

       g.setSeed(); long seed = g.getSeed();
       SearchResult rp = p.nextMove(g.getStartPosition());
       g.setSeed(seed);  
       SearchResult rq = q.nextMove(g.getStartPosition());
       g.setSeed(seed);  
       SearchResult rr = r.nextMove(g.getStartPosition());


       System.out.println(
                rp.value+" "+rp.move+" ("+p.getNumEvals()+") = " +
                rq.value+" "+rq.move+" ("+q.getNumEvals()+") = " +
                rr.value+" "+rr.move+" ("+r.getNumEvals()+") = " +
                          "");


    }

  }


}
