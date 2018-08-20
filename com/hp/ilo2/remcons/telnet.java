/*     */ package com.hp.ilo2.remcons;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Panel;
/*     */ import java.awt.TextField;
/*     */ import java.awt.event.FocusEvent;
/*     */ import java.awt.event.FocusListener;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.KeyListener;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
/*     */ import java.net.UnknownHostException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.util.Properties;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class telnet
/*     */   extends Panel
/*     */   implements Runnable, MouseListener, FocusListener, KeyListener
/*     */ {
/*     */   public static final int TELNET_PORT = 23;
/*     */   public static final int TELNET_ENCRYPT = 192;
/*     */   public static final int TELNET_CHG_ENCRYPT_KEYS = 193;
/*     */   public static final int TELNET_SE = 240;
/*     */   public static final int TELNET_NOP = 241;
/*     */   public static final int TELNET_DM = 242;
/*     */   public static final int TELNET_BRK = 243;
/*     */   public static final int TELNET_IP = 244;
/*     */   public static final int TELNET_AO = 245;
/*     */   public static final int TELNET_AYT = 246;
/*     */   public static final int TELNET_EC = 247;
/*     */   public static final int TELNET_EL = 248;
/*     */   public static final int TELNET_GA = 249;
/*     */   public static final int TELNET_SB = 250;
/*     */   public static final int TELNET_WILL = 251;
/*     */   public static final int TELNET_WONT = 252;
/*     */   public static final int TELNET_DO = 253;
/*     */   public static final int TELNET_DONT = 254;
/*     */   public static final int TELNET_IAC = 255;
/*     */   private static final int CMD_TS_AVAIL = 194;
/*     */   private static final int CMD_TS_NOT_AVAIL = 195;
/*     */   private static final int CMD_TS_STARTED = 196;
/*     */   private static final int CMD_TS_STOPPED = 197;
/*     */   protected dvcwin screen;
/*     */   protected TextField status_box;
/*     */   protected Thread receiver;
/*     */   protected Socket s;
/*     */   protected DataInputStream in;
/*     */   protected DataOutputStream out;
/* 112 */   protected String login = "";
/*     */   
/*     */ 
/* 115 */   protected String host = "";
/*     */   
/*     */ 
/* 118 */   protected int port = 23;
/*     */   
/*     */ 
/* 121 */   protected int connected = 0;
/*     */   
/*     */ 
/*     */   protected int fore;
/*     */   
/*     */ 
/*     */   protected int back;
/*     */   
/*     */ 
/*     */   protected int hi_fore;
/*     */   
/*     */ 
/*     */   protected int hi_back;
/*     */   
/*     */ 
/*     */   protected String escseq;
/*     */   
/*     */ 
/*     */   protected String curr_num;
/*     */   
/*     */ 
/* 142 */   protected int[] escseq_val = new int[10];
/*     */   
/*     */ 
/* 145 */   protected int escseq_val_count = 0;
/*     */   
/* 147 */   private boolean crlf_enabled = false;
/*     */   
/* 149 */   public boolean mirror = false;
/*     */   
/*     */   private RC4 RC4decrypter;
/* 152 */   protected byte[] decrypt_key = new byte[16];
/* 153 */   private boolean decryption_active = false;
/* 154 */   protected boolean encryption_enabled = false;
/* 155 */   private Process rdpProc = null;
/* 156 */   private boolean enable_terminal_services = false;
/* 157 */   private int terminalServicesPort = 3389;
/*     */   
/*     */   int ts_type;
/* 160 */   private boolean tbm_mode = false;
/* 161 */   private boolean dvc_mode = false;
/* 162 */   private boolean dvc_encryption = false;
/* 163 */   private int total_count = 0;
/*     */   
/* 165 */   private String st_fld1 = "";
/* 166 */   private String st_fld2 = "";
/* 167 */   private String st_fld3 = "";
/* 168 */   private String st_fld4 = "";
/*     */   
/* 170 */   private boolean seized = false;
/*     */   
/* 172 */   LocaleTranslator translator = new LocaleTranslator();
/*     */   
/*     */   public void setLocale(String paramString)
/*     */   {
/* 176 */     this.translator.selectLocale(paramString);
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
/*     */   public telnet()
/*     */   {
/* 189 */     this.status_box = new TextField(60);
/*     */     
/* 191 */     this.screen = new dvcwin(1600, 1200);
/* 192 */     this.status_box.setEditable(false);
/*     */     
/* 194 */     this.screen.addMouseListener(this);
/*     */     
/* 196 */     addFocusListener(this);
/* 197 */     this.screen.addFocusListener(this);
/* 198 */     this.screen.addKeyListener(this);
/*     */     
/*     */ 
/*     */ 
/* 202 */     focusTraversalKeysDisable(this.screen);
/* 203 */     focusTraversalKeysDisable(this);
/*     */     
/*     */ 
/* 206 */     setLayout(new BorderLayout());
/* 207 */     add("South", this.status_box);
/* 208 */     add("North", this.screen);
/*     */     
/* 210 */     set_status(1, "Offline");
/* 211 */     set_status(2, "");
/* 212 */     set_status(3, "");
/* 213 */     set_status(4, "");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 218 */     if ((System.getProperty("os.name").toLowerCase().startsWith("windows")) && 
/* 219 */       (!this.translator.windows)) {
/* 220 */       this.translator.selectLocale("en_US");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void enable_debug() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void disable_debug() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void startRdp()
/*     */   {
/* 246 */     if (this.rdpProc == null) {
/* 247 */       Runtime localRuntime = Runtime.getRuntime();
/*     */       
/*     */       String str1;
/* 250 */       if (this.ts_type == 0) {
/* 251 */         str1 = "mstsc";
/* 252 */       } else if (this.ts_type == 1) {
/* 253 */         str1 = "vnc";
/*     */       } else {
/* 255 */         str1 = "type" + this.ts_type;
/*     */       }
/*     */       
/* 258 */       String str2 = remcons.prop.getProperty(str1 + ".program");
/* 259 */       System.out.println(str1 + " = " + str2);
/* 260 */       if (str2 != null) {
/* 261 */         str2 = percent_sub(str2);
/* 262 */         System.out.println("exec: " + str2);
/*     */         try {
/* 264 */           this.rdpProc = localRuntime.exec(str2);
/* 265 */           transmit("ÿÄ");
/*     */         } catch (SecurityException localSecurityException1) {
/* 267 */           System.out.println("SecurityException: " + localSecurityException1.getMessage() + ":: Attempting to launch " + str2);
/*     */         } catch (IOException localIOException1) {
/* 269 */           System.out.println("IOException: " + localIOException1.getMessage() + ":: " + str2);
/*     */         }
/* 271 */         return;
/*     */       }
/*     */       
/*     */ 
/* 275 */       int i = 0;
/*     */       try
/*     */       {
/* 278 */         System.out.println("Executing mstsc. Port is " + this.terminalServicesPort);
/*     */         
/* 280 */         this.rdpProc = localRuntime.exec("mstsc /f /console /v:" + this.host + ":" + this.terminalServicesPort);
/*     */         
/*     */ 
/* 283 */         transmit("ÿÄ");
/*     */       }
/*     */       catch (SecurityException localSecurityException2) {
/* 286 */         System.out.println("SecurityException: " + localSecurityException2.getMessage() + ":: Attempting to launch mstsc.");
/*     */       }
/*     */       catch (IOException localIOException2) {
/* 289 */         System.out.println("IOException: " + localIOException2.getMessage() + ":: mstsc not found in system directory. Looking in \\Program Files\\Remote Desktop.");
/* 290 */         i = 1;
/*     */       }
/*     */       String[] arrayOfString;
/* 293 */       if (i != 0) {
/* 294 */         i = 0;
/* 295 */         arrayOfString = new String[] { "\\Program Files\\Remote Desktop\\mstsc /f /console /v:" + this.host + ":" + this.terminalServicesPort };
/*     */         try
/*     */         {
/* 298 */           this.rdpProc = localRuntime.exec(arrayOfString);
/*     */           
/*     */ 
/* 301 */           transmit("ÿÄ");
/*     */         }
/*     */         catch (SecurityException localSecurityException3) {
/* 304 */           System.out.println("SecurityException: " + localSecurityException3.getMessage() + ":: Attempting to launch mstsc.");
/*     */         }
/*     */         catch (IOException localIOException3) {
/* 307 */           System.out.println("IOException: " + localIOException3.getMessage() + ":: Unable to find mstsc. Verify that Terminal Services client is installed.");
/* 308 */           i = 1;
/*     */         }
/*     */       }
/* 311 */       if (i != 0) {
/* 312 */         arrayOfString = new String[] { "\\Program Files\\Terminal Services Client\\mstsc" };
/*     */         try
/*     */         {
/* 315 */           this.rdpProc = localRuntime.exec(arrayOfString);
/*     */           
/*     */ 
/* 318 */           transmit("ÿÄ");
/*     */         }
/*     */         catch (SecurityException localSecurityException4) {
/* 321 */           System.out.println("SecurityException: " + localSecurityException4.getMessage() + ":: Attempting to launch mstsc.");
/*     */         }
/*     */         catch (IOException localIOException4) {
/* 324 */           System.out.println("IOException: " + localIOException4.getMessage() + ":: Unable to find mstsc. Verify that Terminal Services client is installed.");
/*     */         }
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
/*     */   public void keyTyped(KeyEvent paramKeyEvent)
/*     */   {
/* 339 */     transmit(translate_key(paramKeyEvent));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void keyPressed(KeyEvent paramKeyEvent)
/*     */   {
/* 349 */     transmit(translate_special_key(paramKeyEvent));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void keyReleased(KeyEvent paramKeyEvent)
/*     */   {
/* 360 */     transmit(translate_special_key_release(paramKeyEvent));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void send_auto_alive_msg()
/*     */   {
/* 369 */     transmit("\033[&");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void focusGained(FocusEvent paramFocusEvent)
/*     */   {
/* 380 */     if (paramFocusEvent.getComponent() != this.screen)
/*     */     {
/*     */ 
/*     */ 
/* 384 */       this.screen.requestFocus();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void focusLost(FocusEvent paramFocusEvent)
/*     */   {
/* 396 */     if (paramFocusEvent.getComponent() == this.screen) {}
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
/*     */   public synchronized void mouseClicked(MouseEvent paramMouseEvent)
/*     */   {
/* 410 */     super.requestFocus();
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
/*     */   public synchronized void mousePressed(MouseEvent paramMouseEvent) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void mouseReleased(MouseEvent paramMouseEvent) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void mouseEntered(MouseEvent paramMouseEvent) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void mouseExited(MouseEvent paramMouseEvent) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void addNotify()
/*     */   {
/* 462 */     super.addNotify();
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
/*     */   public synchronized void set_status(int paramInt, String paramString)
/*     */   {
/* 478 */     switch (paramInt)
/*     */     {
/*     */     case 1: 
/* 481 */       this.st_fld1 = paramString;
/* 482 */       break;
/*     */     case 2: 
/* 484 */       this.st_fld2 = paramString;
/* 485 */       break;
/*     */     case 3: 
/* 487 */       this.st_fld3 = paramString;
/* 488 */       break;
/*     */     case 4: 
/* 490 */       this.st_fld4 = paramString;
/*     */     }
/*     */     
/* 493 */     this.status_box.setText(this.st_fld1 + " " + this.st_fld2 + " " + this.st_fld3 + " " + this.st_fld4);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reinit_vars() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setup_decryption(byte[] paramArrayOfByte)
/*     */   {
/* 509 */     System.arraycopy(paramArrayOfByte, 0, this.decrypt_key, 0, 16);
/*     */     
/* 511 */     this.RC4decrypter = new RC4(paramArrayOfByte);
/* 512 */     this.encryption_enabled = true;
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
/*     */   public synchronized void connect(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 525 */     this.enable_terminal_services = ((paramInt2 & 0x1) == 1);
/* 526 */     this.ts_type = (paramInt2 >> 8);
/*     */     
/* 528 */     if (paramInt3 != 0) {
/* 529 */       this.terminalServicesPort = paramInt3;
/*     */     }
/*     */     
/* 532 */     if (this.connected == 0) {
/* 533 */       this.screen.start_updates();
/*     */       
/* 535 */       this.connected = 1;
/* 536 */       this.host = paramString1;
/* 537 */       this.login = paramString2;
/* 538 */       this.port = paramInt1;
/*     */       
/* 540 */       requestFocus();
/*     */       try {
/* 542 */         set_status(1, "Connecting");
/* 543 */         this.s = new Socket(this.host, this.port);
/*     */         try {
/* 545 */           this.s.setSoLinger(true, 0);
/*     */         }
/*     */         catch (SocketException localSocketException1) {
/* 548 */           System.out.println("telnet.connect() linger SocketException: " + localSocketException1);
/*     */         }
/*     */         
/* 551 */         this.in = new DataInputStream(this.s.getInputStream());
/* 552 */         this.out = new DataOutputStream(this.s.getOutputStream());
/* 553 */         set_status(1, "Online");
/*     */         
/*     */ 
/* 556 */         this.receiver = new Thread(this);
/*     */         
/* 558 */         this.receiver.setName("telnet_rcvr");
/* 559 */         this.receiver.start();
/*     */         
/* 561 */         transmit(this.login);
/*     */       }
/*     */       catch (SocketException localSocketException2) {
/* 564 */         System.out.println("telnet.connect() SocketException: " + localSocketException2);
/* 565 */         set_status(1, localSocketException2.toString());
/* 566 */         this.s = null;
/* 567 */         this.in = null;
/* 568 */         this.out = null;
/* 569 */         this.receiver = null;
/* 570 */         this.connected = 0;
/*     */       }
/*     */       catch (UnknownHostException localUnknownHostException) {
/* 573 */         System.out.println("telnet.connect() UnknownHostException: " + localUnknownHostException);
/* 574 */         set_status(1, localUnknownHostException.toString());
/* 575 */         this.s = null;
/* 576 */         this.in = null;
/* 577 */         this.out = null;
/* 578 */         this.receiver = null;
/* 579 */         this.connected = 0;
/*     */       }
/*     */       catch (IOException localIOException) {
/* 582 */         System.out.println("telnet.connect() IOException: " + localIOException);
/* 583 */         set_status(1, localIOException.toString());
/* 584 */         this.s = null;
/* 585 */         this.in = null;
/* 586 */         this.out = null;
/* 587 */         this.receiver = null;
/* 588 */         this.connected = 0;
/*     */       }
/*     */     }
/*     */     else {
/* 592 */       requestFocus();
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
/*     */   public void connect(String paramString1, String paramString2, int paramInt1, int paramInt2)
/*     */   {
/* 605 */     connect(paramString1, paramString2, this.port, paramInt1, paramInt2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void connect(String paramString, int paramInt1, int paramInt2)
/*     */   {
/* 617 */     connect(paramString, this.login, this.port, paramInt1, paramInt2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void disconnect()
/*     */   {
/* 627 */     if (this.connected == 1) {
/* 628 */       this.screen.stop_updates();
/* 629 */       this.connected = 0;
/*     */       
/* 631 */       if ((this.receiver != null) && (this.receiver.isAlive())) {
/* 632 */         this.receiver.interrupt();
/*     */       }
/* 634 */       this.receiver = null;
/*     */       
/* 636 */       if (this.s != null) {
/*     */         try {
/* 638 */           System.out.println("Closing socket");
/* 639 */           this.s.close();
/*     */         }
/*     */         catch (IOException localIOException) {
/* 642 */           System.out.println("telnet.disconnect() IOException: " + localIOException);
/* 643 */           set_status(1, localIOException.toString());
/*     */         }
/*     */       }
/* 646 */       this.s = null;
/* 647 */       this.in = null;
/* 648 */       this.out = null;
/* 649 */       set_status(1, "Offline");
/* 650 */       reinit_vars();
/*     */       
/* 652 */       this.decryption_active = false;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void transmit(String paramString)
/*     */   {
/* 664 */     if (this.out == null) {
/* 665 */       return;
/*     */     }
/* 667 */     if (paramString.length() != 0) {
/* 668 */       byte[] arrayOfByte = new byte[paramString.length()];
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 677 */       for (int i = 0; i < paramString.length(); i++) {
/* 678 */         arrayOfByte[i] = ((byte)paramString.charAt(i));
/*     */       }
/*     */       try
/*     */       {
/* 682 */         this.out.write(arrayOfByte, 0, arrayOfByte.length);
/*     */       }
/*     */       catch (IOException localIOException) {
/* 685 */         System.out.println("telnet.transmit() IOException: " + localIOException);
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
/*     */   protected synchronized String translate_key(KeyEvent paramKeyEvent)
/*     */   {
/* 700 */     char c = paramKeyEvent.getKeyChar();
/*     */     String str;
/* 702 */     switch (c) {
/*     */     case '\n': 
/*     */     case '\r': 
/* 705 */       if (paramKeyEvent.isShiftDown()) {
/* 706 */         str = "\n";
/*     */       }
/*     */       else {
/* 709 */         str = "\r";
/*     */       }
/* 711 */       break;
/*     */     
/*     */     case '\t': 
/* 714 */       str = "";
/* 715 */       break;
/*     */     case '\013': case '\f': 
/*     */     default: 
/* 718 */       str = this.translator.translate(c);
/*     */     }
/*     */     
/* 721 */     return str;
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
/*     */   protected synchronized String translate_special_key(KeyEvent paramKeyEvent)
/*     */   {
/* 735 */     String str = "";
/*     */     
/* 737 */     switch (paramKeyEvent.getKeyCode()) {
/*     */     case 9: 
/* 739 */       paramKeyEvent.consume();
/* 740 */       str = "\t";
/*     */     }
/*     */     
/*     */     
/* 744 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected synchronized String translate_special_key_release(KeyEvent paramKeyEvent)
/*     */   {
/* 751 */     String str = "";
/* 752 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   boolean process_dvc(char paramChar)
/*     */   {
/* 760 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void run()
/*     */   {
/* 770 */     int i = 0;
/* 771 */     int j = 0;
/* 772 */     int k = 0;
/* 773 */     int m = 0;
/* 774 */     byte[] arrayOfByte = new byte['Ѐ'];
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 780 */     this.screen.show_text("Connecting");
/*     */     try
/*     */     {
/*     */       for (;;)
/*     */       {
/* 785 */         if (this.rdpProc != null) {
/*     */           try {
/* 787 */             this.rdpProc.exitValue();
/* 788 */             this.rdpProc.destroy();
/* 789 */             this.rdpProc = null;
/*     */             
/*     */ 
/* 792 */             transmit("ÿÅ");
/*     */           }
/*     */           catch (IllegalThreadStateException localIllegalThreadStateException) {}
/*     */         }
/*     */         
/*     */ 
/*     */         int n;
/*     */         
/*     */         try
/*     */         {
/* 802 */           if ((this.s == null) || (this.in == null))
/*     */           {
/* 804 */             System.out.println("telnet.run() s or in is null");
/* 805 */             break;
/*     */           }
/* 807 */           this.s.setSoTimeout(1000);
/* 808 */           n = this.in.read(arrayOfByte);
/*     */ 
/*     */         }
/*     */         catch (InterruptedIOException localInterruptedIOException)
/*     */         {
/*     */           continue;
/*     */         }
/*     */         catch (Exception localException2)
/*     */         {
/* 817 */           System.out.println("telnet.run().read Exception, class:" + localException2.getClass() + "  msg:" + localException2.getMessage());
/* 818 */           localException2.printStackTrace();
/* 819 */           n = -1;
/*     */         }
/*     */         
/* 822 */         if (n < 0) {
/*     */           break;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 829 */         for (int i1 = 0; i1 < n; i1++)
/*     */         {
/* 831 */           char c1 = (char)arrayOfByte[i1];
/* 832 */           c1 = (char)(c1 & 0xFF);
/*     */           
/* 834 */           if (this.dvc_mode)
/*     */           {
/*     */ 
/*     */ 
/* 838 */             if (this.dvc_encryption)
/*     */             {
/* 840 */               char c2 = (char)(this.RC4decrypter.randomValue() & 0xFF);
/* 841 */               c1 = (char)(c1 ^ c2);
/* 842 */               c1 = (char)(c1 & 0xFF);
/*     */             }
/*     */             
/* 845 */             this.dvc_mode = process_dvc(c1);
/* 846 */             if (!this.dvc_mode)
/*     */             {
/* 848 */               System.out.println("DVC mode turned off");
/* 849 */               set_status(1, "DVC Mode off at run");
/*     */ 
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */           }
/* 856 */           else if (c1 == '\033') {
/* 857 */             j = 1;
/* 858 */           } else if ((j == 1) && (c1 == '[')) {
/* 859 */             j = 2;
/* 860 */           } else if ((j == 2) && (c1 == 'R'))
/*     */           {
/*     */ 
/* 863 */             this.dvc_mode = true;
/* 864 */             this.dvc_encryption = true;
/* 865 */             set_status(1, "DVC Mode (RC4-128 bit)");
/*     */           }
/* 867 */           else if ((j == 2) && (c1 == 'r'))
/*     */           {
/*     */ 
/* 870 */             this.dvc_mode = true;
/* 871 */             this.dvc_encryption = false;
/* 872 */             set_status(1, "DVC Mode (no encryption)");
/*     */           }
/*     */           else {
/* 875 */             j = 0;
/*     */           }
/*     */           
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception localException1)
/*     */     {
/* 883 */       System.out.println("telnet.run() Exception, class:" + localException1.getClass() + "  msg:" + localException1.getMessage());
/* 884 */       localException1.printStackTrace();
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/*     */ 
/* 890 */       if (!this.seized) {
/* 891 */         this.screen.show_text("Offline");
/* 892 */         set_status(1, "Offline");
/* 893 */         set_status(2, "");
/* 894 */         set_status(3, "");
/* 895 */         set_status(4, "");
/* 896 */         disconnect();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void change_key()
/*     */   {
/*     */     try {
/* 904 */       this.RC4decrypter.update_key();
/*     */     } catch (NoSuchAlgorithmException e) {
/*     */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   void focusTraversalKeysDisable(Object paramObject)
/*     */   {
/* 909 */     Class[] arrayOfClass = { Boolean.TYPE };
/* 910 */     Object[] arrayOfObject1 = { Boolean.TRUE };
/* 911 */     Object[] arrayOfObject2 = { Boolean.FALSE };
/*     */     try
/*     */     {
/* 914 */       paramObject.getClass().getMethod("setFocusTraversalKeysEnabled", arrayOfClass).invoke(paramObject, arrayOfObject2);
/*     */     }
/*     */     catch (Throwable localThrowable1) {}
/*     */     
/*     */     try
/*     */     {
/* 920 */       paramObject.getClass().getMethod("setFocusCycleRoot", arrayOfClass).invoke(paramObject, arrayOfObject1);
/*     */     }
/*     */     catch (Throwable localThrowable2) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void stop_rdp()
/*     */   {
/* 929 */     if (this.rdpProc != null)
/*     */     {
/*     */       try
/*     */       {
/* 933 */         this.rdpProc.exitValue();
/*     */       }
/*     */       catch (IllegalThreadStateException localIllegalThreadStateException)
/*     */       {
/* 937 */         System.out.println("IllegalThreadStateException thrown. Destroying TS.");
/* 938 */         this.rdpProc.destroy();
/*     */       }
/* 940 */       this.rdpProc = null;
/* 941 */       transmit("ÿÅ");
/*     */     }
/* 943 */     System.out.println("TS stop.");
/*     */   }
/*     */   
/*     */ 
/*     */   public void seize()
/*     */   {
/* 949 */     this.seized = true;
/* 950 */     this.screen.show_text("Session Acquired by another user.");
/* 951 */     set_status(1, "Offline");
/* 952 */     set_status(2, "");
/* 953 */     set_status(3, "");
/* 954 */     set_status(4, "");
/* 955 */     disconnect();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String percent_sub(String paramString)
/*     */   {
/* 962 */     StringBuffer localStringBuffer = new StringBuffer();
/*     */     
/* 964 */     for (int i = 0; i < paramString.length(); i++) {
/* 965 */       char c = paramString.charAt(i);
/* 966 */       if (c == '%') {
/* 967 */         c = paramString.charAt(++i);
/* 968 */         if (c == 'h') {
/* 969 */           localStringBuffer.append(this.host);
/* 970 */         } else if (c == 'p') {
/* 971 */           localStringBuffer.append(this.terminalServicesPort);
/*     */         } else {
/* 973 */           localStringBuffer.append(c);
/*     */         }
/*     */       }
/*     */       else {
/* 977 */         localStringBuffer.append(c);
/*     */       } }
/* 979 */     return localStringBuffer.toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\anton\Documents\ILO2\rc175p10.jar!\com\hp\ilo2\remcons\telnet.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */
