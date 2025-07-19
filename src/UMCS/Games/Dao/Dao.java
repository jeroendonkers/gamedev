package UMCS.Games.Dao;
import  UMCS.Games.Lib.*; 
import  UMCS.stat.*; 

public class Dao extends AbstractGame implements Game {

// first standard Dao code

   static boolean SOLVED = false; 
   static final int EMPTYFIELD = 0;
   static final int WHITE = 1;
   static final int BLACK = 2;

   static final int STARTPOS = 0x05AF369C;

   static final int FieldMask[] = {
		0xC0000000,  0x30000000,
		0x0C000000,  0x03000000,
		0x00C00000,  0x00300000,
		0x000C0000,  0x00030000,
		0x0000C000,  0x00003000,
		0x00000C00,  0x00000300,
		0x000000C0,  0x00000030,
		0x0000000C,  0x00000003 };

   static final int Dir[][] =  
	   {{-1,-1,-1,-1,0,1,2,3,4,5,6,7,8,9,10,11,12},  // up
		{4,5,6,7,8,9,10,11,12,13,14,15,-1,-1,-1,-1}, // down
		{1,2,3,-1,5,6,7,-1,9,10,11,-1,13,14,15,-1},  // right
		{-1,0,1,2,-1,4,5,6,-1,8,9,10,-1,12,13,14},   // left
		{-1,-1,-1,-1,1,2,3,-1,5,6,7,-1,9,10,11,-1},  // rightup
		{-1,-1,-1,-1,-1,0,1,2,-1,4,5,6,-1,8,9,10},   // leftup    
		{5,6,7,-1,9,10,11,-1,13,14,15,-1,-1,-1,-1,-1}, // rightdown
		{-1,4,5,6,-1,8,9,10,-1,12,13,14,-1,-1,-1,-1,-1}}; //leftdown

   static final int WhiteMask[] = {
		0x40000000,  0x10000000,
		0x04000000,  0x01000000,
		0x00400000,  0x00100000,
		0x00040000,  0x00010000,
		0x00004000,  0x00001000,
		0x00000400,  0x00000100,
		0x00000040,  0x00000010,
		0x00000004,  0x00000001 };

   static final int BlackMask[] = {
		0x80000000,  0x20000000,
		0x08000000,  0x02000000,
		0x00800000,  0x00200000,
		0x00080000,  0x00020000,
		0x00008000,  0x00002000,
		0x00000800,  0x00000200,
		0x00000080,  0x00000020,
		0x00000008,  0x00000002 };

   static final int WhitesMask = 0x55555555;
   static final int BlacksMask = 0xAAAAAAAA;

   static final int WhiteCornersMask = 0x41000041;
   static final int BlackCornersMask = 0x82000082;

// symmetry mappings 

static int flipcolor(int b) {
   return ((b & WhitesMask) << 1) | (((b & BlacksMask) >> 1) & 0x7FFFFFFF);
}

static int fliphorizontal(int b) {
   return (((b & 0xFF000000) >> 24) & 0x000000FF) |  // sign-bit !
		   ((b & 0x00FF0000) >> 8)  |
		   ((b & 0x0000FF00) << 8)  | 
		   ((b & 0x000000FF) << 24);
}

static int flipvertical(int b) {
   return (((b & 0xC0C0C0C0) >> 6) & 0x03FFFFFF) |  // sign-bit !
		   ((b & 0x30303030) >> 2) |
		   ((b & 0x0C0C0C0C) << 2) | 
		   ((b & 0x03030303) << 6);
}

static int flipdiagonal1(int b) {
  return   (b & 0xC0300C03) |
		  ((b & 0x300C0300) >> 6) |
		  ((b & 0x0C030000) >> 12) |
		  ((b & 0x03000000) >> 18) |
		  ((b & 0x00C0300C) << 6) |        
		  ((b & 0x0000C030) << 12) |
		  ((b & 0x000000C0) << 18);
}

static int turn1(int b) { return flipvertical(flipdiagonal1(b)); }
static int turn2(int b) { return flipvertical(fliphorizontal(b)); }
static int turn3(int b) { return fliphorizontal(flipdiagonal1(b)); }
static int flipdiagonal2(int b) { return flipdiagonal1(turn2(b)); }

static int[] allFlips(int b) {
	int[] sym = new int[8];
	sym[0] = b;
	sym[1] = fliphorizontal(b);
	sym[2] = flipvertical(b);
	sym[3] = flipdiagonal1(b);
	sym[4] = flipdiagonal2(b);
	sym[5] = turn1(b);
	sym[6] = turn2(b);
	sym[7] = turn3(b);
	return sym;
}

static void allFlips(int b, int[] sym) {
	if (sym.length<8) return;
	sym[0] = b;
	sym[1] = fliphorizontal(b);
	sym[2] = flipvertical(b);
	sym[3] = flipdiagonal1(b);
	sym[4] = flipdiagonal2(b);
	sym[5] = turn1(b);
	sym[6] = turn2(b);
	sym[7] = turn3(b);
}

static boolean validBoard(int b) {
   int bc=0, mask=3;
   for (int i=0; i<16; i++) {
	   if ((b & mask)!=0) bc++; 
	   if ((b & mask)==mask) return false; // two pieces on one field!
	   mask <<= 2;
   }
   return (bc==8);
}

/* Second encoding : 8 times 4 bits for the positions of the stones */

static int board2pos(int b) {
  int wshift = 28; int bshift = 12;
  int p = 0;
  for (int i=0; i<16; i++) {
	 if ((b & WhiteMask[i]) != 0) { p |= i << wshift; wshift -= 4; }
	 else if ((b & BlackMask[i]) != 0) { p |= i << bshift; bshift -= 4; }
  }
  return p;
}


static int pos2board(int p) {
   int b=0;
   for (int i=0; i<4; i++) {
	  b |= BlackMask[p & 15];
	  p = (p >> 4) & 0x0FFFFFFF; // sign-bit !
   }
   for (int i=0; i<4; i++) {
	  b |= WhiteMask[p & 15];
	  p = (p >> 4) & 0x0FFFFFFF; // sign-bit !
   }
   return b;
}


// ------------ LEX order computation ------------


static int[][] binom = 
{{0,1,2,3,4, 5, 6, 7, 8,  9, 10, 11, 12, 13,  14,  15,  16},
 {0,0,1,3,6,10,15,21,28, 36, 45, 55, 66, 78,  91, 105, 120},
 {0,0,0,1,4,10,20,35,56, 84,120,165,220,286, 364, 455, 560},
 {0,0,0,0,1, 5,15,35,70,126,210,330,495,715,1001,1365,1820}};
  

static int rank(int p) {

  // white stones
  int pa = ((p & 0xF0000000)>>28) & 0x0000000F; // sign bit
  int pb = (p & 0x0F000000)>>24;
  int pc = (p & 0x00F00000)>>20;
  int pd = (p & 0x000F0000)>>16;
  int r = 495 * (binom[0][pa] + binom[1][pb] + binom[2][pc] + binom[3][pd]);

  // black stones
  int mask = 0x0000F000, shift = 12;
  for (int i=0; i<=3; i++) {
	 int k = (p & mask) >> shift;
	 if (k>pd) k--; 
	 if (k>pc) k--; 
	 if (k>pb) k--; 
	 if (k>pa) k--;
	 r += binom[i][k];
	 mask>>=4; shift-=4;
  }
  return r; 
}

static int unrank(int r) {
   if (r<0 || r>=900900) return 0;  // not in range;

   int rw = r / 495, rb = r % 495;
   int pa=0, pb=0, pc=0, pd=0;

   // extract white stones
   int p=0, shift=16;
   for (int i=0; i<4; i++) {
	   int y=16;
	   while (binom[3-i][y]>rw) y--; 
	   rw-=binom[3-i][y];
	   p = p | (y<<shift);
	   if (i==0) pd=y; 
	   else if (i==1) pc=y; 
	   else if (i==2) pb=y; 
	   else pa=y;
	   shift+=4;
   } 

  // extract black stones
   shift=0;
   for (int i=0; i<=3; i++) {
	   int y=16;
	   while (binom[3-i][y]>rb) y--;
	   rb-=binom[3-i][y];
	   if (y>=pa) y++;
	   if (y>=pb) y++;
	   if (y>=pc) y++;
	   if (y>=pd) y++;
	   p = p | ((y)<<shift);
	   shift+=4;
   } 

   return p;
}


// ------------ terminal detection ----------------------

static int[] WhiteWins = {
   0x41000041,  // corners
   0x55000000, 0x00550000, 0x00005500, 0x00000055, // horlines
   0x40404040, 0x10101010, 0x04040404, 0x01010101, // verlines
   0x50500000, 0x05050000, 0x00505000, 0x00050500,  // squares
   0x00005050, 0x00000505, 0x14140000, 0x00141400, 0x00001414,
   0x60A00000, 0x090A0000, 0x00000A09, 0x0000A060  // enclosed
};

static int wins(int board) {
   for (int i=0; i<22; i++) if ((board&WhiteWins[i])==WhiteWins[i]) return WHITE;
   board = flipcolor(board);
   for (int i=0; i<22; i++) if ((board&WhiteWins[i])==WhiteWins[i]) return BLACK;
   return 0;
}


// only white moves
static void getMoves(int p, int[] moves) {

  if (moves.length<32) return;

  int mask = 0x000F0000, shift = 16;
  for (int i=0; i<32; i++) moves[i]=0;
  int cnt=0;
  int b = pos2board(p);

  p = (p >> 16) & 0x0000FFFF; // sign-bit !

  for (int i=0; i<4; i++) {
	 int f = (p & 15);
	 p = (p >> 4) & 0x0FFFFFFF; // sign-bit !

	 for (int d=0; d<8; d++) {
		int t=f;

		for (int j=0; j<3; j++) {
		   if (Dir[d][t]==-1) break;
		   if ((b & FieldMask[Dir[d][t]])!=0) break;
		   t=Dir[d][t];
		}
		if (t!=f) {
		   moves[cnt++]=WhiteMask[f] | WhiteMask[t];
		}

	 }
  }
}


static void getBlackMoves(int p, int[] moves) {
   int b = pos2board(p);
   getMoves(board2pos(flipcolor(b)),moves); 	
}	

// ------------ String routines -------------------------


static String board2str(int b) {
   String s = "";
   for (int i=0; i<16; i++) {
	 if ((b & WhiteMask[i]) != 0)  s+="W";
	 else if ((b & BlackMask[i]) != 0) s+="B";
	 else s+="."; 
   }
   return s;
}

static String pos2str(int p) {
   String s = "";
   for (int i=0; i<8; i++) {
	  s = (p & 15)+" "+s;
	  p = (p >> 4) & 0x0FFFFFFF; // sign-bit !
   }
   return s;
}

static  int[] SymClasses; // per position-rank the equivalence class number
static  int[] SymRefs; // per equivalence class one position

static int ClassCount=113028;

static void createClasses() {
  SymClasses = new int[900900];   
  SymRefs = new int[ClassCount];     
  int sym[] = new int[8];
  for (int i=0; i<900900; i++) SymClasses[i]=0;
  int cnt=0;
  for (int i=0; i<900900; i++) if (SymClasses[i]==0) {
	cnt++;    
	int pos=unrank(i);  
	allFlips(pos2board(pos),sym); 
	for (int j=0; j<8; j++) 
	  SymClasses[rank(board2pos(sym[j]))]=cnt;
	SymRefs[cnt-1]=pos;
  }
}


static int[][] inarcs;
static int[][] outarcs;
static int[] indegree;

static void createNetwork() {
  inarcs = new int[ClassCount][26];
  outarcs = new int[ClassCount][19];
  indegree = new int[ClassCount];

  int[] moves = new int[32];
  int[] targets = new int[32];

  for (int j=0; j<ClassCount; j++) indegree[j]=0; 

  for (int j=0; j<ClassCount; j++) { 
	 for (int i=0; i<26; i++) inarcs[j][i]=0;
	 for (int i=0; i<19; i++) outarcs[j][i]=0;       
  }

  for (int j=0; j<ClassCount; j++) { 
	 int p = SymRefs[j], b = pos2board(p);
	 if (wins(b)==0) {
		getMoves(p,moves);
		int cnt=0;
		for (int i=0; i<32; i++) {
		   if (moves[i]==0) break;
		   int nb = flipcolor(b ^ moves[i]);
		   int tar = SymClasses[rank(board2pos(nb))];
		   boolean dble=false;
		   for (int k=0; k<cnt; k++) {
			  if (targets[k]==tar) { dble=true; break; }
		   }
		   if (!dble) { targets[cnt]=tar; cnt++; }
		}  
		for (int i=0; i<cnt; i++) {
		   int tar=targets[i];
		   outarcs[j][i]=tar;
		   int k=indegree[tar-1];
		   inarcs[tar-1][k]=j+1;
		   indegree[tar-1]++;
	   }
	 }
   }
}

static public short[] solLabel;  // this array contains the solution of the game.
					 // One entry for every symmertry class.
					 // 1: White wins, -1: Black wins.

static public int farwin;


static void Solve() {

	short[] tsolLabel = new short[ClassCount];
	solLabel = new short[ClassCount];
   int wins=0, losses=0;
   int wnr=0, lnr=0;

   for (int i=0; i<ClassCount; i++) {
	 int p = SymRefs[i], b = pos2board(p);
	 int win=wins(flipcolor(b));
	 if (win==0) {
	 	    solLabel[i]=0;
		    tsolLabel[i]=0;
	 	 } 
	 if (win==WHITE && indegree[i]>0) { 
	 	 tsolLabel[i]=-1; wins++;
	 	 solLabel[i]=1000; 
	 	 }
	 if (win==BLACK && indegree[i]>0) { 
	 	 tsolLabel[i]=+1; losses++;
		 solLabel[i]=-1000;
		 }
	 	 
   }
   

   for (int step=1; step<ClassCount; step++) {
	 int stepcount=0;  
	 int newwins=0, newlosses=0;

	 for (int j=0; j<ClassCount; j++) if (tsolLabel[j]==1 || tsolLabel[j]==-1) {

	   for (int k=0; k<26; k++) {
		 int pr = inarcs[j][k];
		 if (pr==0) break;
		 pr--;
		 if (tsolLabel[pr]!=0) continue;
         
		 boolean canwin=false;
		 boolean unknown=false;
		 for (int i=0; i<19; i++) {
			if (outarcs[pr][i]==0) break;
			int ch = -tsolLabel[outarcs[pr][i]-1];
			if (ch==2 || ch==-2) ch=0;
			if (ch>0) {canwin=true; break;}
			if (ch==0) {unknown=true;}
		 }
		 if (canwin) { 
		 	tsolLabel[pr]=2; newwins++;
		 	farwin = pr; 
	        
		 }
		 else if (!unknown) { 
		 	 tsolLabel[pr]=-2; newlosses++;
		} 
           
		 if (tsolLabel[pr]!=0) stepcount++;
		}     
	  } 
	  
	  for (int j=0; j<ClassCount; j++) {
	 	 if (tsolLabel[j]==2) { tsolLabel[j]=1; solLabel[j]=(short)step;} 
		 if (tsolLabel[j]==-2) { tsolLabel[j]=-1; solLabel[j]=(short)(-step); }
	  }
   
	  if (newwins+newlosses==0) break;
//	  System.out.println(step+": wins "+newwins+", losses "+newlosses);
	  wins += newwins;
	  losses += newlosses;
   }
   
   //System.out.println("total: wins "+wins+", losses "+losses);
}

public static void tracewin() {
	int cl = farwin;
	int col = 1;
	int step=0;
	int cnt=12;
	do {
	   int pos = SymRefs[cl];
	   step = solLabel[cl];
	   System.out.println("STEP: "+ step);
	   if (col==1) System.out.println(showPos(pos));
	      else System.out.println(showBoard(flipcolor(pos2board(pos))));
	   if (step==1000) return;    	
	   
	   if (col==-1) {
		for (int i=0; i<19; i++) 
		if (outarcs[cl][i]>0) {
			System.out.println("label "+solLabel[outarcs[cl][i]-1]);
			if (solLabel[outarcs[cl][i]-1]==-step-1 ||
			      solLabel[outarcs[cl][i]-1]==1000) {
		       cl = outarcs[cl][i]-1; 	
		       break;
		     }
		} else break;
		
	   } else {
		for (int i=0; i<19; i++) 
		  if (solLabel[outarcs[cl][i]-1]==-(step-1)) {
		  	cl = outarcs[cl][i]-1; 	
		  	break;
		  }
	   }
	   col = -col;
       cnt--;	   	
	} while (cnt>0);
	
}


public static String showPos(int p) { 
   return showBoard(pos2board(p)); 
}

public static String showBoard(int b) {
   String s = "      a   b   c   d";
   for (int i=0; i<16; i++) {
	 if (i%4==0) s+="\n\n   "+(4-(i/4))+"  ";
	 if ((b & WhiteMask[i]) != 0)  s+="W   ";
	 else if ((b & BlackMask[i]) != 0) s+="B   ";
	 else s+=".   "; 
   }
   return s+"\n";
}

// our labels


public int visitedWhite[];
public int visitedBlack[];
public int evaluated[];

public void clearVisited() {
	visitedWhite=new int[ClassCount];
	visitedBlack=new int[ClassCount];
	evaluated=new int[ClassCount];
	for (int i=0; i<ClassCount; i++) {
		visitedWhite[i]=0;
		visitedBlack[i]=0;
		evaluated[i]=0; 
	}
}


// evaluation function

public short eval[];

public int countBits(int k) {
	int r = 0, m = 1;
	for (int i=0; i<32; i++) { 
	   if ((k & m) !=0) r++;
	   m <<= 1;
    }
    return r;      	
}

public void computeEval() {
   eval=new short[ClassCount];
   java.util.Random rnd = new java.util.Random();
   rnd.setSeed(0);
   for (int i=0; i<ClassCount; i++) {
	int p = unrank(i);
	int b = pos2board(p);
	int w = wins(b);
   	if (w==WHITE) 
   		eval[i]=10000; 
	else if (w==BLACK)
	    eval[i]=-10000;
	else {  
		int score=0;  
		for (int j=0; j<22; j++) {
			if (countBits(b&WhiteWins[j])==3)
			   score=score+3;
		}
		b=flipcolor(b);
		for (int j=0; j<22; j++) { 
			if (countBits(b&WhiteWins[j])==3)
		        score=score-3;
		}        
	    score = score * 100 + rnd.nextInt(100) - 50;
		score = score + 250*solLabel[i]; 	
	 	eval[i] = (short)score;	   
	 }
  }
}

public long hashv[];
public long sidehash;

public void computeHash() {
   hashv=new long[ClassCount];
   java.util.Random rnd = new java.util.Random();
   rnd.setSeed(0);
   for (int i=0; i<ClassCount; i++) 
		hashv[i] = rnd.nextLong();
   sidehash = rnd.nextLong();	   
 }

 public void computeHash(int seed) {
	hashv=new long[ClassCount];
	java.util.Random rnd = new java.util.Random();
	rnd.setSeed(seed);
	for (int i=0; i<ClassCount; i++) 
		 hashv[i] = rnd.nextLong();
	sidehash = rnd.nextLong();	   
  }



// then the gameslib wrapper



public Dao() {
  if (!SOLVED) {
	System.out.println("Dao Game");
	System.out.println("(c) 2002. Jeroen Donkers");
	System.out.println("          IKAT, Universiteit Maastricht");
	System.out.println("");
	System.out.println("Building network...");
	createClasses();
	createNetwork();
	System.out.println("Solving the game...");
	Solve();
	//tracewin();
	System.out.println("Computing evals...");
	clearVisited();	
	computeEval();
	computeHash();
	System.out.println("");
	SOLVED=true;	
  }
}

public Position getStartPosition() {
	Dpos dp = new Dpos();
	dp.moves = new int[32];
	if (dp.player==WHITE) getMoves(dp.pos, dp.moves);
	else getBlackMoves(dp.pos, dp.moves);
	return dp;
};

public MoveEnumerator generateMoves(Position p) {
	Dpos dp = (Dpos)p;
	dp.moves = new int[32];
   	if (dp.player==WHITE) getMoves(dp.pos, dp.moves);
   	else getBlackMoves(dp.pos, dp.moves);
	return new Dmoves(dp);
}

public Evaluator getDefaultEvaluator() {
	
	return new DEvaluator();
}

public Node doMove(Node nd, short mv) { 
	   Dpos s = (Dpos)(nd.getPosition());
	   Dpos t = new Dpos(s);
	   
	   int move = s.moves[mv];
   	   int board;
   	   
   	   if (s.player==WHITE)
   	    board = pos2board(s.pos) ^ move;
   	   else   
   	   	board = flipcolor(flipcolor(pos2board(s.pos)) ^ move);
   	   
   	   
	   t.pos = board2pos(board);
	   int r=rank(t.pos);	   
	   t.moves=new int[32];
	   t.player = 3-s.player;	   
	   if (t.player==WHITE) { 
	   	  getMoves(t.pos,t.moves);
	   	  visitedWhite[SymClasses[r]]++;
	   } else { 	   
	   	 getBlackMoves(t.pos,t.moves);
	   	 int k = SymClasses[r];
		 visitedBlack[k]++;
	   }	       
	   
	   
	   
	       
	       
	   Node nm = new Node(t);
	   nm.setType(nd.getType());
	   nm.flipType();
	   return nm; 
	};


public void undoMove(Node n, short move) { };
public String moveString(short move) { return ""+move; }
public boolean isRealValue(short value) { return false; }
public boolean isLegalMove(short move, Position p) {
	 Dpos s = (Dpos)p;
	 if (move>=0 && move<32 && s.moves[move]!=0) return true;   
	 else return false; 
 }
 
 public void drawPosition(Position pos) {
	Dpos dp = (Dpos)pos;
	System.out.println(showPos(dp.pos));
 }

 public static short findMove(Position pos, int m) {
	Dpos s = (Dpos)pos;
	for (short i=0; i<32; i++) {
		if (s.moves[i]==m) return i;
		if (s.moves[i]==0) return Player.NOMOVE;
	}
	return Player.NOMOVE;
 }
 
 public static void setMove(Position pos, short m) {
	Dpos s = (Dpos)pos;
	   
	int move = s.moves[m];
	int board;
   	   
	if (s.player==WHITE)
	 board = pos2board(s.pos) ^ move;
	else   
	 board = flipcolor(flipcolor(pos2board(s.pos)) ^ move);
    
    s.player = 3 - s.player;   	   
   	   
	s.pos = board2pos(board);
	if (s.player==WHITE) 
		getMoves(s.pos,s.moves);
	else 	   
		getBlackMoves(s.pos,s.moves);
		
		
 }
 
  
 
  class Dmoves implements MoveEnumerator {
  	Dmoves (Dpos dpos) {
  		mypos = dpos;
  		p=0;
  	}
	public short getMove() {
	   p=0;
	   if (mypos.moves[p]==0) return Player.NOMOVE;
	   else return 0;	
	}
	  
