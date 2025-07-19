package UMCS.Games;
import  UMCS.Games.Lib.*; 
/**
 * MONO GAME
 *
 * The nodes in the game trees for this all have the same value.
 *
 * @author Jeroen Donkers
 * @version SEPTEMBER 2000
 **/

public class MonoGame extends AbstractGame implements Game 
{
   public MonoGame(int w) { width=w; }
   public String toString() { return "MonoGame(width="+width+")"; }
   public String moveString(short move) { return ""+move; }
   public Position getStartPosition()  { return new Ppos(); }
   public Evaluator getDefaultEvaluator() { return new PEvaluator(); }
   public MoveEnumerator generateMoves(Position p) {
      MoveSet ms = new MoveSet();
      for (short i=0; i<width; i++) ms.addMove(i);
      return ms;
   }
   public Node doMove(Node m, short move) {
      Node nm=new Node(new Ppos());
      nm.setType(m.getType()); nm.flipType();
      return nm; 
   }
   public void undoMove(Node m, short move) {}
   private class Ppos implements Position {
       Ppos()  {}
       public String toString() { return "."; }
       public boolean isEnded() { return false; }
       public long getHashValue() { return 0; }
       public Position clonePosition() {return new Ppos(); }
   }
   private class PEvaluator implements Evaluator {
     PEvaluator() {}
     public short evaluate(Node m, int depth) { return 1; }
  }
  public boolean isRealValue(short value) { return false; }
  private int width;
}
