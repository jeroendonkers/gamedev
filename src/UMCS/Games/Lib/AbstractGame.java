package UMCS.Games.Lib;

/**
 * AbstractGame <P>
 *
 * @author H.H.L.M. Donkers
 * @version MARCH 2001
 */


public abstract class AbstractGame implements Game
{
  abstract public Position getStartPosition();
  abstract public MoveEnumerator generateMoves(Position p);
  abstract public Evaluator getDefaultEvaluator();

  public Node doMove(Node n, short move) { return n; };
  public void undoMove(Node n, short move) { };
  public String moveString(short move) { return ""+move; }
  public boolean isRealValue(short value) { return false; }
  public boolean isLegalMove(short move, Position p) { return false; }

  public HHTable getNewHHTable() { return null; }
  public MoveEnumerator generateMoves(Position p, HHTable hht)
    { return generateMoves(p); }

}
