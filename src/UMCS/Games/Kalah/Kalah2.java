package UMCS.Games.Kalah;
import  UMCS.Games.Lib.*; 
import  UMCS.stat.*; 
import  java.util.Random; 
import java.util.zip.*;
import java.io.*;

public class Kalah2 extends AbstractGame implements Game 
{

   public Kalah2() {
      this(6,3);
   }

  public Kalah2(int holes, int seeds) {
    startseeds = seeds;
    numholes = holes;
    kalah = new int[2];
    kalah[0] = numholes;
    kalah[1] = 2*numholes+1;
    numseeds=2 * numholes * startseeds;
    rand = new Random();
    setSeed();
  }


   public int getNumStartSeeds() { return startseeds; }
   public int getNumHoles()   { return numholes; }
   public int getNumSeeds()   { return numseeds; }

   public void setSeed(long s) {
      userand=true;
      seed = s;
   }
   public void setSeed(double s) {
      userand=true;
      seed = Double.doubleToLongBits(s);
   }
   public void setSeed() {
      userand=true;
     seed = Double.doubleToLongBits(Math.random());
   }
   public long getSeed() { return seed; }

  
   public Position getStartPosition() 
   {
      if (seed!=0) rand.setSeed(seed); 
      return new Kpos();
   }

   public Position getRandomPosition() 
   {
      if (seed!=0) rand.setSeed(seed); 
      Kpos s = new Kpos(); s.randomize();
      return s;
   }


   public boolean useEndgameDatabase(String s, int mst) {
     try {
        zip = new ZipFile(s);
     } catch (Exception e) { return false; }
     useedb=true;
     maxedbstones = mst;
     return true;
   }

   private int readEdb(Kpos ks) {
      if (zip==null) return -1;
      int[] a = new int[numholes*2];
      int d=0;
      for (int i=0; i<numholes; i++) {
         d+= ks.hole[i]+ks.hole[numholes+1+i];
         a[i]=ks.hole[i];
         a[i+numholes]=ks.hole[numholes+1+i];
      } 
      if (d>maxedbstones) return -1;
      String s = "Kal"+numholes+"-"+d+".edb";
      try {
          BufferedInputStream f = new BufferedInputStream(zip.getInputStream(zip.getEntry(s)));
          long pos = Combinatoric.lexCompositionRank(a);
          long l = f.skip(pos);
          byte b = (byte)(f.read());  
          f.close();      
          System.out.print(b+" "); 
          return b; 
      } catch (Exception e) { }
      return -1;
   }

   public Evaluator getDefaultEvaluator()
   {
     return new KEvaluator(0);
   }

   public Evaluator getEvaluator(int m)
   {
     return new KEvaluator(m);
   }

   public MoveEnumerator generateMoves(Position p)
   {
      MoveSet ms = new MoveSet();
      Kpos s = (Kpos)p;
      if  (s.ended) return ms;
      boolean[] km = new boolean[numholes];

      int select=0;
      if (userand) select  = (int)Math.round(rand.nextFloat()*numholes);

      for (int ii=0; ii<numholes; ii++) {
          int i = (ii+select) % numholes;
          km[i] = s.iskalahMove(i);
          if (km[i]) ms.addMove((short)i);
      }

      if (userand) select = (int)Math.round(rand.nextFloat()*numholes);

      for (int ii=0; ii<numholes; ii++) {
          int i = (ii+select) % numholes;
          if (!km[i] && !s.isEmptyhole(i)) ms.addMove((short)i);
      }  

      branchcount++;
      branchtot+=ms.size();
      return ms; 
   }

   // do a real Move
   public Node doMove(Node m, int i) {
      Kpos s = (Kpos)(m.getPosition());
      Kpos t = new Kpos(s);
      t.set(i);
      Node nm = new Node(t);
      nm.setType(m.getType());
      if (!t.kalahMove) nm.flipType();
      return nm; 
   }

   // do a real Move from a position (for extern use)
   public Position doMove(Position p, int i) {
      Kpos s = (Kpos)p;
      Kpos t = new Kpos(s);
      t.set(i);
      return t; 
   }


   public void undoMove(Node m, int i) { }

   public String moveString(int move) { return ""+move; }

   public void drawPosition (Position p) {
     Kpos s = (Kpos)p;
     int n = s.hole.length;
     System.out.println("");
     if (s.picked>-1) System.out.print("player "+s.play+" picked "+(s.picked+1));
     if (s.picked==-1) System.out.print("player "+s.play+" passed...");
     if (s.kalahMove) System.out.println(" Kalah!");
     else System.out.println("");  
     System.out.println("");  
     System.out.print("   ");
     for (int i=0; i<numholes; i++) 
        System.out.print(s.hole[n-2-i]+" " );
     System.out.println("");
     System.out.print(s.hole[n-1]);
     for (int i=0; i<numholes; i++) System.out.print("   "); 
     System.out.print(s.hole[numholes]);
     System.out.println("");
     System.out.print("   ");
     for (int i=0; i<numholes; i++) 
        System.out.print(s.hole[i]+" " );
     System.out.println("");
     if (s.ended) {
        if (s.winner==FRONT) System.out.println("FRONT WINS!");
        if (s.winner==BACK) System.out.println("BACK WINS!");
        if (s.winner==NOBODY) System.out.println("NO ONE WINS...");
     }
     System.out.println();
  }


   public int getWinner (Position p) {
      return ((Kpos)p).winner;
   }

   public int getPlayer (Position p) {
      return ((Kpos)p).play;
   }

   public int getHole (Position p, int i) {
      return ((Kpos)p).hole[i];
   }

   public int getKalah (Position p, int i) {
      return ((Kpos)p).hole[kalah[i]];
   }

   public boolean isKalahMove(Position p) {  
     return ((Kpos)p).kalahMove; 
   }

   public String getCompactString(Position p) {
      return ((Kpos)p).compactString();
  }

   public Node readCompactString(String s) {
       Kpos ks = new Kpos();
       ks.readString(s);
       return new Node(ks);
   }

   public int getCompactStringsize() {
      return numholes+3;
  }


   class Kpos implements Position
   {
       Kpos()  
       {
         // start Position, FRONT starts
         play=BACK;
         for (int i=0; i<hole.length; i++)  hole[i]=startseeds; 
         hole[kalah[0]]=0; 
         hole[kalah[1]]=0;
         kalahMove=false; 
       }

       Kpos(Kpos s)  
       {
         // copy Position
         for (int i=0; i<hole.length; i++) hole[i]=s.hole[i];
         play = s.play;
         kalahMove = s.kalahMove;
       }

      public Position clonePosition() {
         return new Kpos(this);
      }

      public long getHashValue() {return 0; }

       void randomize() {
         for (int i=0; i<hole.length; i++) hole[i]=0;
         for (int i=0; i<numseeds; i++) {
             int select = (int)Math.floor(rand.nextFloat()*(hole.length));
             hole[select]++;
         };
       } 


       boolean iskalahMove(int p) {
          int pl = play;
          // select picked hole
          if (p<0 || p>=numholes) return false;
          if (!kalahMove) pl=1-pl;
          if (pl==1) p+=numholes+1;
          // saw the seeds
          int n = hole[p]; 
          if (n==0) return false;
          int pos = p;
          for (int i=0; i<n; i++) {
             pos++;
             if (pos==kalah[1-pl]) pos++;
             if (pos>=hole.length) pos=0;
          }
          // check for kalah Node
          return (pos==kalah[pl]);
       }


       boolean isEmptyhole(int p) {
          int pl = play;
          if (p<0 || p>=numholes) return true;
          if (!kalahMove) pl=1-pl;
          if (pl==1) p+=numholes+1;
          return (hole[p]==0);
       }

 
       void set(int p) {

          // switch player if no kalahMove
          if (!kalahMove) play=1-play;
          kalahMove=false;

          // select picked hole
          picked=p;
          if (picked==-1) {
             pass=true;
             return;
          }

          if (picked<0 || picked>=numholes) return;
          if (play==1) p+=numholes+1;

          // saw the seeds
          int n = hole[p]; 
          if (n==0) return;
          hole[p]=0;
          int pos = p;
          for (int i=0; i<n; i++) {
             pos = (pos+1) % hole.length;
             if (pos==kalah[1-play]) pos=(pos+1) % hole.length;
             hole[pos]++;
          }

          // check for kalah Node
          if (pos==kalah[play]) {
             kalahMove=true;
          } else {
             // collect opposite's seeds
             if (isOurPos(pos) && hole[pos]==1) {
                int opposite  = 2 * numholes - pos;
                hole[kalah[play]] += hole[opposite] + 1;
                hole[opposite] = 0;
                hole[pos] = 0;
                kalahMove=false;
             } 
          }

          // check for emptyness
          int tot=0;
          for (int i=0; i<numholes; i++) tot += hole[i];
          if (tot==0) {              
             ended=true;
             if (hole[kalah[0]] > (numseeds/2)) winner=0; 
             if (hole[kalah[0]] < (numseeds/2)) winner=1; 
             if (hole[kalah[0]] == (numseeds/2)) winner=2; 
             return;
          }

          tot=0;
          for (int i=0; i<numholes; i++) tot += hole[numholes+1+i];
          if (tot==0) {
             ended=true;
             if (hole[kalah[1]] < (numseeds/2)) winner=0; 
             if (hole[kalah[1]] > (numseeds/2)) winner=1; 
             if (hole[kalah[1]] == (numseeds/2)) winner=2; 
             return;
          }


          // check for win
          if (hole[kalah[play]] > (numseeds/2)) {
             ended=true; winner=play;
          }
          if (hole[kalah[1-play]] > (numseeds/2)) {
             ended=true; winner=1-play;
          }


          // check for draw 1
          if (hole[kalah[1-play]]+hole[kalah[play]] == numseeds) ended=true;

          

       }

       public String toString() {
         String s="["+play+":"+picked+"] ";
         for (int i=0; i<hole.length; i++) s = s +hole[i]+" ";
         if (ended) s=s+" ENDED "+winner;
         return s;
       }


       public String compactString() {
         int n=numholes;
         StringBuffer s = new StringBuffer(getCompactStringsize());
         s.append((char)(hole[kalah[0]]));
         s.append((char)(hole[kalah[1]]));
         for (int i=0; i<n; i++) {
            long twoholes= hole[i];
            twoholes = twoholes*256 + hole[i+n+1];
            s.append((char)(twoholes));
         }
         int flags = 0;
         if (kalahMove) flags=1-play; else flags=play;
         if (ended) flags+=2;
         flags+=winner<<2;

         s.append((char)(flags));
         return s.toString();
       }

       public void readString(String s) {
         int n=numholes, j=0; 
         hole[kalah[0]] = s.charAt(j++);
         hole[kalah[1]] = s.charAt(j++);

         for (int i=0; i<n; i++) {
             hole[i]=s.charAt(j) >> 8;
             hole[i+n+1]=s.charAt(j) & 255;
             j++;
         }
         int flags = s.charAt(j++);
         play = flags & 1; 
         kalahMove = false;
         ended = ((flags & 2) == 2);
         winner = flags>>2;
       }
 
       public boolean isEnded() { return ended; }

       public int getHashKey() { return 0; }

       public int get2ndHashKey() { return 0; }


       public int ourSide(Node m) {
         int us=FRONT;
         // if node type is MAX, it is our Node.
         if ((m.getType()==Node.MIN && play==1) ||
             (m.getType()==Node.MAX && play==0)) us=BACK;  
         return us;
       }

       public boolean isOurPos(int pos) {
           if ((play==0) && (pos<=numholes)) return true;
           if ((play==1) && (pos>numholes)) return true;
           return false;
       }
    
       int[] hole = new int[numholes*2 + 2];
       int play;
       boolean ended = false;
       int winner = NOBODY;
       int picked = -1;
       boolean kalahMove = false;
       boolean pass = false;
  }


  class KEvaluator implements Evaluator {

   KEvaluator(int m) {
      mode=m;
   }

   public short evaluate(Node m, int depth) 
   {
      Kpos s = (Kpos)(m.getPosition());

      int us=s.ourSide(m);
      if (s.ended) {
         if (s.winner==us)   return (short)(1000 - depth);   
         else if (s.winner==1-us)  return (short)(-1000 + depth);
         return 0; 
      }
      int v=0;

      switch (mode) {
        case 0: // default evaluator

          v = 10 * (s.hole[kalah[us]] - s.hole[kalah[1-us]]);
//          if (s.kalahMove) { if (s.play==us) v +=10; else v-=10; }
//          if (s.pass) { if (s.play==us) v -=50; else v+=50; }
          break;

        case 1:  // selfish

          v = 10 * s.hole[kalah[us]];
//          if (s.kalahMove) { if (s.play==us) v +=10; else v-=10; }
//          if (s.pass) { if (s.play==us) v -=50; else v+=50; }
          break;

       case 2: // faulty evaluator

          v = - (10 * (s.hole[kalah[us]] - s.hole[kalah[1-us]]));
          break;

       case 100: // Use end game database;
          v = readEdb(s);
          if (v>=0) {
             v+=s.hole[kalah[us]];
             if (v>numseeds/2) { v =800; break; }
             if (v<numseeds/2) { v =800; break; }
             v=0; break;
          }  
          v = 10 * (s.hole[kalah[us]] - s.hole[kalah[1-us]]);
          break; 

      }

      return (short)v;
    }



    int mode=0;
  }

  public boolean isRealValue(short value) { 
     return (value<-900 || value>900);
  }

   public double getAvgBranchfac() {
        if (branchcount!=0) 
          return (branchtot*1.0)/branchcount;
        else return 0;
   }



   static public final int FRONT = 0;
   static public final int BACK = 1; 
   static public final int NOBODY = 2; 

   private int startseeds;
   private int numholes;
   private int[] kalah;
   private int numseeds;

   private Random rand;  
   private long seed=0;
   private boolean userand = false;
 
   private long branchtot=0;
   private int branchcount=0;

   private ZipFile zip; // endgame database;
   private boolean useedb = false;
   private int maxedbstones = 0;
}
