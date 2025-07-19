package UMCS.Games.Chess;
import  UMCS.Games.Lib.*; 

/**
 * Histrory-Heuristic Table for Chess
 **/

public class ChessHHTable implements HHTable {
    
   public void clear() {
     for (int i=0; i<64; i++)
        for (int j=0; j<64; j++) hh[i][j] = 0;
   }

   public void refresh() {
     for (int i=0; i<64; i++)
        for (int j=0; j<64; j++) hh[i][j] >>= 2;
   }

   public void addMove(short move, int depth) {
      hh[Board.moveFromShort(move)][Board.moveToShort(move)] += (1L << depth);
   }

   public long getMoveScore(short move) {
     return hh[Board.moveFromShort(move)][Board.moveToShort(move)];
   }


   public String toString() {
     String s="";
     for (int i=0; i<64; i++) {
        for (int j=0; j<64; j++) s += hh[i][j]+",";
        s+="\n";
     }
     return s; 
   }

   private long[][] hh = new long[64][64];

}
