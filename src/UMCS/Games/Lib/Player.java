package UMCS.Games.Lib;
import UMCS.iotools.*;
import java.util.*;

/**
 * Player <P>
 * This base class is the start of every game playing class. <BR>
 * It provides a number of tools that are useful in any game-playing
 * algorithm, such as a timer, transposition tables, log files,
 * and evaluation counters.
 *
 * @author H.H.L.M. Donkers
 * @version JUNE 2000
 */


public abstract class Player implements TimerAlarm {

  /**
   * Create a new player, playing game g.<BR>
   */
   public Player(Game g) {
     game = g;
   }
 
 /**
  * Retrieve the game being played.<BR>
  */
   public Game getGame()  {
      return game; 
   }

 /**
  * Find the next move to play.<BR>
  * All players must implement this, because it is the core of the playing.
  */
   public abstract SearchResult nextMove(Position pos); 

 /**
  * Return a short description of the player.<BR>
  */
   public abstract String getName(); 

   // EVALUATION COUNTING ========================

 /**
  * Clear the evaluation counters.<BR>
  */
   public void clearEvals() { numevals=0; numpartialevals=0;}

   /**
   * Return the number of node evaluations since the last clear.<BR>
   */
   public int getNumEvals() { return numevals; } 

   /**
   * Increment the number of node evaluations with one.<BR>
   */
   public void incEvals() { numevals++; }

  /**
   * Increment the number of node evaluations with one.<BR>
   */
   public void incEvals(int n) { numevals+=n; }


   /**
   * Return the number of partial node evaluations since the last clear.<BR>
   */
   public int getNumPartialEvals() { return numpartialevals; } 

   /**
   * Increment the number of partial node evaluations with one.<BR>
   */
   public void incPartialEvals() { numpartialevals++; }


   // LOGGING ===================================



   /**
   * Set the name of the log-file for this player.<BR>
   */
   public void setLog(String s) { out = Log.createLog(s); out.startLog(); logging=true; }

   /**
   * Set the log-file for this player to the standard output.<BR>
   */
   public void setLog() { out = Log.createLog(); out.startLog(); logging=true; }

   /**
   * Switch logging messages on.<BR>
   */
   public void startLog() { 
       if (out==null) return;
       out.startLog(); logging=true; 
   }

   /**
   * Switch logging messages off.<BR>
   */
   public void stopLog() { 
      if (out==null) return;
      out.stopLog(); logging=false; 
   }

   /**
   * Write a message to the log. (If logging is on) <BR>
   */
   public void log(String s) { 
       if (out==null) return;
       out.log(s); 
   }

   public boolean amLogging() { return logging; }
   
   // TIMER FUNCTION ===================

   /**
   * Start a timer that should wait the given amount of milliseconds.<BR>
   */
   public void startTimer(long wait) {
     timer = new UMCS.iotools.Timer(this, wait, "Player");
     timing = true;
     timed = false;
     timer.start();
   }

   /**
   * Stop the timer. Return true if the given time has elapsed.<BR>
   */
   public long stopTimer() {   
     if (timing && !timed) timer.interrupt();
     timed=true;
     return timer.getElapsedTime();
   }

   /**
   * Called when the timer goes off.<BR>
   */
   public void alarm(boolean interrupted) {
     timed=true;
   }

   /**
   * Check the timer and throw an exception if the time elapses.
   * 
   * @see OutOfTimeException
   */
   public void checkTime() throws OutOfTimeException { 
      if (timing && timed) { 
         timing=false;
         throw new OutOfTimeException(); 
      }
   }

   // TRANSPOSITION TABLES ===========================

   public void setTTable(TTable t) { tp=t; }
   public TTable getTTable() { return tp; }
   public boolean hasTTable() { return (tp!=null); }
   public void clearTTable() { 
        if (tp!=null) tp.clear(); 
    }

   // Vector of SearchResults for iterative methods

   public void clearSearchResults() {searchResults=new Vector(); }
   public void addSearchResult(SearchResult r) {
     searchResults.add(r); }
   public int getNumSearchResults() {
     return searchResults.size(); }
   public SearchResult getSearchResult(int i) {
     return (SearchResult)(searchResults.elementAt(i)); }


   // PRIVATE FIELDS =======================================

   private Game game;
   private Log out = null; 
   private boolean logging = false;
   private UMCS.iotools.Timer timer;
   private boolean timed=false, timing=false;
   private TTable tp=null;
   private int numevals;
   private int numpartialevals;
   private Vector searchResults =  new Vector();

  // USEFUL CONSTANTS
  public final static short MININF = (short)(-32760);
  public final static short POSINF = (short)(32760);
  public final static short NOMOVE = Short.MIN_VALUE;
}
