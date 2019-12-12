package com.hp.ilo2.remcons;

import java.util.Date;

class Timer implements Runnable {
  enum State {
    INIT, RUNNING, PAUSED, STOPPED
  }
  private State state = State.INIT;

  private static final int POLL_PERIOD = 50;

  private int timeout_count;
  private int timeout_max;
  private boolean one_shot;
  private long start_time_millis;
  private long stop_time_millis;

  private TimerListener callback;

  private Object callbackInfo;

  private final Object mutex;

  public Timer(int timeoutMax, boolean isOneShot, Object mutex) {
    this.timeout_max = timeoutMax;
    this.one_shot = isOneShot;
    this.mutex = mutex;
  }

  public void setListener(TimerListener listener, Object callbackInfo) {
    synchronized (this.mutex) {
      this.callback = listener;
      this.callbackInfo = callbackInfo;
    }
  }

  public void start() {
    synchronized (this.mutex) {
      switch (this.state) {
        case INIT:
          this.state = State.RUNNING;
          this.timeout_count = 0;
          new Thread(this).start();
          break;
        case RUNNING:
          this.timeout_count = 0;
          break;
        case PAUSED:
          this.timeout_count = 0;
          this.state = State.RUNNING;
          break;
        case STOPPED:
          this.timeout_count = 0;
          this.state = State.RUNNING;
      }
    }
  }

  public void stop() {
    synchronized (this.mutex) {
      if (this.state != State.INIT) {
        this.state = State.STOPPED;
      }
    }
  }

  public void pause() {
    synchronized (this.mutex) {
      if (this.state == State.RUNNING) {
        this.state = State.PAUSED;
      }
    }
  }

  public void cont() {
    synchronized (this.mutex) {
      if (this.state == State.PAUSED) {
        this.state = State.RUNNING;
      }
    }
  }

  public void run() {
    do {
      Date date = new Date();
      this.start_time_millis = date.getTime();
      try {
        Thread.sleep(POLL_PERIOD);
      } catch (InterruptedException ignored) {
      }

      date = new Date();
      this.stop_time_millis = date.getTime();
    } while (process_state());
  }

  private boolean process_state() {
    boolean shouldStop = true;

    synchronized (this.mutex) {
      switch (this.state)
      {
      case INIT:
        break;
      case PAUSED:
        if (this.stop_time_millis > this.start_time_millis) {
          this.timeout_count = ((int)(this.timeout_count + (this.stop_time_millis - this.start_time_millis)));
        }
        else
          this.timeout_count += 50;
        if (this.timeout_count >= this.timeout_max)
        {
          if (this.callback != null) {
            this.callback.timeout(this.callbackInfo);
          }
          if (this.one_shot) {
            this.state = State.INIT;
            shouldStop = false;
          }
          else {
            this.timeout_count = 0;
          }
        }

        break;
      case RUNNING:
        break;
      case STOPPED:
        this.state = State.INIT;
        shouldStop = false;
      }

    }
    return shouldStop;
  }
}