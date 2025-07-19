import  UMCS.Games.*; 
import  UMCS.Games.Lib.*; 

public class PlayRandom
{
  public static void main(String[] args) {

    int md = 8; int opp=1;
    CheapRandomGame g = new CheapRandomGame(3,3,2);

//    AlphaBetaPlayer ab = new AlphaBetaPlayer(g,g.getEvaluator(0),md);
//    AbTTPlayer abtt = new AbTTPlayer(g,g.getEvaluator(0),md);
//    OmPurePlayer p = new OmPurePlayer(g,g.getEvaluator(0),g.getEvaluator(opp),md);


//    OmTTPlayer ptt = new OmTTPlayer(g,g.getEvaluator(0),g.getEvaluator(opp),md);
//    abtt.donotIterate();
//    abtt.setTTable(new SimpleTT(18));

//    ptt.donotIterate();
//    ptt.setTTable(new SimpleTT(18));
//    ab.setLog("ab.log"); ab.startLog();
//    abtt.setLog("abtt.log"); abtt.startLog();

/*
    OmPureOrigPlayer pt = new OmPureOrigPlayer(g,g.getEvaluator(0),g.getEvaluator(opp),md);
    OmTTPlayer ptt = new OmTTPlayer(g,g.getEvaluator(0),g.getEvaluator(opp),md);
    ptt.setTTable(new SimpleTT(18));
    AbTTPlayer abtt = new AbTTPlayer(g,g.getEvaluator(0),md);
    abtt.setTTable(new SimpleTT(18));
    AlphaBetaPlayer ab = new AlphaBetaPlayer(g,g.getEvaluator(0),md);

    OmPureNabPlayer q = new OmPureNabPlayer(g,g.getEvaluator(0),g.getEvaluator(opp),md);
    OmPlainAbPlayer omab = new OmPlainAbPlayer(g,g.getEvaluator(0),g.getEvaluator(opp),md);
    OmPlainPlayer om = new OmPlainPlayer(g,g.getEvaluator(0),g.getEvaluator(opp),md);


    PromPlainPlayer plp = new PromPlainPlayer(g,ev,pr,md);
    PromPlainNabPlayer pnp = new PromPlainNabPlayer(g,ev,pr,md);

*/

    Evaluator[] ev = new Evaluator[2];
    ev[0] = g.getEvaluator(0);     ev[1] = g.getEvaluator(1);
    double[] pr = {0.5,0.5};

    PromPurePlayer p = new PromPurePlayer(g,ev,pr,md);
    PromPurePlayer q = new PromPurePlayer(g,ev,pr,md);
   // q.reusebeta=true;

    for (int i=0; i<100; i++) {

       g.setSeed(); long seed = g.getSeed();
       SearchResult r = p.nextMove(g.getStartPosition());
       g.setSeed(seed);  
       SearchResult rs = q.nextMove(g.getStartPosition());

/*
       SearchResult rtt = ptt.nextMove(g.getStartPosition());
       SearchResult w = ab.nextMove(g.getStartPosition()); 
       SearchResult r = p.nextMove(g.getStartPosition());
       g.setSeed(seed);  
       SearchResult rt = pt.nextMove(g.getStartPosition());
       g.setSeed(seed);  
       SearchResult rtt = ptt.nextMove(g.getStartPosition());
       g.setSeed(seed);  
       SearchResult wtt = abtt.nextMove(g.getStartPosition());
       g.setSeed(seed);  
       SearchResult w = ab.nextMove(g.getStartPosition());
       g.setSeed(seed);  
       SearchResult s = q.nextMove(g.getStartPosition());
       g.setSeed(seed);  
       SearchResult t = omab.nextMove(g.getStartPosition());
       g.setSeed(seed);  
       SearchResult u = om.nextMove(g.getStartPosition());
       g.setSeed(seed);  
       SearchResult v = prp.nextMove(g.getStartPosition());
       g.setSeed(seed);  
       SearchResult vl = plp.nextMove(g.getStartPosition());
       g.setSeed(seed);  
       SearchResult vn = pnp.nextMove(g.getStartPosition());

*/

       System.out.println(
                r.value+" "+r.move+" ("+p.getNumEvals()+") = " +
                rs.value+" "+rs.move+" ("+q.getNumEvals()+") = " +
/*

                         rt.value+" "+rt.move+" ("+pt.getNumEvals()+") = " +
                         rtt.value+" "+rtt.move+" ("+ptt.getNumEvals()+") = " +
                         wtt.value+" "+wtt.move+" ("+abtt.getNumEvals()+") = " +
                         w.value+" "+w.move+" ("+ab.getNumEvals()+") = " +
                         s.value+" "+s.move+" ("+q.getNumEvals()+") = " +
                         t.value+" "+t.move+" ("+omab.getNumEvals()+") = " +
                         u.value+" "+u.move+" ("+om.getNumEvals()+") = " +
                         v.value+" "+v.move+" ("+prp.getNumEvals()+") = " +
                         vl.value+" "+vl.move+" ("+plp.getNumEvals()+") = " +
                         vn.value+" "+vn.move+" ("+pnp.getNumEvals()+") " +
                         w.value+" "+w.move+" ("+ab.getNumEvals()+") = " +

*/
                          "");


    }

  }


}
