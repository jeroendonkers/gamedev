package UMCS.Games.Lib;

/**
 * Game <P>
 * The Game interface is the core skeleton of every game. <BR>
 * The players only have to use these methods in order to be able to
 * play a game. 
 *
 * @author H.H.L.M. Donkers
 * @version JUNE 2000
 */


public interface Game
{

 /**
  * Return the start position of the game.
  * Generate a mew instance preferably.<BR>
  */
   Position getStartPosition();


 /**
  * Return an enumerator of moves containing legal moves from
  * a given position.<BR>
  */
   MoveEnumerator generateMoves(Position p);


 /**
  * Return an enumerator of moves containing legal moves from
  * a given position, using History-heuristic table.<BR>
  */
   MoveEnumerator generateMoves(Position p, HHTable hht);


 /**
  * Checks if a given move is legal in the given position.<BR>
  * (used in killer moves and other heuristics)
  */
   public boolean isLegalMove(short move, Position p);

/**
 * Perform a move and produce a new node. <BR>
 */
  public Node doMove(Node n, short move);

/**
 * Undo a move that led to the position in the node. <BR>
 */
  public void undoMove(Node n, short move);





/**
 * Present a string representation of a move. <BR>
 */
  public String moveString(short move);

 /**
  * Return a default move evaluator for the game.<BR>
  */
   Evaluator getDefaultEvaluator();

 /**
  * Return whether a searchvalue is a real value (not heuristic).<BR>
  **/
  public boolean isRealValue(short value);

 /**
  * Get a new history-heuristic table.<BR>
  **/
  public HHTable getNewHHTable();

}
