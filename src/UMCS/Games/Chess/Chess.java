package UMCS.Games.Chess;
import  UMCS.Games.Lib.*; 
import  UMCS.stat.*; 
import  java.util.Random; 
import java.util.zip.*;
import java.io.*;

public class Chess extends AbstractGame
{

   public Chess() {
     rand = new Random();
     setSeed();
   }

   public void setSeed(long s) {
      userand=true;
      seed = s;
      rand.setSeed(seed);
      Board.generateHashNumbers(rand);
   }
   public void setSeed(double s) {
      userand=true;
      seed = Double.doubleToLongBits(s);
      rand.setSeed(seed);
      Board.generateHashNumbers(rand);
   }
   public void setSeed() {
      userand=true;
      seed = Double.doubleToLongBits(Math.random());
      rand.setSeed(seed);
      Board.generateHashNumbers(rand);
   }
   public long getSeed() { return seed; }

  
   public Position getStartPosition() 
   {
      return new Board();
   }

   public Position getStartPosition(String s) throws IllegalMoveException
   {
      return new Board(s);
   }


   public Evaluator getDefaultEvaluator() { return new SimpleEvaluator(); }

   public MoveEnumerator generateMoves(Position p)
   {
      return ((Board)p).generateMoveList(null);
   }

   public MoveEnumerator generateMoves(Position p, HHTable hht)
   {
      return ((Board)p).generateMoveList(hht);
   }


   public Node doMove(Node m, short move) {
      Board s = (Board)(m.getPosition());
      try {
        s.doMove(move);
      } catch (IllegalMoveException e) {
        System.out.println(e.toString());
        drawPosition(s);
        System.exit(0);
      } 
      Node nm = new Node(s); nodecount++;
      nm.setType(m.getType()); nm.flipType();
      return nm;
   }

   public void undoMove(Node m, short move) {
      Board s = (Board)(m.getPosition());
      s.undoMove();
   }

   public String moveString(short move) {
       return Board.moveStr(move);
   }

   // do a real Move
   public Position setMove(Position p, short move) {
      Board s = (Board)(p);
      try {
        MoveList list = s.generateMoveList();
        if (!list.hasMove(move)) throw new IllegalMoveException("Not a legal move!"); 
        s.doMove(move);
      } catch (IllegalMoveException e) {
        System.out.println(e);
        return null;
      } 
      return s;
   }


   public void drawPosition (Position p) {
     ((Board)p).draw();
   }

   public int getWinner (Position p) {
      return ((Board)p).getWinner();
   }

   public int getPlayer (Position p) {
      return ((Board)p).getPlayer();
   }


  public boolean isRealValue(short value) { 
     return (value<-9900 || value>9900);
  }



  public HHTable getNewHHTable() { return new ChessHHTable(); }
 
    private static long nodecount=0;

    public long getCount() { return nodecount; }
    public void clearCount() { nodecount=0; }

   private static Random rand;  
   private static long seed=0;
   private static boolean userand = false;

 
}
