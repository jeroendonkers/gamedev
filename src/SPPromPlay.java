import  UMCS.Games.*; 
import  UMCS.Games.Lib.*; 
import  UMCS.stat.*; 
import  UMCS.iotools.*; 

public class SPPromPlay {
 

  public static void main(String[] args) {
      String iname = "play.txt";
      int st = 1;
      try {
          iname  = args[0];
          if (args.length>1)  st = Integer.parseInt(args[1]);
       } catch (Exception e) {
       }
       new SPPromPlay().run(iname,st);
  }



  public void run(String iname, int startexp) {
    inputname=iname;
    
    expcount = 1;
    Reader in = new Reader();
    if (!in.openFile(inputname)) return;

    if (in.command("output")) {
       in.nextToken();
       outputname = in.token;
    } else outputname =  "play.out";


    out = Log.createLog(outputname);
    out.startLog();

    boxpp = Log.createLog(outputname+"_boxpp.txt");
    boxpp.startLog();
    boxsp = Log.createLog(outputname+"_boxsp.txt");
    boxsp.startLog();

    while (in.command("experiment")) {

       if (in.command("name")) {
          in.nextToken();
          expname = in.token;
       } else expname = "exp"+expcount;	

       if (in.command("minwidth")) {
          in.nextInt(); minwidth=in.iToken;
       } else minwidth=2;

       if (in.command("maxwidth")) {
          in.nextInt(); maxwidth=in.iToken;
       } else maxwidth=2;
      
       if (in.command("maxdepth")) {
          in.nextInt(); maxdepth=in.iToken;
       } else maxdepth=5;

       if (in.command("bound")) {
          in.nextInt(); bound=in.iToken;
       } else nopp=1;

       if (in.command("nopponent")) {
          in.nextInt(); nopp=in.iToken;
       } else nopp=1;

       prob = new double[nopp];
       for (int i=0; i<nopp; i++) prob[i] = 1.0/nopp;

       if (in.command("probs")) {
          for (int i=0; i<nopp; i++) {
            in.nextDouble(); prob[i]=in.dToken;
          }
       } 

       if (in.command("samplesize")) {
          in.nextInt(); samplesize=in.iToken;
       } else samplesize=10;

       if (in.command("confidence")) {
            in.nextDouble(); confidence=in.dToken;
       } else confidence=0.95;

       if (in.command("bias")) {
            in.nextDouble(); bias=in.dToken;
       } else bias=0;



       if (expcount>=startexp) {
       	
         log = Log.createLog("psprom_"+expname+".dat");
         log.startLog();

         CheapBDRandomGame g = new CheapBDRandomGame(minwidth,maxwidth,nopp,bound);
         g.setBias(bias);
         Evaluator[] e = new Evaluator[nopp];
         for (int i=0; i<nopp; i++) e[i] = g.getEvaluator(i);     

         System.out.println("Experiment "+expname); 
         headExperiment(log);  headExperiment(out);
         log.println("# Format: seed ab val pureprom val psprom val");

         Player ab = new AlphaBetaPlayer(g,maxdepth);
         Player pp = new PromPurePlayer(g,e,prob,maxdepth);
         Player sp = new SPPromPlayer(g,e,prob,bound,maxdepth);

         Sample sam = new Sample();
         Histogram his = new Histogram(10000,0.05);
         Sample samsp = new Sample();
         Histogram hissp = new Histogram(10000,0.05);
         Sample samab = new Sample();         

         for (int i=0; i<samplesize; i++) {
           g.setSeed();	
           long seed = g.getSeed();
           	
           Position start = g.getStartPosition();
           System.out.print(expname+","+i+": ");
           String s=seed+" ";


           SearchResult rab = ab.nextMove(start);
           long abeval = ab.getNumEvals();
           System.out.print(abeval+" "+rab.value+", ");
           s=s+abeval+" "+rab.value+" ";

           samab.add(abeval);

           SearchResult rpp = pp.nextMove(start);
           long eval = pp.getNumEvals();
           System.out.print(eval+" "+rpp.value+", ");
           s=s+eval+" "+rpp.value+" ";
           
           sam.add(eval);
           his.add(1.0*eval/abeval);

           SearchResult rsp = sp.nextMove(start);
           eval = sp.getNumEvals();
           System.out.print(eval+" "+rsp.value+", ");
           s=s+eval+" "+rsp.value;

           samsp.add(eval);
           hissp.add(1.0*eval/abeval);
           
           log.println(s);
           System.out.println();
         }

         logSample(samab, ab+" evals ");
         out.println("");
         logSample(sam, pp+" evals ");
         out.println("");
         logHist(his,pp+" evals/ab ");
         boxpp.println(his.boxplot());
         out.println("");
         logSample(samsp, sp+" evals ");
         out.println("");
         logHist(hissp,sp+" evals/ab ");
         boxsp.println(hissp.boxplot());                  
         footExperiment(out);
         log.close();
        
       }
       expcount++;

    }
    out.close();
    in.closeFile();
  }

   private void logSample(Sample s, String pre) {
      ConfidenceInterval c = s.getMean(confidence);
      out.println(pre+c);
   }

   private void logHist(Histogram s, String pre) {
      out.println(pre+s.summary());
      out.println("Boxplot: med qtl1 qtl2 p0.05 p0.95");
      out.println(s.boxplot());
      out.println("");
   }


   private void headExperiment(Log l) {
       l.println("# =================================================");
       l.println("# Experiment "+expname+" "+inputname+"/"+expcount+": started at "+new java.util.Date());
       l.println("# RandomGame : #opp="+nopp+
                   ", Width = "+minwidth+"/"+maxwidth+", depth = "+maxdepth+
                   ", Bound = "+bound);
       String s = "# Probs  : ";
       for (int i=0; i<nopp; i++) s = s+prob[i]+" ";
       l.println(s);
       l.println("# bias: "+bias);              
       l.println("# Sample size: "+samplesize+" confidence level: "+confidence);
       
   }

   private void footExperiment(Log l) {
       l.println("# Experiment "+expname+" "+inputname+"/"+expcount+": ended at "+new java.util.Date());
       l.println("# =================================================");
       l.println();
   }


   private int expcount = 0;
   private int minwidth, maxwidth, nopp, maxdepth, samplesize, bound;
   private double[] prob;
   private Log out,log,boxpp,boxsp;
   private String expname,playname,outputname,inputname;
   double confidence, bias;
}
