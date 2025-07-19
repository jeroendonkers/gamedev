package UMCS.Games.Chess;
import  UMCS.Games.Lib.*; 

public class MaterialEvaluator implements Evaluator {

   MaterialEvaluator() {
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

      // count score
      int score[]=new int[2];
      int pside;

      for (int col=0; col<8; col++) for (int row=0; row<8; row++) {
          int field = Board.field(col,row);
          if (!board.isEmpty(field)) {
             pside = board.getSide(field);
             switch(board.getPiece(field)) {
             case Const.PAWN:    score[pside]+=100;  break;
             case Const.KNIGHT:  score[pside]+=300;  break;
             case Const.BISSHOP: score[pside]+=325;  break;
             case Const.ROOK:    score[pside]+=500;  break;
             case Const.QUEEN:   score[pside]+=900;  break;
             }
          }
      }

      if (board.getStatus() == Const.CHECK) score[board.getPlayer()] -= 50;
      
      return (short)(score[us]-score[1-us]);
   }

}



