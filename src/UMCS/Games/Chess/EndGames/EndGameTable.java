package UMCS.Games.Chess.EndGames;
import java.lang.*;

/**
 * EndGameTable <P>
 * This class gives access to the endgame tables as
 * published on three CD-ROMS by Ken Thomson of AT&T Bell Laboratories. 
 * This class is based on the ANSI-C code as present on these CD-ROMS.
 *
 * The class EndgameTable uses the class HuffmanTable for
 * file-access and encapsulates the computation of
 * hash-codes in the HuffmanTable.  
 *
 * @author H.H.L.M. Donkers
 * @version SEPTEMBER 2001
 */


public class EndGameTable
{
  public EndGameTable(String code, String base) {
      findpieces(code);
      basedir=base;
      createFilename(code);
      huff = null;
      if (filename.length()>0) huff = new HuffmanTable(filename);
  }

  private void findpieces(String code) {

      piece1=0; piece2=0; piece3=0;
      pawn = false;

      if (code.length()!=4) return;

      boolean black=false;
      for (int i=0; i<4; i++) {
        int p=0;
        switch (code.charAt(i)) {
        case '_': black=true; break;
        case 'P': p=WPAWN; pawn = true; break;
        case 'N': p=WKNIGHT; break;
        case 'R': p=WROOK; break;
        case 'B': p=WBISSHOP; break;
        case 'Q': p=WQUEEN; break;
        case 'K': p=WKING; break;
        }
        if (p>0) {
          if (i==0) piece1=p;
          else if (i==3) piece3=p;
          else piece2=p;
          if (black) p+=BLACK;
        }
     } 
  }


  private void createFilename(String code) {
     filename = basedir+code+".G";
  }

  public String getFilename() { return filename; }


  public boolean open() {
     if (huff == null) return false;
     return huff.open();
  }

  public void close() {
     if (huff == null) return;
     huff.close();
  }

  public int lookup(int[] pv, int[] pi) {

     if (huff == null) return NOTOPEN;
     if (!huff.isOpen())  
          if (!huff.open()) return NOTOPEN;

     if (pv.length>5 || pi.length>5 || pv.length!=pi.length) return ILLEGALPOS;

     dk1=-1; dk2=-1; dp1=-1; dp2=-1; dp3=-1; 
     int n = 0;
     for (int i=0; i<pv.length; i++) {

        if (pv[i]==WKING) {
           if (dk1>=0) return ILLEGALPOS;
           dk1 = pi[i];
           n++;
           continue;
        }

        if (pv[i]==BKING) {
           if (dk2>=0) return ILLEGALPOS;
           dk2 = pi[i];
           n++;
           continue;
        }

        if ((pv[i]==piece1) && (dp1<0)) {
           dp1 = pi[i];
           n++;
           continue;
        }

        if ((pv[i]==piece2) && (dp2<0)) {
           dp2 = pi[i];
           n++;
           continue;
        }

        if ((pv[i]==piece3) && (dp3<0)) {
           dp3 = pi[i];
           n++;
           continue;
        }
     }
     if (n<pv.length) return ILLEGALPOS;  

     computehash();
      
     return huff.lookup(hash);
  }


  private void computehash() {



  } 





  public static int BLACK    = 8;

  public static int WPAWN    = 1;
  public static int WKNIGHT  = 2;
  public static int WBISSHOP = 3;
  public static int WROOK    = 4;
  public static int WQUEEN   = 5;
  public static int WKING    = 6;

  public static int BPAWN    = 9;
  public static int BKNIGHT  = 10;
  public static int BBISSHOP = 11;
  public static int BROOK    = 12;
  public static int BQUEEN   = 13;
  public static int BKING    = 14;

  public static int NOTOPEN = -1;
  public static int OUTOFRANGE = -2;
  public static int IOERROR = -3;
  public static int ILLEGALPOS = -4;

   private HuffmanTable huff;
  private int piece1;
  private int piece2;
  private int piece3;
  private boolean pawn;
  private String basedir, filename;
 
  private int dk1;
  private int dk2;
  private int dp1;
  private int dp2;
  private int dp3;
  private int hash;
}
