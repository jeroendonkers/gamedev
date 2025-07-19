package UMCS.Games.Lib;

public class NzsAbfPlayer extends Player {

   class dummyEvaluator implements Evaluator {
      public short evaluate(Node m, int depth) {
         return 0;
      }
   }

   public NzsAbfPlayer(Game g, Evaluator ev, int md) 
   { super(g); 
     evalv = ev;
     evalc = new dummyEvaluator();
     maxDepth = md;
     maxc = (short)(POSINF / 2);
   }


   public NzsAbfPlayer(Game g, Evaluator ev, Evaluator ec, int md) 
   { super(g); 
     evalv = ev;
     evalc = ec;
     maxDepth = md;
   }

   public String getName() { return "Nzs AlphaBeta Player"; }

   public void setEvaluator(Evaluator e) {evalv = e;}

   public void setEvaluatorC(Evaluator e) {evalc = e;}

   public void setMaxC(short mc) {maxc = mc;}

   public void setMaxDepth(int md) { 
      maxDepth=md; 
   }

   public SearchResult nextMove(Position pos) {
      if (amLogging()) log("------ next move ---------");
      clearEvals();
      Node m = new Node(Node.MAX,pos); 
      return Ab(m, MININF,(short)0, POSINF,(short)0, maxDepth, "");
   }

   NzsSearchResult Ab(Node m, short alphav, short alphac,
                              short betav, short betac, int depth, String pre ) {

      if (m.isTerminal() || depth==0) {
         incEvals();
         short v = evalv.evaluate(m,maxDepth-depth);
         short c = evalc.evaluate(m,maxDepth-depth);
         // we have to flip sign at uneven depth!
         if (((maxDepth - depth) & 1) !=0) v = (short)-v;  

         if (amLogging()) log(pre+"terminal: v="+v+". c="+c);

         return new NzsSearchResult(c,v,NOMOVE,null);
      }

      MoveEnumerator enum = getGame().generateMoves(m.getPosition());
      short move = enum.getMove();
      if (move==NOMOVE) 
        return new NzsSearchResult();  // illegal situation! m should be terminal!

      if (amLogging()) log(pre+"ab: alphav="+alphav+", alphac="+alphac+", betav="+betav+", betac="+betac);

      NzsSearchResult bestResult=null;
      short bestv=MININF, bestc=0, bestMove=NOMOVE; 

      while (move!=NOMOVE) {
         short c,v;

         if (amLogging()) log(pre+"try "+getGame().moveString(move));
         Node mm = getGame().doMove(m,move);

         short av = bestv; 
         short ac = bestc;      
         if (-av+ac > -alphav+alphac) {
            av = alphav; 
            ac = alphac;      
         }

         NzsSearchResult r = Ab(mm, (short)-betav, betac, (short)-av, ac, depth-1, pre+"  ");

         v = (short)-r.vvalue; 
         c = r.cvalue; 

         if (amLogging()) log(pre+"ab returned: v="+v+", c="+c);

         if (amLogging()) log(pre+"undo "+getGame().moveString(move)+": value="+(v+c));
         getGame().undoMove(mm,move);

         if (v + c > bestv + bestc) {
            bestv = v; bestc = c;
            bestMove = move; 
            bestResult = r;
         }

         if (v >=  betav - betac + 2*maxc) { // pruning  (// let op: niet betav+betac!!

           if (amLogging()) log(pre+" pruning because "+v+">"+(betav - betac + 2*maxc));

           move = NOMOVE;
         } else {
           move = enum.nextMove();
         }
      }     

      if (amLogging()) log(pre + "OPT "+getGame().moveString(bestMove)+": value="+(bestv+bestc));
      return new NzsSearchResult(bestc,bestv,bestMove,bestResult);   
   }


   Evaluator evalv, evalc;
   int maxDepth;
   short maxc;
}

