package com.hp.ilo2.remcons;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.MemoryImageSource;

public class dvcwin extends Canvas implements Runnable
{
  protected Image offscreen_image = null;
  protected Image first_image = null;

  protected Graphics offscreen_gc;

  protected MemoryImageSource image_source;

  protected int screen_x;
  protected int screen_y;
  protected int block_y;
  protected int block_x;
  protected java.awt.image.ColorModel cm;
  public int[] pixel_buffer;
  protected Thread screen_updater = null;

  protected static final int REFRESH_RATE = 60;

  private int refresh_count = 0;
  private int need_to_refresh = 1;
  private int need_to_refresh_r = 1;
  private int need_to_refresh_w = 1;
  public boolean mirror = false;
  private int frametime = 0;
  private int paint_count = 0;
  protected boolean updater_running = false;


  public dvcwin(int paramInt1, int paramInt2)
  {
    this.screen_x = paramInt1;
    this.screen_y = paramInt2;

    this.cm = new java.awt.image.DirectColorModel(32, 16711680, 65280, 255, 0);

    set_framerate(0);
  }




  public boolean isFocusable()
  {
    return true;
  }






  public void addNotify()
  {
    super.addNotify();

    if (this.offscreen_image == null) {
      this.offscreen_image = createImage(this.screen_x, this.screen_y);
    }
  }













  public boolean repaint_it(int paramInt)
  {
    boolean bool = false;
    if (paramInt == 1)
    {
      this.need_to_refresh_w += 1;

    }
    else
    {
      int i = this.need_to_refresh_w;
      if (this.need_to_refresh_r != i)
      {
        this.need_to_refresh_r = i;
        bool = true;
      }
    }
    return bool;
  }






  public void paint(Graphics paramGraphics)
  {
    if (paramGraphics == null) {
      System.out.println("dvcwin.paint() g is null");
      return;
    }

    if (this.offscreen_image != null) {
      paramGraphics.drawImage(this.offscreen_image, 0, 0, this);
    }
  }









  public void update(Graphics paramGraphics)
  {
    if (this.offscreen_image == null)
    {
      this.offscreen_image = createImage(getSize().width, getSize().height);

      this.offscreen_gc = this.offscreen_image.getGraphics();
    }


    if (this.first_image != null) {
      this.offscreen_gc.drawImage(this.first_image, 0, 0, this);
    }
    paramGraphics.drawImage(this.offscreen_image, 0, 0, this);
  }






  public void paste_array(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3)
  {
    int j;




    if (paramInt2 + 16 > this.screen_y)
    {
      j = this.screen_y - paramInt2;
    }
    else {
      j = 16;
    }
    for (int i = 0; i < j; i++)
    {
      try
      {
        System.arraycopy(paramArrayOfInt, i * 16, this.pixel_buffer, (paramInt2 + i) * this.screen_x + paramInt1, paramInt3);

      }
      catch (Exception localException)
      {

        return;
      }
    }


    this.image_source.newPixels(paramInt1, paramInt2, paramInt3, 16, false);
  }




  public void set_abs_dimensions(int paramInt1, int paramInt2)
  {
    if ((paramInt1 != this.screen_x) || (paramInt2 != this.screen_y))
    {


      synchronized (this) {
        this.screen_x = paramInt1;
        this.screen_y = paramInt2;
      }

      this.offscreen_image = null;

      this.pixel_buffer = new int[this.screen_x * this.screen_y];


      this.image_source = new MemoryImageSource(this.screen_x, this.screen_y, this.cm, this.pixel_buffer, 0, this.screen_x);

      this.image_source.setAnimated(true);
      this.image_source.setFullBufferUpdates(false);
      this.first_image = createImage(this.image_source);









      invalidate();







      Container localContainer = getParent();
      if (localContainer != null) {
        while (localContainer.getParent() != null) {
          localContainer.invalidate();
          localContainer = localContainer.getParent();
        }
        localContainer.invalidate();
        localContainer.validate();
      }
      System.gc();
    }
  }



  public Dimension getPreferredSize()
  {
    Dimension localDimension;


    synchronized (this) {
      localDimension = new Dimension(this.screen_x, this.screen_y);
    }
    return localDimension;
  }






  public Dimension getMinimumSize()
  {
    return getPreferredSize();
  }


  public void show_text(String paramString)
  {
    if (this.screen_updater == null)
    {

      return;
    }


    if ((this.screen_x != 640) || (this.screen_y != 100))
    {

      set_abs_dimensions(640, 100);
      this.image_source = null;
      this.first_image = null;
      this.offscreen_image = null;
      this.offscreen_image = createImage(this.screen_x, this.screen_y);
    }






    Graphics localGraphics = this.offscreen_image.getGraphics();


    new Color(0);localGraphics.setColor(Color.black);
    localGraphics.fillRect(0, 0, this.screen_x, this.screen_y);
    Font localFont = new Font("Courier", 0, 20);
    new Color(0);localGraphics.setColor(Color.white);
    localGraphics.setFont(localFont);
    localGraphics.drawString(paramString, 10, 20);
    localGraphics.drawImage(this.offscreen_image, 0, 0, this);
    localGraphics.dispose();
    System.gc();
    repaint();
  }





  public void set_framerate(int paramInt)
  {
    if (paramInt > 0)
    {
      this.frametime = (1000 / paramInt);
    }
    else
    {
      this.frametime = 66;
    }
  }











  public void run()
  {
    while (this.updater_running)
    {



      try
      {



        Thread.sleep(this.frametime);
      }
      catch (InterruptedException localInterruptedException) {}





      if (repaint_it(0))
      {
        repaint();
      }
    }
  }




  public synchronized void start_updates()
  {
    this.screen_updater = new Thread(this, "dvcwin");
    this.updater_running = true;
    this.screen_updater.start();
  }







  public synchronized void stop_updates()
  {
    if ((this.screen_updater != null) && (this.screen_updater.isAlive()))
    {



      this.updater_running = false;
    }
    this.screen_x = 0;
    this.screen_y = 0;
    this.screen_updater = null;
  }
}


/* Location:              C:\Users\anton\Documents\ILO2\rc175p10.jar!\com\hp\ilo2\remcons\dvcwin.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */