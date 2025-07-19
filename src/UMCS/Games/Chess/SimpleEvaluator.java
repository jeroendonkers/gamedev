package UMCS.Games.Chess;
import  UMCS.Games.Lib.*; 

public class SimpleEvaluator implements Evaluator {

   SimpleEvaluator() {
     makepawntable();
   }

   public short evaluate(Node m, int depth) 
   {
      Board board = (Board)(m.getPosition());

      int us=board.ourSide(m); 
      if (board.isEnded()) {
         if (board.getWinner()==us)   return (short)(10000 - depth);   
         else if (board.getWinner()==1-us)  return (short)(-10000 + depth);
         else return (short)0; 
      }
      int v=0;


      // count score
      int score[]=new int[2];
      int pside;

      for (int col=0; col<8; col++)
         for (int row=0; row<8; row++) {
            int field = Board.field(col,row);
            pside = board.getSide(field);
            if (pside!=Const.VOID) {
              switch(board.getPiece(field)) {
              case Const.PAWN:  
                  if (pside==Const.WHITE) score[pside]+=pawnscore[field];
                  else                    score[pside]+=pawnscore[144-field];
        //      if(b.A[field+12]==chessboard.PAWN+pside) score[pside]-=20;//double pawn
                  break;
              case Const.KNIGHT:  score[pside]+=280;  break;
              case Const.BISSHOP: score[pside]+=300;  break;
              case Const.ROOK:    score[pside]+=500;  break;
              case Const.QUEEN:   score[pside]+=900;  break;
              case Const.KING:    score[pside]+=3200; break;
              }
      }

     return (short)(score[us]-score[1-us]);
 }




      return (short)v;
   }


 static void makepawntable()
 {
    if(pawnscore!=null) return;
    pawnscore=new int[144];
    for(int i=0;i<144;i++) pawnscore[i]=100+5*((i-2)/12);
    for(int i=0;i<12;i++) {
      pawnscore[96+i]=240;
      pawnscore[84+i]=160;
    }
    pawnscore[66]=pawnscore[65]=pawnscore[78]=pawnscore[77]=180;
 }


  static int[] pawnscore = null;
}



