package UMCS.Games.Chess;
import  java.util.Random; 
import  UMCS.Games.Lib.*; 

public class Board implements Position {

   public Board() {
      this(0);
   }

   public Board(int type) {
      clearBoard();
      switch (type) {
      case -1: break; // empty board;
      case 0: // standard opening position
         for (int col=0; col<8; col++) {
           putPiece(col,0,Const.LINE[col],Const.WHITE);
           putPiece(col,1,Const.PAWN,Const.WHITE);
           putPiece(col,6,Const.PAWN,Const.BLACK);
           putPiece(col,7,Const.LINE[col],Const.BLACK);
           }
         break;
      } 
   }

   public Board(String s) throws IllegalMoveException {
     parseBoard(s);
   }


   // ---- Moving and Unmoving----


   void doMove(short move) throws IllegalMoveException {
      set(move);
      generatedlist = null;

      check50rule(); if (status==Const.DRAW) return;
      check3rule(); if (status==Const.DRAW) return;

      boolean check = inCheck(play);
      generatedlist = generateMoveList();       

      if (generatedlist.size()==0 && check) {
         status = Const.CHECKMATE; winner = 1-play;
      } else if (generatedlist.size()==0 && !check) {
         status = Const.STALMATE; winner = Const.NOBODY;
      } else if (inDraw()) {
         status = Const.DRAW; winner = Const.NOBODY;
      } else if (check) status = Const.CHECK;
      else status = Const.OPEN;
   }


   boolean inDraw() {
      if (piececount[0]<=1 && piececount[1]<=1) return true;
      if (piececount[0]>1 && piececount[1]>1) return false;
      // one of the players has 1 piece
      int twop = (piececount[0]==1) ? 1 : 0;
      if (piececount[twop]>2) return false;
      if (perpiececount[Const.BISSHOP][twop]==1) return true;
      if (perpiececount[Const.KNIGHT][twop]==1) return true;
      return false;  
   }

   void undoMove() {
      unset();
      boolean check = inCheck(play);
      generatedlist = null;
      if (check) status = Const.CHECK; else status = Const.OPEN;
   }

   void set(short move) throws IllegalMoveException {
      // do some simple checks

      if (move==Const.NULLMOVE) {
         storeMove(move,0,0,false);
         play = 1-play;
         hashvalue ^= hashnumberplay;
         return;
      }

      int from = moveFrom(move);
      int to = moveTo(move);

      if (!donotcheck) if (from==to)
          throw new IllegalMoveException(moveStr(move)+": No move!");

      int newPiece = moveNewpiece(move);
      boolean capt = moveCapt(move);
      boolean enpassant = capt && (newPiece == Const.ENPASSANT);

      if (!donotcheck) {
        // check from field on color
        if (isEmpty(from)) 
               throw new IllegalMoveException(moveStr(move)+": No piece there!");
        if (getSide(from)!=play) 
               throw new IllegalMoveException(moveStr(move)+": Cannot move opponent's piece!");
      }

      int movePiece = getPiece(from);
      boolean fresh = getFresh(from);

      if (!donotcheck) {
        // check abusive capture
        if (!capt && (!isEmpty(to)))
            throw new IllegalMoveException(moveStr(move)+": Target field is occupied! "+board[to]);
        if (getSide(to)==play)
            throw new IllegalMoveException(moveStr(move)+": Target field is occupied by own side!");
      }

      int capPiece = 0;
      if (enpassant) capPiece = Const.PAWN;

      //  check non-capture
      if (capt && !enpassant) {

         if (!donotcheck) if (getSide(to)!=(1-play))
            throw new IllegalMoveException(moveStr(move)+": Target field is not occupied by opponent!");

         capPiece = getFreshPiece(to); // include fresh flags!

         if (!donotcheck) if ((capPiece & Const.PIECEMASK) == Const.KING)  
            throw new IllegalMoveException(moveStr(move)+": Cannot capture the KING!");
      }

      // do actual move  -- no checks anymore



      clearField(from);
      putPiece(to, movePiece, play);

      switch (newPiece) {
         case 0: break;
         case Const.ENPASSANT:
              // pawn moves diagonal to empty field
              clearField(row(from)+colNumber(to));  //remove a pawn;
              break;
         case Const.CASTLE:
              clearField(row(from)+7);
              putPiece(row(from)+5, Const.ROOK, play);
              break;
         case Const.CAASTLE:
              clearField(row(from));
              putPiece(row(from)+3, Const.ROOK, play); 
              break;
         default: putPiece(to, newPiece, play);
      }

      if (movePiece==Const.PAWN) checkEnpassant(from,to,1-play);
      play = 1 - play;

      hashvalue ^= getEnpassantHash();
      hashvalue ^= hashnumberplay;

      storeMove(move,movePiece,capPiece,fresh);
      checkBoard("set "+moveStr(move));
  }


   void unset() {
      play = 1 - play;

      boolean fresh = storedFresh();
      int capture = storedCapture();
      short move = unstoreMove();
      int newPiece = moveNewpiece(move);
      int from = moveFrom(move);
      int to = moveTo(move);
      boolean capt = moveCapt(move);
      int movePiece = getPiece(to); 

      hashvalue ^= getEnpassantHash();
      hashvalue ^= hashnumberplay;

      clearField(to);
      if (fresh) 
         putPiece(from, movePiece | Const.FRESHMASK , play);
      else 
         putPiece(from, movePiece , play);
      if (capt) putPiece(to,capture,1-play);

      switch (newPiece) {
         case 0: break;
         case Const.ENPASSANT:
              // pawn moves diagonal to empty field
              clearField(to);
              putPiece(row(from)+colNumber(to), Const.PAWN, 1-play); 
                    //restore pawn;
              break;
         case Const.CASTLE:
              putPiece(row(from)+7, Const.FRESHROOK, play);
              clearField(row(from)+5);
              break;
         case Const.CAASTLE:
              putPiece(row(from), Const.FRESHROOK, play);
              clearField(row(from)+3); 
              break;
         default: putPiece(from, Const.PAWN, play); // demote
      }


      checkBoard("unset "+moveStr(move));
   }

   void checkBoard(String s) {
     if (donotcheck) return;
      for (int col=0; col<8; col++) 
        for (int row=0; row<8; row++) {
           int f = field(col,row);
           if (board[f] != Const.VOID) 
           switch (getPiece(f)) {
              case Const.PAWN: 
              case Const.ROOK: 
              case Const.BISSHOP: 
              case Const.KNIGHT:
              case Const.QUEEN:
              case Const.KING: break;
              default: 
         System.out.println("INCONSISTENCY! at field "+col+","+row+": "+board[f]);
         System.out.println("occured at: "+s);
         draw();
         System.exit(0);
           }
           
      }
   }


// ---- Move generation: ATTACKS and CHECKS ----


 /* is side in check? */
 boolean inCheck(int side) {
   return attack(king[side],1-side);
 }

 /* is field f attacked by side? */
 boolean attack(int f, int side) {

   for(int i=0; i<8; i++) {
      if (occupies(f + Const.KNIGHTJUMP[i],Const.KNIGHT,side)) return true;
      if (occupies(f + Const.KINGJUMP[i],Const.KING,side)) return true;
   }

   for (int d=0; d<4; d++) {
      if (linethreat(f,Const.ROOKDIR[d],Const.ROOK,side)) return true;
      if (linethreat(f,Const.ROOKDIR[d],Const.QUEEN,side)) return true;
      if (linethreat(f,Const.BISSHOPDIR[d],Const.BISSHOP,side)) return true;
      if (linethreat(f,Const.BISSHOPDIR[d],Const.QUEEN,side)) return true;
   }

   int sign = ((side==Const.WHITE)?-1:1);
   if (occupies(f + sign*11,Const.PAWN,side)) return true;
   if (occupies(f + sign*13,Const.PAWN,side)) return true;

    /*enpassant is ignored.*/

    return false;

 } /* end attack */


// ---- Move generation: MOVELIST GENERATION ----

 /*Add four pawn promotion moves.*/
 public void addPromotions(MoveList list, int from, int to, boolean capture) {
   for (int i=0; i<4; i++) 
     if (capture)
      list.addMove(newCapMove(from,to,Const.PROMOTION[i]));
     else
      list.addMove(newMove(from,to,Const.PROMOTION[i]));
 }

 /* add jumpmoves for KNIGHT and KING */
 public void addJumpMoves(MoveList list, int side, int from, int[] jump) {
    for (int i=0;i<8;i++) {
      int to = from+jump[i];
      if (inBoard(to) && getSide(to)!=side && getPiece(to)!=Const.KING) {
        if (getSide(to)==1-side)
          list.addMove(newCapMove(from,to));
        else
          list.addMove(newMove(from,to));
      } 
    }
 }

 /* add slidemoves for ROOK, QUEEN and BISSHOP */
 public void addSlideMoves(MoveList list,int side, int from, int dir) {
   int to=from;
   for (int i=0; i<8; i++) {   
      to += dir;
      if (!inBoard(to) || getSide(to)==side) return;
      if (getSide(to)==1-side) {
         list.addMove(newCapMove(from,to));
         return;
      } else list.addMove(newMove(from,to));
    }
 }


    /* add Pawn moves */

void addPawnMoves(MoveList list, int side, int from) {
   int forward,firstline,lastline,to;

   if (side==Const.WHITE) { forward=LINESKIP;  firstline=row(0,1); lastline=row(0,6); }
                    else  { forward=-LINESKIP; firstline=row(0,6); lastline=row(0,1); }

   int fromline = row(from); 

   if (fromline == lastline) { 
     to = from+forward; 
     if (isEmpty(to)) addPromotions(list,from,to,false);
     to = from+forward+1; 
     if (inBoard(to) && getSide(to)==1-side) addPromotions(list,from,to,true);
     to = from+forward-1; 
     if (inBoard(to) && getSide(to)==1-side) addPromotions(list,from,to,true);
     return;
   }

   to = from+forward; 
   if (isEmpty(to)) {
     list.addMove(newMove(from,to));
     to = from+2*forward; 
     if (fromline==firstline && isEmpty(to)) list.addMove(newMove(from,to));
   }
  
   to = from+forward+1; 
   if (inBoard(to) && getSide(to)==1-side)
        list.addMove(newCapMove(from,to));

   to = from+forward-1; 
   if (inBoard(to) && getSide(to)==1-side)
        list.addMove(newCapMove(from,to));
}

 public void addCastling(MoveList list, int side) {

    int homeline=((side==Const.WHITE)?row(0,0):row(0,7));

    if (getSide(homeline+4)!=side || getFreshPiece(homeline+4)!=Const.FRESHKING) 
        return;

    // CAASTLE
    if ( getFreshPiece(homeline)==Const.FRESHROOK &&
         isEmpty(homeline+1) && isEmpty(homeline+2) && isEmpty(homeline+3)) {
       boolean b1 = !attack(homeline+2,1-side);   
       boolean b2 = !attack(homeline+3,1-side);   
       boolean b3 = !attack(homeline+4,1-side);   
       if (b1 && b2 && b3) list.addMove(newMove(homeline+4,homeline+2,Const.CAASTLE));
    }

    // CASTLE
    if  (getFreshPiece(homeline+7)==Const.FRESHROOK && isEmpty(homeline+5) && isEmpty(homeline+6)) {
       boolean b1 = !attack(homeline+6,1-side);   
       boolean b2 = !attack(homeline+5,1-side);   
       boolean b3 = !attack(homeline+4,1-side);   
       if (b1 && b2 && b3) list.addMove(newMove(homeline+4,homeline+6,Const.CASTLE));
    }
 }


 public void checkEnpassant(int from, int to, int side) {
     enPassantRights = 0;
     int forward = ((side==Const.WHITE)?LINESKIP:-LINESKIP);
     if (from-to == 2*forward) {
         if (inBoard(to+1) && getSide(to+1) == side
             && getPiece(to+1)==Const.PAWN && isEmpty(to+forward))
                enPassantRights |= (byte)(1 << colNumber(to+1));

         if (inBoard(to-1) && getSide(to-1)==side
             && getPiece(to-1) == Const.PAWN && isEmpty(to+forward))
                enPassantRights |= (byte)(1 << colNumber(to-1));
     }
 }


 public void addEnpassant(MoveList list, int side) {
     short m = previousMove();
     if (m == Const.NOMOVE) return;
 
     int forward = ((side==Const.WHITE)?LINESKIP:-LINESKIP);
     int to=moveTo(m);
     int from=moveFrom(m);

     if (getPiece(to)==Const.PAWN && from-to == 2*forward) {

         if (inBoard(to+1) && getSide(to+1) == side
             && getPiece(to+1)==Const.PAWN && isEmpty(to+forward))
          list.addMove(newCapMove(to+1,to+forward,Const.ENPASSANT));

         if (inBoard(to-1) && getSide(to-1)==side
             && getPiece(to-1) == Const.PAWN && isEmpty(to+forward))
          list.addMove(newCapMove(to-1,to+forward,Const.ENPASSANT));
     }
 }


public MoveList generateRoughMoveList() {
   MoveList list=new MoveList();
   int side=play;

   for (int i=0;i<BOARDSIZE;i++) 
      if (inBoard(i) && getSide(i)==side) 
         switch(getPiece(i)) {
         case Const.PAWN: 
              addPawnMoves(list,side,i); break;
         case Const.KNIGHT:
              addJumpMoves(list,side,i,Const.KNIGHTJUMP); break;
         case Const.KING:
              addJumpMoves(list,side,i,Const.KINGJUMP); break;
         case Const.ROOK: for (int d=0;d<4;d++) 
              addSlideMoves(list,side,i,Const.ROOKDIR[d]); break;
         case Const.BISSHOP: for(int d=0;d<4;d++) 
              addSlideMoves(list,side,i,Const.BISSHOPDIR[d]); break;
         case Const.QUEEN:  for(int d=0;d<4;d++){
                addSlideMoves(list,side,i,Const.ROOKDIR[d]);
                addSlideMoves(list,side,i,Const.BISSHOPDIR[d]);
              }
              break;
         }

   addCastling(list,side);
   addEnpassant(list,side);
   return list;
}


public MoveList generateMoveList() {
  if (generatedlist != null) return generatedlist;

  generatedlist = generateRoughMoveList();
  if (generatedlist.size()==0) return generatedlist;
  int i=0;
  boolean check = false;
  while (i<generatedlist.size()) {
     short move = generatedlist.getMoveAt(i);
     try {
         set(move);
         check = inCheck(1-play);
         unset();
     } catch (Exception e) { check=true; }

     if (check) generatedlist.removeMoveAt(i); else i++;   
  }
  return generatedlist;
}



public MoveList generateMoveList(HHTable hht) {
  if (generatedlist == null) generatedlist = generateMoveList();
  if (!generatedlist.isProcessed()) generatedlist.process(this, hht);
  return generatedlist;
}

// ---- move history ----------
   
   private void storeMove(short move, int piece, int captured, boolean fresh) {
      if (hispointer>=MAXHIST) return;
      hispointer++;
      movehist[hispointer] = move;
      caphist[hispointer] = captured;
      freshhist[hispointer] = fresh;
      enhist[hispointer] = enPassantRights;
      boardhist[hispointer] = hashvalue;
      if (piece==Const.PAWN || moveCapt(move) || hispointer==0)
          cnt50hist[hispointer]=0; 
      else cnt50hist[hispointer]=(byte)(cnt50hist[hispointer-1]+1);
   }


   private int storedCapture() {
     return  caphist[hispointer];
   }

   private boolean storedFresh() {
     return  freshhist[hispointer];
   }

   private short unstoreMove() {
        if (hispointer<0) return Const.NOMOVE;
        short move = movehist[hispointer];
        enPassantRights = enhist[hispointer];
        hispointer--;
        return move;
   }

   private short previousMove() {
     if (hispointer<0) return Const.NOMOVE; 
     else return movehist[hispointer];
   }

   public int numPlyPlayed() { return hispointer+1; }  


   public void check3rule() {
      int cnt=0;
      for (int i=hispointer-2; i>=0; i-=2) {
         if (boardhist[i] == boardhist[hispointer] &&
             enhist[i] == enhist[hispointer]) cnt++;
         if (cnt==2) break; 
      } 
      if (cnt==2) status = Const.DRAW;
   }

   public void check50rule() {
      if (cnt50hist[hispointer]>=100) status = Const.DRAW;
   }

// ---- hashing -----------

   public long getHash(int fld, int piece, int side) {
       int p=0;
       switch (piece) {
       case Const.PAWN: p=0; break; 
       case Const.ROOK: p=1; break; 
       case Const.FRESHROOK: p=2; break; 
       case Const.BISSHOP: p=3; break; 
       case Const.KNIGHT: p=4; break; 
       case Const.QUEEN: p=5; break; 
       case Const.KING: p=6; break; 
       case Const.FRESHKING: p=7; break; 
       }
       return hashnumber[fld][p][side];
   }


   public long getEnpassantHash() {
       if (enPassantRights==0) return 0;
       byte m = 1;
       long h=0;
       for (int i=0; i<=8; i++) {
          if ((enPassantRights & m) != 0) h ^= enpassanthashnumber[i];
          m <<= 1;
       }
       return h;
   }

   public static void generateHashNumbers(Random rand) {
       for (int i=0; i<144; i++) for (int j=0; j<8; j++) {
          hashnumber[i][j][0]=rand.nextLong();
          hashnumber[i][j][1]=rand.nextLong();
       }
       for (int j=0; j<8; j++) enpassanthashnumber[j]=rand.nextLong();
       hashnumberplay=rand.nextLong(); 
   }

// ----- conversion routines for boardpositions, moves and strings

    // c,r --> 12x12
    public static int field(int col, int row) {   
         return ((row & 7)+2)*12 + (col & 7) + 2; } 

    // 8x8 --> 12x12
    public static int expandfield(int field) {   
         return (((field >> 3) & 7)+2)*12 + (field & 7) + 2; } 

    // 12x12 --> c 8
    public static int colNumber(int field) { 
        return ((field % 12)-2) & 7; 
    }

    // 12x12 --> r 8
    public static int rowNumber(int field) { 
        return ((field / 12)-2) & 7; 
    }

    // 12x12 --> row 12 (first field on row)
    public static int row(int field) { 
        return field - (field % 12) + 2; 
    }

    // 8x8 --> row 12 (first field on row)
    public static int row(int c, int r) { 
        return 26 + r*12; 
    }


    // 12x12 --> 8x8
    public static int compressfield(int field) {   
         return ((((field / 12)-2) & 7) << 3) + (((field % 12)-2) & 7) ; } 

    public static short newCapMove(int from, int to) {
         return  (short)(((compressfield(from) & 63) << 6) |  
                          (compressfield(to) & 63) | 
                           Const.CAPMASK); }

    public static short newCapMove(int from, int to, int newpiece) {
         return  (short)(((Const.NEWPIECEFLAG[newpiece] & 7) << 12) |  
                         ((compressfield(from) & 63) << 6) | 
                          (compressfield(to) & 63) |
                           Const.CAPMASK); }

    public static short newMove(int from, int to) {
         return  (short)(((compressfield(from) & 63) << 6) | 
                          (compressfield(to) & 63)); }

    public static short newMove(int from, int to, int newpiece) {
         return  (short)(((Const.NEWPIECEFLAG[newpiece] & 7) << 12) |  
                         ((compressfield(from) & 63) << 6) | 
                          (compressfield(to) & 63)); }

    public static int moveFrom(short move)   {  
          return expandfield((move >> 6) & 63); }

    public static int moveTo(short move) {   
          return expandfield(move & 63); }


    // from move to 8x8 board fields
    public static int moveFromShort(short move)   {  
          return (move >> 6) & 63; }

    public static int moveToShort(short move) {   
          return move & 63; }


    public static int moveFlags(short move) {
          return ((move >> 12) & 7);  }

    public static int moveNewpiece(short move) {
          return Const.TONEWPIECE[((move >> 12) & 7)];  }

    public static boolean moveCapt(short move) {
          return ((move & Const.CAPMASK) != 0); }


// ---- board representation ----

  // is a field inside the board?
  boolean inBoard(int field) {
     if (field<0||field>=BOARDSIZE) return false;
     return board[field]!=Const.OUT;
  }

  // getside returns BLACK,WHITE or VOID
  int getSide(int field) {
     if(!inBoard(field)) return Const.VOID;
     int piece = board[field]; 
     return (piece==Const.VOID?Const.VOID:piece&1);
   }

  // retrieve empty field
  boolean isEmpty(int field) {
      return (board[field]==Const.VOID);
  }

  // retrieve piece at field
  int getPiece(int field) {
      return board[field] & Const.PIECEMASK;
  }

 // retrieve piece at field including fresh flag
  int getFreshPiece(int field) {
      return board[field] & (Const.PIECEMASK | Const.FRESHMASK);
  }


  boolean occupies(int field, int piece, int side) {
    return (board[field] & (Const.PIECEMASK | 1)) == (piece | side);
  }

  boolean linethreat(int field, int dir, int piece, int side) {
    int x = field; 
    for (int i=0; i<7; i++) {
        x += dir; if (!inBoard(x)) return false;
        if ((board[x] & (Const.PIECEMASK | 1)) == (piece | side)) return true;
        if (board[x]!=Const.VOID) return false;
    } return false;
  }


  public void putPiece(int col, int row, int piece, int color) {
      putPiece(field(col,row), piece, color);
  }

  public void putPiece(int field, int piece, int color) {
     clearField(field);

     hashvalue ^= getHash(field, piece, color);
     piececount[color]++;
     perpiececount[piece & Const.PIECEMASK][color]++;

     board[field] = (piece & (Const.PIECEMASK | Const.FRESHMASK)) 
                     | (color & 1);

     if ((piece & Const.PIECEMASK) == Const.KING)
        king[color] = field; 
  }

  public void clearField(int field) {
     if (!isEmpty(field)) {
        int color = getSide(field);
        int piece = getFreshPiece(field);
        hashvalue ^= getHash(field, piece ,color);
        piececount[color]--;
        perpiececount[piece & Const.PIECEMASK][color]--;
        
     }
     board[field] = Const.VOID;
  }  



  // retrieve fresh piece at field
  boolean getFresh(int field) {
      return (board[field] & Const.FRESHMASK)!=0;
  }

   private void clearBoard() {
     for(int i=0;i<BOARDSIZE;i++) board[i]=Const.OUT;
     for (int col=0; col<8; col++) 
        for (int row=0; row<8; row++) 
            board[field(col,row)]=Const.VOID;
     king[0] = -1; king[1] = -1;
     piececount[0] = 0; piececount[1] = 0;
     for (int i=0; i<13; i++) {
          perpiececount[i][0] = 0; perpiececount[i][1] = 0;
     }
     hashvalue = 0;
  }



// --------- I/O and String conversion ----------------

   // official move representation
   static String moveStr(short move) {
       int from = moveFrom(move);
       int to = moveTo(move);
       int piece = moveNewpiece(move);
       boolean capt = moveCapt(move);
       return ""+Const.COLSYMB.charAt(colNumber(from))+""+
              Const.ROWSYMB.charAt(rowNumber(from))+
              (capt?"x":"-")+
              Const.COLSYMB.charAt(colNumber(to))+""+
              Const.ROWSYMB.charAt(rowNumber(to))+
              (piece>0?"("+Const.PIECESYMB[0].charAt(piece)+")":"");
    }


    // parse strings to moves (reverse to moveStr)
    //
    // format:  a2-a4,  e4xd5, e1-g1(C), E1-C1(L), d7 d8 Q, c5xd6(E)
    //
    static public short parseMove(String s) throws IllegalMoveException {
       if (s.length()==0) return Const.NULLMOVE;
       if (s.length()<5) throw new IllegalMoveException("'"+s+"': String too short");

       int c1 = (s.charAt(0) - 'a') & 7;
       int r1 = (s.charAt(1) - '1') & 7;
       int c2 = (s.charAt(3) - 'a') & 7;
       int r2 = (s.charAt(4) - '1') & 7;
       boolean capture = (s.charAt(2) ==  'x');
       int newPiece = 0;
       if (s.length()>=7) switch(s.charAt(6)) {
       case 'Q': case 'q': newPiece = Const.QUEEN; break;
       case 'R': case 'r': newPiece = Const.ROOK; break;
       case 'N': case 'n': newPiece = Const.KNIGHT; break;
       case 'B': case 'b': newPiece = Const.BISSHOP; break;
       case 'C': case 'c': newPiece = Const.CASTLE; break;
       case 'L': case 'l': newPiece = Const.CAASTLE; break;
       case 'E': case 'e': newPiece = Const.ENPASSANT; capture=true; break;
       }
       if (capture) return newCapMove(field(c1,r1),field(c2,r2),newPiece);
       else return newMove(field(c1,r1),field(c2,r2),newPiece);
    }


  public void draw () {
      for (int row=7; row>=0; row--) {
         System.out.print("   "+Const.ROWSYMB.charAt(row)+" ");
         for (int col=0; col<8; col++) {
             int f =  field(col,row);
             int piece = getPiece(f);
             int color = getSide(f);
             if (color!=Const.VOID) {
                System.out.print(""+Const.PIECESYMB[color].charAt(piece));
                boolean fresh = getFresh(f);
                System.out.print((fresh?"'":" "));
             } else
                System.out.print( (((col+row) & 1)==0? "." : " ")+" " );
          }
          System.out.println();         
      }
     System.out.print("     ");
     for (int col=0; col<8; col++) 
       System.out.print(""+Const.COLSYMB.charAt(col)+" ");
     System.out.println();         
     System.out.print("Move history: ");         
     for (int i=0; i<=hispointer; i++) {
        if (i % 2 ==0) System.out.print((i/2+1)+": ");
        System.out.print(moveStr(movehist[i])+" ");
     }
     System.out.println();         
     if (generatedlist==null) generatedlist = generateMoveList();
     System.out.println("Legal moves: "+generatedlist); 
     if (status==Const.CHECKMATE) System.out.println("CHECKMATE! (GAME OVER: "
          +(play==1?" WHITE WINS)":" BLACK WINS)"));
     else if (status==Const.STALMATE) System.out.println("STALMATE! (GAME OVER: DRAW)");
     else if (status==Const.DRAW) { System.out.println("GAME OVER: DRAW"); 
     }
     else {
         if (status==Const.CHECK) System.out.println("CHECK!");         
         System.out.println(""+(play==0?"WHITE":"BLACK")+" to move");         
     } 
 
     System.out.println();         
  }
 

 public String boardString () {
      StringBuffer b = new StringBuffer(128); 
      for (int row=7; row>=0; row--) 
         for (int col=0; col<8; col++) {
             int f =  field(col,row);
             int piece = getPiece(f);
             int color = getSide(f);
             if (color!=Const.VOID) {
                b.append(Const.PIECESYMB[color].charAt(piece));
                b.append(getFresh(f)?'f':' ');
             } else
                b.append("  ");
      }
      return b.toString();
  }
 
 

