package com.hp.ilo2.remcons;

import java.util.Date;







class Timer
  implements Runnable
{
  private static final int STATE_INIT = 0;
  private static final int STATE_RUNNING = 1;
  private static final int STATE_PAUSED = 2;
  private static final int STATE_STOPPED = 3;
  private int state = 0;

  private static final int POLL_PERIOD = 50;

  private int timeout_count;
  private int timeout_max;
  private boolean one_shot;
  private long start_time_millis;
  private long stop_time_millis;
  private Date date = new Date();





  private TimerListener callback;





  private Object callback_info;





  private Object mutex;





  public Timer(int paramInt, boolean paramBoolean, Object paramObject)
  {
    this.timeout_max = paramInt;
    this.one_shot = paramBoolean;
    this.mutex = paramObject;
  }







  public void setListener(TimerListener paramTimerListener, Object paramObject)
  {
    synchronized (this.mutex) {
      this.callback = paramTimerListener;
      this.callback_info = paramObject;
    }
  }





  public void start()
  {
    synchronized (this.mutex) {
      switch (this.state) {
      case 0:
        this.state = 1;
        this.timeout_count = 0;
        new Thread(this).start();
        break;

      case 1:
        this.timeout_count = 0;
        break;

      case 2:
        this.timeout_count = 0;
        this.state = 1;
        break;

      case 3:
        this.timeout_count = 0;
        this.state = 1;
      }

    }
  }





  public void stop()
  {
    synchronized (this.mutex) {
      if (this.state != 0) {
        this.state = 3;
      }
    }
  }




  public void pause()
  {
    synchronized (this.mutex) {
      if (this.state == 1) {
        this.state = 2;
      }
    }
  }






  public void cont()
  {
    synchronized (this.mutex) {
      if (this.state == 2) {
        this.state = 1;
      }
    }
  }








  public void run()
  {
    for (;;)
    {
      this.date = new Date();
      this.start_time_millis = this.date.getTime();
      try {
        Thread.sleep(50L);
      }
      catch (InterruptedException localInterruptedException) {}

      this.date = new Date();
      this.stop_time_millis = this.date.getTime();
      if (!process_state()) {
        break;
      }
    }
  }









  private boolean process_state()
  {
    boolean bool = true;

    synchronized (this.mutex) {
      switch (this.state)
      {
      case 0:
        break;
      case 1:
        if (this.stop_time_millis > this.start_time_millis) {
          this.timeout_count = ((int)(this.timeout_count + (this.stop_time_millis - this.start_time_millis)));
        }
        else
          this.timeout_count += 50;
        if (this.timeout_count >= this.timeout_max)
        {
          if (this.callback != null) {
            this.callback.timeout(this.callback_info);
          }
          if (this.one_shot) {
            this.state = 0;
            bool = false;
          }
          else {
            this.timeout_count = 0;
          }
        }


        break;
      case 2:
        break;
      case 3:
        this.state = 0;
        bool = false;
      }

    }
    return bool;
  }
}


/* Location:              C:\Users\anton\Documents\ILO2\rc175p10.jar!\com\hp\ilo2\remcons\Timer.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */