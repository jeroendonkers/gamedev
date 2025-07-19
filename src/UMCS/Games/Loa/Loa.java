package UMCS.Games.Loa;
import  UMCS.Games.Lib.*; 
import  UMCS.stat.*; 
import  java.util.Random; 
import java.util.zip.*;
import java.io.*;

public class Loa extends AbstractGame implements Game
{

   public Loa() {
     rand = new Random();
     setSeed();
   }

   public void setSeed(long s) {
      userand=true;
      seed = s;
      generateHashNumbers();
   }
   public void setSeed(double s) {
      userand=true;
      seed = Double.doubleToLongBits(s);
      generateHashNumbers();
   }
   public void setSeed() {
      userand=true;
      seed = Double.doubleToLongBits(Math.random());
      generateHashNumbers();
   }
   public long getSeed() { return seed; }

  
   public Position getStartPosition() 
   {
      return new Lpos();
   }

   public Position getSpecialPosition(int type) 
   {
      return new Lpos(type);
   }

   public Position getSpecialPosition(int[] board) 
   {
   	//eerst het bord, van links naar rechts, van boven naar beneden
    // daarna de speler aan zet.
    // 0 = leeg, -1 = wit, 1 = zwart	
    return new Lpos(board);
   }


   public Evaluator getDefaultEvaluator() { return new LEvaluator(0); }
   public Evaluator getEvaluator(int m) { return new LEvaluator(m); }
   public Evaluator getNetEvaluator(String s) { return new NetEvaluator(s); }



   public MoveEnumerator generateMoves(Position p)
   {
      MoveSet ms = new MoveSet();
      Lpos s = (Lpos)p;
      if  (s.ended) return ms;
      int n = s.stones[s.play];
      for (int i=0; i<n; i++) s.generateMoves(i,s.play,ms);
      return ms;
   }

   public Node doMove(Node m, short move) {
      Lpos s = (Lpos)(m.getPosition());
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
      Lpos s = (Lpos)(m.getPosition());
      try {
        s.unset(move);
      } catch (IllegalMoveException e) {
        System.out.println(e);
        System.out.println(s.toString());
        System.out.println(moveString(move));
        System.exit(0);
      } 
   }

   public String posString(int pos) {
      byte r = (byte)(pos>>3 & 7);
      byte c = (byte)(pos & 7);
      String s = (char)('a'+c)+""+(r+1);
      return s;
   }

   public String moveString(short move) {
      if (move==Player.NOMOVE) return "not a move";
      byte r0 = (byte)(move>>10 & 0x7);
      byte c0 = (byte)(move>>7 & 0x7);
      byte r1 = (byte)(move>>4 & 0x7);
      byte c1 = (byte)(move>>1 & 0x7);
      boolean capture = ((move & 0x1)!=0);
      String s = (char)('a'+c0)+""+(r0+1);
      if (capture) s+="x"; else s+="-";
      s += (char)('a'+c1)+""+(r1+1);
      return s;
   }