  public void parseBoard (String s) throws IllegalMoveException {
  //
  // format:  "Kc2,Ra2,nd8,kd8,Fd8"  (F is for FRESH)
  // Upper case = white, lower case = black
  //
      clearBoard();
      if (s.length()==0) return;
      int i=0, n = s.length(), color=0, piece=0;
      for (i=0; i<n-2; i+=4) { 
          switch (s.charAt(i)) {
          case 'K': piece = Const.KING; color = Const.WHITE; break;
          case 'k': piece = Const.KING; color = Const.BLACK; break;
          case 'Q': piece = Const.QUEEN; color = Const.WHITE; break;
          case 'q': piece = Const.QUEEN; color = Const.BLACK; break;
          case 'B': piece = Const.BISSHOP; color = Const.WHITE; break;
          case 'b': piece = Const.BISSHOP; color = Const.BLACK; break;
          case 'N': piece = Const.KNIGHT; color = Const.WHITE; break;
          case 'n': piece = Const.KNIGHT; color = Const.BLACK; break;
          case 'R': piece = Const.ROOK; color = Const.WHITE; break;
          case 'r': piece = Const.ROOK; color = Const.BLACK; break;
          case 'P': piece = Const.PAWN; color = Const.WHITE; break;
          case 'p': piece = Const.PAWN; color = Const.BLACK; break;
          case 'F': piece = Const.FRESHMASK; break;
          default: throw new IllegalMoveException("Illegal character at position "+i);
          }
          int col = (s.charAt(i+1)-'a') & 7;
          int row = (s.charAt(i+2)-'1') & 7;
          int f = field(col,row);

          if (piece==Const.FRESHMASK) {
              if (isEmpty(f)) throw new IllegalMoveException("Fresh mark for empty field!");
              piece = getPiece(f) | Const.FRESHMASK;
              color = getSide(f);
          }
          putPiece(f,piece,color);
      }
     
  }


// -------- end conversion procedures


// --------- simple access methods -------------

   public boolean isEnded() { return !(status==Const.OPEN || status==Const.CHECK); }

   public long getHashValue() { return hashvalue; }

   public int getPlayer() { return play; }

   public int getStatus() { return status; }

   public int getWinner() { return winner; }

   public int getKingField(int side) { return king[side]; }

   public int ourSide(Node m) {
     int us=Const.WHITE;
     // if node type is MAX, it is our Node.
      if ((m.getType()==Node.MIN && play==Const.WHITE) ||
          (m.getType()==Node.MAX && play==Const.BLACK)) us=Const.BLACK;  
      return us;
   }


public Position clonePosition() {
    Board b = new Board();
    for (int i=0; i<BOARDSIZE; i++) b.board[i]=board[i];
    for (int i=0; i<2; i++) {
       b.king[i] = king[i];
       b.piececount[i] = piececount[i];
       for (int j=0; j<13; j++)
          b.perpiececount[j][i] = perpiececount[j][i];
       b.enPassantRights = enPassantRights;
       b.play = play;
       b.winner = winner;
       b.status = status;
       b.generatedlist = generatedlist;
    }

    for (int i=0; i<MAXHIST; i++) {
       b.movehist[i] = movehist[i];
       b.caphist[i] = caphist[i];
       b.freshhist[i] = freshhist[i];
       b.enhist[i] = enhist[i];
       b.cnt50hist[i] = cnt50hist[i];
       b.boardhist[i] = boardhist[i];
    }
 
   b.hispointer = hispointer;

   return b;

}


// ----------- private board variables -------------------

   private static int BOARDSIZE=144;  
   private int[] board = new int[BOARDSIZE];
   private static int LINESKIP=12;


   // extra board information
   private int king[] = {-1, -1}; // king positions

   private int piececount[] = {0, 0}; // total number of pieces
   private int perpiececount[][] = new int[13][2]; // number of pieces

   byte enPassantRights = 0; 
   
   private int play = Const.WHITE;   
   private int winner = Const.NOBODY;
   private int status = Const.OPEN;

   // random numbers for hashtable
   private long hashvalue = 0;

   private static long[][][] hashnumber = new long[144][8][2];
   private static long[] enpassanthashnumber = new long[8];
   private static long hashnumberplay;

   private MoveList generatedlist = null;
 
   private static final int MAXHIST = 1024;
   private short[] movehist = new short[MAXHIST];
   private int[] caphist = new int[MAXHIST];
   private boolean[] freshhist = new boolean[MAXHIST];
   private byte[] enhist = new byte[MAXHIST];
   private byte[] cnt50hist = new byte[MAXHIST];
   private long[] boardhist = new long[MAXHIST];
   private int hispointer = -1;

 

   private static boolean donotcheck=true; 
}
