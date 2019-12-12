package com.hp.ilo2.remcons;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.MemoryImageSource;

public class dvcwin extends Canvas implements Runnable {
    private Image offscreenImage = null;
    private Image firstImage = null;

    private Graphics offscreenGc;

    private MemoryImageSource imageSource;

    private int screenX;
    private int screenY;
    private java.awt.image.ColorModel colorModel;
    private int[] pixelBuffer;
    private Thread screenUpdater = null;

    protected static final int REFRESH_RATE = 60;

    private int refreshCount = 0;
    private int needToRefresh = 1;
    private int needToRefreshR = 1;
    private int needToRefreshW = 1;
    private int frametime = 0;
    private int paintCount = 0;
    private boolean updaterRunning = false;


    public dvcwin(int screenX, int screenY) {
        this.screenX = screenX;
        this.screenY = screenY;

        this.colorModel = new java.awt.image.DirectColorModel(32, 0xff0000, 0xff00, 0xff, 0);

        set_framerate(0);
    }

    @Override
    public boolean isFocusable() {
        return true;
    }

    @Override
    public void addNotify() {
        super.addNotify();

        if (this.offscreenImage == null) {
            this.offscreenImage = createImage(this.screenX, this.screenY);
        }
    }

    public boolean repaint_it(boolean paramInt) {
        boolean shouldRepaint = false;
        if (paramInt) {
            this.needToRefreshW += 1;
        } else {
            int i = this.needToRefreshW;
            if (this.needToRefreshR != i) {
                this.needToRefreshR = i;
                shouldRepaint = true;
            }
        }
        return shouldRepaint;
    }

    @Override
    public void paint(Graphics g) {
        if (g == null) {
            System.out.println("dvcwin.paint() g is null");
            return;
        }

        if (this.offscreenImage != null) {
            g.drawImage(this.offscreenImage, 0, 0, this);
        }
    }

    @Override
    public void update(Graphics paramGraphics) {
        if (this.offscreenImage == null) {
            this.offscreenImage = createImage(getSize().width, getSize().height);
            this.offscreenGc = this.offscreenImage.getGraphics();
        }

        if (this.firstImage != null) {
            this.offscreenGc.drawImage(this.firstImage, 0, 0, this);
        }
        paramGraphics.drawImage(this.offscreenImage, 0, 0, this);
    }


    public void paste_array(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3) {
        int j;

        if (paramInt2 + 16 > this.screenY) {
            j = this.screenY - paramInt2;
        } else {
            j = 16;
        }
        for (int i = 0; i < j; i++) {
            try {
                System.arraycopy(paramArrayOfInt, i * 16, this.pixelBuffer, (paramInt2 + i) * this.screenX + paramInt1, paramInt3);
            } catch (Exception localException) {
                return;
            }
        }

        this.imageSource.newPixels(paramInt1, paramInt2, paramInt3, 16, false);
    }

    public void set_abs_dimensions(int width, int height) {
        if ((width != this.screenX) || (height != this.screenY)) {
            synchronized (this) {
                this.screenX = width;
                this.screenY = height;
            }

            this.offscreenImage = null;

            this.pixelBuffer = new int[this.screenX * this.screenY];

            this.imageSource = new MemoryImageSource(this.screenX, this.screenY, this.colorModel, this.pixelBuffer, 0, this.screenX);

            this.imageSource.setAnimated(true);
            this.imageSource.setFullBufferUpdates(false);
            this.firstImage = createImage(this.imageSource);

            invalidate();

            Container parent = getParent();
            if (parent != null) {
                while (parent.getParent() != null) {
                    parent.invalidate();
                    parent = parent.getParent();
                }
                parent.invalidate();
                parent.validate();
            }
            System.gc();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension localDimension;

        synchronized (this) {
            localDimension = new Dimension(this.screenX, this.screenY);
        }
        return localDimension;
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    void show_text(String text) {
        if (this.screenUpdater == null) {
            return;
        }

        if ((this.screenX != 640) || (this.screenY != 100)) {
            set_abs_dimensions(640, 100);
            this.imageSource = null;
            this.firstImage = null;
            this.offscreenImage = null;
            this.offscreenImage = createImage(this.screenX, this.screenY);
        }

        Graphics g = this.offscreenImage.getGraphics();

        new Color(0);
        g.setColor(Color.black);
        g.fillRect(0, 0, this.screenX, this.screenY);
        Font localFont = new Font("Courier", Font.PLAIN, 20);
        new Color(0);
        g.setColor(Color.white);
        g.setFont(localFont);
        g.drawString(text, 10, 20);
        g.drawImage(this.offscreenImage, 0, 0, this);
        g.dispose();
        System.gc();
        repaint();
    }

    protected void set_framerate(int rate) {
        if (rate > 0) {
            this.frametime = 1000 / rate;
        } else {
            this.frametime = 1000 / 15;
        }
    }

    public void run() {
        while (this.updaterRunning) {
            try {
                Thread.sleep(this.frametime);
            } catch (InterruptedException ignored) {}

            if (repaint_it(false)) {
                repaint();
            }
        }
    }

    public synchronized void start_updates() {
        this.screenUpdater = new Thread(this, "dvcwin");
        this.updaterRunning = true;
        this.screenUpdater.start();
    }

    public synchronized void stop_updates() {
        if ((this.screenUpdater != null) && (this.screenUpdater.isAlive())) {
            this.updaterRunning = false;
        }
        this.screenX = 0;
        this.screenY = 0;
        this.screenUpdater = null;
    }
}