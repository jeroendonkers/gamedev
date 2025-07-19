package UMCS.Games.Chess;

// Constants

public class Const {

    static public final byte NOBODY = -1;   

    // color = bit 0
    static public final byte WHITE = 0;
    static public final byte BLACK = 1; 

    final public static int OUT = 32;
    final public static int VOID = -1;


    // pieces = bit 1 .. 4
    final public static int PIECEMASK = 14;

    final public static int PAWN = 2;
    final public static int ROOK = 4;
    final public static int KNIGHT = 6;
    final public static int BISSHOP = 8;
    final public static int QUEEN = 10;
    final public static int KING = 12;

    // newpieces ... never on the board
    final public static int ENPASSANT = 3;
    final public static int CASTLE = 5;
    final public static int CAASTLE=7;

    // names
    final public static String[] PIECENAME = 
        {"none","","Pawn","Empassant","Rook","Castle","Knight","LongCastle","Bisshop","","Queen","","King"};

    // symbols for board display
    final public static String[] PIECESYMB = 
        {" .PERCNLB.Q.K"," .percnlb.q.k"};

    final public static String COLSYMB = "abcdefgh";
    final public static String ROWSYMB = "12345678";

    // fresh flag
    final public static int FRESHMASK = 16;

    final public static int FRESHKING = FRESHMASK | KING;
    final public static int FRESHROOK = FRESHMASK | ROOK;

    // starting line of pieces

    final public static int[] LINE={FRESHROOK,KNIGHT,BISSHOP,QUEEN,
                FRESHKING,BISSHOP,KNIGHT,FRESHROOK};

   // promotion pieces

    final public static  int[] PROMOTION = {QUEEN,KNIGHT,ROOK,BISSHOP};

    // capture mask
    final public static short CAPMASK = (short)(1 << 15);

    // newpiece to flag translations:
    final public static int[] TONEWPIECE = {0,3,4,5,6,7,8,10};
    final public static int[] NEWPIECEFLAG = {0,0,0,1,2,3,4,5,6,0,7};

    final public static short NULLMOVE = 0;
    final public static int NOMOVE = -1;

    final public static int CHECKMATE = 0;
    final public static int STALMATE = 1;
    final public static int CHECK = 2;
    final public static int DRAW = 3;
    final public static int OPEN = 4;

    final public static int[] KNIGHTJUMP = {-10,10,-14,14,-23,23,-25,25};
    final public static int[] KINGJUMP = {-1,1,-13,-12,-11,13,12,11};
    final public static int[] BISSHOPDIR = {13,-13,11,-11};
    final public static int[] ROOKDIR = {1,-1,12,-12};

}

