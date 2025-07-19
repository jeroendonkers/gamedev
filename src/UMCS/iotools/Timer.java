package UMCS.iotools;

public class Timer extends Thread {

  public Timer(TimerAlarm a, long time, String name) {
    super(name);
    timeralarm=a;
    waittime = time;
  }

  public void run() {
     running=true;
     starttime = System.currentTimeMillis();
     try { sleep(waittime); }
     catch  (InterruptedException e) {
         elapsedtime = System.currentTimeMillis() - starttime;
         running=false;
         timeralarm.alarm(true);
         return;
     }
     elapsedtime = waittime;
     running=false;
     timeralarm.alarm(false); 
  }

  public long getElapsedTime() { 
    if (running) return System.currentTimeMillis() - starttime;
    else return elapsedtime; 
  } 

  long waittime, starttime;
  long elapsedtime = 0;
  boolean running = false;
  TimerAlarm timeralarm;

}
