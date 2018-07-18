/*      */ package com.hp.ilo2.remcons;
/*      */ 
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.event.MouseListener;
/*      */ import java.awt.event.MouseMotionListener;
/*      */ import java.io.PrintStream;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class MouseSync
/*      */   implements MouseListener, MouseMotionListener, TimerListener
/*      */ {
/*      */   private static final int CMD_START = 0;
/*      */   private static final int CMD_STOP = 1;
/*      */   private static final int CMD_SYNC = 2;
/*      */   private static final int CMD_SERVER_MOVE = 3;
/*      */   private static final int CMD_SERVER_SCREEN = 4;
/*      */   private static final int CMD_SERVER_DISABLE = 5;
/*      */   private static final int CMD_TIMEOUT = 6;
/*      */   private static final int CMD_CLICK = 7;
/*      */   private static final int CMD_ENTER = 8;
/*      */   private static final int CMD_EXIT = 9;
/*      */   private static final int CMD_PRESS = 10;
/*      */   private static final int CMD_RELEASE = 11;
/*      */   private static final int CMD_DRAG = 12;
/*      */   private static final int CMD_MOVE = 13;
/*      */   private static final int CMD_ALIGN = 14;
/*      */   private static final int STATE_INIT = 0;
/*      */   private static final int STATE_SYNC = 1;
/*      */   private static final int STATE_ENABLE = 2;
/*      */   private static final int STATE_DISABLE = 3;
/*      */   private int state;
/*      */   private MouseSyncListener listener;
/*      */   private int server_w;
/*      */   private int server_h;
/*      */   private int server_x;
/*      */   private int server_y;
/*      */   private int client_x;
/*      */   private int client_y;
/*      */   private int client_dx;
/*      */   private int client_dy;
/*      */   private int[] send_dx;
/*      */   private int[] send_dy;
/*      */   private int[] recv_dx;
/*      */   private int[] recv_dy;
/*      */   private int send_dx_index;
/*      */   private int send_dy_index;
/*      */   private static final int SYNC_SUCCESS_COUNT = 2;
/*      */   private static final int SYNC_FAIL_COUNT = 4;
/*      */   private int send_dx_count;
/*      */   private int send_dy_count;
/*      */   private int send_dx_success;
/*      */   private int send_dy_success;
/*      */   private boolean sync_successful;
/*      */   private static final int TIMEOUT_DELAY = 5;
/*      */   private static final int TIMEOUT_MOVE = 200;
/*      */   private static final int TIMEOUT_SYNC = 2000;
/*      */   private Timer timer;
/*      */   public static final int MOUSE_BUTTON_LEFT = 4;
/*      */   public static final int MOUSE_BUTTON_CENTER = 2;
/*      */   public static final int MOUSE_BUTTON_RIGHT = 1;
/*      */   private int pressed_button;
/*      */   private boolean dragging;
/*      */   private Object mutex;
/*   87 */   private boolean debug_msg = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public MouseSync(Object paramObject)
/*      */   {
/*  104 */     this.mutex = paramObject;
/*  105 */     this.state = 0;
/*  106 */     state_machine(0, null, 0, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setListener(MouseSyncListener paramMouseSyncListener)
/*      */   {
/*  116 */     this.listener = paramMouseSyncListener;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void enableDebug()
/*      */   {
/*  124 */     this.debug_msg = true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void disableDebug()
/*      */   {
/*  132 */     this.debug_msg = false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void restart()
/*      */   {
/*  140 */     go_state(0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void align()
/*      */   {
/*  148 */     state_machine(14, null, 0, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void sync()
/*      */   {
/*  156 */     state_machine(2, null, 0, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void serverMoved(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  167 */     state_machine(3, null, paramInt1, paramInt2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void serverScreen(int paramInt1, int paramInt2)
/*      */   {
/*  178 */     state_machine(4, null, paramInt1, paramInt2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void serverDisabled()
/*      */   {
/*  186 */     state_machine(5, null, 0, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void timeout(Object paramObject)
/*      */   {
/*  199 */     state_machine(6, null, 0, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void mouseClicked(MouseEvent paramMouseEvent)
/*      */   {
/*  207 */     state_machine(7, paramMouseEvent, 0, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void mouseEntered(MouseEvent paramMouseEvent)
/*      */   {
/*  215 */     //state_machine(8, paramMouseEvent, 0, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void mouseExited(MouseEvent paramMouseEvent)
/*      */   {
/*  223 */     state_machine(9, paramMouseEvent, 0, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void mousePressed(MouseEvent paramMouseEvent)
/*      */   {
/*  231 */     state_machine(10, paramMouseEvent, 0, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void mouseReleased(MouseEvent paramMouseEvent)
/*      */   {
/*  239 */     state_machine(11, paramMouseEvent, 0, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void mouseDragged(MouseEvent paramMouseEvent)
/*      */   {
/*  247 */     state_machine(12, paramMouseEvent, 0, 0);
/*  248 */     move_delay();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void mouseMoved(MouseEvent paramMouseEvent)
/*      */   {
/*  256 */     state_machine(13, paramMouseEvent, 0, 0);
/*  257 */     move_delay();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void move_delay()
/*      */   {
/*      */     try
/*      */     {
/*  272 */       Thread.sleep(5L);
/*      */     }
/*      */     catch (InterruptedException localInterruptedException) {}
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void sync_default()
/*      */   {
/*  284 */     int[] arrayOfInt = { 1, 4, 6, 8, 12, 16, 32, 64 };
/*      */     
/*  286 */     this.send_dx = new int[arrayOfInt.length];
/*  287 */     this.send_dy = new int[arrayOfInt.length];
/*  288 */     this.recv_dx = new int[arrayOfInt.length];
/*  289 */     this.recv_dy = new int[arrayOfInt.length];
/*      */     
/*  291 */     for (int i = 0; i < arrayOfInt.length; i++) {
/*  292 */       this.send_dx[i] = arrayOfInt[i];
/*  293 */       this.send_dy[i] = arrayOfInt[i];
/*  294 */       this.recv_dx[i] = arrayOfInt[i];
/*  295 */       this.recv_dy[i] = arrayOfInt[i];
/*      */     }
/*      */     
/*  298 */     this.send_dx_index = 0;
/*  299 */     this.send_dy_index = 0;
/*      */     
/*  301 */     this.send_dx_count = 0;
/*  302 */     this.send_dy_count = 0;
/*  303 */     this.send_dx_success = 0;
/*  304 */     this.send_dy_success = 0;
/*  305 */     this.sync_successful = false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void sync_continue()
/*      */   {
/*  313 */     int i = 1;
/*  314 */     int j = 1;
/*  315 */     int k = 0;
/*  316 */     int m = 0;
/*      */     
/*      */ 
/*  319 */     if (this.server_x > this.server_w / 2) {
/*  320 */       i = -1;
/*      */     }
/*  322 */     if (this.server_y < this.server_h / 2) {
/*  323 */       j = -1;
/*      */     }
/*      */     
/*  326 */     if (this.send_dx_index >= 0) {
/*  327 */       k = i * this.send_dx[this.send_dx_index];
/*      */     }
/*  329 */     if (this.send_dy_index >= 0) {
/*  330 */       m = j * this.send_dy[this.send_dy_index];
/*      */     }
/*  332 */     this.listener.serverMove(k, m, this.client_x, this.client_y);
/*  333 */     this.timer.start();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void sync_update(int paramInt1, int paramInt2)
/*      */   {
/*  348 */     this.timer.pause();
/*      */     
/*      */ 
/*  351 */     int i = paramInt1 - this.server_x;
/*  352 */     int j = this.server_y - paramInt2;
/*      */     
/*  354 */     this.server_x = paramInt1;
/*  355 */     this.server_y = paramInt2;
/*      */     
/*  357 */     if (i < 0) {
/*  358 */       i = -i;
/*      */     }
/*  360 */     if (j < 0) {
/*  361 */       j = -j;
/*      */     }
/*      */     
/*      */ 
/*  365 */     if (this.send_dx_index >= 0) {
/*  366 */       if (this.recv_dx[this.send_dx_index] == i)
/*      */       {
/*  368 */         this.send_dx_success += 1;
/*      */       }
/*  370 */       this.recv_dx[this.send_dx_index] = i;
/*  371 */       this.send_dx_count += 1;
/*  372 */       if (this.send_dx_success >= 2)
/*      */       {
/*  374 */         this.send_dx_index -= 1;
/*  375 */         this.send_dx_success = 0;
/*  376 */         this.send_dx_count = 0;
/*      */       }
/*  378 */       else if (this.send_dx_count >= 4)
/*      */       {
/*  380 */         if (this.debug_msg) {
/*  381 */           System.out.println("no x sync:" + this.send_dx[this.send_dx_index]);
/*      */         }
/*  383 */         go_state(2);
/*  384 */         return;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  389 */     if (this.send_dy_index >= 0) {
/*  390 */       if (this.recv_dy[this.send_dy_index] == j)
/*      */       {
/*  392 */         this.send_dy_success += 1;
/*      */       }
/*  394 */       this.recv_dy[this.send_dy_index] = j;
/*  395 */       this.send_dy_count += 1;
/*  396 */       if (this.send_dy_success >= 2)
/*      */       {
/*  398 */         this.send_dy_index -= 1;
/*  399 */         this.send_dy_success = 0;
/*  400 */         this.send_dy_count = 0;
/*      */       }
/*  402 */       else if (this.send_dy_count >= 4)
/*      */       {
/*  404 */         if (this.debug_msg) {
/*  405 */           System.out.println("no y sync:" + this.send_dy[this.send_dy_index]);
/*      */         }
/*  407 */         go_state(2);
/*  408 */         return;
/*      */       }
/*      */     }
/*  411 */     if ((this.send_dx_index < 0) && (this.send_dy_index < 0))
/*      */     {
/*  413 */       for (int k = this.send_dx.length - 1; k >= 0; k--) {
/*  414 */         if ((this.recv_dx[k] == 0) || (this.recv_dy[k] == 0))
/*      */         {
/*  416 */           if (this.debug_msg) {
/*  417 */             System.out.println("no movement:" + this.send_dx[k]);
/*      */           }
/*  419 */           go_state(2);
/*  420 */           return;
/*      */         }
/*  422 */         if ((k != 0) && (
/*  423 */           (this.recv_dx[k] < this.recv_dx[(k - 1)]) || (this.recv_dy[k] < this.recv_dy[(k - 1)])))
/*      */         {
/*  425 */           if (this.debug_msg) {
/*  426 */             System.out.println("not linear:" + this.send_dx[k]);
/*      */           }
/*  428 */           go_state(2);
/*  429 */           return;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  434 */       this.sync_successful = true;
/*  435 */       this.send_dx_index = 0;
/*  436 */       this.send_dy_index = 0;
/*  437 */       go_state(2);
/*      */     }
/*      */     else
/*      */     {
/*  441 */       sync_continue();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void init_vars()
/*      */   {
/*  450 */     this.server_w = 640;
/*  451 */     this.server_h = 480;
/*  452 */     this.server_x = 0;
/*  453 */     this.server_y = 0;
/*  454 */     this.client_x = 0;
/*  455 */     this.client_y = 0;
/*  456 */     this.client_dx = 0;
/*  457 */     this.client_dy = 0;
/*  458 */     this.pressed_button = 0;
/*  459 */     this.dragging = false;
/*      */     
/*  461 */     sync_default();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void move_server(boolean paramBoolean1, boolean paramBoolean2)
/*      */   {
/*  480 */     int i1 = 0;
/*  481 */     int i2 = 0;
/*  482 */     int i3 = 0;
/*  483 */     int i4 = 0;
/*      */     
/*  485 */     this.timer.pause();
/*      */     
/*  487 */     int j = this.client_dx;
/*  488 */     int k = this.client_dy;
/*      */     int m;
/*  490 */     if (j >= 0) {
/*  491 */       m = 1;
/*      */     }
/*      */     else {
/*  494 */       m = -1;
/*  495 */       j = -j; }
/*      */     int n;
/*  497 */     if (k >= 0) {
/*  498 */       n = 1;
/*      */     }
/*      */     else {
/*  501 */       n = -1;
/*  502 */       k = -k;
/*      */     }
/*      */     for (;;) {

/*      */       int i;
/*  506 */       if (j != 0) {
/*  507 */         for (i = this.send_dx.length - 1; i >= this.send_dx_index; i--) {
/*  508 */           if (this.recv_dx[i] <= j) {
/*  509 */             i1 = m * this.send_dx[i];
/*  510 */             i3 += this.recv_dx[i];
/*  511 */             j -= this.recv_dx[i];
/*  512 */             break;
/*      */           }
/*      */         }
/*  515 */         if (i < this.send_dx_index)
/*      */         {
/*  517 */           i1 = 0;
/*  518 */           i3 += j;
/*  519 */           j = 0;
/*      */         }
/*      */       }
/*      */       else {
/*  523 */         i1 = 0;
/*      */       }
/*      */       
/*      */ 
/*  527 */       if (k != 0) {
/*  528 */         for (i = this.send_dy.length - 1; i >= this.send_dy_index; i--) {
/*  529 */           if (this.recv_dy[i] <= k) {
/*  530 */             i2 = n * this.send_dy[i];
/*  531 */             i4 += this.recv_dy[i];
/*  532 */             k -= this.recv_dy[i];
/*  533 */             break;
/*      */           }
/*      */         }
/*  536 */         if (i < this.send_dy_index)
/*      */         {
/*  538 */           i2 = 0;
/*  539 */           i4 += k;
/*  540 */           k = 0;
/*      */         }
/*      */       }
/*      */       else {
/*  544 */         i2 = 0;
/*      */       }
/*      */       
/*      */ 
/*  548 */       if ((i1 != 0) || (i2 != 0)) {
/*  549 */         this.listener.serverMove(i1, i2, this.client_x, this.client_y);
/*      */       }
/*      */       
/*      */ 
/*  553 */       if (true) { if ((j == 0) && (k == 0)) {
/*      */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  559 */     this.client_dx -= m * i3;
/*  560 */     this.client_dy -= n * i4;
/*      */     
/*  562 */     if (!paramBoolean2)
/*      */     {
/*      */ 
/*  565 */       this.server_x += m * i3;
/*  566 */       this.server_y -= n * i4;
/*  567 */       if (this.debug_msg) {
/*  568 */         System.out.println("Server:" + this.server_x + "," + this.server_y);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  573 */     if ((this.client_dx != 0) || (this.client_dy != 0)) {
/*  574 */       this.timer.start();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void go_state(int paramInt)
/*      */   {
/*  585 */     synchronized (this.mutex) {
/*  586 */       state_machine(1, null, 0, 0);
/*  587 */       this.state = paramInt;
/*  588 */       state_machine(0, null, 0, 0);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void state_machine(int paramInt1, MouseEvent paramMouseEvent, int paramInt2, int paramInt3)
/*      */   {
                int lol = 0;
/*  603 */     synchronized (this.mutex) {
/*  604 */       switch (this.state) {
/*      */       case 0: 
/*  606 */         state_init(paramInt1, paramMouseEvent, paramInt2, paramInt3);
/*  607 */         break;
/*      */       
/*      */       case 1: 
/*  610 */         state_sync(paramInt1, paramMouseEvent, paramInt2, paramInt3);
/*  611 */         break;
/*      */       
/*      */       case 2: 
/*  614 */         state_enable(paramInt1, paramMouseEvent, paramInt2, paramInt3);
/*  615 */         break;
/*      */       
/*      */       case 3: 
/*  618 */         state_disable(paramInt1, paramMouseEvent, paramInt2, paramInt3);
/*      */       }
/*      */       
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void state_init(int paramInt1, MouseEvent paramMouseEvent, int paramInt2, int paramInt3)
/*      */   {
/*  636 */     switch (paramInt1) {
/*      */     case 0: 
/*  638 */       init_vars();
/*  639 */       go_state(3);
/*  640 */       break;
/*      */     }
/*      */     
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void state_sync(int paramInt1, MouseEvent paramMouseEvent, int paramInt2, int paramInt3)
/*      */   {
/*  675 */     switch (paramInt1) {
/*      */     case 0: 
/*  677 */       this.timer = new Timer(2000, false, this.mutex);
/*  678 */       this.timer.setListener(this, null);
/*  679 */       sync_default();
/*  680 */       this.send_dx_index = (this.send_dx.length - 1);
/*  681 */       this.send_dy_index = (this.send_dy.length - 1);
/*  682 */       sync_continue();
/*  683 */       break;
/*      */     
/*      */     case 1: 
/*  686 */       this.timer.stop();
/*  687 */       this.timer = null;
/*  688 */       if (!this.sync_successful) {
/*  689 */         if (this.debug_msg) {
/*  690 */           System.out.println("fail");
/*      */         }
/*  692 */         sync_default();
/*      */ 
/*      */       }
/*  695 */       else if (this.debug_msg) {
/*  696 */         System.out.println("success");
/*      */       }
/*      */       
/*  699 */       if (this.debug_msg)
/*      */       {
/*  701 */         for (int i = 0; i < this.send_dx.length; i++) {
/*  702 */           System.out.println(this.recv_dx[i]);
/*      */         }
/*  704 */         for (int i = 0; i < this.send_dx.length; i++) {
/*  705 */           System.out.println(this.recv_dy[i]);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */       break;
/*      */     case 2: 
/*  712 */       go_state(1);
/*  713 */       break;
/*      */     
/*      */     case 3: 
/*  716 */       if ((paramInt2 > 2000) || (paramInt3 > 2000))
/*      */       {
/*  718 */         go_state(3);
/*      */       }
/*      */       else {
/*  721 */         sync_update(paramInt2, paramInt3);
/*      */       }
/*  723 */       break;
/*      */     
/*      */     case 4: 
/*  726 */       this.server_w = paramInt2;
/*  727 */       this.server_h = paramInt3;
/*  728 */       break;
/*      */     
/*      */ 
/*      */     case 5: 
/*  732 */       go_state(3);
/*  733 */       break;
/*      */     
/*      */ 
/*      */     case 6: 
/*  737 */       go_state(2);
/*  738 */       break;
/*      */     
/*      */ 
/*      */     case 8: 
/*      */     case 9: 
/*      */     case 12: 
/*      */     case 13: 
/*  745 */       this.client_x = paramMouseEvent.getX();
/*  746 */       this.client_y = paramMouseEvent.getY();
/*  747 */       break;
/*      */     }
/*      */     
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void state_enable(int paramInt1, MouseEvent paramMouseEvent, int paramInt2, int paramInt3)
/*      */   {
/*  775 */     switch (paramInt1) {
/*      */     case 0: 
/*  777 */       if (this.debug_msg) {
/*  778 */         System.out.println("enable");
/*      */       }
/*  780 */       this.timer = new Timer(200, false, this.mutex);
/*  781 */       this.timer.setListener(this, null);
/*  782 */       break;
/*      */     
/*      */     case 1: 
/*  785 */       this.timer.stop();
/*  786 */       this.timer = null;
/*  787 */       break;
/*      */     
/*      */     case 2: 
/*  790 */       go_state(1);
/*  791 */       break;
/*      */     
/*      */     case 3: 
/*  794 */       if (this.debug_msg) {
/*  795 */         System.out.println("Server:" + paramInt2 + "," + paramInt3);
/*      */       }
/*  797 */       if ((paramInt2 > 2000) || (paramInt3 > 2000))
/*      */       {
/*  799 */         go_state(3);
/*      */       }
/*      */       else {
/*  802 */         this.server_x = paramInt2;
/*  803 */         this.server_y = paramInt3;
/*      */       }
/*  805 */       break;
/*      */     
/*      */     case 4: 
/*  808 */       this.server_w = paramInt2;
/*  809 */       this.server_h = paramInt3;
/*  810 */       break;
/*      */     
/*      */     case 5: 
/*  813 */       go_state(3);
/*  814 */       break;
/*      */     
/*      */     case 14: 
/*  817 */       this.client_dx = (this.client_x - this.server_x);
/*  818 */       this.client_dy = (this.server_y - this.client_y);
/*  819 */       move_server(true, true);
/*  820 */       break;
/*      */     
/*      */ 
/*      */     case 6: 
/*  824 */       move_server(true, true);
/*  825 */       break;
/*      */     
/*      */     case 8: 
/*      */     case 9: 
/*  829 */       this.client_x = paramMouseEvent.getX();
/*  830 */       this.client_y = paramMouseEvent.getY();
/*  831 */       if (this.client_x < 0) {
/*  832 */         this.client_x = 0;
/*      */       }
/*  834 */       if (this.client_x > this.server_w) {
/*  835 */         this.client_x = this.server_w;
/*      */       }
/*  837 */       if (this.client_y < 0) {
/*  838 */         this.client_y = 0;
/*      */       }
/*  840 */       if (this.client_y > this.server_h) {
/*  841 */         this.client_y = this.server_h;
/*      */       }
/*  843 */       if (this.debug_msg) {
/*  844 */         System.out.println("eClient:" + this.client_x + "," + this.client_y);
/*      */       }
/*  846 */       if ((this.pressed_button != 1) && ((paramMouseEvent.getModifiers() & 0x2) == 0))
/*      */       {
/*      */ 
/*  849 */         align();
/*      */       }
/*      */       
/*      */       break;
/*      */     case 12: 
/*  854 */       if (this.pressed_button != 1)
/*      */       {
/*  856 */         if (this.pressed_button > 0)
/*      */         {
/*  858 */           this.pressed_button = (-this.pressed_button);
/*  859 */           this.listener.serverPress(this.pressed_button);
/*      */         }
/*  861 */         this.client_dx += paramMouseEvent.getX() - this.client_x;
/*  862 */         this.client_dy += this.client_y - paramMouseEvent.getY();
/*  863 */         move_server(false, true);
/*      */       }
/*  865 */       this.client_x = paramMouseEvent.getX();
/*  866 */       this.client_y = paramMouseEvent.getY();
/*  867 */       if (this.debug_msg) {
/*  868 */         System.out.println("Client:" + this.client_x + "," + this.client_y);
/*      */       }
/*  870 */       this.dragging = true;
/*  871 */       break;
/*      */     
/*      */     case 13: 
/*  874 */       if ((paramMouseEvent.getModifiers() & 0x2) == 0)
/*      */       {
/*  876 */         this.client_dx += paramMouseEvent.getX() - this.client_x;
/*  877 */         this.client_dy += this.client_y - paramMouseEvent.getY();
/*  878 */         move_server(false, true);
/*      */       }
/*  880 */       this.client_x = paramMouseEvent.getX();
/*  881 */       this.client_y = paramMouseEvent.getY();
/*  882 */       if (this.debug_msg) {
/*  883 */         System.out.println("Client:" + this.client_x + "," + this.client_y);
/*      */       }
/*      */       
/*      */       break;
/*      */     case 10: 
/*  888 */       if (this.pressed_button == 0) {
/*  889 */         if ((paramMouseEvent.getModifiers() & 0x4) != 0) {
/*  890 */           this.pressed_button = 1;
/*      */         }
/*  892 */         else if ((paramMouseEvent.getModifiers() & 0x8) != 0) {
/*  893 */           this.pressed_button = 2;
/*      */         }
/*      */         else {
/*  896 */           this.pressed_button = 4;
/*      */         }
/*  898 */         this.dragging = false;
/*      */       }
/*      */       
/*      */ 
/*      */       break;
/*      */     case 11: 
/*  904 */       if (this.pressed_button == -4) {
/*  905 */         this.listener.serverRelease(4);
/*      */       }
/*  907 */       else if (this.pressed_button == -2) {
/*  908 */         this.listener.serverRelease(2);
/*      */       }
/*  910 */       else if (this.pressed_button == -1) {
/*  911 */         this.listener.serverRelease(1);
/*      */       }
/*  913 */       this.pressed_button = 0;
/*  914 */       break;
/*      */     
/*      */ 
/*      */     case 7: 
/*  918 */       if (!this.dragging) {
/*  919 */         if ((paramMouseEvent.getModifiers() & 0x10) != 0) {
/*  920 */           this.listener.serverClick(4, 1);
/*      */         }
/*  922 */         else if ((paramMouseEvent.getModifiers() & 0x8) != 0) {
/*  923 */           this.listener.serverClick(2, 1);
/*      */         }
/*  925 */         else if ((paramMouseEvent.getModifiers() & 0x4) != 0) {
/*  926 */           this.listener.serverClick(1, 1);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       break;
/*      */     }
/*      */     
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void state_disable(int paramInt1, MouseEvent paramMouseEvent, int paramInt2, int paramInt3)
/*      */   {
/*  955 */     switch (paramInt1) {
/*      */     case 0: 
/*  957 */       if (this.debug_msg) {
/*  958 */         System.out.println("disable");
/*      */       }
/*  960 */       this.timer = new Timer(200, false, this.mutex);
/*  961 */       this.timer.setListener(this, null);
/*  962 */       break;
/*      */     
/*      */     case 1: 
/*  965 */       this.timer.stop();
/*  966 */       this.timer = null;
/*  967 */       break;
/*      */     
/*      */     case 2: 
/*  970 */       sync_default();
/*  971 */       break;
/*      */     
/*      */     case 3: 
/*  974 */       if (this.debug_msg) {
/*  975 */         System.out.println("Server:" + paramInt2 + "," + paramInt3);
/*      */       }
/*  977 */       if ((paramInt2 < 2000) && (paramInt3 < 2000))
/*      */       {
/*  979 */         this.server_x = paramInt2;
/*  980 */         this.server_y = paramInt3;
/*  981 */         go_state(2);
/*      */       }
/*      */       
/*      */ 
/*      */       break;
/*      */     case 4: 
/*  987 */       this.server_w = paramInt2;
/*  988 */       this.server_h = paramInt3;
/*  989 */       break;
/*      */     
/*      */     case 5: 
/*      */       break;
/*      */     
/*      */     case 14: 
/*  995 */       this.client_dx = (this.client_x - this.server_x);
/*  996 */       this.client_dy = (this.server_y - this.client_y);
/*  997 */       move_server(true, false);
/*  998 */       break;
/*      */     
/*      */ 
/*      */     case 6: 
/* 1002 */       move_server(true, false);
/* 1003 */       break;
/*      */     
/*      */     case 8: 
/*      */     case 9: 
/* 1007 */       this.client_x = paramMouseEvent.getX();
/* 1008 */       this.client_y = paramMouseEvent.getY();
/* 1009 */       if (this.client_x < 0) {
/* 1010 */         this.client_x = 0;
/*      */       }
/* 1012 */       if (this.client_x > this.server_w) {
/* 1013 */         this.client_x = this.server_w;
/*      */       }
/* 1015 */       if (this.client_y < 0) {
/* 1016 */         this.client_y = 0;
/*      */       }
/* 1018 */       if (this.client_y > this.server_h) {
/* 1019 */         this.client_y = this.server_h;
/*      */       }
/* 1021 */       if (this.debug_msg) {
/* 1022 */         System.out.println("eClient:" + this.client_x + "," + this.client_y);
/*      */       }
/* 1024 */       if ((this.pressed_button != 1) && ((paramMouseEvent.getModifiers() & 0x2) == 0))
/*      */       {
/*      */ 
/* 1027 */         align();
/*      */       }
/*      */       
/*      */       break;
/*      */     case 12: 
/* 1032 */       if (this.pressed_button != 1)
/*      */       {
/* 1034 */         if (this.pressed_button > 0)
/*      */         {
/* 1036 */           this.pressed_button = (-this.pressed_button);
/* 1037 */           this.listener.serverPress(this.pressed_button);
/*      */         }
/* 1039 */         this.client_dx += paramMouseEvent.getX() - this.client_x;
/* 1040 */         this.client_dy += this.client_y - paramMouseEvent.getY();
/* 1041 */         move_server(false, false);
/*      */       }
/*      */       else
/*      */       {
/* 1045 */         this.server_x = paramMouseEvent.getX();
/* 1046 */         this.server_y = paramMouseEvent.getY();
/*      */       }
/* 1048 */       this.client_x = paramMouseEvent.getX();
/* 1049 */       this.client_y = paramMouseEvent.getY();
/* 1050 */       if (this.debug_msg) {
/* 1051 */         System.out.println("Client:" + this.client_x + "," + this.client_y);
/*      */       }
/* 1053 */       this.dragging = true;
/* 1054 */       break;
/*      */     
/*      */     case 13: 
/* 1057 */       if ((paramMouseEvent.getModifiers() & 0x2) == 0)
/*      */       {
/* 1059 */         this.client_dx += paramMouseEvent.getX() - this.client_x;
/* 1060 */         this.client_dy += this.client_y - paramMouseEvent.getY();
/* 1061 */         move_server(false, false);
/*      */       }
/*      */       else
/*      */       {
/* 1065 */         this.server_x = paramMouseEvent.getX();
/* 1066 */         this.server_y = paramMouseEvent.getY();
/*      */       }
/* 1068 */       this.client_x = paramMouseEvent.getX();
/* 1069 */       this.client_y = paramMouseEvent.getY();
/* 1070 */       if (this.debug_msg) {
/* 1071 */         System.out.println("Client:" + this.client_x + "," + this.client_y);
/*      */       }
/*      */       
/*      */       break;
/*      */     case 10: 
/* 1076 */       if (this.pressed_button == 0) {
/* 1077 */         if ((paramMouseEvent.getModifiers() & 0x4) != 0) {
/* 1078 */           this.pressed_button = 1;
/*      */         }
/* 1080 */         else if ((paramMouseEvent.getModifiers() & 0x8) != 0) {
/* 1081 */           this.pressed_button = 2;
/*      */         }
/*      */         else {
/* 1084 */           this.pressed_button = 4;
/*      */         }
/* 1086 */         this.dragging = false;
/*      */       }
/*      */       
/*      */ 
/*      */       break;
/*      */     case 11: 
/* 1092 */       if (this.pressed_button == -4) {
/* 1093 */         this.listener.serverRelease(4);
/*      */       }
/* 1095 */       else if (this.pressed_button == -2) {
/* 1096 */         this.listener.serverRelease(2);
/*      */       }
/* 1098 */       else if (this.pressed_button == -1) {
/* 1099 */         this.listener.serverRelease(1);
/*      */       }
/* 1101 */       this.pressed_button = 0;
/* 1102 */       break;
/*      */     
/*      */ 
/*      */     case 7: 
/* 1106 */       if (!this.dragging) {
/* 1107 */         if ((paramMouseEvent.getModifiers() & 0x10) != 0) {
/* 1108 */           this.listener.serverClick(4, 1);
/*      */         }
/* 1110 */         else if ((paramMouseEvent.getModifiers() & 0x8) != 0) {
/* 1111 */           this.listener.serverClick(2, 1);
/*      */         }
/* 1113 */         else if ((paramMouseEvent.getModifiers() & 0x4) != 0) {
/* 1114 */           this.listener.serverClick(1, 1);
/*      */         }
/*      */       }
/*      */       break;
/*      */     }
/*      */   }
/*      */ }


/* Location:              C:\Users\anton\Documents\ILO2\rc175p10.jar!\com\hp\ilo2\remcons\MouseSync.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */