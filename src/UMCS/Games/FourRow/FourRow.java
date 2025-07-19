package UMCS.Games.FourRow;
import  UMCS.Games.Lib.*; 

public class FourRow extends AbstractGame implements Game 
{

   public FourRow() {
   }

   public FourRow(int w, int h) {
      boardwidth=w;
      boardheight=h;
   }
   
   public Position getStartPosition() 
   {
      return new FRpos();
   }


   public Evaluator getDefaultEvaluator()
   {
     return new FREvaluator();
   }

   public MoveEnumerator generateMoves(Position p)
   {
      MoveSet ms = new MoveSet();
      FRpos s = (FRpos)p;
      if  (s.ended) return ms;
      int select = (int)Math.round(Math.random()*8);
      for (int i=0; i<boardwidth; i++) {
          int ii = (i+select) % boardwidth;
          if (s.colcount[ii]<boardheight) ms.addMove((short)ii);
      }
      return ms;
   }

   public Node doMove(Node m, short move) {
      FRpos s = (FRpos)(m.getPosition());
      try {
        s.set(move);
      } catch (IllegalMoveException e) {
        System.out.println(e);
        System.out.println(s.toString());
        System.out.println(moveString(move));
        System.exit(0);
      } 
      Node nm = new Node(s); nodecount++;
      nm.setType(m.getType()); nm.flipType();
      return nm;
   }

   public void undoMove(Node m, short move) {
      FRpos s = (FRpos)(m.getPosition());
      try {
        s.unset(move);
      } catch (IllegalMoveException e) {
        System.out.println(e);
        System.out.println(s.toString());
        System.out.println(moveString(move));
        System.exit(0);
      } 
   }

   public String moveString(short move) {
      return "" + (move+1);
   }

   // do a real Move
   public Position setMove(Position p, short move) {
      FRpos s = (FRpos)(p);
      try {
        s.set(move);
      } catch (IllegalMoveException e) {
        System.out.println(e);
        System.out.println(s.toString());
        System.out.println(move);
        System.exit(0);
      } 
      return s;
   }


   public boolean canMove(Position p, int move) {
     FRpos s = (FRpos)(p);
     return (s.colcount[move]<boardheight);
   }
 
   public void drawPosition (Position p) {
     FRpos s = (FRpos)(p);
     System.out.println("");
     for (int i=boardheight-1; i>=0; i--) {
        System.out.print("-");
        for (int j=0; j<boardwidth; j++) {
            if (s.field[j][i]==NOBODY) System.out.print("   ");
            if (s.field[j][i]==RED) System.out.print(" r ");
            if (s.field[j][i]==YELLOW) System.out.print(" y ");
        }
        System.out.println();
     }
     System.out.print("-");
     for (int j=0; j<boardwidth; j++) System.out.print("---");
     System.out.println();
     System.out.print("-");
     for (int j=0; j<boardwidth; j++) System.out.print(" "+(j+1)+" ");
     System.out.println();
     if (s.ended) {
        if (s.winner==YELLOW) System.out.println("YELLOW WINS!");
        if (s.winner==RED) System.out.println("RED WINS!");
        if (s.winner==NOBODY) System.out.println("NO ONE WINS...");
     }
     System.out.println();
   }


   class FRpos implements Position
   {
       FRpos()  
       {
         colcount = new int[boardwidth];
         field = new int[boardwidth][boardheight];
         for (int i=0; i<boardwidth; i++) {
            colcount[i]=0;
            for (int j=0; j<boardheight; j++) field[i][j]=NOBODY;
         }
         play=RED;
       }


      public Position clonePosition()  
       {
         FRpos p = new FRpos();
         for (int i=0; i<boardwidth; i++) {
            colcount[i]=p.colcount[i];
            for (int j=0; j<boardheight; j++) field[i][j]=p.field[i][j];
         }
         play=p.play;
         return p;
       }



       void set(short i) throws IllegalMoveException {

          if (i<0 || i>=boardwidth) 
             throw new IllegalMoveException("Not a column ("+i+")." );

          if (colcount[i]>=boardheight) 
             throw new IllegalMoveException("Column is full" );

          field[i][colcount[i]]=play;
          colcount[i]++;
          numset++;

          checkFour(i);

          if (numset == boardwidth*boardheight) ended=true;
          play=1-play;
       }

       void unset(short i) throws IllegalMoveException {
          play=1-play;
          if (i<0 || i>=boardwidth) 
             throw new IllegalMoveException("Not a column ("+i+")." );

          if (colcount[i]==0) 
             throw new IllegalMoveException("Column is empty" );

          colcount[i]--;
          field[i][colcount[i]]=NOBODY;
          numset--;
          ended=false; winner=NOBODY;
       }

       public void checkFour(int c) {
          ended=false; winner=NOBODY;
          int r = colcount[c]-1;
          if (checkit(r,c,0,1) || checkit(r,c,1,0) || checkit(r,c,1,1) || checkit(r,c,-1,1)) {
             ended=true; winner=play;
          }
      }     



       boolean checkit(int r, int c, int dr, int dc) {
         int cc; int rr; int cnt=0;
         cc=c; rr=r;
         while (cc>=0 && rr>=0 && rr<boardheight && field[cc][rr]==play) 
              {cc-=dc; rr-=dr; cnt++;}
         cc=c; rr=r;
         while (cc<boardwidth && rr>=0 && rr<boardheight && field[cc][rr]==play) 
              {cc+=dc; rr+=dr; cnt++;}
         return cnt==5;
       }

       public String toString() {
         String s="["+COL[play]+"] ";
         if (ended) s=s+" ENDED "+winner;
         return s;
       }
 
       public boolean isEnded() { return ended; }

       public long getHashValue() { return 0; }

       public int ourColor(Node m) {
         int us=RED;
         // if node type is MAX, it is our move.
         if ((m.getType()==Node.MIN && play==RED) ||
             (m.getType()==Node.MAX && play==YELLOW)) us=YELLOW;  
         return us;
       }
    
       int[][] field;
       int[] colcount;
  
       int play;
       boolean ended = false;
       int winner = NOBODY;
       int numset = 0;
   }

   class FREvaluator implements Evaluator {

      public short evaluate(Node m, int depth)  {
        FRpos s = (FRpos)(m.getPosition());
        int us = s.ourColor(m); 
        int them = 1-us;
        if (s.ended) {
           if (s.winner==us)   return (short)(1000 - depth);   
           else                return (short)(-1000 + depth);
        }
        return (short)0; 
      }
   }

   int boardwidth = 7;
   int boardheight = 6;
   long nodecount=0;

   static public final int YELLOW = 0;
   static public final int RED = 1; 
   static public final int NOBODY = 2; 
   static public final char[] COL = { 'y', 'r', ' ' };
}
