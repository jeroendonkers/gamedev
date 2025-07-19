package UMCS.Games.Lib;

public class MoveSet extends java.util.Vector implements MoveEnumerator
{
   
   public void reset() {
    act = 0;
   } 
  

   public void addMove(short move) {
      addElement(new Short(move));
   }

   public short getMove()
   {
      if (act<size()) return ((Short)elementAt(act)).shortValue(); else return Player.NOMOVE;
   }

   public short nextMove()
   {
      act++;
      return getMove();
   }

   int act = 0;
 
}
