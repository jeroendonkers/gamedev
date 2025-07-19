package UMCS.Games;
import  UMCS.Games.Lib.*; 
import  java.util.Random; 

/**
 * Bounded Sum - RANDOM GAME
 * Implements an incremental random game tree for OmSearch.
 * Gives evaluations simultaneously for a set of opponents.
 * The Branchingfactor is selected randomly per child between minwidth and maxwidth
 * (inclusive).
 *
 * the two evaluation functions are bounded to each other.
 *
 * Parent node base[0] is used as random seed for children. 
 * Games played with the same start move will be identical, independent of the
 * used search algorithm.
 *
 * @author Jeroen Donkers
 * @version JUNE 2003
 **/

public class BSRandomGame extends AbstractGame implements Game 
{

   private BSRandomGame() {
      rand = new UMCS.stat.MersenneTwister();
      bound=-1;
      setSeed();
   }

 /**
  * Create a random game with branching factor [minw - maxw] inclusive, and 
  * bound b (>0).
  **/

   public BSRandomGame(int minw, int maxw, int b) {
      this();
      minwidth=minw;
      maxwidth=maxw;
      bound = b;
   } 

  /**
   *
   **/
   public String toString() { return "BsRandomGame(width="+minwidth+"-"+maxwidth+",bound="+bound+")"; }

  /**
   * Set the random seed for the next start move.
   **/
  public void setSeed(long s) {
     seed = s;
  }

  /**
   * Set the random seed for the next start move.
   **/
  public void setSeed(double s) {
     seed = Double.doubleToLongBits(s);
  }

  /**
   * Set the random seed for the next start move, using Math.random().
   **/
  public void setSeed() {
     seed = Double.doubleToLongBits(Math.random());
  }


  /**
   * Retrieve the used random seed.
   **/
  public long getSeed() { return seed; }

  /**
   * Get a start move. Games played from the same start move are identical.
   **/
   public Position getStartPosition() 
   {
      if (seed!=0) rand.setSeed(seed); 
      return new Rpos();
   }

  /**
   * Get a default evaluator ( evaluator for opponent type 0).
   **/
   public Evaluator getDefaultEvaluator()
   {
     return new BSEvaluator(0);
   }


  /**
   * Get the evaluator for opponent.
   **/
   public Evaluator getOppEvaluator()
   {
     return new BSEvaluator(1);
   }


  /**
   * Generate a set of child moves from move m.
   **/
   public MoveEnumerator generateMoves(Position p)
   {
      MoveSet ms = new MoveSet();
      Rpos s = (Rpos)p;  
      // set random seed, based on parent
      rand.setSeed(Double.doubleToLongBits(s.base)); 
      // generate child count
      int select = minwidth + (int)Math.floor(rand.nextFloat()*(maxwidth - minwidth+1));
      s.children = new Rpos[select];
      // generate children all at once, because of random sequence
      for (int i=0; i<select; i++) {
         s.children[i] = new Rpos(s);
         ms.addMove((short)i);
      }
      return ms; 
   }


    public Node doMove(Node m, short move) {
        Rpos s = (Rpos)(m.getPosition());
        if (s.children==null) generateMoves(s);
        Node nm=new Node(s.children[move]);
        nm.setType(m.getType()); nm.flipType();
        return nm;
    }

    public void undoMove(Node m, short move) { }


   public String moveString(short move) { return ""+move; }

  /**
   * Random game situation. Contains only a set of doubles, one for every opponent type.
   **/
   private class Rpos implements Position
   {

     /**
      * Create a new position. 
      * Base values are generated randomly.
      **/

       Rpos()  {
          base = rand.nextDouble();
          diff = rand.nextDouble();
       }

     /**
      * Create a position based on s. 
      * S.base is added to generated base values.
      **/

       Rpos(Rpos s)  {
         this();
         base+=s.base;
       }

       public String toString() {
         String s="" + base+" ";
         return s;
       }


       public Position clonePosition() {
          Rpos p = new Rpos();
          p.base=base;
          p.diff=diff;
          return p;
       }

       public boolean isEnded() { return false; }
       public long getHashValue() { return (long)(Math.round(base*10000000000000L)); }
       double base,diff;
       Rpos[] children;
   }


  public double getBase(Node m) {
   return ((Rpos)m.getPosition()).base;
  }

  /**
   * Random game evaluator.
   * simply selects base value in situation for selected opponent type.
   **/
   private class BSEvaluator implements Evaluator {

     BSEvaluator(int opp) { if (opp==0 || opp==1) opponent=opp; }

     public short evaluate(Node m, int depth) 
     {
        Rpos s = (Rpos)(m.getPosition());
        return (short)(Math.round(1000.0*s.base + opponent*bound*(s.diff-0.5)));
      }
     int opponent=0;  
  }

  public boolean isRealValue(short value) { return false; }

  private int maxwidth = 5;
  private int minwidth = 2;
 
  private Random rand;  
  private long seed=0;
  private int bound=-1;
 
}
