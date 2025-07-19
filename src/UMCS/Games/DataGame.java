package UMCS.Games;
import  UMCS.Games.Lib.*; 
import  UMCS.iotools.*; 
import  java.util.*; 

/**
 * DATA GAME
 *
 * A game tree that is read from a data file.
 *
 * @author Jeroen Donkers
 * @version OCTOBER 2000
 **/

public class DataGame extends AbstractGame implements Game 
{
   public DataGame(String s) { 
        datafile=s; 
        readGame();
   }
   public String toString() { return "Datagame("+datafile+")"; }
   public String moveString(short m) { return ""+m; }
   public Position getStartPosition()  { return node(0); }
   public Evaluator getDefaultEvaluator() { return new DEvaluator(); }
   public MoveEnumerator generateMoves(Position p) {
      MoveSet ms = new MoveSet();
      DPos dp = (DPos)p;
      if (dp.terminal || dp.first<0) return ms;
      dp = node(dp.first);
      do {
        ms.addMove((short)dp.id);
        dp=node(dp.next);
      } while (dp!=null);
      return ms;
   }
   public boolean isRealValue(short value) { return false; }
   public Node doMove(Node m, short move) {
      count++;
      Node nm=new Node(findnode(move));
      nm.setType(m.getType()); nm.flipType();
      return nm; 
   }

   private class DEvaluator implements Evaluator {
     DEvaluator() {}
     public short evaluate(Node m, int depth) { 
        return ((DPos)(m.getPosition())).value;
     }
  }

  private class DPos implements Position {
     DPos(long d, short val, boolean t) {
        id=d; value=val; terminal=t; 
     }
     public String toString() { return ""+id; }
     
     public boolean isEnded() { return terminal; }
     
     public long getHashValue() { return 0x1fffffffffff0000L+id; }
     
     public Position clonePosition() {
     	DPos p = new DPos(id,value,terminal);
     	return p;
     }
     
     long id;
     short value;
     boolean terminal;  
     int first = -1;
     int next = -1;
  }

  private void readGame() {
    Reader in = new Reader();
    if (!in.openFile(datafile)) return;
    in.nextToken(); 
    while (in.token.equals("node")) {
      if (!in.nextLong()) { System.out.println("ID expected"); break; }
      long id = in.lToken;
      if (!in.command("value")) { System.out.println("'value' expected"); break; }
      if (!in.nextInt()) { System.out.println("value expected"); break; }
      short v = (short)(in.iToken);
      boolean t=false; 
      DPos p = new DPos(id,v,t);
      tree.add(p);
      in.nextToken(); 
      if (in.token.equals("first")) {
         if (!in.nextInt()) { System.out.println("first id expected"); break; }
         p.first = in.iToken;
         in.nextToken(); 
      } else p.terminal=true;
      if (in.token.equals("next")) {
        if (!in.nextInt()) { System.out.println("next id expected"); break; }
        p.next = in.iToken;
        in.nextToken(); 
      }
    }
    in.closeFile();

    // postprocess
    if (tree.size()==0) return;

    id = new long[tree.size()];
    for (int i=0; i<tree.size(); i++) id[i]=node(i).id;
    for (int i=0; i<tree.size(); i++) {
      DPos p = node(i);
      if (p.first>=0) for (int j=0; j<tree.size(); j++) 
              if (id[j]==p.first) { p.first=j; break; }
      if (p.next>=0) for (int j=0; j<tree.size(); j++) 
              if (id[j]==p.next) { p.next=j; break; }
    }
    System.out.println(tree.size()+" nodes read from "+datafile);
  }

  private DPos node(int i) {
     if (i<0) return null;
     if (i>=tree.size()) return null;
     return (DPos)(tree.elementAt(i));
  }

  private DPos findnode(int i) {
     for (int j=0; j<tree.size(); j++) if (id[j]==i) return node(j);
     return null;
  }

  public long getCount() { return count; }

  private String datafile;
  private Vector tree = new Vector();
  private long count=0;
  private long[] id;
}
