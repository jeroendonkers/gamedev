package UMCS.iotools;

import java.io.*;

public class Console {

   public static String input(String prompt) {
     System.out.print(prompt);
     String s = "";
     try {
       s = in.readLine(); 
     } catch (Exception e) {};
     return s;
   }

   static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
}
