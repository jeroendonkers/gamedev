package UMCS.Games;
import  UMCS.Games.Lib.*; 
import  java.util.Random; 

/**
 * WELL-ORDERED GAME
 *
 * The nodes in the game trees for this game are ordered:
 * Descending for MAX nodes and ascending for MIN nodes
 * (inverse ordering is also possible)
 *
 * @author Jeroen Donkers
 * @version SEPTEMBER 2000
 **/

public class OrderedGame extends AbstractGame implements Game 
{

   public OrderedGame(int w) { width=w; }

   public String toString() { return "OrderedGame(width="+width+",inverse="+inverse+")"; }


   public String moveString(short move) { return move+""; }

   public void setInverse() {  inverse=true;  }

   public Position getStartPosition() 
   {
      return new Ppos();
   }

   public Evaluator getDefaultEvaluator()
   {
     return new PEvaluator();
   }


  /**
   * Generate a set of child moves from move m.
   **/
   public MoveEnumerator generateMoves(Position s)
   {
      MoveSet ms = new MoveSet();
      for (short i=0; i<width; i++) ms.addMove(i);
      return ms;
   }

  public Node doMove(Node m, short move) {
      Ppos s = (Ppos)(m.getPosition());
      Ppos t = new Ppos(s);
      Node nm=new Node(t);
      boolean ascending = (m.getType()==Node.MIN);
      if (inverse) ascending = !ascending;
      if (ascending) {
         t.min=s.min + move*(s.max-s.min)/width;
         t.max=s.min + (move+1)*(s.max-s.min)/width;
      } else {
         t.max=s.max - move*(s.max-s.min)/width;
         t.min=s.max - (move+1)*(s.max-s.min)/width;
      }
      nm.setType(m.getType()); 
      nm.flipType();
      return nm;
   }

  public void undoMove(Node n, short move) { }

  /**
   * Perfect game situation.
   **/
   private class Ppos implements Position
   {
       Ppos()  {
           min=0; max=1;
       }
       Ppos(Ppos s)  {
         this();
         min=s.min; max=s.max; 
       }
       public String toString() {
         String s="["+min+"-"+max+"]";
         return s;
       }
       public boolean isEnded() { return false; }

       public long getHashValue() { return (long)(Math.round(max*1000000+min*1000)); }

       public Position clonePosition() { return new Ppos(this); }

       double min,max; 
   }



   private class PEvaluator implements Evaluator {
     PEvaluator() {}
     public short evaluate(Node m, int depth) 
     {
        Ppos s = (Ppos)(m.getPosition());
        return (short)(Math.round(60000.0 * (s.min+s.max)/2 - 30000.0));
      }
  }
  public boolean isRealValue(short value) { return false; }

  private int width = 3;
  private boolean inverse = false;
}
