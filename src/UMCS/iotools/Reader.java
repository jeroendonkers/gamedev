package UMCS.iotools;
import java.util.Vector;
import java.util.StringTokenizer;
import java.io.*;

public class Reader {

  public Reader() {
  }

  public String getError() { return error+" at "+linenumber; }

  public void setError(String s) { error = s; }

  public boolean openFile(String filename) {
   linenumber=-1;
   try {
     in = new BufferedReader(new FileReader(filename));
     buffer=in.readLine();
   } catch (Exception e) { 
      System.out.println("open error reader: "+e);
      return false; 
   }
   if (buffer==null) return false;
   st = new StringTokenizer(buffer);
   linenumber=0;
   return true;
  }

  public boolean nextToken() {
    while (checkNextLine()) {
       token=st.nextToken();
       if (token.charAt(0)!='#') return true;
       if (!nextLine()) return false;
    }
    return false;
  } 

  public boolean checkNextLine() {
     while (!st.hasMoreTokens()) {
        if (!nextLine()) return false;
     } 
     return true;
  }

  public boolean nextLine() {
      try {
        buffer = in.readLine();
      } catch (Exception e) { return false; }
      linenumber++;
      if (buffer==null) return false;
      st = new StringTokenizer(buffer);
      return true;
  }

  public boolean nextInt() {
    if (!nextToken()) return false;
    try {
       iToken = Integer.valueOf(token).intValue();
    } catch (Exception e) { return false; }
    return true;
  }  


  public boolean nextLong() {
    if (!nextToken()) return false;
    try {
       lToken = Long.valueOf(token).longValue();
    } catch (Exception e) { return false; }
    return true;
  }  


  public boolean nextDouble() {
    if (!nextToken()) return false;
    try {
       dToken = Double.valueOf(token).doubleValue();
    } catch (Exception e) { return false; }
    return true;
  }  


   public boolean command(String c) {
      if (!nextToken()) return false;
      if (!token.equals(c)) return false;
      return true;
   }

  public void closeFile() {
    try { in.close(); } catch (Exception e) {}
  }

  public void abort(String s) throws Exception {
    throw new Exception(s);
  }

  public String token;
  public int iToken;
  public long lToken;
  public double dToken;

  private int linenumber;
  private String error;

  private String buffer;
  private StringTokenizer st;
  private BufferedReader in;
}
