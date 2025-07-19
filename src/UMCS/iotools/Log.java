package UMCS.iotools;
import java.io.*;

public class Log extends PrintWriter {
   public Log() { super(System.out,true); }

   public Log(OutputStream o) { super(o,true); }

   public Log(String s) throws IOException {  super(new FileOutputStream(s),true); }

   public static Log createLog(String s) {
      try { return new Log(s); } 
      catch (Exception e) {
         return new Log();
      }
   }

   public static Log createLog() {
      return new Log();
   }  

   public static Log createLog(OutputStream o) {
      return new Log(o);
   }  
   
   public void log(String s) {
      if (dolog) println(s);
   }

   public void startLog() { dolog=true; }
   public void stopLog() { dolog=false; }

   private boolean dolog = false;
}
