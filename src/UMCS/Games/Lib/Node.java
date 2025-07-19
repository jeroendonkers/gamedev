package UMCS.Games.Lib;

/**
 * Game-Tree Node<BR>
 * @author H.H.L.M. Donkers
 * @version MAY 2000
 */


public class Node
{
  /**
   * Create a new Node of type t, position s and move m.
   *
   * Type is one of Node.MAX, Node.MIN, Node.NAT.
   **/
  public Node(byte t, Position s) {
      type = t;
      pos = s;
  } 

  /**
   * Create a new Node with position s.
   **/
  public Node(Position s) {
      pos = s;
  } 

  /**
   * Retrieve the Position stored at the Node.
   **/
  public Position getPosition() {
     return pos;
  }


  /**
   * Set the type of a Node.
   *
   * Type is one of Node.MAX, Node.MIN, Node.NAT.
   **/
  public void setType(byte t) { 
     type = t; 
  }

  /**
   * Retrieve the type of a Node.
   *
   * Type is one of Node.UNKNOWN, Node.MAX, Node.MIN, Node.NAT.
   **/
   public byte getType() { 
     return type; 
  }

  /**
   * Is this a terminal node?
   *
   * The node is terminal if the position at the node is
   * an end position of the game.
   **/
  public boolean isTerminal() {
    return pos.isEnded();
  }

  public String toString() {
     String s = "";
     if (type==MAX) s = "MAX "; 
     if (type==MIN) s = "MIN "; 
     if (type==NAT) s = "NAT "; 
     s = s + pos;
     return s;
  }

  /**
   * Flip the type of a Node.  (MIN to MAX and MAX to MIN).
   *
   * Use in a game (generateNodes) to indicate a next turn.
   **/
  public void flipType() {
      if (type==MAX) type=MIN;
      else if (type==MIN) type=MAX;
      flipped=true;
  }

  /**
   * Has the type of the node been flipped?
   **/
  public boolean nextTurn() { return flipped; }

  /**
   * Node type
   **/
  public static final byte UNKNOWN = 3;

  /**
   * Node type
   **/
  public static final byte MAX = 0;

  /**
   * Node type
   **/
  public static final byte MIN = 1;

  /**
   * Node type
   **/
  public static final byte NAT = 2;

  private byte type = UNKNOWN;
  private Position pos;
  private boolean flipped = false;
}

