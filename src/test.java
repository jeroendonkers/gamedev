import UMCS.Games.Lib.*;
import java.util.Random; 


public class test {

 public static void main(String[] args) {
    Random rand = new Random();
    long l = rand.nextLong();
    System.out.println(Long.toHexString(l));

    int bits = 20;
    long  n = 1L << (bits-1);
    long  m = 1L << (63-bits);
    long mask = n*2-1;
    long keymask1 = (long)-1 ^ mask;
    long keymask2 = m*2-1;
    System.out.println("mask="+Long.toHexString(mask));
    System.out.println("keymask1="+Long.toHexString(keymask1));
    System.out.println("keymask2="+Long.toHexString(keymask2));


    int index = (int)(l & mask); 
    System.out.println("index="+Integer.toHexString(index));
    long key = ((l & keymask1) >> bits) & keymask2;
    System.out.println("key="+Long.toHexString(key));

     short a =  (short)(key);
     short b = (short)((key >> 16) & 0xffff);
     short c = (short)((key >> 32) & 0xffff);
    
    System.out.println("a="+Integer.toHexString(a));
    System.out.println("b="+Integer.toHexString(b));
    System.out.println("c="+Integer.toHexString(c));

 }

}
