package UMCS.Games.Lib;

/**
 * Position Move Enumerator<BR>
 * @author H.H.L.M. Donkers
 * @version JUNE 2000
 *
 * Games should provide a class that implements this interface. 
 *
 * @see  Game#generateMoves(Position)
 */

public interface MoveEnumerator
{
  /**
   * Retrieve the first move. (Player.NOMOVE if no move is present.)
   **/
   public short getMove();

  /**
   * Retrieve the next move. (Player.NOMOVE if no move is left.)
   **/
   public short nextMove();
}