    public short nextMove() {
       if (mypos.moves[p]==0) return Player.NOMOVE;
       p++;
	   if (mypos.moves[p]==0) return Player.NOMOVE;
	   else return p;
    } 
  	Dpos mypos;
  	short p;
  }

  class Dpos implements Position
	 {
		 Dpos()  
		 {
		   pos = STARTPOS;
		   player = WHITE; 
		 }

		 Dpos(Dpos s) {
		   pos = s.pos;
		   player=s.player;
		 }
 
	   public Position clonePosition() {
		   return new Dpos(this);
		}
 
	   public long getHashValue() {
         long h = hashv[SymClasses[rank(pos)]];
         if (player==WHITE) h =h^sidehash; 	   	
	   	 return h;
	   }

	   public boolean isEnded() { return (wins(pos2board(pos))!=0); }

	   public int getHashKey() { 
		rnd.setSeed(pos);
		rnd.nextLong();
	    return rnd.nextInt();
	   }

	   public int get2ndHashKey() { 
		rnd.setSeed(pos);
		rnd.nextLong(); rnd.nextInt();
		return rnd.nextInt();	   	
	   }
   
	   int pos, player;
	   int[] moves;
	   
		 
	}

    class DEvaluator implements Evaluator {
		
		
	public short evaluate(Node m, int depth) 
	  {
	  	Dpos s = (Dpos)m.getPosition();
	  	
		evaluated[SymClasses[rank(s.pos)]]++;
		
	  	int us;
		if (m.getType()==Node.MAX) us = s.player;
		else us = 3-s.player;
		
	    int win = wins(pos2board(s.pos));
	    if (win==0) {
	       short v = eval[SymClasses[rank(s.pos)]]; 	
	       if (us==BLACK) v=(short)(-v);
	       return v;	
	    }	
	    
	    if (win==us) return (short)(1000-depth);
	    else return (short)(depth-1000); 
	    	
	  }	
    }
	
	static java.util.Random rnd = new java.util.Random();
	
}