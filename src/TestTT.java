import UMCS.Games.Lib.*;
import java.util.Random; 


public class TestTT {

 public static void main(String[] args) {

   int N=1000, M=100;
   SimpleTT s = new SimpleTT(20, TTable.RS_NEW);
   Random rand = new Random();
   long[] l = new long[M];
   short[] move = new short[M];
   short[] score = new short[M];
   byte[] flag = new byte[M];
   byte[] ply = new byte[M];

   for (int i=0; i<N; i++) {
      for (int j=0; j<M; j++) {
        move[j] = (short)(rand.nextLong());
        score[j] = (short)(rand.nextLong());
        flag[j] = (byte)(rand.nextLong());
        ply[j] = (byte)(rand.nextLong());
        l[j]  = rand.nextLong();
        s.putEntry(l[j],move[j],score[j],flag[j],ply[j]);
      }
      for (int j=0; j<M; j++) {   
         int idx = s.getEntry(l[j]); 
         if (idx>=0) {
           if (move[j]!=s.getMove(idx)) { System.out.println("move="+move[j]+"<>"+s.getMove(idx)); break; }
           if (score[j]!=s.getScore(idx)) { System.out.println("score="+score[j]+"<>"+s.getScore(idx)); break; }
           if (flag[j]!=s.getFlag(idx)) { System.out.println("flag="+flag[j]+"<>"+s.getFlag(idx)); break; }
           if (ply[j]!=s.getPly(idx)) { System.out.println("ply="+ply[j]+"<>"+s.getPly(idx)); break; }
         }
      }
    }


 }

}
