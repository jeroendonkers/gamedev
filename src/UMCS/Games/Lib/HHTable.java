package UMCS.Games.Lib;

/**
 * Histrory-Heuristic Table interface
 **/

public interface HHTable {
    

   /**
    * Clear the whole table
    **/
   public void clear();


   /**
    * Prepare the table for the next search
    **/
   public void refresh();


   /**
    * Add a best move found at the given depth
    **/
   public void addMove(short move, int depth);


   /**
    * Retrieve the score of a move
    **/
   public long getMoveScore(short move);

}
