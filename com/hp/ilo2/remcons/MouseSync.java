package com.hp.ilo2.remcons;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.PrintStream;

























public class MouseSync
  implements MouseListener, MouseMotionListener, TimerListener
{
  private static final int CMD_START = 0;
  private static final int CMD_STOP = 1;
  private static final int CMD_SYNC = 2;
  private static final int CMD_SERVER_MOVE = 3;
  private static final int CMD_SERVER_SCREEN = 4;
  private static final int CMD_SERVER_DISABLE = 5;
  private static final int CMD_TIMEOUT = 6;
  private static final int CMD_CLICK = 7;
  private static final int CMD_ENTER = 8;
  private static final int CMD_EXIT = 9;
  private static final int CMD_PRESS = 10;
  private static final int CMD_RELEASE = 11;
  private static final int CMD_DRAG = 12;
  private static final int CMD_MOVE = 13;
  private static final int CMD_ALIGN = 14;
  private static final int STATE_INIT = 0;
  private static final int STATE_SYNC = 1;
  private static final int STATE_ENABLE = 2;
  private static final int STATE_DISABLE = 3;
  private int state;
  private MouseSyncListener listener;
  private int server_w;
  private int server_h;
  private int server_x;
  private int server_y;
  private int client_x;
  private int client_y;
  private int client_dx;
  private int client_dy;
  private int[] send_dx;
  private int[] send_dy;
  private int[] recv_dx;
  private int[] recv_dy;
  private int send_dx_index;
  private int send_dy_index;
  private static final int SYNC_SUCCESS_COUNT = 2;
  private static final int SYNC_FAIL_COUNT = 4;
  private int send_dx_count;
  private int send_dy_count;
  private int send_dx_success;
  private int send_dy_success;
  private boolean sync_successful;
  private static final int TIMEOUT_DELAY = 5;
  private static final int TIMEOUT_MOVE = 200;
  private static final int TIMEOUT_SYNC = 2000;
  private Timer timer;
  public static final int MOUSE_BUTTON_LEFT = 4;
  public static final int MOUSE_BUTTON_CENTER = 2;
  public static final int MOUSE_BUTTON_RIGHT = 1;
  private int pressed_button;
  private boolean dragging;
  private Object mutex;
  private boolean debug_msg = false;
  













  public MouseSync(Object paramObject)
  {
    this.mutex = paramObject;
    this.state = 0;
    state_machine(0, null, 0, 0);
  }
  





  public void setListener(MouseSyncListener paramMouseSyncListener)
  {
    this.listener = paramMouseSyncListener;
  }
  



  public void enableDebug()
  {
    this.debug_msg = true;
  }
  



  public void disableDebug()
  {
    this.debug_msg = false;
  }
  



  public void restart()
  {
    go_state(0);
  }
  



  public void align()
  {
    state_machine(14, null, 0, 0);
  }
  



  public void sync()
  {
    state_machine(2, null, 0, 0);
  }
  






  public void serverMoved(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    state_machine(3, null, paramInt1, paramInt2);
  }
  






  public void serverScreen(int paramInt1, int paramInt2)
  {
    state_machine(4, null, paramInt1, paramInt2);
  }
  



  public void serverDisabled()
  {
    state_machine(5, null, 0, 0);
  }
  








  public void timeout(Object paramObject)
  {
    state_machine(6, null, 0, 0);
  }
  



  public void mouseClicked(MouseEvent paramMouseEvent)
  {
    state_machine(7, paramMouseEvent, 0, 0);
  }
  



  public void mouseEntered(MouseEvent paramMouseEvent)
  {
    //state_machine(8, paramMouseEvent, 0, 0);
  }
  



  public void mouseExited(MouseEvent paramMouseEvent)
  {
    state_machine(9, paramMouseEvent, 0, 0);
  }
  



  public void mousePressed(MouseEvent paramMouseEvent)
  {
    state_machine(10, paramMouseEvent, 0, 0);
  }
  



  public void mouseReleased(MouseEvent paramMouseEvent)
  {
    state_machine(11, paramMouseEvent, 0, 0);
  }
  



  public void mouseDragged(MouseEvent paramMouseEvent)
  {
    state_machine(12, paramMouseEvent, 0, 0);
    move_delay();
  }
  



  public void mouseMoved(MouseEvent paramMouseEvent)
  {
    state_machine(13, paramMouseEvent, 0, 0);
    move_delay();
  }
  








  private void move_delay()
  {
    try
    {
      Thread.sleep(5L);
    }
    catch (InterruptedException localInterruptedException) {}
  }
  





  private void sync_default()
  {
    int[] arrayOfInt = { 1, 4, 6, 8, 12, 16, 32, 64 };
    
    this.send_dx = new int[arrayOfInt.length];
    this.send_dy = new int[arrayOfInt.length];
    this.recv_dx = new int[arrayOfInt.length];
    this.recv_dy = new int[arrayOfInt.length];
    
    for (int i = 0; i < arrayOfInt.length; i++) {
      this.send_dx[i] = arrayOfInt[i];
      this.send_dy[i] = arrayOfInt[i];
      this.recv_dx[i] = arrayOfInt[i];
      this.recv_dy[i] = arrayOfInt[i];
    }
    
    this.send_dx_index = 0;
    this.send_dy_index = 0;
    
    this.send_dx_count = 0;
    this.send_dy_count = 0;
    this.send_dx_success = 0;
    this.send_dy_success = 0;
    this.sync_successful = false;
  }
  



  private void sync_continue()
  {
    int i = 1;
    int j = 1;
    int k = 0;
    int m = 0;
    

    if (this.server_x > this.server_w / 2) {
      i = -1;
    }
    if (this.server_y < this.server_h / 2) {
      j = -1;
    }
    
    if (this.send_dx_index >= 0) {
      k = i * this.send_dx[this.send_dx_index];
    }
    if (this.send_dy_index >= 0) {
      m = j * this.send_dy[this.send_dy_index];
    }
    this.listener.serverMove(k, m, this.client_x, this.client_y);
    this.timer.start();
  }
  










  private void sync_update(int paramInt1, int paramInt2)
  {
    this.timer.pause();
    

    int i = paramInt1 - this.server_x;
    int j = this.server_y - paramInt2;
    
    this.server_x = paramInt1;
    this.server_y = paramInt2;
    
    if (i < 0) {
      i = -i;
    }
    if (j < 0) {
      j = -j;
    }
    

    if (this.send_dx_index >= 0) {
      if (this.recv_dx[this.send_dx_index] == i)
      {
        this.send_dx_success += 1;
      }
      this.recv_dx[this.send_dx_index] = i;
      this.send_dx_count += 1;
      if (this.send_dx_success >= 2)
      {
        this.send_dx_index -= 1;
        this.send_dx_success = 0;
        this.send_dx_count = 0;
      }
      else if (this.send_dx_count >= 4)
      {
        if (this.debug_msg) {
          System.out.println("no x sync:" + this.send_dx[this.send_dx_index]);
        }
        go_state(2);
        return;
      }
    }
    

    if (this.send_dy_index >= 0) {
      if (this.recv_dy[this.send_dy_index] == j)
      {
        this.send_dy_success += 1;
      }
      this.recv_dy[this.send_dy_index] = j;
      this.send_dy_count += 1;
      if (this.send_dy_success >= 2)
      {
        this.send_dy_index -= 1;
        this.send_dy_success = 0;
        this.send_dy_count = 0;
      }
      else if (this.send_dy_count >= 4)
      {
        if (this.debug_msg) {
          System.out.println("no y sync:" + this.send_dy[this.send_dy_index]);
        }
        go_state(2);
        return;
      }
    }
    if ((this.send_dx_index < 0) && (this.send_dy_index < 0))
    {
      for (int k = this.send_dx.length - 1; k >= 0; k--) {
        if ((this.recv_dx[k] == 0) || (this.recv_dy[k] == 0))
        {
          if (this.debug_msg) {
            System.out.println("no movement:" + this.send_dx[k]);
          }
          go_state(2);
          return;
        }
        if ((k != 0) && (
          (this.recv_dx[k] < this.recv_dx[(k - 1)]) || (this.recv_dy[k] < this.recv_dy[(k - 1)])))
        {
          if (this.debug_msg) {
            System.out.println("not linear:" + this.send_dx[k]);
          }
          go_state(2);
          return;
        }
      }
      

      this.sync_successful = true;
      this.send_dx_index = 0;
      this.send_dy_index = 0;
      go_state(2);
    }
    else
    {
      sync_continue();
    }
  }
  



  private void init_vars()
  {
    this.server_w = 640;
    this.server_h = 480;
    this.server_x = 0;
    this.server_y = 0;
    this.client_x = 0;
    this.client_y = 0;
    this.client_dx = 0;
    this.client_dy = 0;
    this.pressed_button = 0;
    this.dragging = false;
    
    sync_default();
  }
  














  private void move_server(boolean paramBoolean1, boolean paramBoolean2)
  {
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    int i4 = 0;
    
    this.timer.pause();
    
    int j = this.client_dx;
    int k = this.client_dy;
    int m;
    if (j >= 0) {
      m = 1;
    }
    else {
      m = -1;
      j = -j; }
    int n;
    if (k >= 0) {
      n = 1;
    }
    else {
      n = -1;
      k = -k;
    }
    for (;;) {

      int i;
      if (j != 0) {
        for (i = this.send_dx.length - 1; i >= this.send_dx_index; i--) {
          if (this.recv_dx[i] <= j) {
            i1 = m * this.send_dx[i];
            i3 += this.recv_dx[i];
            j -= this.recv_dx[i];
            break;
          }
        }
        if (i < this.send_dx_index)
        {
          i1 = 0;
          i3 += j;
          j = 0;
        }
      }
      else {
        i1 = 0;
      }
      

      if (k != 0) {
        for (i = this.send_dy.length - 1; i >= this.send_dy_index; i--) {
          if (this.recv_dy[i] <= k) {
            i2 = n * this.send_dy[i];
            i4 += this.recv_dy[i];
            k -= this.recv_dy[i];
            break;
          }
        }
        if (i < this.send_dy_index)
        {
          i2 = 0;
          i4 += k;
          k = 0;
        }
      }
      else {
        i2 = 0;
      }
      

      if ((i1 != 0) || (i2 != 0)) {
        this.listener.serverMove(i1, i2, this.client_x, this.client_y);
      }
      

      if (true) { if ((j == 0) && (k == 0)) {
          break;
        }
      }
    }
    
    this.client_dx -= m * i3;
    this.client_dy -= n * i4;
    
    if (!paramBoolean2)
    {

      this.server_x += m * i3;
      this.server_y -= n * i4;
      if (this.debug_msg) {
        System.out.println("Server:" + this.server_x + "," + this.server_y);
      }
    }
    

    if ((this.client_dx != 0) || (this.client_dy != 0)) {
      this.timer.start();
    }
  }
  





  private void go_state(int paramInt)
  {
    synchronized (this.mutex) {
      state_machine(1, null, 0, 0);
      this.state = paramInt;
      state_machine(0, null, 0, 0);
    }
  }
  









  private void state_machine(int paramInt1, MouseEvent paramMouseEvent, int paramInt2, int paramInt3)
  {
                int lol = 0;
    synchronized (this.mutex) {
      switch (this.state) {
      case 0: 
        state_init(paramInt1, paramMouseEvent, paramInt2, paramInt3);
        break;
      
      case 1: 
        state_sync(paramInt1, paramMouseEvent, paramInt2, paramInt3);
        break;
      
      case 2: 
        state_enable(paramInt1, paramMouseEvent, paramInt2, paramInt3);
        break;
      
      case 3: 
        state_disable(paramInt1, paramMouseEvent, paramInt2, paramInt3);
      }
      
    }
  }
  










  private void state_init(int paramInt1, MouseEvent paramMouseEvent, int paramInt2, int paramInt3)
  {
    switch (paramInt1) {
    case 0: 
      init_vars();
      go_state(3);
      break;
    }
    
  }
  




























  private void state_sync(int paramInt1, MouseEvent paramMouseEvent, int paramInt2, int paramInt3)
  {
    switch (paramInt1) {
    case 0: 
      this.timer = new Timer(2000, false, this.mutex);
      this.timer.setListener(this, null);
      sync_default();
      this.send_dx_index = (this.send_dx.length - 1);
      this.send_dy_index = (this.send_dy.length - 1);
      sync_continue();
      break;
    
    case 1: 
      this.timer.stop();
      this.timer = null;
      if (!this.sync_successful) {
        if (this.debug_msg) {
          System.out.println("fail");
        }
        sync_default();

      }
      else if (this.debug_msg) {
        System.out.println("success");
      }
      
      if (this.debug_msg)
      {
        for (int i = 0; i < this.send_dx.length; i++) {
          System.out.println(this.recv_dx[i]);
        }
        for (int i = 0; i < this.send_dx.length; i++) {
          System.out.println(this.recv_dy[i]);
        }
      }
      

      break;
    case 2: 
      go_state(1);
      break;
    
    case 3: 
      if ((paramInt2 > 2000) || (paramInt3 > 2000))
      {
        go_state(3);
      }
      else {
        sync_update(paramInt2, paramInt3);
      }
      break;
    
    case 4: 
      this.server_w = paramInt2;
      this.server_h = paramInt3;
      break;
    

    case 5: 
      go_state(3);
      break;
    

    case 6: 
      go_state(2);
      break;
    

    case 8: 
    case 9: 
    case 12: 
    case 13: 
      this.client_x = paramMouseEvent.getX();
      this.client_y = paramMouseEvent.getY();
      break;
    }
    
  }
  





















  private void state_enable(int paramInt1, MouseEvent paramMouseEvent, int paramInt2, int paramInt3)
  {
    switch (paramInt1) {
    case 0: 
      if (this.debug_msg) {
        System.out.println("enable");
      }
      this.timer = new Timer(200, false, this.mutex);
      this.timer.setListener(this, null);
      break;
    
    case 1: 
      this.timer.stop();
      this.timer = null;
      break;
    
    case 2: 
      go_state(1);
      break;
    
    case 3: 
      if (this.debug_msg) {
        System.out.println("Server:" + paramInt2 + "," + paramInt3);
      }
      if ((paramInt2 > 2000) || (paramInt3 > 2000))
      {
        go_state(3);
      }
      else {
        this.server_x = paramInt2;
        this.server_y = paramInt3;
      }
      break;
    
    case 4: 
      this.server_w = paramInt2;
      this.server_h = paramInt3;
      break;
    
    case 5: 
      go_state(3);
      break;
    
    case 14: 
      this.client_dx = (this.client_x - this.server_x);
      this.client_dy = (this.server_y - this.client_y);
      move_server(true, true);
      break;
    

    case 6: 
      move_server(true, true);
      break;
    
    case 8: 
    case 9: 
      this.client_x = paramMouseEvent.getX();
      this.client_y = paramMouseEvent.getY();
      if (this.client_x < 0) {
        this.client_x = 0;
      }
      if (this.client_x > this.server_w) {
        this.client_x = this.server_w;
      }
      if (this.client_y < 0) {
        this.client_y = 0;
      }
      if (this.client_y > this.server_h) {
        this.client_y = this.server_h;
      }
      if (this.debug_msg) {
        System.out.println("eClient:" + this.client_x + "," + this.client_y);
      }
      if ((this.pressed_button != 1) && ((paramMouseEvent.getModifiers() & 0x2) == 0))
      {

        align();
      }
      
      break;
    case 12: 
      if (this.pressed_button != 1)
      {
        if (this.pressed_button > 0)
        {
          this.pressed_button = (-this.pressed_button);
          this.listener.serverPress(this.pressed_button);
        }
        this.client_dx += paramMouseEvent.getX() - this.client_x;
        this.client_dy += this.client_y - paramMouseEvent.getY();
        move_server(false, true);
      }
      this.client_x = paramMouseEvent.getX();
      this.client_y = paramMouseEvent.getY();
      if (this.debug_msg) {
        System.out.println("Client:" + this.client_x + "," + this.client_y);
      }
      this.dragging = true;
      break;
    
    case 13: 
      if ((paramMouseEvent.getModifiers() & 0x2) == 0)
      {
        this.client_dx += paramMouseEvent.getX() - this.client_x;
        this.client_dy += this.client_y - paramMouseEvent.getY();
        move_server(false, true);
      }
      this.client_x = paramMouseEvent.getX();
      this.client_y = paramMouseEvent.getY();
      if (this.debug_msg) {
        System.out.println("Client:" + this.client_x + "," + this.client_y);
      }
      
      break;
    case 10: 
      if (this.pressed_button == 0) {
        if ((paramMouseEvent.getModifiers() & 0x4) != 0) {
          this.pressed_button = 1;
        }
        else if ((paramMouseEvent.getModifiers() & 0x8) != 0) {
          this.pressed_button = 2;
        }
        else {
          this.pressed_button = 4;
        }
        this.dragging = false;
      }
      

      break;
    case 11: 
      if (this.pressed_button == -4) {
        this.listener.serverRelease(4);
      }
      else if (this.pressed_button == -2) {
        this.listener.serverRelease(2);
      }
      else if (this.pressed_button == -1) {
        this.listener.serverRelease(1);
      }
      this.pressed_button = 0;
      break;
    

    case 7: 
      if (!this.dragging) {
        if ((paramMouseEvent.getModifiers() & 0x10) != 0) {
          this.listener.serverClick(4, 1);
        }
        else if ((paramMouseEvent.getModifiers() & 0x8) != 0) {
          this.listener.serverClick(2, 1);
        }
        else if ((paramMouseEvent.getModifiers() & 0x4) != 0) {
          this.listener.serverClick(1, 1);
        }
      }
      









      break;
    }
    
  }
  









  private void state_disable(int paramInt1, MouseEvent paramMouseEvent, int paramInt2, int paramInt3)
  {
    switch (paramInt1) {
    case 0: 
      if (this.debug_msg) {
        System.out.println("disable");
      }
      this.timer = new Timer(200, false, this.mutex);
      this.timer.setListener(this, null);
      break;
    
    case 1: 
      this.timer.stop();
      this.timer = null;
      break;
    
    case 2: 
      sync_default();
      break;
    
    case 3: 
      if (this.debug_msg) {
        System.out.println("Server:" + paramInt2 + "," + paramInt3);
      }
      if ((paramInt2 < 2000) && (paramInt3 < 2000))
      {
        this.server_x = paramInt2;
        this.server_y = paramInt3;
        go_state(2);
      }
      

      break;
    case 4: 
      this.server_w = paramInt2;
      this.server_h = paramInt3;
      break;
    
    case 5: 
      break;
    
    case 14: 
      this.client_dx = (this.client_x - this.server_x);
      this.client_dy = (this.server_y - this.client_y);
      move_server(true, false);
      break;
    

    case 6: 
      move_server(true, false);
      break;
    
    case 8: 
    case 9: 
      this.client_x = paramMouseEvent.getX();
      this.client_y = paramMouseEvent.getY();
      if (this.client_x < 0) {
        this.client_x = 0;
      }
      if (this.client_x > this.server_w) {
        this.client_x = this.server_w;
      }
      if (this.client_y < 0) {
        this.client_y = 0;
      }
      if (this.client_y > this.server_h) {
        this.client_y = this.server_h;
      }
      if (this.debug_msg) {
        System.out.println("eClient:" + this.client_x + "," + this.client_y);
      }
      if ((this.pressed_button != 1) && ((paramMouseEvent.getModifiers() & 0x2) == 0))
      {

        align();
      }
      
      break;
    case 12: 
      if (this.pressed_button != 1)
      {
        if (this.pressed_button > 0)
        {
          this.pressed_button = (-this.pressed_button);
          this.listener.serverPress(this.pressed_button);
        }
        this.client_dx += paramMouseEvent.getX() - this.client_x;
        this.client_dy += this.client_y - paramMouseEvent.getY();
        move_server(false, false);
      }
      else
      {
        this.server_x = paramMouseEvent.getX();
        this.server_y = paramMouseEvent.getY();
      }
      this.client_x = paramMouseEvent.getX();
      this.client_y = paramMouseEvent.getY();
      if (this.debug_msg) {
        System.out.println("Client:" + this.client_x + "," + this.client_y);
      }
      this.dragging = true;
      break;
    
    case 13: 
      if ((paramMouseEvent.getModifiers() & 0x2) == 0)
      {
        this.client_dx += paramMouseEvent.getX() - this.client_x;
        this.client_dy += this.client_y - paramMouseEvent.getY();
        move_server(false, false);
      }
      else
      {
        this.server_x = paramMouseEvent.getX();
        this.server_y = paramMouseEvent.getY();
      }
      this.client_x = paramMouseEvent.getX();
      this.client_y = paramMouseEvent.getY();
      if (this.debug_msg) {
        System.out.println("Client:" + this.client_x + "," + this.client_y);
      }
      
      break;
    case 10: 
      if (this.pressed_button == 0) {
        if ((paramMouseEvent.getModifiers() & 0x4) != 0) {
          this.pressed_button = 1;
        }
        else if ((paramMouseEvent.getModifiers() & 0x8) != 0) {
          this.pressed_button = 2;
        }
        else {
          this.pressed_button = 4;
        }
        this.dragging = false;
      }
      

      break;
    case 11: 
      if (this.pressed_button == -4) {
        this.listener.serverRelease(4);
      }
      else if (this.pressed_button == -2) {
        this.listener.serverRelease(2);
      }
      else if (this.pressed_button == -1) {
        this.listener.serverRelease(1);
      }
      this.pressed_button = 0;
      break;
    

    case 7: 
      if (!this.dragging) {
        if ((paramMouseEvent.getModifiers() & 0x10) != 0) {
          this.listener.serverClick(4, 1);
        }
        else if ((paramMouseEvent.getModifiers() & 0x8) != 0) {
          this.listener.serverClick(2, 1);
        }
        else if ((paramMouseEvent.getModifiers() & 0x4) != 0) {
          this.listener.serverClick(1, 1);
        }
      }
      break;
    }
  }
}


/* Location:              C:\Users\anton\Documents\ILO2\rc175p10.jar!\com\hp\ilo2\remcons\MouseSync.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */