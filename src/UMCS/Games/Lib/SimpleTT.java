package UMCS.Games.Lib;


// Simple traditional transposition table, without overflow area

public class SimpleTT implements TTable {

   public SimpleTT() {
      this(16,RS_DEEP);
   }

   public SimpleTT(int b) {
      this(b,RS_DEEP);
   }

   public SimpleTT(int b, int rs) {
      bits = b; 
     // if (bits<16) bits = 16;
      n = 1L << bits;
      long  m = 1L << (63-bits);
      mask = n-1;
      keymask1 = (long)-1 ^ mask;
      keymask2 = m*2-1;
      table = new short[(int)(n*6)];
      rscheme = rs;
   }

   public void clear() {
      java.util.Arrays.fill(table,(short)0);
   }


   public int putEntry(long hashvalue, short score) {
       return putEntry(hashvalue, Player.NOMOVE, score, EXACT, (byte)0);
   }

   public int putEntry(long hashvalue, short move, short score, byte flag, byte ply) {
     int index = (int)(hashvalue & mask) * 6;
     switch (rscheme) {
     case RS_OLD: if (table[index] != 0 || table[index+1] != 0 || table[index+2] == 0) return COLLISION; break;
     case RS_DEEP: if (ply < (byte)(table[index+5])) return COLLISION; break;
	 case RS_DEEPER: if (ply <= (byte)(table[index+5])) return COLLISION; break;
     default: ; // all other schemes are ignored: just put entry in
     }
     long key = ((hashvalue & keymask1) >> bits) & keymask2;
     table[index++] = (short)(key);
     table[index++] = (short)(key >> 16);
     table[index++] = (short)(key >> 32);
     table[index++] = move;
     table[index++] = score;
     table[index] = (short)  (((long)flag & 0xFF) <<8 | ((long)ply & 0xFF));
     return index-5;
   }

   public int getIndex(long hashvalue) {return (int)(hashvalue & mask) * 6; }

   public int getEntry(long hashvalue) {
     int index = (int)(hashvalue & mask) * 6; 
     if  ( table[index] == 0 && table[index+1] == 0 && table[index+2] == 0) 
          return EMPTY; // empty entry

     long key = ((hashvalue & keymask1) >> bits) & keymask2;
     if  ( table[index] != (short)(key) ||
           table[index+1] != (short)(key >> 16) ||
           table[index+2] != (short)(key >> 32)) return COLLISION; // collision
     return index;        
   }

   public short getMove(int index) { return table[index+3]; }
   public short getScore(int index) { return table[index+4]; }
   public byte getFlag(int index) {  
           return (byte)( ((long)(table[index+5])& 0xff00)  >>8); }
   public byte getPly(int index) { return (byte)(table[index+5]); }

   int rscheme = RS_DEEP;
   int bits;
   long n=0; 
   long mask=0, keymask1=0, keymask2=0;
   short[] table;

}
