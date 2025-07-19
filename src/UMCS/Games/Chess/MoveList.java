package UMCS.Games.Chess;
import UMCS.Games.Lib.*;
import java.util.Vector;

public class MoveList implements MoveEnumerator {

    public MoveList() { 
        moveptr = 0; 
        m=new short[MAX]; 
        isprocessed=false;
    }

    public short getMove() {
       moveptr = 0;
       return nextMove();
    }

    public short nextMove() {
       if (moveptr == size) return Player.NOMOVE;
       short move = getMoveAt(moveptr);
       moveptr++;
       return move;
    }


    public void addMove(short move) {
       if (size==MAX) {
          System.out.println("Move List Overflow"); System.exit(0);          
       }
       m[size]=move;
       size++;
       isprocessed=false;
    }  

    public boolean hasMove(short move) {
       if (size==0) return false;
       for (int i=0; i<size; i++) if (move == m[i]) return true;
       return false;
    }  


    public short getMoveAt(int i) {
       return m[i];
    }

    public void removeMoveAt(int i) {
       if (i<0 || i>=size) return;
       m[i] = m[size-1]; size--;
       isprocessed=false;
    }


    public int size() { return size; }


   int value(int piece) {
    switch(piece)
    {
     case Const.PAWN: return 105;
     case Const.KNIGHT: return 300;
     case Const.ROOK: return 500;
     case Const.QUEEN: return 900;
     case Const.BISSHOP:return 320;
     case Const.KING: return 3200;
     case Const.VOID: return 0;
    }
    return 0;
  }

   void fixedEstimates(Board b)  {
     short move;
     for(int i=0; i<size; i++)  {
        move = m[i];
        int newpiece = Board.moveNewpiece(move);
        if (Board.moveCapt(move)) v[i]=1000; else v[i]=0;
        switch (newpiece) {
        case 0: v[i] += value(b.getPiece(Board.moveTo(move))); break;
        case Const.ENPASSANT: v[i]+=110; break;
        case Const.CASTLE: v[i]+=20; break;
        case Const.CAASTLE: v[i]+=16; break;
        default: v[i]+=10*value(newpiece);
        }
    }
  }


   void hhtEstimates(HHTable hht)  {
      for(int i=0; i<size; i++) {
          v[i] = hht.getMoveScore(m[i]);
          if (Board.moveNewpiece(m[i])!=0) v[i] |= XTRMASK; // captures in front
          if (Board.moveCapt(m[i])) v[i] |= CAPMASK; // captures in front
      }
  }


   // sort the move list
   public void process(Board b, HHTable hht) {
     if (size==0) return;

     if (hht == null) fixedEstimates(b); else hhtEstimates(hht);

     // bubble sort
     for (int i=size-1; i>=0; i--) for (int j=0; j<i; j++) 
          if (v[j]<v[j+1]) {
             long t = v[j+1]; v[j+1]=v[j]; v[j]=t;
             short st = m[j+1]; m[j+1]=m[j]; m[j]=st;
          } 
     isprocessed=true;
   }

   public String toString() {
      String s = "("; 
      for (int i=0; i<size; i++) {
         s += Board.moveStr(m[i]);
         if (i<size-1) s += ", ";
      }
      return s+")";  
   }


   public boolean isProcessed() { return isprocessed; }

   private int moveptr=0, size=0;
   private short[] m;
   private static int MAX = 100;
   private static long[] v = new long[MAX];
   private boolean isprocessed = false;
   private static long CAPMASK = 1L << 50;
   private static long XTRMASK = 1L << 52;
}
