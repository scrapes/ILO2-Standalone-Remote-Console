/*     */ package com.hp.ilo2.remcons;
/*     */ 
/*     */ import java.awt.Canvas;
/*     */ import java.awt.Color;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Image;
/*     */ import java.awt.image.MemoryImageSource;
/*     */ 
/*     */ public class dvcwin extends Canvas implements Runnable
/*     */ {
/*  14 */   protected Image offscreen_image = null;
/*  15 */   protected Image first_image = null;
/*     */   
/*     */   protected Graphics offscreen_gc;
/*     */   
/*     */   protected MemoryImageSource image_source;
/*     */   
/*     */   protected int screen_x;
/*     */   protected int screen_y;
/*     */   protected int block_y;
/*     */   protected int block_x;
/*     */   protected java.awt.image.ColorModel cm;
/*     */   public int[] pixel_buffer;
/*  27 */   protected Thread screen_updater = null;
/*     */   
/*     */   protected static final int REFRESH_RATE = 60;
/*     */   
/*  31 */   private int refresh_count = 0;
/*  32 */   private int need_to_refresh = 1;
/*  33 */   private int need_to_refresh_r = 1;
/*  34 */   private int need_to_refresh_w = 1;
/*  35 */   public boolean mirror = false;
/*  36 */   private int frametime = 0;
/*  37 */   private int paint_count = 0;
/*  38 */   protected boolean updater_running = false;
/*     */   
/*     */ 
/*     */   public dvcwin(int paramInt1, int paramInt2)
/*     */   {
/*  43 */     this.screen_x = paramInt1;
/*  44 */     this.screen_y = paramInt2;
/*     */     
/*  46 */     this.cm = new java.awt.image.DirectColorModel(32, 16711680, 65280, 255, 0);
/*     */     
/*  48 */     set_framerate(0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isFocusable()
/*     */   {
/*  56 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addNotify()
/*     */   {
/*  66 */     super.addNotify();
/*     */     
/*  68 */     if (this.offscreen_image == null) {
/*  69 */       this.offscreen_image = createImage(this.screen_x, this.screen_y);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean repaint_it(int paramInt)
/*     */   {
/*  87 */     boolean bool = false;
/*  88 */     if (paramInt == 1)
/*     */     {
/*  90 */       this.need_to_refresh_w += 1;
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*  95 */       int i = this.need_to_refresh_w;
/*  96 */       if (this.need_to_refresh_r != i)
/*     */       {
/*  98 */         this.need_to_refresh_r = i;
/*  99 */         bool = true;
/*     */       }
/*     */     }
/* 102 */     return bool;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void paint(Graphics paramGraphics)
/*     */   {
/* 112 */     if (paramGraphics == null) {
/* 113 */       System.out.println("dvcwin.paint() g is null");
/* 114 */       return;
/*     */     }
/*     */     
/* 117 */     if (this.offscreen_image != null) {
/* 118 */       paramGraphics.drawImage(this.offscreen_image, 0, 0, this);
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
/*     */   public void update(Graphics paramGraphics)
/*     */   {
/* 132 */     if (this.offscreen_image == null)
/*     */     {
/* 134 */       this.offscreen_image = createImage(getSize().width, getSize().height);
/*     */       
/* 136 */       this.offscreen_gc = this.offscreen_image.getGraphics();
/*     */     }
/*     */     
/*     */ 
/* 140 */     if (this.first_image != null) {
/* 141 */       this.offscreen_gc.drawImage(this.first_image, 0, 0, this);
/*     */     }
/* 143 */     paramGraphics.drawImage(this.offscreen_image, 0, 0, this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void paste_array(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/*     */     int j;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 158 */     if (paramInt2 + 16 > this.screen_y)
/*     */     {
/* 160 */       j = this.screen_y - paramInt2;
/*     */     }
/*     */     else {
/* 163 */       j = 16;
/*     */     }
/* 165 */     for (int i = 0; i < j; i++)
/*     */     {
/*     */       try
/*     */       {
/* 169 */         System.arraycopy(paramArrayOfInt, i * 16, this.pixel_buffer, (paramInt2 + i) * this.screen_x + paramInt1, paramInt3);
/*     */ 
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/*     */ 
/* 175 */         return;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 180 */     this.image_source.newPixels(paramInt1, paramInt2, paramInt3, 16, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void set_abs_dimensions(int paramInt1, int paramInt2)
/*     */   {
/* 188 */     if ((paramInt1 != this.screen_x) || (paramInt2 != this.screen_y))
/*     */     {
/*     */ 
/*     */ 
/* 192 */       synchronized (this) {
/* 193 */         this.screen_x = paramInt1;
/* 194 */         this.screen_y = paramInt2;
/*     */       }
/*     */       
/* 197 */       this.offscreen_image = null;
/*     */       
/* 199 */       this.pixel_buffer = new int[this.screen_x * this.screen_y];
/*     */       
/*     */ 
/* 202 */       this.image_source = new MemoryImageSource(this.screen_x, this.screen_y, this.cm, this.pixel_buffer, 0, this.screen_x);
/*     */       
/* 204 */       this.image_source.setAnimated(true);
/* 205 */       this.image_source.setFullBufferUpdates(false);
/* 206 */       this.first_image = createImage(this.image_source);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 216 */       invalidate();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 224 */       Container localContainer = getParent();
/* 225 */       if (localContainer != null) {
/* 226 */         while (localContainer.getParent() != null) {
/* 227 */           localContainer.invalidate();
/* 228 */           localContainer = localContainer.getParent();
/*     */         }
/* 230 */         localContainer.invalidate();
/* 231 */         localContainer.validate();
/*     */       }
/* 233 */       System.gc();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Dimension getPreferredSize()
/*     */   {
/*     */     Dimension localDimension;
/*     */     
/*     */ 
/* 244 */     synchronized (this) {
/* 245 */       localDimension = new Dimension(this.screen_x, this.screen_y);
/*     */     }
/* 247 */     return localDimension;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Dimension getMinimumSize()
/*     */   {
/* 257 */     return getPreferredSize();
/*     */   }
/*     */   
/*     */ 
/*     */   public void show_text(String paramString)
/*     */   {
/* 263 */     if (this.screen_updater == null)
/*     */     {
/*     */ 
/* 266 */       return;
/*     */     }
/*     */     
/*     */ 
/* 270 */     if ((this.screen_x != 640) || (this.screen_y != 100))
/*     */     {
/*     */ 
/* 273 */       set_abs_dimensions(640, 100);
/* 274 */       this.image_source = null;
/* 275 */       this.first_image = null;
/* 276 */       this.offscreen_image = null;
/* 277 */       this.offscreen_image = createImage(this.screen_x, this.screen_y);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 285 */     Graphics localGraphics = this.offscreen_image.getGraphics();
/*     */     
/*     */ 
/* 288 */     new Color(0);localGraphics.setColor(Color.black);
/* 289 */     localGraphics.fillRect(0, 0, this.screen_x, this.screen_y);
/* 290 */     Font localFont = new Font("Courier", 0, 20);
/* 291 */     new Color(0);localGraphics.setColor(Color.white);
/* 292 */     localGraphics.setFont(localFont);
/* 293 */     localGraphics.drawString(paramString, 10, 20);
/* 294 */     localGraphics.drawImage(this.offscreen_image, 0, 0, this);
/* 295 */     localGraphics.dispose();
/* 296 */     System.gc();
/* 297 */     repaint();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void set_framerate(int paramInt)
/*     */   {
/* 306 */     if (paramInt > 0)
/*     */     {
/* 308 */       this.frametime = (1000 / paramInt);
/*     */     }
/*     */     else
/*     */     {
/* 312 */       this.frametime = 66;
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
/*     */ 
/*     */ 
/*     */   public void run()
/*     */   {
/* 328 */     while (this.updater_running)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       try
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 338 */         Thread.sleep(this.frametime);
/*     */       }
/*     */       catch (InterruptedException localInterruptedException) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 346 */       if (repaint_it(0))
/*     */       {
/* 348 */         repaint();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void start_updates()
/*     */   {
/* 358 */     this.screen_updater = new Thread(this, "dvcwin");
/* 359 */     this.updater_running = true;
/* 360 */     this.screen_updater.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void stop_updates()
/*     */   {
/* 371 */     if ((this.screen_updater != null) && (this.screen_updater.isAlive()))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 376 */       this.updater_running = false;
/*     */     }
/* 378 */     this.screen_x = 0;
/* 379 */     this.screen_y = 0;
/* 380 */     this.screen_updater = null;
/*     */   }
/*     */ }


/* Location:              C:\Users\anton\Documents\ILO2\rc175p10.jar!\com\hp\ilo2\remcons\dvcwin.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */