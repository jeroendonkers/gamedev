package UMCS.Games.Lib;

/**
 * Position <P>
 * This interface is the base for positions in all games. The 
 * exact representation of a game's position is the responsability
 * of a game. Usually, the Position interface is implemented by
 * a inner class of a game.
 *
 * @author H.H.L.M. Donkers
 * @version SEPTEMBER 2000
 */

public interface Position 
{

/**
 * Return whether a position represents an end position in the game.
 */
  public boolean isEnded();

/**
 * Give the hashkey of this position.
 */
  public long getHashValue();


/**
 * Clone this position (deep copy).
 */
  public Position clonePosition();


}

