package UMCS.Games.Chess.EndGames;
import java.io.*;


/**
 * HuffmanTable <P>
 * This class gives access to the Huffman-encoded endgame tables as
 * published on three CD-ROMS by Ken Thomson of AT&T Bell Laboratories. 
 * This class is based on the ANSI-C code as present on these CD-ROMS.
 *
 * The class HuffmanTable encapsulates all file-io 
 * and all huffman-decoding. An instance of this class representes one
 * endgame table. To access multiple tables, just create one instance
 * per table.
 *
 * @author H.H.L.M. Donkers
 * @version SEPTEMBER 2001
 */


public class HuffmanTable
{
   /**
   /* Constructor: initiate the name of the file that contains the
   /* Huffman-encoded Chess endgame database. 
   **/
   public HuffmanTable(String name) {
      filename =name;
      opened =false; 
   }


   public String getFilename() { return filename;}


   /**
   /* Open the file and initialize the datastructures for
   /* the Huffman decoding. Returns true on success.
   **/
   public boolean open() {
     if (opened) return true;  
     opened = false;
     try {
        f = new RandomAccessFile(filename,"r");
     } catch (Exception e) {
       System.out.println(e); 
       return false;
     }
     opened = true;
     if (init()) return true;
     else {
        try { f.close(); } catch (Exception e) {}
        opened = false;
        return false;
     }
   }

   /**
   /* Check if the file has been opened.
   **/
   public boolean isOpen() { return opened; }


   /**
   /* Close the file.
   **/
   public void close() {
      if (opened) try { f.close(); } catch (Exception e) {}
      opened = false;
   }


    
    


   // HUFFMAN DECODING ROUTINES

   /**
   /* Read the file-header and initialize the datastructures.
   **/
   private boolean init() {

      try{ 

	grouplen = f.readInt();
        nspec = f.readInt();
        if (nspec>0) {
          special = new int[nspec];  
          for (int i =0; i<nspec; i++) special[i] = f.readInt();  
        }
        int k = f.readInt();
        if (k>0) {
           tree = new int[k];
           for (int i =0; i<k; i++) tree[i] = f.readInt();  
        }

        int j = f.readInt();
        if (j>0) {
           ztree = new int[j];
           for (int i =0; i<j; i++) ztree[i] = f.readInt();  
        }

        indexmod = f.readInt();
        indexlen = f.readInt();
        if (indexlen>0) {
          index = new int[indexlen];  
          int off = (indexlen + j + k + nspec + 6) * 4 * 8;
          for (int i =0; i<indexlen; i++) 
              index[i] = f.readInt() + off;  
        }

      } catch (IOException e) {
        return false;
      };
      return true;
  }


   /**
   /* Print a report of the file header to System.out.
   /* (for debugging)
   **/
  public void report() {
     if (!opened) {      System.out.println("not open!"); return; }
     System.out.println("Report Huffman-encoded EndGame table "+filename);
     System.out.println("grouplen = "+grouplen);
     System.out.println("nspec = "+nspec);
     System.out.println("tree size = "+tree.length);
     System.out.println("ztree size = "+ztree.length);
     System.out.println("index mod = "+indexmod);
     System.out.println("index size = "+indexlen);
  }



   // Auxillary data structures
   private static int BSIZE = 1024;
   private static int ENDFLAG = 0x800000;
   private byte[] buf = new byte[BSIZE]; 
   private int[] group = new int[32];

   /**
   /* Read a 1024-byte block from the file.
   /* Returns true on succes.
   **/
   private boolean readbuf() {
      if (!opened) return false; 
      try { 
         f.readFully(buf);       
      } catch (Exception e) {
         return false;
      }
      return true;
   } 


   /**
   /* Lookup an entry in the endgame table.
   /* This function does not translate chess-positions to offsets. 
   /*
   /* return values: 
   /*   NOTOPEN (-1) if the file is not open
   /*   OUTOFRANGE (-2) if the byteoffset is out of range
   /*   IOERROR (-3) if reading the file fails at any point
   /*   ILLEGALTYPE (-4) if the type of the file is not 2 or 4
   /*   otherwise the content of the entry is returned:
   /*   999	Black does not lose. It could be a draw, Black
   /*   	win or even an illegal position.
   /*   1-9	Not used.
   /*   10	Btm and he is mated.
   /*   11-49	Subgames -- btm and get mated in n-10.
   /*   50      mated with 5 pieces!
   /*   51-	All five pieces -- btm and convert to a
   /*   	subgame in n-50. For the pawn games, this
   /*   	will be a capture or promotion. The play
   /*   	after promotion is not contained in the
   /*   	pawn data-base, but all of the promotion
   /*   	data-bases are included.
   **/
   public int lookup(int byteoffset) {

      if (!opened) return NOTOPEN;
      
      int code, bitoffset, nbyte, i, j, k;
      int nspeco, mask, bp, bend;
      byte abyte;

      int indexentry = byteoffset/indexmod;
      if (indexentry<0 || indexentry>indexlen) return OUTOFRANGE;

      bitoffset = index[indexentry];
      nbyte = byteoffset % indexmod;
      nspeco = nspec+1;
      try { f.seek(bitoffset/8); } catch (Exception e) { return IOERROR; }
      if (!readbuf()) return IOERROR;

      bp = 0; bend = BSIZE;
      mask = 1 << (7-(bitoffset & 7));
      abyte = buf[bp++];

      j = 0;
      for (;;) {
         if ((abyte & mask)!=0) j++;

         k = tree[j];

         if ((k & ENDFLAG)!=0) {
           code = k & ~ENDFLAG;
           for( i=0; i<grouplen; i++) {
              group[i] = code % nspeco;
              code /= nspeco;
           }
           i = grouplen; 
           while (--i >= 0) {
              if (group[i] < nspec) {
                 if (nbyte-- <= 0) {
                     int t= special[group[i]];
                     if (t==0) t=999;
                     return t;
                 }  
                 continue;
              }
              j = 0;
              for (;;) {
                  // NEXTIN
                  if ((mask>>=1) == 0) {
                     mask = 0x80;
                     if (bp >= bend) { 
                          if (!readbuf()) return IOERROR;
                          bp = 0; 
                     } 
                     abyte = buf[bp++];
                   }  
                   if ((abyte & mask)!=0) j++;
                   k = ztree[j];
                   if ((k & ENDFLAG)!=0) {
                      if (nbyte-- <= 0) return k & 255;
                      break;
                   }
                   j = k;
              }
           }
           k = 0;
	}
	j = k;
        // NEXTIN
  	if ((mask>>=1) == 0) {
   	  mask = 0x80;
	  if(bp >= bend) { 
             if (!readbuf()) return IOERROR;
             bp = 0; 
          } 
	  abyte = buf[bp++];
        } 
    }
  };






 // private data structures

  private boolean opened = false;
  private String filename = "";
   private RandomAccessFile f;
  private int indexlen;
  private int grouplen;
  private int nspec;
  private int indexmod;
  private int[] special;
  private int[] tree;
  private int[] ztree;
  private int[] index;


// global constants
  public static int NOTOPEN = -1;
  public static int OUTOFRANGE = -2;
  public static int IOERROR = -3;
 


}
