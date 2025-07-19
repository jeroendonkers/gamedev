package UMCS.Games.Lib;

public interface TTable {
   public int putEntry(long hashvalue, short score);
   public int putEntry(long hashvalue, short move, short score, byte flag, byte ply);
   public int getEntry(long hashvalue);
   public int getIndex(long hashvalue);
   public void clear();

   public short getMove(int index);
   public short getScore(int index);
   public byte getFlag(int index);
   public byte getPly(int index);

   // getEntry results
   public static int EMPTY = -1;
   public static int COLLISION = -2;

   // replacement schemes
   public static int RS_DEEP = 0;
   public static int RS_NEW = 1;
   public static int RS_OLD = 2;
   public static int RS_BIG = 3;
   public static int RS_BIGALL = 4;
   public static int RS_DEEPER = 5;

  // flag values
   public static byte EXACT = 0;
   public static byte LOWER = 1;
   public static byte UPPER = 2;

}
