package UMCS.Games.Loa;
import  UMCS.Games.Lib.*; 
import  UMCS.iotools.*; 

public class LoaNet {
 
  public LoaNet(String s, double[] input) {
    LoaNetData d;
    try {
       Class c = getClass().getClassLoader().loadClass("UMCS.Games.Loa."+s);
       d = (LoaNetData)(c.newInstance());
    } catch (Exception e) { 
       System.out.println("Cannot load Loa Net "+s+"\n"+e); 
       return; 
    }
    System.out.println("Loaded Loa Net "+s+": "+d.description()); 
    M=d.getM(); 
    N=d.getN();
    x = input;
    h = new double[N];
    w_hx = d.getWHX(); 
    w_yh = d.getWYH(); 
    w_yx = d.getWYX();
  }


 public void setInput(double[] input) { x=input; }

 public double Response(){
  int j,k;

  y = (double)(0.0);
  for (j = 0; j < N; j++) h[j] = 0;

  for (k=0; k<M; k++) if (x[k] != 0.0) {
    for (j = 0; j < N; j++) 
      h[j] += w_hx[j*M+k] * x[k];
    y += w_yx[k] * x[k];
  }

  for (j = 0; j < N; j++) {
    h[j] = 2.0/(1.0+Math.exp(-h[j])) - 1.0;     /* tanh */
    y += w_yh[j] * h[j];
  }
  
  /* this step is not absolutely necessary */
  y = 2.0/(1.0+Math.exp(-y)) - 1.0;
  return y;   
} 

 static final double black  = (double)(1.0);
 static final double white = (double)(-1.0); 
 static final double empty =  (double)(0.0);   

 static final double[] startboard =
   {empty, black, black, black, black, black, black, empty, 
    white, empty, empty, empty, empty, empty, empty, white, 
    white, empty, empty, empty, empty, empty, empty, white, 
    white, empty, empty, empty, empty, empty, empty, white, 
    white, empty, empty, empty, empty, empty, empty, white, 
    white, empty, empty, empty, empty, empty, empty, white, 
    white, empty, empty, empty, empty, empty, empty, white, 
    empty, black, black, black, black, black, black, empty,
    black  /* side to move */
  };
 
  public static void main(String[] args){
    LoaNet L = new LoaNet(args[0],startboard);
    if (L.M==0 || L.N == 0) return;
    L.Response();
    System.out.println("minimax estimation for start board: "+L.y);
  }

  double y;
  double[] x, h;
  double[] w_hx, w_yh, w_yx;

  int M=0;
  int N=0;
  

}


