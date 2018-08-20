/*      */ package com.hp.ilo2.remcons;
/*      */ 
/*      */ import java.awt.Cursor;
/*      */ import java.awt.Image;
/*      */ import java.awt.Point;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.image.MemoryImageSource;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;

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
/*      */ public class cim
/*      */   extends telnet
/*      */   implements MouseSyncListener
/*      */ {
/*      */   private static final int CMD_MOUSE_MOVE = 208;
/*      */   private static final int CMD_BUTTON_PRESS = 209;
/*      */   private static final int CMD_BUTTON_RELEASE = 210;
/*      */   private static final int CMD_BUTTON_CLICK = 211;
/*      */   private static final int CMD_BYTE = 212;
/*      */   private static final int CMD_SET_MODE = 213;
/*      */   private static final char MOUSE_USBABS = '\001';
/*      */   private static final char MOUSE_USBREL = '\002';
/*      */   static final int CMD_ENCRYPT = 192;
/*      */   public static final int MOUSE_BUTTON_LEFT = 4;
/*      */   public static final int MOUSE_BUTTON_CENTER = 2;
/*      */   public static final int MOUSE_BUTTON_RIGHT = 1;
/*   56 */   private char prev_char = ' ';
/*   57 */   private boolean disable_kbd = false;
/*   58 */   private boolean altlock = false;
/*      */   
/*      */ 
/*      */ 
/*      */   private static final int block_width = 16;
/*      */   
/*      */ 
/*      */ 
/*      */   private static final int block_height = 16;
/*      */   
/*      */ 
/*   69 */   public int[] color_remap_table = new int['က'];
/*      */   
/*      */ 
/*   72 */   private int scale_x = 1;
/*   73 */   private int scale_y = 1;
/*      */   
/*      */ 
/*   76 */   private int screen_x = 1;
/*   77 */   private int screen_y = 1;
/*   78 */   private int mouse_protocol = 0;
/*      */   
/*   80 */   protected MouseSync mouse_sync = new MouseSync(this);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   87 */   public boolean UI_dirty = false;
/*      */   
/*   89 */   private boolean sending_encrypt_command = false;
/*   90 */   public byte[] encrypt_key = new byte[16];
/*      */   private RC4 RC4encrypter;
/*   92 */   private boolean encryption_active = false;
/*   93 */   private int key_index = 0;
/*      */   
/*      */   private static final int RESET = 0;
/*      */   
/*      */   private static final int START = 1;
/*      */   
/*      */   private static final int PIXELS = 2;
/*      */   
/*      */   private static final int PIXLRU1 = 3;
/*      */   
/*      */   private static final int PIXLRU0 = 4;
/*      */   
/*      */   private static final int PIXCODE1 = 5;
/*      */   
/*      */   private static final int PIXCODE2 = 6;
/*      */   
/*      */   private static final int PIXCODE3 = 7;
/*      */   private static final int PIXGREY = 8;
/*      */   private static final int PIXRGBR = 9;
/*      */   private static final int PIXRPT = 10;
/*      */   private static final int PIXRPT1 = 11;
/*      */   private static final int PIXRPTSTD1 = 12;
/*      */   private static final int PIXRPTSTD2 = 13;
/*      */   private static final int PIXRPTNSTD = 14;
/*      */   private static final int CMD = 15;
/*      */   private static final int CMD0 = 16;
/*      */   private static final int MOVEXY0 = 17;
/*      */   private static final int EXTCMD = 18;
/*      */   private static final int CMDX = 19;
/*      */   private static final int MOVESHORTX = 20;
/*      */   private static final int MOVELONGX = 21;
/*      */   private static final int BLKRPT = 22;
/*      */   private static final int EXTCMD1 = 23;
/*      */   private static final int FIRMWARE = 24;
/*      */   private static final int EXTCMD2 = 25;
/*      */   private static final int MODE0 = 26;
/*      */   private static final int TIMEOUT = 27;
/*      */   private static final int BLKRPT1 = 28;
/*      */   private static final int BLKRPTSTD = 29;
/*      */   private static final int BLKRPTNSTD = 30;
/*      */   private static final int PIXFAN = 31;
/*      */   private static final int PIXCODE4 = 32;
/*      */   private static final int PIXDUP = 33;
/*      */   private static final int BLKDUP = 34;
/*      */   private static final int PIXCODE = 35;
/*      */   private static final int PIXSPEC = 36;
/*      */   private static final int EXIT = 37;
/*      */   private static final int LATCHED = 38;
/*      */   private static final int MOVEXY1 = 39;
/*      */   private static final int MODE1 = 40;
/*      */   private static final int PIXRGBG = 41;
/*      */   private static final int PIXRGBB = 42;
/*      */   private static final int HUNT = 43;
/*      */   private static final int PRINT0 = 44;
/*      */   private static final int PRINT1 = 45;
/*      */   private static final int CORP = 46;
/*      */   private static final int MODE2 = 47;
/*      */   private static final int SIZE_OF_ALL = 48;
/*  151 */   private static int[] bits_to_read = { 0, 1, 1, 1, 1, 1, 2, 3, 4, 4, 1, 1, 3, 3, 8, 1, 1, 7, 1, 1, 3, 7, 1, 1, 8, 1, 7, 0, 1, 3, 7, 1, 4, 0, 0, 0, 1, 0, 1, 7, 7, 4, 4, 1, 8, 8, 1, 4 };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  161 */   private static int[] next_0 = { 1, 2, 31, 2, 2, 10, 10, 10, 10, 41, 2, 33, 2, 2, 2, 16, 19, 39, 22, 20, 1, 1, 34, 25, 46, 26, 40, 1, 29, 1, 1, 36, 10, 2, 1, 35, 8, 37, 38, 1, 47, 42, 10, 43, 45, 45, 1, 1 };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  171 */   private static int[] next_1 = { 1, 15, 3, 11, 11, 10, 10, 10, 10, 41, 11, 12, 2, 2, 2, 17, 18, 39, 23, 21, 1, 1, 28, 24, 46, 27, 40, 1, 30, 1, 1, 35, 10, 2, 1, 35, 9, 37, 38, 1, 47, 42, 10, 0, 45, 45, 24, 1 };
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
/*  182 */   private static int dvc_cc_active = 0;
/*  183 */   private static int[] dvc_cc_color = new int[17];
/*  184 */   private static int[] dvc_cc_usage = new int[17];
/*  185 */   private static int[] dvc_cc_block = new int[17];
/*      */   
/*  187 */   private static int[] dvc_lru_lengths = { 0, 0, 0, 1, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4 };
/*      */   
/*  189 */   private static int[] dvc_getmask = { 0, 1, 3, 7, 15, 31, 63, 127, 255 };
/*  190 */   private static int[] dvc_reversal = new int['Ā'];
/*  191 */   private static int[] dvc_left = new int['Ā'];
/*  192 */   private static int[] dvc_right = new int['Ā'];
/*      */   private static int dvc_pixel_count;
/*      */   private static int dvc_size_x;
/*      */   private static int dvc_size_y;
/*      */   private static int dvc_y_clipped;
/*      */   private static int dvc_lastx;
/*      */   private static int dvc_lasty;
/*      */   private static int dvc_newx;
/*      */   private static int dvc_newy;
/*  201 */   private static int dvc_color; private static int dvc_last_color; private static int dvc_ib_acc = 0;
/*  202 */   private static int dvc_ib_bcnt = 0;
/*  203 */   private static int dvc_zero_count = 0;
/*      */   
/*  205 */   private static int dvc_decoder_state = 0;
/*  206 */   private static int dvc_next_state = 0;
/*  207 */   private static int dvc_pixcode = 38;
/*      */   private static int dvc_code;
/*  209 */   private static int[] block = new int['Ā'];
/*      */   private static int dvc_red;
/*      */   private static int dvc_green;
/*      */   private static int dvc_blue;
/*      */   private static int fatal_count;
/*  214 */   private static int printchan = 0;
/*  215 */   private static String printstring = "";
/*  216 */   private static long count_bytes = 0L;
/*  217 */   private static int[] cmd_p_buff = new int['Ā'];
/*  218 */   private static int cmd_p_count = 0;
/*  219 */   private static int cmd_last = 0;
/*      */   
/*  221 */   private static int framerate = 30;
/*      */   
/*      */ 
/*  224 */   private static boolean debug_msgs = false;
/*  225 */   private static char last_bits = '\000';
/*  226 */   private static char last_bits2 = '\000';
/*  227 */   private static char last_bits3 = '\000';
/*  228 */   private static char last_bits4 = '\000';
/*  229 */   private static char last_bits5 = '\000';
/*  230 */   private static char last_bits6 = '\000';
/*  231 */   private static char last_bits7 = '\000';
/*  232 */   private static int last_len = 0;
/*  233 */   private static int last_len1 = 0;
/*  234 */   private static int last_len2 = 0;
/*  235 */   private static int last_len3 = 0;
/*  236 */   private static int last_len4 = 0;
/*  237 */   private static int last_len5 = 0;
/*  238 */   private static int last_len6 = 0;
/*  239 */   private static int last_len7 = 0;
/*  240 */   private static int last_len8 = 0;
/*  241 */   private static int last_len9 = 0;
/*  242 */   private static int last_len10 = 0;
/*  243 */   private static int last_len11 = 0;
/*  244 */   private static int last_len12 = 0;
/*  245 */   private static int last_len13 = 0;
/*  246 */   private static int last_len14 = 0;
/*  247 */   private static int last_len15 = 0;
/*  248 */   private static int last_len16 = 0;
/*  249 */   private static int last_len17 = 0;
/*  250 */   private static int last_len18 = 0;
/*  251 */   private static int last_len19 = 0;
/*  252 */   private static int last_len20 = 0;
/*  253 */   private static int last_len21 = 0;
/*  254 */   private static char dvc_new_bits = '\000';
/*  255 */   private static int debug_lastx = 0;
/*  256 */   private static int debug_lasty = 0;
/*  257 */   private static int debug_show_block = 0;
/*  258 */   private static long timeout_count = 0L;
/*  259 */   private static long dvc_counter_block = 0L;
/*  260 */   private static long dvc_counter_bits = 0L;
/*  261 */   private static boolean show_bitsblk_count = false;
/*  262 */   private static long show_slices = 0L;
/*  263 */   private static boolean dvc_process_inhibit = false;
/*      */   
/*  265 */   private static boolean video_detected = true;
/*      */   
/*      */ 
/*      */ 
/*  269 */   private boolean ignore_next_key = false;
/*      */   
/*      */   private static final int B = -16777216;
/*      */   
/*      */   private static final int W = -8355712;
/*      */   
/*      */   public cim()
/*      */   {
/*  277 */     dvc_reversal['ÿ'] = 0;
/*  278 */     this.current_cursor = Cursor.getDefaultCursor();
/*  279 */     this.screen.addMouseListener(this.mouse_sync);
/*  280 */     this.screen.addMouseMotionListener(this.mouse_sync);
/*  281 */     this.mouse_sync.setListener(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setup_encryption(byte[] paramArrayOfByte, int paramInt)
/*      */   {
/*  289 */     System.arraycopy(paramArrayOfByte, 0, this.encrypt_key, 0, 16);
/*      */     
/*  291 */     this.RC4encrypter = new RC4(paramArrayOfByte);
/*  292 */     this.key_index = paramInt;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void reinit_vars()
/*      */   {
/*  300 */     super.reinit_vars();
/*      */     
/*  302 */     this.prev_char = ' ';
/*  303 */     this.disable_kbd = false;
/*  304 */     this.altlock = false;
/*      */     
/*  306 */     dvc_reversal['ÿ'] = 0;
/*      */     
/*  308 */     this.scale_x = 1;
/*  309 */     this.scale_y = 1;
/*      */     
/*  311 */     this.mouse_sync.restart();
/*      */     
/*  313 */     dvc_process_inhibit = false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void enable_debug()
/*      */   {
/*  321 */     debug_msgs = true;
/*  322 */     super.enable_debug();
/*  323 */     this.mouse_sync.enableDebug();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void disable_debug()
/*      */   {
/*  332 */     debug_msgs = false;
/*  333 */     super.disable_debug();
/*  334 */     this.mouse_sync.disableDebug();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void sync_start()
/*      */   {
/*  343 */     this.mouse_sync.sync();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void serverMove(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  353 */     if (paramInt1 < -128)
/*      */     {
/*  355 */       paramInt1 = -128;
/*  356 */     } else if (paramInt1 > 127)
/*      */     {
/*  358 */       paramInt1 = 127;
/*      */     }
/*  360 */     if (paramInt2 < -128)
/*      */     {
/*  362 */       paramInt2 = -128;
/*  363 */     } else if (paramInt2 > 127)
/*      */     {
/*  365 */       paramInt2 = 127;
/*      */     }
/*  367 */     this.UI_dirty = true;
/*      */     
/*  369 */     if ((this.screen_x > 0) && (this.screen_y > 0))
/*      */     {
/*  371 */       paramInt3 = 3000 * paramInt3 / this.screen_x;
/*  372 */       paramInt4 = 3000 * paramInt4 / this.screen_y;
/*      */     }
/*      */     else
/*      */     {
/*  376 */       paramInt3 = 3000 * paramInt3 / 1;
/*  377 */       paramInt4 = 3000 * paramInt4 / 1;
/*      */     }
/*  379 */     char c1 = (char)(paramInt3 / 256);char c2 = (char)(paramInt3 % 256);
/*  380 */     char c3 = (char)(paramInt4 / 256);char c4 = (char)(paramInt4 % 256);
/*  381 */     if (this.mouse_protocol == 0)
/*      */     {
/*  383 */       transmit("ÿÐ" + (char)paramInt1 + "" + (char)paramInt2);
/*      */     }
/*      */     else {
/*  386 */       transmit("ÿÐ" + (char)paramInt1 + "" + (char)paramInt2 + "" + c1 + "" + c2 + "" + c3 + "" + c4);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public void mouse_mode_change(boolean paramBoolean)
/*      */   {
/*  393 */     char c = paramBoolean ? '\001' : '\002';
/*  394 */     transmit("ÿÕ" + c);
/*      */   }
/*      */   
/*      */
              public void mouseEntered(MouseEvent paramMouseEvent)
              {
                this.UI_dirty = true;
                setCursor(this.current_cursor);
                super.mouseEntered(paramMouseEvent);
              }

/*      */ 
/*      */ 
/*      */ 
/*      */   public void serverPress(int paramInt)
/*      */   {
/*  410 */     this.UI_dirty = true;
/*  411 */     send_mouse_press(paramInt);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void serverRelease(int paramInt)
/*      */   {
/*  420 */     this.UI_dirty = true;
/*  421 */     send_mouse_release(paramInt);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void serverClick(int paramInt1, int paramInt2)
/*      */   {
/*  430 */     this.UI_dirty = true;
/*  431 */     send_mouse_click(paramInt1, paramInt2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public synchronized void mouseExited(MouseEvent paramMouseEvent)
/*      */   {
/*  440 */     super.mouseExited(paramMouseEvent);
/*  441 */     setCursor(Cursor.getDefaultCursor());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void disable_keyboard()
/*      */   {
/*  450 */     this.disable_kbd = true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void enable_keyboard()
/*      */   {
/*  459 */     this.disable_kbd = false;
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
/*      */ 
/*      */ 
/*      */ 
/*      */   public void disable_altlock()
/*      */   {
/*  495 */     this.altlock = false;
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
/*      */   public void enable_altlock()
/*      */   {
/*  509 */     this.altlock = true;
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
/*      */   public synchronized void connect(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/*  523 */     char[] arrayOfChar = { 'ÿ', 'À' };
/*      */     
/*  525 */     if (this.encryption_enabled) {
/*  526 */       this.encryption_active = true;
/*  527 */       paramString2 = "" + arrayOfChar[0] + "" + arrayOfChar[1] + "    " + paramString2;
/*      */       
/*      */ 
/*  530 */       this.sending_encrypt_command = true;
/*      */     }
/*      */     
/*  533 */     super.connect(paramString1, paramString2, paramInt1, paramInt2, paramInt3);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public synchronized void transmit(String paramString)
/*      */   {
/*  544 */     if (this.out == null)
/*      */     {
/*  546 */       return;
/*      */     }
/*  548 */     if (paramString.length() != 0)
/*      */     {
/*  550 */       byte[] arrayOfByte = new byte[paramString.length()];
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       int i;
/*      */       
/*      */ 
/*      */ 
/*  559 */       if (this.encryption_active)
/*      */       {
/*  561 */         if (this.sending_encrypt_command)
/*      */         {
/*  563 */           arrayOfByte[0] = ((byte)paramString.charAt(0));
/*  564 */           arrayOfByte[1] = ((byte)paramString.charAt(1));
/*  565 */           arrayOfByte[2] = ((byte)((this.key_index & 0xFF000000) >>> 24));
/*  566 */           arrayOfByte[3] = ((byte)((this.key_index & 0xFF0000) >>> 16));
/*  567 */           arrayOfByte[4] = ((byte)((this.key_index & 0xFF00) >>> 8));
/*  568 */           arrayOfByte[5] = ((byte)((this.key_index & 0xFF) >>> 0));
/*      */           
/*      */ 
/*      */ 
/*  572 */           for (i = 6; i < paramString.length(); i++) {
/*  573 */             arrayOfByte[i] = ((byte)(paramString.charAt(i) ^ this.RC4encrypter.randomValue()));
/*      */           }
/*  575 */           this.sending_encrypt_command = false;
/*      */         }
/*      */         else
/*      */         {
/*  579 */           for (i = 0; i < paramString.length(); i++) {
/*  580 */             arrayOfByte[i] = ((byte)(paramString.charAt(i) ^ this.RC4encrypter.randomValue()));
/*      */           }
/*      */           
/*      */         }
/*      */       }
/*      */       else {
/*  586 */         for (i = 0; i < paramString.length(); i++)
/*      */         {
/*  588 */           arrayOfByte[i] = ((byte)paramString.charAt(i));
/*      */         }
/*      */       }
/*      */       
/*      */       try
/*      */       {
/*  594 */         this.out.write(arrayOfByte, 0, arrayOfByte.length);
/*      */       }
/*      */       catch (IOException localIOException) {}
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
/*      */   protected String translate_key(KeyEvent paramKeyEvent)
/*      */   {
/*  611 */     String str = "";
/*  612 */     int i = paramKeyEvent.getKeyChar();
/*  613 */     int j = 0;
/*  614 */     int k = 1;
/*      */     
/*  616 */     if (this.disable_kbd)
/*      */     {
/*  618 */       return "";
/*      */     }
/*      */     
/*  621 */     if (this.ignore_next_key)
/*      */     {
/*  623 */       this.ignore_next_key = false;
/*  624 */       return "";
/*      */     }
/*      */     
/*  627 */     this.UI_dirty = true;
/*  628 */     if (paramKeyEvent.isShiftDown())
/*      */     {
/*  630 */       j = 1;
/*  631 */     } else if (paramKeyEvent.isControlDown())
/*      */     {
/*  633 */       j = 2;
/*  634 */     } else if ((this.altlock) || (paramKeyEvent.isAltDown()))
/*      */     {
/*  636 */       j = 3;
/*  637 */       if (paramKeyEvent.isAltDown())
/*      */       {
/*  639 */         paramKeyEvent.consume();
/*      */       }
/*      */     }
/*      */     
/*  643 */     switch (i)
/*      */     {
/*      */ 
/*      */     case 27: 
/*  647 */       k = 0;
/*  648 */       break;
/*      */     
/*      */ 
/*      */     case 10: 
/*      */     case 13: 
/*  653 */       switch (j)
/*      */       {
/*      */       case 0: 
/*  656 */         str = "\r";
/*  657 */         break;
/*      */       
/*      */       case 1: 
/*  660 */         str = "\033[3\r";
/*  661 */         break;
/*      */       
/*      */       case 2: 
/*  664 */         str = "\n";
/*  665 */         break;
/*      */       
/*      */       case 3: 
/*  668 */         str = "\033[1\r";
/*      */       }
/*      */       
/*  671 */       k = 0;
/*  672 */       break;
/*      */     
/*      */ 
/*      */     case 8: 
/*  676 */       switch (j)
/*      */       {
/*      */       case 0: 
/*  679 */         str = "\b";
/*  680 */         break;
/*      */       
/*      */       case 1: 
/*  683 */         str = "\033[3\b";
/*  684 */         break;
/*      */       
/*      */       case 2: 
/*  687 */         str = "";
/*  688 */         break;
/*      */       
/*      */       case 3: 
/*  691 */         str = "\033[1\b";
/*      */       }
/*      */       
/*  694 */       k = 0;
/*  695 */       break;
/*      */     
/*      */     default: 
/*  698 */       str = super.translate_key(paramKeyEvent);
/*      */     }
/*      */     
/*      */     
/*  702 */     if ((k == 1) && (str.length() != 0) && (j == 3))
/*      */     {
/*  704 */       str = "\033[1" + str;
/*      */     }
/*  706 */     return str;
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
/*      */   protected String translate_special_key(KeyEvent paramKeyEvent)
/*      */   {
/*  720 */     String str = "";
/*  721 */     int i = 1;
/*  722 */     int j = 0;
/*      */     
/*  724 */     if (this.disable_kbd)
/*      */     {
/*  726 */       return "";
/*      */     }
/*      */     
/*  729 */     this.UI_dirty = true;
/*  730 */     if (paramKeyEvent.isShiftDown())
/*      */     {
/*  732 */       j = 1;
/*  733 */     } else if (paramKeyEvent.isControlDown())
/*      */     {
/*  735 */       j = 2;
/*  736 */     } else if ((this.altlock) || (paramKeyEvent.isAltDown()))
/*      */     {
/*  738 */       j = 3;
/*      */     }
/*      */     
/*  741 */     switch (paramKeyEvent.getKeyCode())
/*      */     {
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     case 27: 
/*  781 */       str = "\033";
/*  782 */       break;
/*      */     
/*      */     case 9: 
/*  785 */       paramKeyEvent.consume();
/*  786 */       str = "\t";
/*  787 */       break;
/*      */     
/*      */     case 127: 
/*  790 */       if ((paramKeyEvent.isControlDown()) && ((this.altlock) || (paramKeyEvent.isAltDown())))
/*      */       {
/*      */ 
/*  793 */         send_ctrl_alt_del();
/*  794 */         return "";
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  799 */       if (System.getProperty("java.version", "0").compareTo("1.4.2") < 0) {
/*  800 */         str = "";
/*      */       }
/*      */       
/*      */       break;
/*      */     case 36: 
/*  805 */       str = "\033[H";
/*  806 */       break;
/*      */     
/*      */     case 35: 
/*  809 */       str = "\033[F";
/*  810 */       break;
/*      */     
/*      */     case 33: 
/*  813 */       str = "\033[I";
/*  814 */       break;
/*      */     
/*      */     case 34: 
/*  817 */       str = "\033[G";
/*  818 */       break;
/*      */     
/*      */     case 155: 
/*  821 */       str = "\033[L";
/*  822 */       break;
/*      */     
/*      */     case 38: 
/*  825 */       str = "\033[A";
/*  826 */       break;
/*      */     
/*      */     case 40: 
/*  829 */       str = "\033[B";
/*  830 */       break;
/*      */     
/*      */     case 37: 
/*  833 */       str = "\033[D";
/*  834 */       break;
/*      */     
/*      */     case 39: 
/*  837 */       str = "\033[C";
/*  838 */       break;
/*      */     
/*      */     case 112: 
/*  841 */       switch (j)
/*      */       {
/*      */       case 0: 
/*  844 */         str = "\033[M";
/*  845 */         break;
/*      */       
/*      */       case 1: 
/*  848 */         str = "\033[Y";
/*  849 */         break;
/*      */       
/*      */       case 2: 
/*  852 */         str = "\033[k";
/*  853 */         break;
/*      */       
/*      */       case 3: 
/*  856 */         str = "\033[w";
/*      */       }
/*      */       
/*  859 */       paramKeyEvent.consume();
/*  860 */       i = 0;
/*  861 */       break;
/*      */     
/*      */     case 113: 
/*  864 */       switch (j)
/*      */       {
/*      */       case 0: 
/*  867 */         str = "\033[N";
/*  868 */         break;
/*      */       
/*      */       case 1: 
/*  871 */         str = "\033[Z";
/*  872 */         break;
/*      */       
/*      */       case 2: 
/*  875 */         str = "\033[l";
/*  876 */         break;
/*      */       
/*      */       case 3: 
/*  879 */         str = "\033[x";
/*      */       }
/*      */       
/*  882 */       paramKeyEvent.consume();
/*  883 */       i = 0;
/*  884 */       break;
/*      */     
/*      */     case 114: 
/*  887 */       switch (j)
/*      */       {
/*      */       case 0: 
/*  890 */         str = "\033[O";
/*  891 */         break;
/*      */       
/*      */       case 1: 
/*  894 */         str = "\033[a";
/*  895 */         break;
/*      */       
/*      */       case 2: 
/*  898 */         str = "\033[m";
/*  899 */         break;
/*      */       
/*      */       case 3: 
/*  902 */         str = "\033[y";
/*      */       }
/*      */       
/*  905 */       paramKeyEvent.consume();
/*  906 */       i = 0;
/*  907 */       break;
/*      */     
/*      */     case 115: 
/*  910 */       switch (j)
/*      */       {
/*      */       case 0: 
/*  913 */         str = "\033[P";
/*  914 */         break;
/*      */       
/*      */       case 1: 
/*  917 */         str = "\033[b";
/*  918 */         break;
/*      */       
/*      */       case 2: 
/*  921 */         str = "\033[n";
/*  922 */         break;
/*      */       
/*      */       case 3: 
/*  925 */         str = "\033[z";
/*      */       }
/*      */       
/*  928 */       paramKeyEvent.consume();
/*  929 */       i = 0;
/*  930 */       break;
/*      */     
/*      */     case 116: 
/*  933 */       switch (j)
/*      */       {
/*      */       case 0: 
/*  936 */         str = "\033[Q";
/*  937 */         break;
/*      */       
/*      */       case 1: 
/*  940 */         str = "\033[c";
/*  941 */         break;
/*      */       
/*      */       case 2: 
/*  944 */         str = "\033[o";
/*  945 */         break;
/*      */       
/*      */       case 3: 
/*  948 */         str = "\033[@";
/*      */       }
/*      */       
/*  951 */       paramKeyEvent.consume();
/*  952 */       i = 0;
/*  953 */       break;
/*      */     
/*      */     case 117: 
/*  956 */       switch (j)
/*      */       {
/*      */       case 0: 
/*  959 */         str = "\033[R";
/*  960 */         break;
/*      */       
/*      */       case 1: 
/*  963 */         str = "\033[d";
/*  964 */         break;
/*      */       
/*      */       case 2: 
/*  967 */         str = "\033[p";
/*  968 */         break;
/*      */       
/*      */       case 3: 
/*  971 */         str = "\033[[";
/*      */       }
/*      */       
/*  974 */       paramKeyEvent.consume();
/*  975 */       i = 0;
/*  976 */       break;
/*      */     
/*      */     case 118: 
/*  979 */       switch (j)
/*      */       {
/*      */       case 0: 
/*  982 */         str = "\033[S";
/*  983 */         break;
/*      */       
/*      */       case 1: 
/*  986 */         str = "\033[e";
/*  987 */         break;
/*      */       
/*      */       case 2: 
/*  990 */         str = "\033[q";
/*  991 */         break;
/*      */       
/*      */       case 3: 
/*  994 */         str = "\033[\\";
/*      */       }
/*      */       
/*  997 */       paramKeyEvent.consume();
/*  998 */       i = 0;
/*  999 */       break;
/*      */     
/*      */     case 119: 
/* 1002 */       switch (j)
/*      */       {
/*      */       case 0: 
/* 1005 */         str = "\033[T";
/* 1006 */         break;
/*      */       
/*      */       case 1: 
/* 1009 */         str = "\033[f";
/* 1010 */         break;
/*      */       
/*      */       case 2: 
/* 1013 */         str = "\033[r";
/* 1014 */         break;
/*      */       
/*      */       case 3: 
/* 1017 */         str = "\033[]";
/*      */       }
/*      */       
/* 1020 */       paramKeyEvent.consume();
/* 1021 */       i = 0;
/* 1022 */       break;
/*      */     
/*      */     case 120: 
/* 1025 */       switch (j)
/*      */       {
/*      */       case 0: 
/* 1028 */         str = "\033[U";
/* 1029 */         break;
/*      */       
/*      */       case 1: 
/* 1032 */         str = "\033[g";
/* 1033 */         break;
/*      */       
/*      */       case 2: 
/* 1036 */         str = "\033[s";
/* 1037 */         break;
/*      */       
/*      */       case 3: 
/* 1040 */         str = "\033[^";
/*      */       }
/*      */       
/* 1043 */       paramKeyEvent.consume();
/* 1044 */       i = 0;
/* 1045 */       break;
/*      */     
/*      */     case 121: 
/* 1048 */       switch (j)
/*      */       {
/*      */       case 0: 
/* 1051 */         str = "\033[V";
/* 1052 */         break;
/*      */       
/*      */       case 1: 
/* 1055 */         str = "\033[h";
/* 1056 */         break;
/*      */       
/*      */       case 2: 
/* 1059 */         str = "\033[t";
/* 1060 */         break;
/*      */       
/*      */       case 3: 
/* 1063 */         str = "\033[_";
/*      */       }
/*      */       
/* 1066 */       paramKeyEvent.consume();
/* 1067 */       i = 0;
/* 1068 */       break;
/*      */     
/*      */     case 122: 
/* 1071 */       switch (j)
/*      */       {
/*      */       case 0: 
/* 1074 */         str = "\033[W";
/* 1075 */         break;
/*      */       
/*      */       case 1: 
/* 1078 */         str = "\033[i";
/* 1079 */         break;
/*      */       
/*      */       case 2: 
/* 1082 */         str = "\033[u";
/* 1083 */         break;
/*      */       
/*      */       case 3: 
/* 1086 */         str = "\033[`";
/*      */       }
/*      */       
/* 1089 */       paramKeyEvent.consume();
/* 1090 */       i = 0;
/* 1091 */       break;
/*      */     
/*      */     case 123: 
/* 1094 */       switch (j)
/*      */       {
/*      */       case 0: 
/* 1097 */         str = "\033[X";
/* 1098 */         break;
/*      */       
/*      */       case 1: 
/* 1101 */         str = "\033[j";
/* 1102 */         break;
/*      */       
/*      */       case 2: 
/* 1105 */         str = "\033[v";
/* 1106 */         break;
/*      */       
/*      */       case 3: 
/* 1109 */         str = "\033['";
/*      */       }
/*      */       
/* 1112 */       paramKeyEvent.consume();
/* 1113 */       i = 0;
/* 1114 */       break;
/*      */     
/*      */     default: 
/* 1117 */       i = 0;
/* 1118 */       str = super.translate_special_key(paramKeyEvent);
/*      */     }
/*      */     
/*      */     
/* 1122 */     if (str.length() != 0)
/*      */     {
/* 1124 */       if (i == 1)
/*      */       {
/* 1126 */         switch (j)
/*      */         {
/*      */         case 1: 
/* 1129 */           str = "\033[3" + str;
/* 1130 */           break;
/*      */         
/*      */         case 2: 
/* 1133 */           str = "\033[2" + str;
/* 1134 */           break;
/*      */         
/*      */         case 3: 
/* 1137 */           str = "\033[1" + str;
/*      */         }
/*      */         
/*      */       }
/*      */     }
/*      */     
/* 1143 */     return str;
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
/*      */   protected String translate_special_key_release(KeyEvent paramKeyEvent)
/*      */   {
/* 1167 */     String str = "";
/* 1168 */     int i = 0;
/*      */     
/* 1170 */     if (paramKeyEvent.isShiftDown())
/*      */     {
/* 1172 */       i = 1;
/*      */     }
/*      */     
/* 1175 */     if ((this.altlock) || (paramKeyEvent.isAltDown()))
/*      */     {
/* 1177 */       i += 2;
/*      */     }
/*      */     
/* 1180 */     if (paramKeyEvent.isControlDown())
/*      */     {
/* 1182 */       i += 4;
/*      */     }
/*      */     
/* 1185 */     switch (paramKeyEvent.getKeyCode())
/*      */     {
/*      */     case 243: 
/*      */     case 244: 
/*      */     case 263: 
/* 1190 */       i += 128;
/* 1191 */       break;
/*      */     case 29: 
/* 1193 */       i += 136;
/* 1194 */       break;
/*      */     case 28: 
/*      */     case 256: 
/*      */     case 257: 
/* 1198 */       i += 144;
/* 1199 */       break;
/*      */     case 241: 
/*      */     case 242: 
/*      */     case 245: 
/* 1203 */       i += 152;
/*      */     }
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
/* 1215 */     if (i > 127)
/*      */     {
/* 1217 */       str = "" + (char)i;
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 1222 */       str = "";
/*      */     }
/*      */     
/* 1225 */     return str;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void send_ctrl_alt_del()
/*      */   {
/* 1233 */     transmit("\033[2\033[");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void send_mouse_press(int paramInt)
/*      */   {
/* 1241 */     transmit("ÿÑ" + (char)paramInt);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void send_mouse_release(int paramInt)
/*      */   {
/* 1249 */     transmit("ÿÒ" + (char)paramInt);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void send_mouse_click(int paramInt1, int paramInt2)
/*      */   {
/* 1257 */     transmit("ÿÓ" + (char)paramInt1 + "" + (char)paramInt2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void send_mouse_byte(int paramInt)
/*      */   {
/* 1266 */     transmit("ÿÔ" + (char)paramInt);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void refresh_screen()
/*      */   {
/* 1274 */     transmit("\033[~");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void send_keep_alive_msg()
/*      */   {
/* 1282 */     transmit("\033[(");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String byteToHex(byte paramByte)
/*      */   {
/* 1294 */     StringBuffer localStringBuffer = new StringBuffer();
/* 1295 */     localStringBuffer.append(toHexChar(paramByte >>> 4 & 0xF));
/* 1296 */     localStringBuffer.append(toHexChar(paramByte & 0xF));
/* 1297 */     return localStringBuffer.toString();
/*      */   }
/*      */   
/*      */   public static String intToHex(int paramInt)
/*      */   {
/* 1302 */     byte b = (byte)paramInt;
/* 1303 */     return byteToHex(b);
/*      */   }
/*      */   
/*      */   public static String intToHex4(int paramInt) {
/* 1307 */     StringBuffer localStringBuffer = new StringBuffer();
/* 1308 */     localStringBuffer.append(byteToHex((byte)(paramInt / 256)));
/* 1309 */     localStringBuffer.append(byteToHex((byte)(paramInt & 0xFF)));
/* 1310 */     return localStringBuffer.toString();
/*      */   }
/*      */   
/*      */ 
/*      */   public static String charToHex(char paramChar)
/*      */   {
/* 1316 */     byte b = (byte)paramChar;
/* 1317 */     return byteToHex(b);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static char toHexChar(int paramInt)
/*      */   {
/* 1327 */     if ((0 <= paramInt) && (paramInt <= 9))
/*      */     {
/* 1329 */       return (char)(48 + paramInt);
/*      */     }
/*      */     
/* 1332 */     return (char)(65 + (paramInt - 10));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected synchronized void set_framerate(int paramInt)
/*      */   {
/* 1339 */     framerate = paramInt;
/* 1340 */     this.screen.set_framerate(paramInt);
/* 1341 */     set_status(3, "" + framerate);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void show_error(String paramString)
/*      */   {
/* 1382 */     System.out.println("dvc:" + paramString + ": state " + dvc_decoder_state + " code " + dvc_code);
/* 1383 */     System.out.println("dvc:error at byte count " + count_bytes);
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
/*      */   final void cache_reset()
/*      */   {
/* 1399 */     dvc_cc_active = 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   final int cache_lru(int paramInt)
/*      */   {
/* 1410 */     int k = dvc_cc_active;
/* 1411 */     int j = 0;
/* 1412 */     int n = 0;
/*      */     
/*      */ 
/* 1415 */     for (int i = 0; i < k; i++)
/*      */     {
/* 1417 */       if (paramInt == dvc_cc_color[i])
/*      */       {
/*      */ 
/*      */ 
/* 1421 */         j = i;
/*      */         
/* 1423 */         n = 1;
/* 1424 */         break; }
/* 1425 */       if (dvc_cc_usage[i] == k - 1) {
/* 1426 */         j = i;
/*      */       }
/*      */     }
/*      */     
/* 1430 */     int m = dvc_cc_usage[j];
/*      */     
/* 1432 */     if (n == 0)
/*      */     {
/*      */ 
/* 1435 */       if (k < 17)
/*      */       {
/*      */ 
/* 1438 */         j = k;
/* 1439 */         m = k;
/* 1440 */         k++;
/* 1441 */         dvc_cc_active = k;
/*      */         
/* 1443 */         if (dvc_cc_active < 2) {
/* 1444 */           dvc_pixcode = 38;
/* 1445 */         } else if (dvc_cc_active == 2) {
/* 1446 */           dvc_pixcode = 4;
/* 1447 */         } else if (dvc_cc_active == 3) {
/* 1448 */           dvc_pixcode = 5;
/* 1449 */         } else if (dvc_cc_active < 6) {
/* 1450 */           dvc_pixcode = 6;
/* 1451 */         } else if (dvc_cc_active < 10) {
/* 1452 */           dvc_pixcode = 7;
/*      */         } else
/* 1454 */           dvc_pixcode = 32;
/* 1455 */         next_1[31] = dvc_pixcode;
/*      */       }
/*      */       
/*      */ 
/* 1459 */       dvc_cc_color[j] = paramInt;
/*      */     }
/*      */     
/* 1462 */     dvc_cc_block[j] = 1;
/*      */     
/*      */ 
/* 1465 */     for (int i = 0; i < k; i++)
/*      */     {
/* 1467 */       if (dvc_cc_usage[i] < m) {
/* 1468 */         dvc_cc_usage[i] += 1;
/*      */       }
/*      */     }
/* 1471 */     dvc_cc_usage[j] = 0;
/* 1472 */     return n;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   final int cache_find(int paramInt)
/*      */   {
/* 1482 */     int i = dvc_cc_active;
/*      */     
/* 1484 */     for (int j = 0; j < i; j++)
/*      */     {
/* 1486 */       if (paramInt == dvc_cc_usage[j])
/*      */       {
/*      */ 
/* 1489 */         int m = dvc_cc_color[j];
/* 1490 */         int k = j;
/*      */         
/* 1492 */         for (j = 0; j < i; j++)
/*      */         {
/* 1494 */           if (dvc_cc_usage[j] < paramInt) {
/* 1495 */             dvc_cc_usage[j] += 1;
/*      */           }
/*      */         }
/* 1498 */         dvc_cc_usage[k] = 0;
/* 1499 */         dvc_cc_block[k] = 1;
/* 1500 */         return m;
/*      */       }
/*      */     }
/* 1503 */     return -1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   final void cache_prune()
/*      */   {
/* 1512 */     int j = dvc_cc_active;
/*      */     
/*      */ 
/* 1515 */     for (int i = 0; i < j;)
/*      */     {
/* 1517 */       int k = dvc_cc_block[i];
/* 1518 */       if (k == 0)
/*      */       {
/*      */ 
/* 1521 */         j--;
/* 1522 */         dvc_cc_block[i] = dvc_cc_block[j];
/* 1523 */         dvc_cc_color[i] = dvc_cc_color[j];
/* 1524 */         dvc_cc_usage[i] = dvc_cc_usage[j];
/*      */       }
/*      */       else {
/* 1527 */         dvc_cc_block[i] -= 1;
/* 1528 */         i++;
/*      */       }
/*      */     }
/* 1531 */     dvc_cc_active = j;
/* 1532 */     if (dvc_cc_active < 2) {
/* 1533 */       dvc_pixcode = 38;
/* 1534 */     } else if (dvc_cc_active == 2) {
/* 1535 */       dvc_pixcode = 4;
/* 1536 */     } else if (dvc_cc_active == 3) {
/* 1537 */       dvc_pixcode = 5;
/* 1538 */     } else if (dvc_cc_active < 6) {
/* 1539 */       dvc_pixcode = 6;
/* 1540 */     } else if (dvc_cc_active < 10) {
/* 1541 */       dvc_pixcode = 7;
/*      */     } else
/* 1543 */       dvc_pixcode = 32;
/* 1544 */     next_1[31] = dvc_pixcode;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void next_block(int paramInt)
/*      */   {
/* 1553 */     int k = 1;
/* 1554 */     if (!video_detected) {
/* 1555 */       k = 0;
/*      */     }
/*      */     
/* 1558 */     if (dvc_pixel_count != 0)
/*      */     {
/*      */ 
/* 1561 */       if ((dvc_y_clipped > 0) && (dvc_lasty == dvc_size_y))
/*      */       {
/*      */ 
/*      */ 
/* 1565 */         int m = this.color_remap_table[0];
/* 1566 */         for (int j = dvc_y_clipped; j < 256; j++) {
/* 1567 */           block[j] = m;
/*      */         }
/*      */       }
/*      */     }
/* 1571 */     dvc_pixel_count = 0;
/* 1572 */     dvc_next_state = 1;
/*      */     
/* 1574 */     int i = dvc_lastx * 16;
/* 1575 */     int j = dvc_lasty * 16;
/* 1576 */     while (paramInt != 0)
/*      */     {
/* 1578 */       if (k != 0)
/*      */       {
/* 1580 */         this.screen.paste_array(block, i, j, 16);
/*      */       }
/*      */       
/* 1583 */       dvc_lastx += 1;
/* 1584 */       i += 16;
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
/* 1600 */       if (dvc_lastx >= dvc_size_x)
/*      */         break;
/* 1602 */       paramInt--;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void init_reversal()
/*      */   {
/* 1613 */     for (int i = 0; i < 256; i++)
/*      */     {
/* 1615 */       int i1 = 8;
/* 1616 */       int n = 8;
/* 1617 */       int k = i;
/* 1618 */       int m = 0;
/* 1619 */       for (int j = 0; j < 8; j++)
/*      */       {
/* 1621 */         m <<= 1;
/* 1622 */         if ((k & 0x1) == 1)
/*      */         {
/* 1624 */           if (i1 > j)
/* 1625 */             i1 = j;
/* 1626 */           m |= 0x1;
/* 1627 */           n = 7 - j;
/*      */         }
/* 1629 */         k >>= 1;
/*      */       }
/* 1631 */       dvc_reversal[i] = m;
/* 1632 */       dvc_right[i] = i1;
/* 1633 */       dvc_left[i] = n;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   final int add_bits(char paramChar)
/*      */   {
/* 1641 */     dvc_zero_count += dvc_right[paramChar];
/*      */     
/*      */ 
/* 1644 */     int i = paramChar;
/* 1645 */     dvc_ib_acc |= i << dvc_ib_bcnt;
/*      */     
/* 1647 */     dvc_ib_bcnt += 8;
/*      */     
/* 1649 */     if (dvc_zero_count > 30)
/*      */     {
/*      */ 
/* 1652 */       if (debug_msgs)
/*      */       {
/* 1654 */         if ((dvc_decoder_state == 38) && (fatal_count < 40) && (fatal_count > 0))
/*      */         {
/* 1656 */           System.out.println("reset caused a false alarm");
/*      */         }
/*      */         else
/*      */         {
/* 1660 */           System.out.println("Reset sequence detected at " + count_bytes);
/*      */         }
/*      */       }
/* 1663 */       dvc_next_state = 43;
/* 1664 */       dvc_decoder_state = 43;
/* 1665 */       return 4;
/*      */     }
/*      */     
/* 1668 */     if (paramChar != 0) {
/* 1669 */       dvc_zero_count = dvc_left[paramChar];
/*      */     }
/* 1671 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   final int get_bits(int paramInt)
/*      */   {
/* 1681 */     if (paramInt == 1)
/*      */     {
/* 1683 */       dvc_code = dvc_ib_acc & 0x1;
/* 1684 */       dvc_ib_acc >>= 1;
/* 1685 */       dvc_ib_bcnt -= 1;
/* 1686 */       return 0;
/*      */     }
/*      */     
/*      */ 
/* 1690 */     if (paramInt == 0) {
/* 1691 */       return 0;
/*      */     }
/*      */     
/* 1694 */     int i = dvc_ib_acc & dvc_getmask[paramInt];
/*      */     
/*      */ 
/* 1697 */     dvc_ib_bcnt -= paramInt;
/*      */     
/*      */ 
/* 1700 */     dvc_ib_acc >>= paramInt;
/*      */     
/*      */ 
/* 1703 */     i = dvc_reversal[i];
/*      */     
/*      */ 
/* 1706 */     i >>= 8 - paramInt;
/*      */     
/* 1708 */     dvc_code = i;
/*      */     
/*      */ 
/* 1711 */     return 0;
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
/*      */   int process_bits(char paramChar)
/*      */   {
/* 1725 */     int n = 1;
/*      */     
/* 1727 */     int m = 0;
/*      */     
/*      */ 
/* 1730 */     add_bits(paramChar);
/* 1731 */     dvc_new_bits = paramChar;
/* 1732 */     count_bytes += 1L;
/* 1733 */     int k = 0;
/*      */     
/*      */     label2353:
/* 1736 */     while (m == 0)
/*      */     {
/* 1738 */       k = bits_to_read[dvc_decoder_state];
/*      */       
/* 1740 */       if (k > dvc_ib_bcnt)
/*      */       {
/*      */ 
/* 1743 */         m = 0;
/* 1744 */         break;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1749 */       int i = get_bits(k);
/* 1750 */       dvc_counter_bits += k;
/*      */       
/*      */ 
/* 1753 */       if (dvc_code == 0) {
/* 1754 */         dvc_next_state = next_0[dvc_decoder_state];
/*      */       } else {
/* 1756 */         dvc_next_state = next_1[dvc_decoder_state];
/*      */       }
/*      */       
/*      */       int j;
/* 1760 */       switch (dvc_decoder_state)
/*      */       {
/*      */ 
/*      */ 
/*      */       case 3: 
/*      */       case 4: 
/*      */       case 5: 
/*      */       case 6: 
/*      */       case 7: 
/*      */       case 32: 
/* 1770 */         if (dvc_cc_active == 1) {
/* 1771 */           dvc_code = dvc_cc_usage[0];
/* 1772 */         } else if (dvc_decoder_state == 4) {
/* 1773 */           dvc_code = 0;
/* 1774 */         } else if (dvc_decoder_state == 3) {
/* 1775 */           dvc_code = 1;
/* 1776 */         } else if (dvc_code != 0) {
/* 1777 */           dvc_code += 1;
/*      */         }
/* 1779 */         dvc_color = cache_find(dvc_code);
/* 1780 */         if (dvc_color == -1)
/*      */         {
/*      */ 
/* 1783 */           show_error("could not find color for LRU " + dvc_code + ", cache has " + dvc_cc_active + " colors");
/* 1784 */           dvc_next_state = 38;
/*      */         }
/*      */         else {
/* 1787 */           dvc_last_color = this.color_remap_table[dvc_color];
/*      */           
/* 1789 */           if (dvc_pixel_count < 256) {
/* 1790 */             block[dvc_pixel_count] = dvc_last_color;
/*      */           }
/*      */           else
/*      */           {
/* 1794 */             System.out.println("dvc:too many block0");
/* 1795 */             dvc_next_state = 38;
/*      */             break label2353;
/*      */           }
/* 1798 */           dvc_pixel_count += 1; }
/* 1799 */         break;
/*      */       
/*      */       case 12: 
/* 1802 */         if (dvc_code == 7) {
/* 1803 */           dvc_next_state = 14;
/* 1804 */         } else if (dvc_code == 6) {
/* 1805 */           dvc_next_state = 13;
/*      */         }
/*      */         else
/*      */         {
/* 1809 */           dvc_code += 2;
/* 1810 */           for (j = 0; j < dvc_code; j++)
/*      */           {
/*      */ 
/* 1813 */             if (dvc_pixel_count < 256) {
/* 1814 */               block[dvc_pixel_count] = dvc_last_color;
/*      */             }
/*      */             else
/*      */             {
/* 1818 */               show_error("too many pixels in a block2");
/* 1819 */               dvc_next_state = 38;
/* 1820 */               break;
/*      */             }
/* 1822 */             dvc_pixel_count += 1;
/*      */           }
/*      */         }
/* 1825 */         break;
/*      */       case 13: 
/* 1827 */         dvc_code += 8;
/*      */       
/*      */       case 14: 
/* 1830 */         if ((dvc_decoder_state == 14) && (dvc_code < 16))
/*      */         {
/*      */ 
/* 1833 */           if (debug_msgs)
/*      */           {
/* 1835 */             System.out.println("dvc:non-std repeat misused");
/*      */           }
/*      */         }
/* 1838 */         for (j = 0; j < dvc_code; j++)
/*      */         {
/*      */ 
/* 1841 */           if (dvc_pixel_count < 256) {
/* 1842 */             block[dvc_pixel_count] = dvc_last_color;
/*      */           }
/*      */           else
/*      */           {
/* 1846 */             show_error("too many pixels in a block3");
/* 1847 */             dvc_next_state = 38;
/* 1848 */             break;
/*      */           }
/* 1850 */           dvc_pixel_count += 1;
/*      */         }
/* 1852 */         break;
/*      */       
/*      */       case 33: 
/* 1855 */         if (dvc_pixel_count < 256) {
/* 1856 */           block[dvc_pixel_count] = dvc_last_color;
/*      */         }
/*      */         else
/*      */         {
/* 1860 */           show_error("too many pixels in a block4");
/* 1861 */           dvc_next_state = 38;
/*      */           break label2353;
/*      */         }
/* 1864 */         dvc_pixel_count += 1;
/* 1865 */         break;
/*      */       
/*      */       case 1: 
/*      */       case 2: 
/*      */       case 10: 
/*      */       case 11: 
/*      */       case 22: 
/*      */       case 28: 
/*      */       case 31: 
/*      */       case 36: 
/*      */         break;
/*      */       
/*      */       case 35: 
/* 1878 */         dvc_next_state = dvc_pixcode;
/* 1879 */         break;
/*      */       case 9: 
/* 1881 */         dvc_red = dvc_code << 8;
/* 1882 */         break;
/*      */       case 41: 
/* 1884 */         dvc_green = dvc_code << 4;
/* 1885 */         break;
/*      */       case 8: 
/* 1887 */         dvc_red = dvc_code << 8;
/* 1888 */         dvc_green = dvc_code << 4;
/*      */       
/*      */       case 42: 
/* 1891 */         dvc_blue = dvc_code;
/* 1892 */         dvc_color = dvc_red | dvc_green | dvc_blue;
/* 1893 */         i = cache_lru(dvc_color);
/* 1894 */         if (i != 0)
/*      */         {
/* 1896 */           if (debug_msgs)
/*      */           {
/* 1898 */             if (count_bytes > 6L)
/*      */             {
/* 1900 */               show_error("unexpected hit: color " + intToHex4(dvc_color));
/*      */             }
/*      */             else {
/* 1903 */               show_error("possible reset underway: color " + intToHex4(dvc_color));
/*      */             }
/*      */           }
/* 1906 */           dvc_next_state = 38;
/*      */         }
/*      */         else {
/* 1909 */           dvc_last_color = this.color_remap_table[dvc_color];
/*      */           
/* 1911 */           if (dvc_pixel_count < 256) {
/* 1912 */             block[dvc_pixel_count] = dvc_last_color;
/*      */           }
/*      */           else
/*      */           {
/* 1916 */             System.out.println("dvc:too many block1");
/* 1917 */             dvc_next_state = 38;
/*      */             break label2353;
/*      */           }
/* 1920 */           dvc_pixel_count += 1; }
/* 1921 */         break;
/*      */       case 17: 
/*      */       case 26: 
/* 1924 */         dvc_newx = dvc_code;
/* 1925 */         if ((dvc_decoder_state == 17) && (dvc_newx > dvc_size_x))
/*      */         {
/* 1927 */           if (debug_msgs)
/*      */           {
/* 1929 */             System.out.print("dvc:movexy moves x beyond screen " + dvc_newx);
/* 1930 */             System.out.println(" byte count " + count_bytes);
/*      */           }
/* 1932 */           dvc_newx = 0;
/*      */         }
/*      */         
/*      */         break;
/*      */       case 39: 
/* 1937 */         dvc_newy = dvc_code & 0x7F;
/*      */         
/* 1939 */         dvc_lastx = dvc_newx;
/* 1940 */         dvc_lasty = dvc_newy;
/*      */         
/* 1942 */         if (dvc_lasty > dvc_size_y)
/*      */         {
/* 1944 */           if (debug_msgs)
/*      */           {
/* 1946 */             System.out.print("dvc:movexy moves y beyond screen " + dvc_lasty);
/* 1947 */             System.out.println(" byte count " + count_bytes);
/*      */           }
/* 1949 */           dvc_lasty = 0;
/*      */         }
/* 1951 */         this.screen.repaint_it(1);
/* 1952 */         break;
/*      */       
/*      */ 
/*      */       case 20: 
/* 1956 */         dvc_code = dvc_lastx + dvc_code + 1;
/* 1957 */         if (dvc_code > dvc_size_x)
/*      */         {
/* 1959 */           if (debug_msgs)
/*      */           {
/* 1961 */             System.out.print("dvc:short x moves beyond screen " + dvc_code + " lastx " + dvc_lastx);
/* 1962 */             System.out.println(" byte count " + count_bytes);
/*      */           }
/*      */         }
/*      */       
/*      */       case 21: 
/* 1967 */         dvc_lastx = dvc_code & 0x7F;
/* 1968 */         if (dvc_lastx > dvc_size_x)
/*      */         {
/* 1970 */           if (debug_msgs)
/*      */           {
/* 1972 */             System.out.print("dvc:long x moves beyond screen " + dvc_lastx);
/* 1973 */             System.out.println(" byte count " + count_bytes);
/*      */           }
/* 1975 */           dvc_lastx = 0;
/*      */         }
/*      */         
/*      */ 
/*      */         break;
/*      */       case 27: 
/* 1981 */         if (timeout_count == count_bytes - 1L)
/*      */         {
/* 1983 */           show_error("double timeout at " + count_bytes + ", remaining bits " + (dvc_ib_bcnt & 0x7));
/* 1984 */           dvc_next_state = 38;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1990 */         if ((dvc_ib_bcnt & 0x7) != 0)
/* 1991 */           get_bits(dvc_ib_bcnt & 0x7);
/* 1992 */         timeout_count = count_bytes;
/*      */         
/* 1994 */         this.screen.repaint_it(1);
/* 1995 */         break;
/*      */       
/*      */ 
/*      */       case 24: 
/* 1999 */         if (cmd_p_count != 0)
/* 2000 */           cmd_p_buff[(cmd_p_count - 1)] = cmd_last;
/* 2001 */         cmd_p_count += 1;
/*      */         
/* 2003 */         cmd_last = dvc_code;
/* 2004 */         break;
/*      */       
/*      */       case 46: 
/* 2007 */         if (dvc_code == 0)
/*      */         {
/*      */ 
/*      */ 
/* 2011 */           switch (cmd_last)
/*      */           {
/*      */           case 1: 
/* 2014 */             dvc_next_state = 37;
/* 2015 */             break;
/*      */           
/*      */ 
/*      */           case 2: 
/* 2019 */             dvc_next_state = 44;
/* 2020 */             break;
/*      */           
/*      */ 
/*      */           case 3: 
/* 2024 */             if (cmd_p_count != 0) {
/* 2025 */               set_framerate(cmd_p_buff[0]);
/*      */             } else
/* 2027 */               set_framerate(0);
/* 2028 */             break;
/*      */           case 4: 
/*      */           case 5: 
/*      */             break;
/*      */           case 6: 
/* 2033 */             this.screen.show_text("Video suspended");
/* 2034 */             set_status(2, "Video_suspended");
/* 2035 */             this.screen_x = 640;
/* 2036 */             this.screen_y = 100;
/* 2037 */             break;
/*      */           case 7: 
/* 2039 */             this.ts_type = cmd_p_buff[0];
/* 2040 */             startRdp();
/* 2041 */             break;
/*      */           case 8: 
/* 2043 */             stop_rdp();
/* 2044 */             break;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           case 9: 
/* 2055 */             if ((dvc_ib_bcnt & 0x7) != 0) {
/* 2056 */               get_bits(dvc_ib_bcnt & 0x7);
/*      */             }
/* 2058 */             change_key();
/* 2059 */             break;
/*      */           case 10: 
/* 2061 */             seize();
/* 2062 */             break;
/*      */           default: 
/* 2064 */             System.out.println("dvc: unknown firmware command " + cmd_last);
/*      */           }
/*      */           
/* 2067 */           cmd_p_count = 0;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         break;
/*      */       case 44: 
/* 2077 */         printchan = dvc_code;
/* 2078 */         printstring = "";
/* 2079 */         break;
/*      */       
/*      */ 
/*      */       case 45: 
/* 2083 */         if (dvc_code != 0)
/*      */         {
/* 2085 */           printstring += (char)dvc_code;
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/* 2091 */           switch (printchan)
/*      */           {
/*      */           case 1: 
/*      */           case 2: 
/* 2095 */             set_status(2 + printchan, printstring);
/* 2096 */             break;
/*      */           case 3: 
/* 2098 */             System.out.println(printstring);
/* 2099 */             break;
/*      */           
/*      */           case 4: 
/* 2102 */             this.screen.show_text(printstring);
/*      */           }
/*      */           
/*      */           
/* 2106 */           dvc_next_state = 1;
/*      */         }
/* 2108 */         break;
/*      */       
/*      */ 
/*      */       case 15: 
/*      */       case 16: 
/*      */       case 18: 
/*      */       case 19: 
/*      */       case 23: 
/*      */       case 25: 
/*      */         break;
/*      */       
/*      */ 
/*      */ 
/*      */       case 0: 
/* 2122 */         cache_reset();
/* 2123 */         dvc_pixel_count = 0;
/* 2124 */         dvc_lastx = 0;
/* 2125 */         dvc_lasty = 0;
/* 2126 */         dvc_red = 0;
/* 2127 */         dvc_green = 0;
/* 2128 */         dvc_blue = 0;
/* 2129 */         fatal_count = 0;
/* 2130 */         timeout_count = -1L;
/*      */         
/* 2132 */         cmd_p_count = 0;
/* 2133 */         break;
/*      */       
/*      */       case 38: 
/* 2136 */         if (fatal_count == 0)
/*      */         {
/*      */ 
/* 2139 */           debug_lastx = dvc_lastx;
/* 2140 */           debug_lasty = dvc_lasty;
/* 2141 */           debug_show_block = 1;
/*      */         }
/* 2143 */         if (fatal_count == 40)
/*      */         {
/*      */ 
/* 2146 */           System.out.print("Latched: byte count " + count_bytes);
/* 2147 */           System.out.println(" current block at " + dvc_lastx + " " + dvc_lasty);
/*      */         }
/*      */         
/* 2150 */         if (fatal_count == 11680)
/*      */         {
/* 2152 */           refresh_screen();
/*      */         }
/* 2154 */         fatal_count += 1;
/* 2155 */         if (fatal_count == 120000)
/*      */         {
/* 2157 */           System.out.println("Requesting refresh1");
/* 2158 */           refresh_screen();
/*      */         }
/* 2160 */         if (fatal_count == 12000000)
/*      */         {
/* 2162 */           System.out.println("Requesting refresh2");
/* 2163 */           refresh_screen();
/* 2164 */           fatal_count = 41;
/*      */         }
/*      */         
/*      */         break;
/*      */       case 34: 
/* 2169 */         next_block(1);
/* 2170 */         break;
/*      */       case 29: 
/* 2172 */         dvc_code += 2;
/*      */       
/*      */       case 30: 
/* 2175 */         next_block(dvc_code);
/* 2176 */         break;
/*      */       
/*      */       case 40: 
/* 2179 */         dvc_size_x = dvc_newx;
/* 2180 */         dvc_size_y = dvc_code;
/* 2181 */         break;
/*      */       
/*      */ 
/*      */       case 47: 
/* 2185 */         dvc_lastx = 0;
/* 2186 */         dvc_lasty = 0;
/* 2187 */         dvc_pixel_count = 0;
/* 2188 */         cache_reset();
/* 2189 */         this.scale_x = 1;
/* 2190 */         this.scale_y = 1;
/* 2191 */         this.screen_x = (dvc_size_x * 16);
/* 2192 */         this.screen_y = (dvc_size_y * 16 + dvc_code);
/*      */         
/*      */ 
/* 2195 */         if ((this.screen_x == 0) || (this.screen_y == 0)) {
/* 2196 */           video_detected = false;
/*      */         } else {
/* 2198 */           video_detected = true;
/*      */         }
/*      */         
/* 2201 */         if (dvc_code > 0) {
/* 2202 */           dvc_y_clipped = 256 - 16 * dvc_code;
/*      */         } else {
/* 2204 */           dvc_y_clipped = 0;
/*      */         }
/*      */         
/*      */ 
/* 2208 */         if (!video_detected)
/*      */         {
/* 2210 */           this.screen.show_text("No Video");
/* 2211 */           set_status(2, "No Video");
/* 2212 */           this.screen_x = 640;
/* 2213 */           this.screen_y = 100;
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 2218 */           this.screen.set_abs_dimensions(this.screen_x, this.screen_y);
/* 2219 */           this.mouse_sync.serverScreen(this.screen_x, this.screen_y);
/* 2220 */           set_status(2, " Video:" + this.screen_x + "x" + this.screen_y);
/*      */         }
/* 2222 */         break;
/*      */       
/*      */ 
/*      */       case 43: 
/* 2226 */         if (dvc_next_state != dvc_decoder_state)
/*      */         {
/* 2228 */           dvc_ib_bcnt = 0;
/* 2229 */           dvc_ib_acc = 0;
/* 2230 */           dvc_zero_count = 0;
/* 2231 */           count_bytes = 0L;
/*      */         }
/*      */         
/*      */         break;
/*      */       case 37: 
/* 2236 */         return 1;
/*      */       }
/*      */       
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 2243 */       if ((dvc_next_state == 2) && (dvc_pixel_count == 256))
/*      */       {
/* 2245 */         next_block(1);
/* 2246 */         cache_prune();
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
/* 2275 */       if ((dvc_decoder_state == dvc_next_state) && (dvc_decoder_state != 45) && (dvc_decoder_state != 38) && (dvc_decoder_state != 43))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 2280 */         System.out.println("Machine hung in state " + dvc_decoder_state);
/* 2281 */         m = 6;
/*      */       }
/*      */       else {
/* 2284 */         dvc_decoder_state = dvc_next_state;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2296 */     return m;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   boolean process_dvc(char paramChar)
/*      */   {
/* 2306 */     if (dvc_reversal['ÿ'] == 0)
/*      */     {
/*      */ 
/*      */ 
/* 2310 */       System.out.println(" Version 20050808154652 ");
/* 2311 */       init_reversal();
/* 2312 */       cache_reset();
/* 2313 */       dvc_decoder_state = 0;
/* 2314 */       dvc_next_state = 0;
/* 2315 */       dvc_zero_count = 0;
/* 2316 */       dvc_ib_acc = 0;
/* 2317 */       dvc_ib_bcnt = 0;
/* 2318 */       for (int j = 0; j < 4096; j++)
/*      */       {
/* 2320 */         this.color_remap_table[j] = ((j & 0xF00) * 4352 + (j & 0xF0) * 272 + (j & 0xF) * 17);
/*      */       }
/*      */     }
/*      */     
/*      */     int i;
/* 2325 */     if (!dvc_process_inhibit) {
/* 2326 */       i = process_bits(paramChar);
/*      */     } else
/* 2328 */       i = 0;
/*      */     boolean bool;
/* 2330 */     if (i == 0) {
/* 2331 */       bool = true;
/*      */     }
/*      */     else {
/* 2334 */       System.out.println("Exit from DVC mode status =" + i);
/* 2335 */       System.out.println("Current block at " + dvc_lastx + " " + dvc_lasty);
/* 2336 */       System.out.println("Byte count " + count_bytes);
/* 2337 */       bool = true;
/*      */       
/* 2339 */       dvc_decoder_state = 38;
/* 2340 */       dvc_next_state = 38;
/*      */       
/* 2342 */       fatal_count = 0;
/* 2343 */       refresh_screen();
/*      */     }
/* 2345 */     return bool;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void set_sig_colors(int[] paramArrayOfInt) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void change_key()
/*      */   {
/* 2358 */     try {
/*      */       this.RC4encrypter.update_key();
/*      */     } catch (NoSuchAlgorithmException e) {
/*      */       e.printStackTrace();
/*      */     }
/* 2359 */     super.change_key();
/*      */   }
/*      */   
/*      */   public void set_mouse_protocol(int paramInt)
/*      */   {
/* 2364 */     this.mouse_protocol = paramInt;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/* 2370 */   private static final byte[] cursor_none = { 0 };
/*      */   
/*      */ 
/* 2373 */   private static final int[] cursor_outline = { -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, -8355712, 0, 0, 0, 0, 0, 0, -8355712, -8355712, -8355712, -8355712, -8355712, -8355712, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, -8355712, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, -8355712, 0, 0, -8355712, -8355712, -8355712, 0, 0, -8355712, 0, 0, 0, -8355712, 0, -8355712, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, -8355712, -8355712, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, -8355712, 0, 0 };
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
/*      */   protected Cursor current_cursor;
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
/*      */   Cursor customCursor(Image paramImage, Point paramPoint, String paramString)
/*      */   {
/* 2401 */     Cursor localCursor = null;
/*      */     try {
/* 2403 */       Class localClass = Toolkit.class;
/* 2404 */       Method localMethod = localClass.getMethod("createCustomCursor", new Class[] { Image.class, Point.class, String.class });
/*      */       
/*      */ 
/* 2407 */       Toolkit localToolkit = Toolkit.getDefaultToolkit();
/* 2408 */       if (localMethod != null) {
/* 2409 */         localCursor = (Cursor)localMethod.invoke(localToolkit, new Object[] { paramImage, paramPoint, paramString });
/*      */       }
/*      */     }
/*      */     catch (Exception localException) {
/* 2413 */       System.out.println("This JVM cannot create custom cursors");
/*      */     }
/* 2415 */     return localCursor;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   Cursor createCursor(int paramInt)
/*      */   {
/* 2423 */     String str = System.getProperty("java.version", "0");
/*      */     
/*      */ 
/* 2426 */     Toolkit localToolkit = Toolkit.getDefaultToolkit();
/*      */     Image localImage;
/* 2428 */     int[] arrayOfInt; MemoryImageSource localMemoryImageSource; switch (paramInt) {
/*      */     case 0: 
/* 2430 */       return Cursor.getDefaultCursor();
/*      */     case 1: 
/* 2432 */       return Cursor.getPredefinedCursor(1);
/*      */     case 2: 
/* 2434 */       localImage = localToolkit.createImage(cursor_none);
/* 2435 */       break;
/*      */     
/*      */     case 3: 
/* 2438 */       arrayOfInt = new int['Ѐ'];
/* 2439 */       arrayOfInt[0] = (arrayOfInt[1] = arrayOfInt[32] = arrayOfInt[33] = -8355712);
/*      */       
/* 2441 */       localMemoryImageSource = new MemoryImageSource(32, 32, arrayOfInt, 0, 32);
/* 2442 */       localImage = createImage(localMemoryImageSource);
/* 2443 */       break;
/*      */     
/*      */     case 4: 
/* 2446 */       arrayOfInt = new int['Ѐ'];
/* 2447 */       for (int i = 0; i < 21; i++) {
/* 2448 */         for (int j = 0; j < 12; j++) {
/* 2449 */           arrayOfInt[(j + i * 32)] = cursor_outline[(j + i * 12)];
/*      */         }
/*      */       }
/* 2452 */       localMemoryImageSource = new MemoryImageSource(32, 32, arrayOfInt, 0, 32);
/* 2453 */       localImage = createImage(localMemoryImageSource);
/* 2454 */       break;
/*      */     default: 
/* 2456 */       System.out.println("createCursor: unknown cursor " + paramInt);
/* 2457 */       return Cursor.getDefaultCursor();
/*      */     }
/*      */     
/* 2460 */     Cursor localCursor = null;
/* 2461 */     if (str.compareTo("1.2") < 0) {
/* 2462 */       System.out.println("This JVM cannot create custom cursors");
/*      */     } else {
/* 2464 */       localCursor = customCursor(localImage, new Point(), "rcCursor");
/*      */     }
/*      */     
/* 2467 */     return localCursor != null ? localCursor : Cursor.getDefaultCursor();
/*      */   }
/*      */   
/*      */   public void set_cursor(int paramInt)
/*      */   {
/* 2472 */     this.current_cursor = createCursor(paramInt);
/* 2473 */     setCursor(this.current_cursor);
/*      */   }
/*      */ }


/* Location:              C:\Users\anton\Documents\ILO2\rc175p10.jar!\com\hp\ilo2\remcons\cim.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */
