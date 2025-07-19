package UMCS.Games;
import  UMCS.Games.Lib.*; 
import  java.util.Random; 

/**
 * RANDOM GAME
 * Implements an incremental random game tree (for OmSearch and PromSearch).
 * Gives evaluations simultaneously for a set of opponents.
 * The Branchingfactor is selected randomly per child between minwidth and maxwidth
 * (inclusive).
 *
 * Parent node base[0] is used as random seed for children. 
 * Games played with the same start move will be identical, independent of the
 * used search algorithm.
 *
 * @author Jeroen Donkers
 * @version SEPTEMBER 2000
 **/

public class RandomGame extends AbstractGame implements Game 
{

   private RandomGame() {
      rand = new Random();
      setSeed();
   }


 /**
  * Create a random game with branching factor [minw - maxw] inclusive, and 
  * nopp opponent types.
  **/

   public RandomGame(int minw, int maxw, int nopp) {
      this();
      minwidth=minw;
      maxwidth=maxw;
      nopponent=nopp;
   } 

  /**
   *
   **/
   public String toString() { return "RandomGame(width="+minwidth+"-"+maxwidth+",nopp="+nopponent+")"; }

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
     return new REvaluator(0);
   }


  /**
   * Get the evaluator for opponent type opp.
   **/
   public Evaluator getEvaluator(int opp)
   {
     return new REvaluator(opp);
   }


  /**
   * Generate a set of child moves from move m.
   **/
   public MoveEnumerator generateMoves(Position p)
   {
      MoveSet ms = new MoveSet();
      Rpos s = (Rpos)p;  
      // set random seed, based on parent
      rand.setSeed(Double.doubleToLongBits(s.base[0])); 
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
          base = new double[nopponent];
          for (int i=0; i<nopponent; i++) base[i]=rand.nextDouble();
       }

     /**
      * Create a position based on s. 
      * S.base is added to generated base values.
      **/

       Rpos(Rpos s)  {
         this();
         for (int i=0; i<nopponent; i++) base[i]+=s.base[i];
       }

       public String toString() {
         String s="";
         for (int i=0; i<nopponent; i++) s += base[i]+" ";
         return s;
       }

       public Position clonePosition() {
          Rpos p = new Rpos();
          for (int i=0; i<nopponent; i++) p.base[i]=base[i];                    
          return p;
       }

       public boolean isEnded() { return false; }
       public long getHashValue() { return (long)(Math.round(base[0]*10000000000000L)); }
       double[] base;
       Rpos[] children;
   }


  public double[] getBase(Node m) {
   return ((Rpos)m.getPosition()).base;
  }

  /**
   * Random game evaluator.
   * simply selects base value in situation for selected opponent type.
   **/
   private class REvaluator implements Evaluator {

     REvaluator(int opp) { if (opp>=0 && opp<nopponent) opponent=opp; }

     public short evaluate(Node m, int depth) 
     {
        Rpos s = (Rpos)(m.getPosition());
        return (short)(Math.round(1000.0*s.base[opponent]));
      }
      int opponent=0;  
  }

  public boolean isRealValue(short value) { return false; }

  private int maxwidth = 5;
  private int minwidth = 2;
  private int nopponent = 1;
 
  public void useRandomGenerator(Random r) {rand = r;}

  private Random rand;  
  private long seed=0;
 
}
