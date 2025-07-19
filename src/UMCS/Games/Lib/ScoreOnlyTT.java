package UMCS.Games.Lib;

// Reduced transposition table, without overflow area

public class ScoreOnlyTT implements TTable {

   public ScoreOnlyTT() {
      this(16);
   }

   public ScoreOnlyTT(int b) {
      bits = b; 
      if (bits<16) bits = 16;
      n = 1L << bits;
      long  m = 1L << (63-bits);
      mask = n-1;
      keymask = (long)-1 ^ mask;
      table = new long[(int)n];
   }

   public void clear() {
      java.util.Arrays.fill(table,0L);
   }

   public int putEntry(long hashvalue, short move, short score, byte flag, byte ply) {
        return putEntry(hashvalue,score);
   }

   public int putEntry(long hashvalue, short score) {
     int index = (int)(hashvalue & mask);
     table[index] = ((long)score & mask) | (hashvalue & keymask);
     return index;
   }

   public int getIndex(long hashvalue) {return (int)(hashvalue & mask); }

   public int getEntry(long hashvalue) {
     int index = (int)(hashvalue & mask); 
     if  (table[index] == 0) return EMPTY; // empty entry
     if  ((table[index] & keymask) != (hashvalue & keymask)) return COLLISION; // collision
     return index;        
   }

   public short getMove(int index) { return Player.NOMOVE; }
   public short getScore(int index) { return (short)(table[index] & mask); }
   public byte getFlag(int index) {  return EXACT; }
   public byte getPly(int index) { return (byte)0; }

   int bits;
   long n=0; 
   long mask=0, keymask=0;
   long[] table;

}
