package UMCS.Games;
import  UMCS.Games.Lib.*; 
import  java.util.Random; 

/**
 * RANDOM GAME CHEAP IN MEMORY
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

public class CheapRandomGame extends AbstractGame implements Game 
{

   private CheapRandomGame() {
      rand = new Random();
      setSeed();
   }


 /**
  * Create a random game with branching factor [minw - maxw] inclusive, and 
  * nopp opponent types.
  **/

   public CheapRandomGame(int minw, int maxw, int nopp) {
      this();
      minwidth=minw;
      maxwidth=maxw;
      nopponent=nopp;
      stack = new Rpos[maxdepth];
      for (int i=0; i<maxdepth; i++) {
          stack[i]=new Rpos(); stack[i].index=i;
      }
      bases = new double[maxdepth][maxwidth][nopp];
   } 

  void doUseTT() { useTT=true; }

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
      stack[0].randomBase();
      stack[0].childrengenerated=false;
      return stack[0];
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
      Rpos s = (Rpos)p;  
//      if (s.childrengenerated) return s.moves;
      s.moves.clear(); s.moves.reset();
      // set random seed, based on parent
      rand.setSeed(Double.doubleToLongBits(s.base[0])); 
      // generate child count
      int select = minwidth + (int)Math.floor(rand.nextFloat()*(maxwidth - minwidth+1));
      // generate child moves and child basis
      for (int i=0; i<select; i++) {
          s.moves.addMove((short)i);
          for (int j=0; j<nopponent; j++) bases[s.index][i][j]=rand.nextDouble();          
      }
      s.childrengenerated=true;
      return s.moves; 
   }

    public Node doMove(Node m, short move) {
        Rpos s = (Rpos)(m.getPosition());
        if (!s.childrengenerated) generateMoves(s);
        Rpos child = stack[s.index+1];
        child.childrengenerated=false;
        child.randomBase(move);
        child.addBase(s);
        child.node.setType(m.getType()); child.node.flipType();
        return child.node;
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
      **/

       Rpos()  {
          base = new double[nopponent];
          moves = new MoveSet(); moves.ensureCapacity(maxwidth);
          node = new Node(this);
          childrengenerated=false;
       }

       void randomBase() {
          for (int i=0; i<nopponent; i++) base[i]=rand.nextDouble();
       }


       void randomBase(int k) {
          for (int i=0; i<nopponent; i++) base[i]=bases[index-1][k][i];
       }

       void addBase(Rpos s) {
         for (int i=0; i<nopponent; i++) base[i]+=s.base[i];
       }

       public String toString() {
         String s="";
         for (int i=0; i<nopponent; i++) s += base[i]+" ";
         return s;
       }

	   public Position clonePosition() {
			 Rpos p = new Rpos();
			 return p;
			 // not filled in properly!
		  }

       public boolean isEnded() { return false; }
       public long getHashValue() { return (long)(Math.round(base[0]*10000000000000L)); }
//       public long getHashValue() { return (long)(Math.round(base[0]*1000L)*1000000); }
       double[] base;
       int index = -1;
       MoveSet moves;
       Node node;
       boolean childrengenerated=false;
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
  private int maxdepth=100;
  private Rpos[] stack;
  private boolean useTT=false;
  private double[][][] bases;
}
