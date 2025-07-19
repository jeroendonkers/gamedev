package UMCS.Games.Lib;


public class MTDPlayer extends AbTTPlayer {

   public MTDPlayer(Game g, Evaluator e, int md) 
   { super(g,e,md); 
   }

   public String getName() { return "MTD(f) Player"; }


   public void setEvaluator(Evaluator e) {eval = e;}
   public Evaluator getEvaluator() {return eval;}


   public void setMaxDepth(int md) { 
      maxDepth=md; 
   }

   public void donotIterate() { iterate=false; }

   public SearchResult nextMove(Position pos) {
      if (amLogging()) log("MTD(f), depth="+maxDepth);
      if (hasTTable()) getTTable().clear(); else iterate=false;
      clearEvals();
      Node m = new Node(pos); m.setType(Node.MAX); 
      storedBestResult = null;
      try {
        int minDepth=2;
        if (!iterate) minDepth=maxDepth;
        SearchResult r=null;
        short guess=0;
        for (searchDepth=minDepth; searchDepth<=maxDepth; searchDepth++) {
           short upper = POSINF, lower=MININF, g = guess, beta;
           do {
           	  if (g == lower) beta = (short)(g+1); else beta = g;
              r = alphabeta(m, searchDepth,(short)(beta-1),beta,"");
              g = r.value;
              if (g < beta) upper=g; else lower=g; 
           } while (upper > lower);
           guess = g;
        }
        return r;
      } catch (OutOfTimeException e) {
         System.out.println("OUT OF TIME!");
         return storedBestResult;
      }

   }

}
