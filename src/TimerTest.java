import UMCS.iotools.*;

class TimerTest implements TimerAlarm {

public static void main(String[] args) {
  new TimerTest().run();

}

   public void run() {
      long t=0;
      System.out.println("starting");
      b = false;
      t1 = new Timer(this,2000,"Timer 1");
      t2 = new Timer(this,5000,"Timer 2");
      t1.start();
      t2.start();
      do {
         if (b) System.out.println(".");
      } while (!b);

   }


   public void alarm(boolean interrupted) {
      String s = " has elapsed";
      if (interrupted) s=" is interrupted ";
      System.out.println(Thread.currentThread().getName()+s);
      if (!b) { b=true; t2.interrupt(); }
      b=true;
   }


   private boolean b;
   Timer t1, t2;
}