   // do a real Move
   public Position setMove(Position p, short move) {
      Lpos s = (Lpos)(p);
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


   public short canMove(Position p, int row, int col, int dir) {
     Lpos s = (Lpos)(p);
     return s.canMove(row,col,dir);
   }
 
   public void drawPosition (Position p) {
     System.out.println(p.toString());
   }

   public int getWinner (Position p) {
      return ((Lpos)p).winner;
   }

   public int getPlayer (Position p) {
      return ((Lpos)p).play;
   }

   class Lpos implements Position
   {
       Lpos()  { this(0); }

       Lpos(int type)  
       {
         movehistory = new long[MAXMOVEHIST];
         histcount=0;
       	
         for (int i=0; i<BSIZE; i++) for (int j=0; j<BSIZE; j++) 
            board[i][j]=NOBODY;
         for (int i=0; i<BSIZE*BSIZE; i++) {
              Ipos[BLACK][i]=-1; Ipos[WHITE][i]=-1;
         }
         play=BLACK;
         switch (type) {
         case -1:  // empty
           break;
         case 0:  // normal opening
           for (int i=0; i<BSIZE-2; i++) {
             setStone(BLACK,0,i+1);
             setStone(BLACK,BSIZE-1,i+1);
             setStone(WHITE,i+1,0);
             setStone(WHITE,i+1,BSIZE-1);
           }
           break;
         case 1:  // small opening
           // start Position with 2 stones per side
           setStone(BLACK,0,3); setStone(BLACK,0,4); 
           setStone(BLACK,7,3); setStone(BLACK,7,4); 
           setStone(WHITE,3,0); setStone(WHITE,4,0); 
           setStone(WHITE,3,7); setStone(WHITE,4,7); 
           break;
         case 2:  // tiny opening
           // start Position with 1 stone per side
           setStone(BLACK,0,3); setStone(BLACK,7,4);
           setStone(WHITE,4,0); setStone(WHITE,3,7); 
           break;
         case 3: // random opening with 12 stones per side
            for (int i=0; i<2*(BSIZE-2); i++) for (int color=0; color<2; color++) {
            	 int x,y;
            	 do {
                 x = (int)(Math.floor(rand.nextDouble()*BSIZE));
                 y = (int)(Math.floor(rand.nextDouble()*BSIZE));
            	 } while (board[x][y]!=NOBODY);
            	 setStone(color,x,y);
            }
            break;
         }
       }

       Lpos(int[] board)  {
       	   this(-1);
       	   int k=0;
           for (int i=0; i<BSIZE; i++) for (int j=0; j<BSIZE; j++) {
           	 switch (board[k]) {
           	 case 0: break;
           	 case 1: setStone(BLACK,i,j); break;
          	 case -1: setStone(WHITE,i,j); break;
           } k++;
           }
           if (board[k]==-1) play=WHITE;
       }

       // used for setting up the openings.
       private void setStone(int color, int row, int col) {
           int n = stones[color]; stones[color]++;
           byte pos=boardpos(row,col);
           hashvalue ^= hashnumber[color][pos]; 
           Spos[color][n]=pos; 
           Ipos[color][pos]=(byte)n;
           board[row][col]=(byte)color;
           horlines[row]++;
           verlines[col]++;
           diag1lines[diag1(pos)]++;
           diag2lines[diag2(pos)]++;
       }

       // CHECK FOR A CERTAIN STONE OF A COLOR
       // WHICH MOVES CAN BE MADE
       // The working board has to be filled.
       //
       void generateMoves(int stone, int color, MoveSet ms) {
          byte pos=Spos[color][stone];
          int col=stonecol(pos);
          int row=stonerow(pos);
          int cnt=horlines[row];
          checkmove(row,col,cnt,0 ,1, color, ms);
          checkmove(row,col,cnt,0,-1, color, ms);
          cnt=verlines[col];
          checkmove(row,col,cnt,1 ,0, color, ms);
          checkmove(row,col,cnt,-1,0, color, ms);
          cnt=diag1lines[diag1(pos)];
          checkmove(row,col,cnt,1 ,1, color, ms);
          checkmove(row,col,cnt,-1,-1,color, ms);
          cnt=diag2lines[diag2(pos)]; 
          checkmove(row,col,cnt,1 ,-1,color, ms);
          checkmove(row,col,cnt,-1,1, color, ms);
       }


       void checkmove(int r, int c, int n, int dr, int dc, int color, MoveSet ms) {
          short move = boardpos(r,c);
          r+=dr*n; c+=dc*n;
          if (r<0 || r>=BSIZE || c<0 || c>=BSIZE) return;
          if (board[r][c]==color) return;
          move = (short)((move<<7) + (boardpos(r,c)<<1));
          if (board[r][c]!=NOBODY) move++;
          int i=0; 
          do { r-=dr; c-=dc; i++; }
          while (i<n && board[r][c]!=1-color);
          if (i<n) return; 
          ms.addMove(move);
       }


       // check manually entered move (no need for optimization)
       short canMove(int r, int c, int dir) {
          if (r<0 || r>=BSIZE || c<0 || c>=BSIZE) return Player.NOMOVE;
          byte pos=boardpos(r,c);
          short move=pos;
          int stone=Ipos[play][pos];
          if (stone<0) {
             System.out.println("cannot find stone at "+r+","+c);
             return Player.NOMOVE;
          }
          int n=0, dr=0, dc=0;
          switch (dir) {
            case EAST: dc=1;
                     n=horlines[r]; break;
            case WEST: dc=-1; 
                     n=horlines[r]; break;
            case SOUTH: dr=-1;
                     n=verlines[c]; break;
            case NORTH: dr=1; 
                     n=verlines[c]; break;
            case SEAST: dr=-1; dc=1;
                     n=diag1lines[diag1(pos)]; break;
            case NWEST: dr=1; dc=-1;
                     n=diag1lines[diag1(pos)]; break;
            case NEAST: dr=1; dc=1;
                     n=diag2lines[diag2(pos)]; break;
            case SWEST: dr=-1; dc=-1;
                     n=diag2lines[diag2(pos)]; break;
            default: System.out.println("Illegal direction");
                      return Player.NOMOVE;
          }
          r+=dr*n; c+=dc*n; 
          if (r<0 || r>=BSIZE || c<0 || c>=BSIZE) {
              System.out.println("Move outside board "+r+","+c);
              return Player.NOMOVE;
          }
          move = (short)((move<<7) + (boardpos(r,c)<<1));
          if (board[r][c]==play) return -1;
          if (board[r][c]==1-play) move++;
          int i=0; 
          do { r-=dr; c-=dc; i++; }
          while (i<n && board[r][c]!=1-play);
          if (i<n) return Player.NOMOVE; 
          return move;
       }

       void set(short move) throws IllegalMoveException {
           if (move<0)
             throw new IllegalMoveException("Cannot do a negative move.");

           byte pos0 = (byte)(move>>7 & 0x3f);
           byte pos1 = (byte)(move>>1 & 0x3f);
           boolean capture = ((move & 0x1)!=0);
           
           if (!stoneTo(play,pos0,pos1))
              throw new IllegalMoveException("No stone at position "+posString(pos0));

           declines(pos0);

           if (capture) {
             if (!capStone(1-play,pos1))
               throw new IllegalMoveException("No stone to capture at position "+posString(pos1));
             if (checkConnected(Spos[1-play],stones[1-play])) {
                ended=true;
                winner=(byte)(1-play);
             }
          } else 
             inclines(pos1,play);

          if (checkConnected(Spos[play],stones[play])) {
             ended=true;
             winner=play;
          }

          
          play=(byte)(1-play); // switch side
          hashvalue ^= hashnumberplay;

          if (doublePosition(hashvalue)) {
             ended=true;
             winner=NOBODY;
          }
          
          movehistory[histcount] = hashvalue;
          histcount++;
          if (checkcons) checkConsistency("set "+moveString(move));          
       }


       void unset(short move) throws IllegalMoveException {
           if (move<0)
             throw new IllegalMoveException("Cannot undo a negative move.");

           play=(byte)(1-play); // switch side
           hashvalue ^= hashnumberplay; 

           byte pos0 = (byte)(move>>7 & 0x3f);
           byte pos1 = (byte)(move>>1 & 0x3f);
           boolean capture = ((move & 0x1)!=0);

           if (!stoneTo(play,pos1,pos0))
             throw new IllegalMoveException("Cannot find stone for "+play+" at "+posString(pos1));

           inclines(pos0,play); 

           if (capture) 
             uncapStone(1-play,pos1);
           else
             declines(pos1);

          ended=false;
          winner=NOBODY;
          histcount--;
          if (checkcons) checkConsistency("unset "+moveString(move));
       }

      // Adjust Spos and Ipos: move a stone from position apos to bpos. 
      private boolean stoneTo(int color, byte apos, byte bpos) {
         int stone = Ipos[color][apos];
         if (stone<0) return false;
         hashvalue ^= hashnumber[color][apos]; 
         Ipos[color][apos]=-1;
         Spos[color][stone]=bpos; 
         Ipos[color][bpos]=stone;
         hashvalue ^= hashnumber[color][bpos];   // niet twee xor's vlak na elkaar! (JIT error)
         return true;
      }

      // capture a stone;
      private boolean capStone(int color, byte pos) {
         int c=pos&7, r = (pos>>3)&7;
         board[r][c]=(byte)(1-color);
         hashvalue ^= hashnumber[color][pos]; 

         int stone = Ipos[color][pos]; 
         if (stone<0) return false;

         Ipos[color][pos]=-1;

         stones[color]--; 
         int last = stones[color];  // index of last stone in play
         byte lastpos = Spos[color][last];  // position of this stone

         Spos[color][last]=(byte)255;            
         if (pos!=lastpos) {
            Spos[color][stone]=lastpos;
            Ipos[color][lastpos]=stone; 
         } 
         return true;
      }

      // uncapture a stone;
      private void uncapStone(int color, byte pos) {
         int c=pos&7, r = (pos>>3) & 7;
         int last = stones[color]; 
         hashvalue ^= hashnumber[color][pos]; 

         stones[color]++;
         Spos[color][last]=pos;
         Ipos[color][pos]=last;
         board[r][c]=(byte)color;
      }

       // adjust other counters
       private void inclines(byte pos, int color) {
          int c=pos&7, r = (pos>>3)&7;
          board[r][c]=(byte)color;
          verlines[c]++;  horlines[r]++;
          diag1lines[diag1(pos)]++; diag2lines[diag2(pos)]++;
       }

       // adjust other counters
       private void declines(byte pos) {
          int c=pos&7, r = (pos>>3)&7;
          board[r][c]=NOBODY;
          verlines[c]--;  horlines[r]--;
          diag1lines[diag1(pos)]--; diag2lines[diag2(pos)]--;
       }

       // check whether the board contents and the
       // array contents are consitent which each other
       // use only for debugging
       public void checkConsistency(String pre) {
         // check horlines
         boolean error=false;
         for (int r=0; r<BSIZE; r++)  {
           int count=0;
           for (int c=0; c<BSIZE; c++)
              if (board[r][c]!=NOBODY) count++;
           if (count!=horlines[r]) {
             error=true;
             System.out.print("CONSISTENCY ERROR ");
             System.out.println("Horline "+r+" incorrect");
           }
         }
         // check verlines
         for (int c=0; c<BSIZE; c++)  {
           int count=0;
           for (int r=0; r<BSIZE; r++)
              if (board[r][c]!=NOBODY) count++;
           if (count!=verlines[c]) {
             error=true;
             System.out.print("CONSISTENCY ERROR ");
             System.out.println("Verline "+c+" incorrect");
           }
         }

         // check diaglines
         for (int i=0; i<BSIZE*2-1; i++) { diag1cnt[i]=0; diag2cnt[i]=0; }
         for (int c=0; c<BSIZE; c++)  
           for (int r=0; r<BSIZE; r++) 
              if (board[r][c]!=NOBODY) {
                byte pos = boardpos(r,c);
                diag1cnt[diag1(pos)]++;
                diag2cnt[diag2(pos)]++;
              }
         for (int i=0; i<BSIZE*2-1; i++) {
           if (diag1cnt[i]!=diag1lines[i]) {
             error=true;
             System.out.print("CONSISTENCY ERROR ");
             System.out.println("Diag1line "+i+" incorrect");
           }
           if (diag2cnt[i]!=diag2lines[i]) {
             error=true;
             System.out.print("CONSISTENCY ERROR ");
             System.out.println("Diag2line "+i+" incorrect");
           }

         }




         // check spos[] abd ipos
         int blacks=0, whites=0;
         for (int c=0; c<BSIZE; c++) for (int r=0; r<BSIZE; r++) {
            byte pos = boardpos(r,c);
            if (Ipos[WHITE][pos]!=-1 && board[r][c]!=WHITE) {
               error=true;
               System.out.print("CONSISTENCY ERROR ");
               System.out.println("Ipos WHITE "+r+","+c+" incorrect");
            }   
            if (Ipos[BLACK][pos]!=-1 && board[r][c]!=BLACK) {
               error=true;
               System.out.print("CONSISTENCY ERROR ");
               System.out.println("Ipos BLACK "+r+","+c+" incorrect");
            }   
            if (board[r][c]!=NOBODY && Ipos[BLACK][pos]==-1 && Ipos[WHITE][pos]==-1) {
               error=true;
               System.out.print("CONSISTENCY ERROR ");
               System.out.println("Stone on "+r+","+c+" not in Ipos");
            }   
            if (board[r][c]==BLACK) blacks++;
            if (board[r][c]==WHITE) whites++;
         }
         if (blacks!=stones[BLACK]) {
               error=true;
               System.out.print("CONSISTENCY ERROR ");
               System.out.println("Number of blacks not in stones[]");
         }   
         if (whites!=stones[WHITE]) {
               error=true;
               System.out.print("CONSISTENCY ERROR ");
               System.out.println("Number of whites not in stones[]");
         }   
         for (int i=0; i<whites; i++) {
            byte pos = Spos[WHITE][i];
            if (board[stonerow(pos)][stonecol(pos)]!=WHITE) {
               error=true;
               System.out.print("CONSISTENCY ERROR ");
               System.out.println("White stone in Spos[white]["+i+"] not correct");
            }   
         }
         for (int i=0; i<blacks; i++) {
            byte pos = Spos[BLACK][i];
            if (board[stonerow(pos)][stonecol(pos)]!=BLACK) {
               error=true;
               System.out.print("CONSISTENCY ERROR ");
               System.out.println("Black stone in Spos[black]["+i+"] not correct");
            }   
         }
         if (error) {
            fullReportOn();
            System.out.println("Check at "+pre);
            System.out.println(this.toString());
            System.exit(0);
         }
       }

       private boolean doublePosition(long hashvalue) {
       	  for (int i=histcount-1; i>=0; i--)
       	      if (movehistory[i]==hashvalue) return true;
       	  return false;   
       }

       public String toString() { 
          StringBuffer buf = new StringBuffer(BSIZE * (BSIZE*2+4));
          for (int i=BSIZE-1; i>=0; i--) {
             buf.append(""+(i+1)+" ");
             for (int j=0; j<BSIZE; j++) 
                 switch (board[i][j]) {
                 case WHITE: buf.append("W "); break;
                 case BLACK: buf.append("B "); break;
                 case NOBODY: buf.append(". "); break;
                 }
              buf.append("\n");
          }
          char c='a';
          buf.append (" ");
          for (int j=0; j<BSIZE; j++) buf.append(" "+c++);
          buf.append("\n");              

          String s = buf.toString();
          if (ended) s += (winner==WHITE ? "WHITE WINS " : "BLACK WINS ")+"\n";
          s += "              (hashvalue = "+hashvalue+")\n";
          if (fullreport) {

            s=s+"\nWHITE: ";
            for (int i=0; i<stones[WHITE]; i++) s+=Spos[WHITE][i]+" ";
            s=s+"\n";
            for (int i=0; i<BSIZE*BSIZE; i++) 
                           if (Ipos[WHITE][i]>=0) s+=Ipos[WHITE][i]+","; else s+=".";
            s=s+"\nBLACK: ";
            for (int i=0; i<stones[BLACK]; i++) s+=Spos[BLACK][i]+" ";
            s=s+"\n";
            for (int i=0; i<BSIZE*BSIZE; i++)
                           if (Ipos[BLACK][i]>=0) s+=Ipos[BLACK][i]+","; else s+=".";
            s=s+"\nhor: ";                           
            for (int i=0; i<BSIZE; i++) s+=horlines[i]+",";
            s=s+" ver: ";
            for (int i=0; i<BSIZE; i++) s+=verlines[i]+",";
            s=s+"\ndiag1: ";                           
            for (int i=0; i<BSIZE*2-1; i++) s+=diag1lines[i]+",";
            s=s+" diag2: ";
            for (int i=0; i<BSIZE*2-1; i++) s+=diag2lines[i]+",";
            
          }

          return s; 
       }
 
       public boolean isEnded() { return ended; }

       public long getHashValue() { return hashvalue; }

       public int ourSide(Node m) {
         int us=WHITE;
         // if node type is MAX, it is our Node.
         if ((m.getType()==Node.MIN && play==WHITE) ||
             (m.getType()==Node.MAX && play==BLACK)) us=BLACK;  
         return us;
       }

       byte boardpos(int row, int col) { return (byte)(row*BSIZE+col); }
       int stonerow(byte b) { return (int) (b / BSIZE); }  
       int stonecol(byte b) { return (int) (b % BSIZE); }
       int diag1(byte b) {   return BSIZE-1 + stonecol(b) - stonerow(b); }
       int diag2(byte b) {   return stonecol(b) + stonerow(b); }


       public Position clonePosition() {
         Lpos b = new Lpos();
         for (int i=0; i<BSIZE; i++) for (int j=0; j<BSIZE; j++)
            b.board[i][j]=board[i][j];
         for (int i=0; i<2; i++) for (int j=0; j<BSIZE*BSIZE/2; j++)
            b.Spos[i][j] = Spos[i][j];
         for (int i=0; i<2; i++) for (int j=0; j<BSIZE*BSIZE; j++)
            b.Ipos[i][j] = Ipos[i][j];
         b.stones[0] = stones[0]; b.stones[1] = stones[1];
         for (int i=0; i<BSIZE; i++) b.horlines[i] = horlines[i];
         for (int i=0; i<BSIZE; i++) b.verlines[i] = verlines[i];
         for (int i=0; i<BSIZE*2-1; i++) b.diag1lines[i] = diag1lines[i];
         for (int i=0; i<BSIZE*2-1; i++) b.diag2lines[i] = diag2lines[i];
         b.play = play;
         b.ended = ended;
         b.winner = winner;
         for (int i=0; i<histcount; i++)
           b.movehistory[i] = movehistory[i];
         b.histcount = histcount;
         return b;
       }

       // BOARD REPRESENTATION
       private byte[][] board = new byte[BSIZE][BSIZE];

       // positions of black and white stones    
       // fieldnumbering from top top bottom, left to right
       // position 255 means the stone is captured.
       byte[][] Spos = new byte[2][BSIZE*BSIZE/2];


       // Inverse positions of black and white stones    
       // Stone number per field
       int[][] Ipos = new int[2][BSIZE*BSIZE];

       // stones in play
       byte[] stones = {0,0};

       // stonecount per lines  (total BSIZE*6-2 lines)
       // first BSIZE horizontal, 
       // then BSIZE vertical, then 
       // BSIZE*2-1 diagonal lefttop-rightbottom, starting leftbottom, then
       // BSIZE*2-1 diagonal righttop-leftbottom, starting lefttop
       byte[] horlines = new byte[BSIZE];
       byte[] verlines = new byte[BSIZE];
       byte[] diag1lines = new byte[BSIZE*2-1];
       byte[] diag2lines = new byte[BSIZE*2-1];

       

       long hashvalue=0;

       byte play;   // who is to move?
       boolean ended = false;
       byte winner = NOBODY;
       private long[] movehistory;
       private int MAXMOVEHIST=2500;
       private int histcount;       

  }

  
  class NetEvaluator implements Evaluator {

   NetEvaluator(String s) {
      loaNet=new LoaNet(s,netInput);
   }

   public short evaluate(Node m, int depth) {
      Lpos s = (Lpos)(m.getPosition());
      int us=s.ourSide(m);
      if (s.ended) {
         if (s.winner==us)   return (short)(10000 - depth);   
         else if (s.winner==1-us)  return (short)(-10000 + depth);
         else return (short)0; 
      }

      java.util.Arrays.fill(netInput,0.0);
      for (int i=0; i<s.stones[WHITE]; i++) netInput[s.Spos[WHITE][i]]=-1;
      for (int i=0; i<s.stones[BLACK]; i++) netInput[s.Spos[BLACK][i]]=1;
      netInput[BSIZE*BSIZE] = (s.play==WHITE ? -1.0 : 1.0);
      short v=(short)(Math.round(loaNet.Response()*1000));
      if (us==WHITE) v=(short)-v;
      return v;
   } 

   double[] netInput = new double[BSIZE*BSIZE+1];
   LoaNet loaNet; 
  }

  class LEvaluator implements Evaluator {

   LEvaluator(int m) {
      mode=m;
   }

   public short evaluate(Node m, int depth) 
   {
      Lpos s = (Lpos)(m.getPosition());

      int us=s.ourSide(m); 
      if (s.ended) {
         if (s.winner==us)   return (short)(10000 - depth);   
         else if (s.winner==1-us)  return (short)(-10000 + depth);
         else return (short)0; 
      }
      int v=0;

      switch (mode) {
        case 0: // default evaluator
           int tot=0;
           for (int i=0; i<s.stones[us]; i++) tot+=weight[s.Spos[us][i]];
           v += tot; 
           tot=0;
           for (int i=0; i<s.stones[1-us]; i++) tot+=weight[s.Spos[1-us][i]];
           v -= tot; 
           break;
        case 1:  // 
      }

      return (short)v;
    }

    int mode=0;
  }


  public boolean isRealValue(short value) { 
     return (value<-9900 || value>9900);
  }

  
  static final int[] weight = {
        0,  0,  0,  0,  0,  0,  0,  0, 
        0,  1,  1,  1,  1,  1,  1,  0, 
        0,  1,  5,  5,  5,  5,  1,  0, 
        0,  1,  5, 10, 10,  5,  1,  0, 
        0,  1,  5, 10, 10,  5,  1,  0, 
        0,  1,  5,  5,  5,  5,  1,  0, 
        0,  1,  1,  1,  1,  1,  1,  0, 
        0,  0,  0,  0,  0,  0,  0,  0};


    // check whether the stones of one color are connected
    // use 8x8 bitfield inside one long

    private static final long lmask = 0xFEFEFEFEFEFEFEFEL;
    private static final long rmask = 0x7F7F7F7F7F7F7F7FL;
    private static final long umask = 0xFFFFFFFFFFFFFF00L;
    private static final long dmask = 0x00FFFFFFFFFFFFFFL;

    public static boolean checkConnected(byte[] pos, int n) {

       if (n<=1) return true;
       long mask=0, field = 1L << pos[0];

       for (int i=0; i<n; i++) 
             mask |= 1L << pos[i];

       boolean changed=true;
       do {
         long f = field | (field <<8 & umask) | (field >>8 & dmask);
         f |= (f<<1 & lmask) | (f>>1 & rmask);
         f &= mask; 
         if (f==field) changed=false; 
         field=f; 
       } while (changed);

     return (field==mask);
    }


    static public final byte BSIZE = 8;    // NOT LARGER THEN 8 !!!


   static public final byte WHITE = 0;
   static public final byte BLACK = 1; 
   static public final byte NOBODY = 2; 


   static public final byte EAST  = 0;
   static public final byte SEAST = 1;
   static public final byte SOUTH = 2;
   static public final byte SWEST = 3;
   static public final byte WEST  = 4;
   static public final byte NWEST = 5;
   static public final byte NORTH = 6;
   static public final byte NEAST = 7;

   public void fullReportOn() {fullreport=true;}
   public void checkOn() {checkcons=true;}


   private Random rand;  
   private long seed=0;
   private boolean userand = false;
   private boolean fullreport = false;
   private boolean checkcons = false;

   private static long nodecount=0;

   public long getCount() { return nodecount; }
   public void clearCount() { nodecount=0; }


   private void generateHashNumbers() {
       rand.setSeed(seed);
       if (hashnumber==null) throw new Error("Something wrong here!");
       for (int i=0; i<BSIZE*BSIZE; i++) {
          hashnumber[WHITE][i]=rand.nextLong();
          hashnumber[BLACK][i]=rand.nextLong();
        }
        hashnumberplay=rand.nextLong(); 
   }

   // random numbers for hashtable
   private static long[][] hashnumber = new long[2][BSIZE*BSIZE];
   private static long hashnumberplay;
 
   // only for consistency check
   private int[] diag1cnt = new int[BSIZE*2-1];
   private int[] diag2cnt = new int[BSIZE*2-1];

}
