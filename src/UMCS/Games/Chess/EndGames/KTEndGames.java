package UMCS.Games.Chess.EndGames;
import java.lang.*;

/**
 * KTEndGames <P>
 * This class gives access to collection of endgame tables as
 * published on three CD-ROMS by Ken Thomson of AT&T Bell Laboratories. 
 * This class is based on the ANSI-C code as present on these CD-ROMS.
 *
 * @author H.H.L.M. Donkers
 * @version SEPTEMBER 2001
 */


public class KTEndGames {

  public KTEndGames(String base) {
     ntables = tableNames.length()/5;
     table = new EndGameTable[ntables];
     for (int i=0; i<ntables; i++) 
       table[i] = new EndGameTable(tableNames.substring(i*5,i*5+4),base);
  }
 


  public void closeAll() {
     for (int i=0; i<ntables; i++) table[i].close();
  } 




  public static int[] parseString(String s) {
      if (s.length()==0) return null;
      int m = (s.length()+1)/4;
      int[] b = new int[2*m];
      int j=0, n = s.length(), piece;
      for (int i=0; i<n-2; i+=4) { 
          switch (s.charAt(i)) {
          case 'K': piece = EndGameTable.WKING; break;
          case 'k': piece = EndGameTable.BKING; break;
          case 'Q': piece = EndGameTable.WQUEEN; break;
          case 'q': piece = EndGameTable.BQUEEN; break;
          case 'B': piece = EndGameTable.WBISSHOP; break;
          case 'b': piece = EndGameTable.BBISSHOP; break;
          case 'N': piece = EndGameTable.WKNIGHT; break;
          case 'n': piece = EndGameTable.BKNIGHT; break;
          case 'R': piece = EndGameTable.WROOK; break;
          case 'r': piece = EndGameTable.BROOK; break;
          case 'P': piece = EndGameTable.WPAWN; break;
          case 'p': piece = EndGameTable.BPAWN; break;
          default: return null;
          }
          b[j++] = piece;

          int col = (s.charAt(i+1)-'a') & 7;
          int row = (s.charAt(i+2)-'1') & 7;
          b[j++] = (7-row)*8 + col;
     }
     return b;  
  }


  static int[] tabtmp=new int[15];

  public static String tabCode(int[] position) {

     if (position==null) return "? null position";
   
     int n=position.length/2;
     if ((n<3) || (n>5)) return "? too many or too fiew pieces";

     String s="";

     for (int i=0; i<15; i++) tabtmp[i] = 0;
     for (int i=0; i<n; i++) tabtmp[position[i*2]]++;

     if ((tabtmp[EndGameTable.WKING]!=1) || 
            (tabtmp[EndGameTable.BKING]!=1)) return "? no kings?";

     long mask = ~(1L<<56);

     for (int i=0; i<15; i++) if (i!=EndGameTable.WKING &&  i!=EndGameTable.BKING)  {
        if (tabtmp[i]>0) mask &= tabContents[i];
        if (tabtmp[i]>1) mask &= tabContents[0];
     }

     if (mask==0) return "? not found";
     for (int i=0; i<55; i++) {
        mask>>=1;
        if ((mask & 1) == 1) {
            int j = 54-i;
            s = s+tableNames.substring(j*5,(j+1)*5);
        }
     }

     return s;
  }


  // bitstrings (56-bits) that represent the presence of a piece
  // in the endgame tables
  // Bit 0 is not used!
 
  public static long[] tabContents = {
    0xCF80508114000EL,  // doubles: are there two equal pieces?
    0x007F8000000000L,  // white pawn
    0x3F98000E0000F0L,  // white knight
    0xF0600070000F00L,  // white bisshop
    0x00038000FFFFFEL,  // white rook
    0x00047FFFE00000L,  // white queen
    0,0,0,
    0x02000800030000L,  // black pawn
    0x542831240D5440L,  // black knight
    0xA85262489AA888L,  // black bisshop
    0x00808E80203112L,  // black rook
    0x0105041340C224L,  // black queen
    0};


  public static String tableNames =  
   "BB_B BB_N BN_B BN_N NN_B "+
   "NN_N NN_P NN_Q NN_R PB_B "+
   "PB_N PN_B PN_N PQ_Q PR_B "+
   "PR_Q PR_R Q_BB Q_BN Q_NN "+
   "Q_PR Q_QR Q_RB Q_RN Q_RR "+
   "QB_B QB_N QB_Q QN_B QN_N "+
   "QN_Q QQ_Q QR_B QR_Q QR_R "+
   "R_BB R_BN R_NN R_PB R_PN "+
   "R_QB R_QN R_RB R_RN RB_B "+
   "RB_N RB_Q RB_R RN_B RN_N "+
   "RN_Q RN_R RR_B RR_Q RR_R ";


   private EndGameTable[] table;
   private int ntables;

}


