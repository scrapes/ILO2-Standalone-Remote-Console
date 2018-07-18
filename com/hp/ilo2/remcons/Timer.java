/*     */ package com.hp.ilo2.remcons;
/*     */ 
/*     */ import java.util.Date;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class Timer
/*     */   implements Runnable
/*     */ {
/*     */   private static final int STATE_INIT = 0;
/*     */   private static final int STATE_RUNNING = 1;
/*     */   private static final int STATE_PAUSED = 2;
/*     */   private static final int STATE_STOPPED = 3;
/*  18 */   private int state = 0;
/*     */   
/*     */   private static final int POLL_PERIOD = 50;
/*     */   
/*     */   private int timeout_count;
/*     */   private int timeout_max;
/*     */   private boolean one_shot;
/*     */   private long start_time_millis;
/*     */   private long stop_time_millis;
/*  27 */   private Date date = new Date();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private TimerListener callback;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Object callback_info;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Object mutex;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Timer(int paramInt, boolean paramBoolean, Object paramObject)
/*     */   {
/*  53 */     this.timeout_max = paramInt;
/*  54 */     this.one_shot = paramBoolean;
/*  55 */     this.mutex = paramObject;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setListener(TimerListener paramTimerListener, Object paramObject)
/*     */   {
/*  66 */     synchronized (this.mutex) {
/*  67 */       this.callback = paramTimerListener;
/*  68 */       this.callback_info = paramObject;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void start()
/*     */   {
/*  78 */     synchronized (this.mutex) {
/*  79 */       switch (this.state) {
/*     */       case 0: 
/*  81 */         this.state = 1;
/*  82 */         this.timeout_count = 0;
/*  83 */         new Thread(this).start();
/*  84 */         break;
/*     */       
/*     */       case 1: 
/*  87 */         this.timeout_count = 0;
/*  88 */         break;
/*     */       
/*     */       case 2: 
/*  91 */         this.timeout_count = 0;
/*  92 */         this.state = 1;
/*  93 */         break;
/*     */       
/*     */       case 3: 
/*  96 */         this.timeout_count = 0;
/*  97 */         this.state = 1;
/*     */       }
/*     */       
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void stop()
/*     */   {
/* 109 */     synchronized (this.mutex) {
/* 110 */       if (this.state != 0) {
/* 111 */         this.state = 3;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void pause()
/*     */   {
/* 121 */     synchronized (this.mutex) {
/* 122 */       if (this.state == 1) {
/* 123 */         this.state = 2;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void cont()
/*     */   {
/* 135 */     synchronized (this.mutex) {
/* 136 */       if (this.state == 2) {
/* 137 */         this.state = 1;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void run()
/*     */   {
/*     */     for (;;)
/*     */     {
/* 153 */       this.date = new Date();
/* 154 */       this.start_time_millis = this.date.getTime();
/*     */       try {
/* 156 */         Thread.sleep(50L);
/*     */       }
/*     */       catch (InterruptedException localInterruptedException) {}
/*     */       
/* 160 */       this.date = new Date();
/* 161 */       this.stop_time_millis = this.date.getTime();
/* 162 */       if (!process_state()) {
/*     */         break;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean process_state()
/*     */   {
/* 178 */     boolean bool = true;
/*     */     
/* 180 */     synchronized (this.mutex) {
/* 181 */       switch (this.state)
/*     */       {
/*     */       case 0: 
/*     */         break;
/*     */       case 1: 
/* 186 */         if (this.stop_time_millis > this.start_time_millis) {
/* 187 */           this.timeout_count = ((int)(this.timeout_count + (this.stop_time_millis - this.start_time_millis)));
/*     */         }
/*     */         else
/* 190 */           this.timeout_count += 50;
/* 191 */         if (this.timeout_count >= this.timeout_max)
/*     */         {
/* 193 */           if (this.callback != null) {
/* 194 */             this.callback.timeout(this.callback_info);
/*     */           }
/* 196 */           if (this.one_shot) {
/* 197 */             this.state = 0;
/* 198 */             bool = false;
/*     */           }
/*     */           else {
/* 201 */             this.timeout_count = 0;
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */         break;
/*     */       case 2: 
/*     */         break;
/*     */       case 3: 
/* 210 */         this.state = 0;
/* 211 */         bool = false;
/*     */       }
/*     */       
/*     */     }
/* 215 */     return bool;
/*     */   }
/*     */ }


/* Location:              C:\Users\anton\Documents\ILO2\rc175p10.jar!\com\hp\ilo2\remcons\Timer.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */