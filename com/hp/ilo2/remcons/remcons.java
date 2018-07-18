/*     */ package com.hp.ilo2.remcons;
/*     */ 
/*     */ import java.applet.Applet;
/*     */ import java.applet.AppletStub;
          import java.awt.Button;
/*     */ import java.awt.Checkbox;
/*     */ import java.awt.Choice;
/*     */ import java.awt.Color;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.Frame;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Label;
/*     */ import java.awt.Panel;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.net.URL;
/*     */ import java.util.HashMap;
import java.util.Locale;
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
/*     */ public class remcons
/*     */   extends Applet
/*     */   implements ActionListener, ItemListener, TimerListener, Runnable, AppletStub
/*     */ {
            public remcons (HashMap<String, String> hmm)
            {
                params = hmm;
            }
/*     */   private static final int SESSION_TIMEOUT_DEFAULT = 900;
/*     */   private static final int KEEP_ALIVE_INTERVAL = 30;
/*     */   private static final int INFINITE_TIMEOUT = 2147483640;
/*  50 */   private int session_timeout = 900;
/*     */   
/*     */   private cim session;
/*     */   
/*     */   private Panel session_window;
/*     */   
/*     */   private Panel tool_bar;
/*     */   private Button refresh;
/*     */   private Button send_ctrl_alt_del;
/*     */   private Button term_svcs;
/*  60 */   private String term_svcs_label = "Terminal Svcs";
/*     */   
/*     */   private Checkbox alt_lock;
/*     */   
/*     */   private Label alt_lock_label;
/*     */   private Checkbox hp_mouse;
/*     */   private Label hp_mouse_label;
/*     */   private boolean hp_mouse_state;
/*  68 */   private boolean hp_mouse_once = false;
/*  69 */   String hp_mouse_warning = "The High Performance Mouse is supported natively on Microsoft Windows Server 2000 SP3 or later and Windows 2003 or later. Linux users should enable the High-Performance Mouse option once the HP iLO2 High-Performance Mouse for Linux driver is installed.";
/*     */   
/*     */   private Choice local_cursor;
/*     */   
/*     */   private Label local_cursor_label;
/*  74 */   private Choice kbd_locale = null;
/*  75 */   private Label kbd_locale_label = null;
/*     */   
/*     */ 
/*     */   private String login;
/*     */   
/*     */ 
/*     */   private Timer timer;
/*     */   
/*     */ 
/*     */   public int timeout_countdown;
/*     */   
/*  86 */   private int port_num = 23;
/*  87 */   private boolean translate = false;
/*  88 */   private boolean debug_msg = false;
/*  89 */   private String session_ip = null;
/*  90 */   private int num_cursors = 0;
/*  91 */   private int mouse_mode = 0;
/*     */   
/*     */   private Frame parent_frame;
/*     */   
/*  95 */   public int[] rndm_nums = new int[12];
/*     */   
/*  97 */   private int terminalServicesPort = 3389;
/*  98 */   private boolean launchTerminalServices = false;
/*  99 */   private int ts_param = 0;
/*     */   
/* 101 */   public boolean session_encryption_enabled = false;
/* 102 */   public byte[] session_encrypt_key = new byte[16];
/* 103 */   public byte[] session_decrypt_key = new byte[16];
/* 104 */   public int session_key_index = 0;
/*     */   
/* 106 */   private LocaleTranslator lt = new LocaleTranslator();
/*     */   
/*     */   public static Properties prop;
/*     */   
/* 110 */   private static final char[] base64 = { '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '>', '\000', '\000', '\000', '?', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\001', '\002', '\003', '\004', '\005', '\006', '\007', '\b', '\t', '\n', '\013', '\f', '\r', '\016', '\017', '\020', '\021', '\022', '\023', '\024', '\025', '\026', '\027', '\030', '\031', '\000', '\000', '\000', '\000', '\000', '\000', '\032', '\033', '\034', '\035', '\036', '\037', ' ', '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '\000', '\000', '\000', '\000', '\000' };
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
/* 122 */   public int initialized = 0;
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
/*     */   public void init()
/*     */   {
/* 145 */     Thread localThread = new Thread(this);
/* 146 */     localThread.start();
/*     */     
/* 148 */     setBackground(Color.white);
/*     */     
/* 150 */     init_params();
/*     */     
/* 152 */     this.refresh = new Button("");
/* 153 */     this.refresh.addActionListener(this);
/*     */     
/* 155 */     this.send_ctrl_alt_del = new Button("");
/* 156 */     this.send_ctrl_alt_del.addActionListener(this);
/*     */     
/* 158 */     this.term_svcs = new Button("");
/* 159 */     this.term_svcs.addActionListener(this);
/*     */     
/* 161 */     if ((this.ts_param & 0x1) == 0) {
/* 162 */       this.term_svcs.setEnabled(false);
/*     */     }
/*     */     
/* 165 */     this.alt_lock = new Checkbox("", null, false);
/* 166 */     this.alt_lock.addItemListener(this);
/* 167 */     this.alt_lock.setBackground(Color.white);
/*     */     
/* 169 */     this.alt_lock_label = new Label("", 2);
/*     */     
/* 171 */     this.hp_mouse = new Checkbox("", null, this.hp_mouse_state);
/* 172 */     this.hp_mouse.addItemListener(this);
/* 173 */     this.hp_mouse.setBackground(Color.white);
/* 174 */     this.hp_mouse_label = new Label("", 2);
/*     */     
/* 176 */     this.local_cursor = new Choice();
/* 177 */     this.local_cursor.add("Default");
/* 178 */     this.local_cursor.add("Crosshairs");
/* 179 */     if (System.getProperty("java.version", "0").compareTo("1.2") > 0) {
/* 180 */       this.local_cursor.add("Hidden");
/* 181 */       this.local_cursor.add("Dot");
/* 182 */       this.local_cursor.add("Outline");
/*     */     }
/* 184 */     this.local_cursor.addItemListener(this);
/* 185 */     this.local_cursor_label = new Label("", 2);
/*     */     
/* 187 */     String str = this.lt.getSelected();
/* 188 */     if (this.lt.showgui) {
/* 189 */       this.kbd_locale = new Choice();
/* 190 */       String[] localObject = this.lt.getLocales();
/* 191 */       for (int i = 0; i < localObject.length; i++) {
/* 192 */         this.kbd_locale.add(localObject[i]);
/*     */       }
/* 194 */       if (str != null)
/* 195 */         this.kbd_locale.select(str);
/* 196 */       this.kbd_locale.addItemListener(this);
/*     */     }
/* 198 */     this.kbd_locale_label = new Label("", 2);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 205 */     this.session = new cim();
/*     */     
/* 207 */     if (this.session_encryption_enabled)
/*     */     {
/* 209 */       this.session.setup_encryption(this.session_encrypt_key, this.session_key_index);
/* 210 */       this.session.setup_decryption(this.session_decrypt_key);
/*     */     }
/*     */     
/* 213 */     this.session.set_mouse_protocol(this.mouse_mode);
/*     */     
/*     */ 
/*     */ 
/* 217 */     for (int i = 0; i < 12; i++)
/* 218 */       this.rndm_nums[i] = ((int)(Math.random() * 4.0D) * 85);
/* 219 */     this.session.set_sig_colors(this.rndm_nums);
/*     */     
/* 221 */     if (this.debug_msg) {
/* 222 */       this.session.enable_debug();
/*     */     }
/*     */     else {
/* 225 */       this.session.disable_debug();
/*     */     }
/*     */     
/* 228 */     this.tool_bar = new Panel(new FlowLayout(1, 1, 7));
/*     */     
/* 230 */     this.tool_bar.add(this.refresh);
/* 231 */     this.tool_bar.add(this.term_svcs);
/* 232 */     this.tool_bar.add(this.send_ctrl_alt_del);
/*     */     
/*     */ 
/* 235 */     this.tool_bar.add(this.alt_lock_label);
/* 236 */     this.tool_bar.add(this.alt_lock);
/*     */     
/* 238 */     this.tool_bar.add(this.hp_mouse_label);
/* 239 */     this.tool_bar.add(this.hp_mouse);
/*     */     
/* 241 */     this.tool_bar.add(this.local_cursor_label);
/* 242 */     this.tool_bar.add(this.local_cursor);
/*     */     
/* 244 */     if (this.kbd_locale != null) {
/* 245 */       this.tool_bar.add(this.kbd_locale_label);
/* 246 */       this.tool_bar.add(this.kbd_locale);
/*     */     }
/*     */     
/* 249 */     this.session.enable_keyboard();
/*     */     
/* 251 */     update_strings();
/*     */     
/* 253 */     this.session_window = new Panel();
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
/* 264 */     this.session_window.setLayout(new GridBagLayout());
/* 265 */     Object localObject = new GridBagConstraints();
/* 266 */     ((GridBagConstraints)localObject).fill = 0;
/* 267 */     ((GridBagConstraints)localObject).anchor = 17;
/* 268 */     ((GridBagConstraints)localObject).weightx = (((GridBagConstraints)localObject).weighty = 100.0D);
/* 269 */     ((GridBagConstraints)localObject).gridx = (((GridBagConstraints)localObject).gridy = 0);
/* 270 */     ((GridBagConstraints)localObject).gridwidth = (((GridBagConstraints)localObject).gridheight = 1);
/* 271 */     this.session_window.add(this.tool_bar, localObject);
/* 272 */     ((GridBagConstraints)localObject).gridy = 1;
/* 273 */     this.session_window.add(this.session, localObject);
/*     */     
/*     */ 
/* 276 */     setLayout(new FlowLayout(0));
/* 277 */     add(this.session_window);
/* 278 */     System.out.println("Applet initialized...");
/*     */     
/* 280 */     this.initialized = 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void start()
/*     */   {
/* 290 */     update_strings();
/* 291 */     System.out.println("Applet started...");
/*     */     
/* 293 */     this.timeout_countdown = this.session_timeout;
/* 294 */     start_session();
/* 295 */     if (this.session_timeout == 2147483640)
/* 296 */       System.out.println("Remote Console inactivity timeout = infinite."); else {
/* 297 */       System.out.println("Remote Console inactivity timeout = " + this.session_timeout / 60 + " minutes.");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void stop()
/*     */   {
/* 305 */     stop_session();
/* 306 */     System.out.println("Applet stopped...");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void timeout(Object paramObject)
/*     */   {
/* 313 */     if (this.session.UI_dirty)
/*     */     {
/* 315 */       this.session.UI_dirty = false;
/* 316 */       this.timeout_countdown = this.session_timeout;
/*     */       
/* 318 */       this.session.send_keep_alive_msg();
/*     */     }
/*     */     else
/*     */     {
/* 322 */       this.session.send_auto_alive_msg();
/* 323 */       this.timeout_countdown -= 30;
/*     */       
/* 325 */       if (this.timeout_countdown <= 0)
/*     */       {
/* 327 */         if (System.getProperty("java.version", "0").compareTo("1.2") < 0) {
/* 328 */           stop_session();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void update_strings()
/*     */   {
/* 338 */     if (!this.translate)
/*     */     {
/*     */ 
/* 341 */       this.alt_lock_label.setText("Alt Lock");
/* 342 */       this.local_cursor_label.setText("Local Cursor");
/* 343 */       this.kbd_locale_label.setText("Locale");
/* 344 */       this.hp_mouse_label.setText("High Performance Mouse");
/* 345 */       this.refresh.setLabel("Refresh");
/* 346 */       this.send_ctrl_alt_del.setLabel("Ctrl-Alt-Del");
/* 347 */       this.term_svcs.setLabel(this.term_svcs_label);
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/* 353 */       this.alt_lock_label.setText("Altキーロック");
/* 354 */       this.refresh.setLabel("リフレッシュ");
/* 355 */       this.send_ctrl_alt_del.setLabel("Ctrl-Alt-Del");
/* 356 */       this.term_svcs.setLabel(this.term_svcs_label);
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
/*     */   public void itemStateChanged(ItemEvent paramItemEvent)
/*     */   {
/* 373 */     if (paramItemEvent.getSource() == this.alt_lock)
/*     */     {
/* 375 */       if (this.alt_lock.getState()) {
/* 376 */         this.session.enable_altlock();
/*     */       }
/*     */       else {
/* 379 */         this.session.disable_altlock();
/*     */       }
/* 381 */       this.session.requestFocus();
/* 382 */     } else if (paramItemEvent.getSource() == this.hp_mouse) {
/* 383 */       int i = 1;
/* 384 */       boolean bool = this.hp_mouse.getState();
/* 385 */       if (!this.hp_mouse_once) {
/* 386 */         this.hp_mouse_once = true;
/* 387 */         OkCancelDialog localOkCancelDialog = new OkCancelDialog(this.hp_mouse_warning, true);
/* 388 */         if (!localOkCancelDialog.result()) {
/* 389 */           i = 0;
/* 390 */           this.hp_mouse.setState(this.hp_mouse_state);
/*     */         }
/*     */       }
/* 393 */       if (i != 0) {
/* 394 */         this.hp_mouse_state = bool;
/* 395 */         this.session.mouse_mode_change(bool);
/*     */       }
/* 397 */     } else if (paramItemEvent.getSource() == this.local_cursor) {
/* 398 */       String str = this.local_cursor.getSelectedItem();
/* 399 */       if (str.equals("Default")) {
/* 400 */         this.session.set_cursor(0);
/* 401 */       } else if (str.equals("Crosshairs")) {
/* 402 */         this.session.set_cursor(1);
/* 403 */       } else if (str.equals("Hidden")) {
/* 404 */         this.session.set_cursor(2);
/* 405 */       } else if (str.equals("Dot")) {
/* 406 */         this.session.set_cursor(3);
/* 407 */       } else if (str.equals("Outline")) {
/* 408 */         this.session.set_cursor(4);
/*     */       }
/* 410 */     } else if (paramItemEvent.getSource() == this.kbd_locale) {
/* 411 */       this.session.setLocale(this.kbd_locale.getSelectedItem());
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
/*     */   private void start_session()
/*     */   {
/* 429 */     if (this.session_ip == null) {
/* 430 */       this.session.connect(huust, this.login, this.port_num, this.ts_param, this.terminalServicesPort);
/*     */     }
/*     */     else {
/* 433 */       this.session.connect(this.session_ip, this.login, this.port_num, this.ts_param, this.terminalServicesPort);
/*     */     }
/* 435 */     this.timer = new Timer(30000, false, this.session);
/* 436 */     this.timer.setListener(this, null);
/* 437 */     this.timer.start();
/* 438 */     if (this.launchTerminalServices) {
/* 439 */       this.session.startRdp();
/*     */     }
/*     */   }
/*     */   
/*     */   private void stop_session()
/*     */   {
/* 445 */     if (this.timer != null) {
/* 446 */       this.timer.stop();
/* 447 */       this.timer = null;
/*     */     }
/*     */     
/* 450 */     this.session.disconnect();
/*     */   }

            private String huust = "";

            public void SetHost(String hst)
            {
                huust = hst;
            }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void actionPerformed(ActionEvent paramActionEvent)
/*     */   {
/* 461 */     if (paramActionEvent.getSource() == this.refresh) {
/* 462 */       this.session.refresh_screen();
/* 463 */       this.session.requestFocus();
/*     */     }
/* 465 */     else if (paramActionEvent.getSource() == this.send_ctrl_alt_del) {
/* 466 */       this.session.send_ctrl_alt_del();
/* 467 */       this.session.requestFocus();
/*     */     }
/* 469 */     else if (paramActionEvent.getSource() == this.term_svcs) {
/* 470 */       this.session.startRdp();
/*     */     }
/*     */   }
            public String getParameter(String name) {
                return params.get(name);
            }

            public void addParameter(String name, String value) {
                params.put(name, value);
            }
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
/*     */ 
/*     */   protected void init_params()
/*     */   {
/* 533 */     this.login = parse_login(getParameter("INFO0"));
/*     */     
/*     */ 
/* 536 */     if (this.login.length() != 0) {
/* 537 */       if (getParameter("INFO1") != null)
/*     */       {
/* 539 */         this.login = ("\033[4" + this.login);
/*     */       }
/*     */       
/*     */ 
/* 543 */       this.login = ("\033[7\033[9" + this.login);
/*     */     }
/*     */     
/* 546 */     String str = getParameter("INFO6");
/* 547 */     if (str != null) {
/*     */       try {
/* 549 */         this.port_num = Integer.parseInt(str);
/*     */       }
/*     */       catch (NumberFormatException localNumberFormatException1) {
/* 552 */         this.port_num = 23;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 559 */     str = getParameter("INFOM");
/* 560 */     if (str != null) {
/*     */       try {
/* 562 */         this.mouse_mode = Integer.parseInt(str);
/*     */       }
/*     */       catch (NumberFormatException localNumberFormatException2) {
/* 565 */         this.mouse_mode = 0;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 572 */     str = getParameter("INFOMM");
/* 573 */     if (str != null) {
/*     */       try {
/* 575 */         this.hp_mouse_state = (Integer.parseInt(str) == 1);
/*     */       }
/*     */       catch (NumberFormatException localNumberFormatException3) {
/* 578 */         this.hp_mouse_state = false;
/*     */       }
/*     */     }
/*     */     
/* 582 */     str = getParameter("INFO7");
/* 583 */     if (str != null) {
/*     */       try {
/* 585 */         this.session_timeout = Integer.parseInt(str);
/* 586 */         this.session_timeout *= 60;
/*     */       }
/*     */       catch (NumberFormatException localNumberFormatException4) {
/* 589 */         this.session_timeout = 900;
/*     */       }
/*     */       
/*     */     } else {
/* 593 */       this.session_timeout = 900;
/*     */     }
/*     */     
/*     */ 
/* 597 */     str = getParameter("INFOA");
/* 598 */     int i; if (str != null)
/*     */     {
/* 600 */       i = 0;
/*     */       try {
/* 602 */         i = Integer.parseInt(str);
/*     */       }
/*     */       catch (NumberFormatException localNumberFormatException8) {
/* 605 */         i = 0;
/*     */       }
/*     */       
/* 608 */       if (i == 1) {
/* 609 */         this.session_encryption_enabled = true;
/*     */       } else {
/* 611 */         this.session_encryption_enabled = false;
/*     */       }
/*     */     } else {
/* 614 */       this.session_encryption_enabled = false;
/*     */     }
/*     */     
/*     */ 
/* 618 */     if (this.session_encryption_enabled) {
/* 619 */       str = getParameter("INFOB");
/* 620 */       if (str != null) {
/*     */         try
/*     */         {
/* 623 */           for (i = 0; i < 16; i++) {
/* 624 */             this.session_decrypt_key[i] = ((byte)Integer.parseInt(str.substring(2 * i, 2 * i + 2), 16));
/*     */           }
/*     */         }
/*     */         catch (NumberFormatException localNumberFormatException5) {
/* 628 */           System.out.println("Couldn't parse INFOB: " + localNumberFormatException5);
/*     */         }
/*     */         
/*     */       } else {
/* 632 */         this.session_decrypt_key = null;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 643 */       str = getParameter("INFOC");
/* 644 */       if (str != null) {
/*     */         try
/*     */         {
/* 647 */           for (int j = 0; j < 16; j++) {
/* 648 */             this.session_encrypt_key[j] = ((byte)Integer.parseInt(str.substring(2 * j, 2 * j + 2), 16));
/*     */           }
/*     */         }
/*     */         catch (NumberFormatException localNumberFormatException6) {
/* 652 */           System.out.println("Couldn't parse INFOC: " + localNumberFormatException6);
/*     */         }
/*     */         
/*     */       } else {
/* 656 */         this.session_encrypt_key = null;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 667 */       str = getParameter("INFOD");
/* 668 */       if (str != null) {
/*     */         try {
/* 670 */           this.session_key_index = Integer.parseInt(str);
/*     */         }
/*     */         catch (NumberFormatException localNumberFormatException7) {
/* 673 */           this.session_key_index = 0;
/*     */         }
/*     */         
/*     */       } else {
/* 677 */         this.session_key_index = 0;
/*     */       }
/*     */     }
/*     */     
/* 681 */     str = getParameter("INFON");
/* 682 */     int k = 0;
/*     */     
/* 684 */     if (str != null) {
/*     */       try {
/* 686 */         k = Integer.parseInt(str);
/*     */       }
/*     */       catch (NumberFormatException localNumberFormatException9) {
/* 689 */         k = 0;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 694 */     this.ts_param = (k & 0xFF00);
/* 695 */     get_terminal_svcs_label(this.ts_param >> 8);
/* 696 */     k &= 0xFF;
/*     */     
/*     */ 
/* 699 */     if (k == 0)
/*     */     {
/* 701 */       this.launchTerminalServices = false;
/* 702 */       this.ts_param |= 0x1;
/*     */     }
/* 704 */     else if (k == 1)
/*     */     {
/* 706 */       this.launchTerminalServices = false;
/*     */     }
/*     */     else
/*     */     {
/* 710 */       this.launchTerminalServices = true;
/* 711 */       this.ts_param |= 0x1;
/*     */     }
/*     */     
/* 714 */     str = getParameter("INFOO");
/*     */     
/* 716 */     if (str != null) {
/*     */       try {
/* 718 */         this.terminalServicesPort = Integer.parseInt(str);
/*     */       }
/*     */       catch (NumberFormatException localNumberFormatException10) {
/* 721 */         this.terminalServicesPort = 0;
/*     */       }
/*     */     }
/*     */     
/* 725 */     str = getParameter("DEBUG");
/* 726 */     if ((str != null) && (str.length() > 0)) {
/* 727 */       this.debug_msg = true;
/*     */     }
/*     */     else {
/* 730 */       this.debug_msg = false;
/*     */     }
/*     */     
/* 733 */     str = getParameter("IPADDR");
/* 734 */     if (str != null) {
/* 735 */       this.session_ip = str;
/*     */     }
/*     */     
/* 738 */     str = getParameter("cursors");
/* 739 */     if (str != null) {
/* 740 */       this.num_cursors = Integer.parseInt(str);
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
/*     */   private String parse_login(String paramString)
/*     */   {
/* 756 */     if (paramString.startsWith("Compaq-RIB-Login=")) {
/* 757 */       String str = "\033[!";
/*     */       try
/*     */       {
/* 760 */         str = str + paramString.substring(17, 73);
/* 761 */         str = str + '\r';
/* 762 */         str = str + paramString.substring(74, 106);
/* 763 */         str = str + '\r';
/*     */       }
/*     */       catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException) {
/* 766 */         return null;
/*     */       }
/*     */       
/* 769 */       return str;
/*     */     }
/*     */     
/* 772 */     return base64_decode(paramString);
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
/*     */   private String base64_decode(String paramString)
/*     */   {
/* 789 */     int n = 0;
/* 790 */     int i1 = 0;
/* 791 */     String str = "";
/*     */     
/* 793 */     while ((n + 3 < paramString.length()) && (i1 == 0)) {
/* 794 */       int i = base64[(paramString.charAt(n) & 0x7F)];
/* 795 */       int j = base64[(paramString.charAt(n + 1) & 0x7F)];
/* 796 */       int k = base64[(paramString.charAt(n + 2) & 0x7F)];
/* 797 */       int m = base64[(paramString.charAt(n + 3) & 0x7F)];
/*     */       
/* 799 */       char c1 = (char)((i << 2) + (j >> 4));
/* 800 */       char c2 = (char)((j << 4) + (k >> 2));
/* 801 */       char c3 = (char)((k << 6) + m);
/*     */       
/* 803 */       c1 = (char)(c1 & 0xFF);
/* 804 */       c2 = (char)(c2 & 0xFF);
/* 805 */       c3 = (char)(c3 & 0xFF);
/*     */       
/* 807 */       if (c1 == ':') {
/* 808 */         c1 = '\r';
/*     */       }
/* 810 */       if (c2 == ':') {
/* 811 */         c2 = '\r';
/*     */       }
/* 813 */       if (c3 == ':') {
/* 814 */         c3 = '\r';
/*     */       }
/* 816 */       str = str + c1;
/*     */       
/*     */ 
/* 819 */       if (paramString.charAt(n + 2) == '=') {
/* 820 */         i1++;
/*     */       }
/*     */       else {
/* 823 */         str = str + c2;
/*     */       }
/* 825 */       if (paramString.charAt(n + 3) == '=') {
/* 826 */         i1++;
/*     */       }
/*     */       else {
/* 829 */         str = str + c3;
/*     */       }
/* 831 */       n += 4;
/*     */     }
/* 833 */     if (str.length() != 0) {
/* 834 */       str = str + '\r';
/*     */     }
/* 836 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void paint(Graphics paramGraphics) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getTimeoutValue()
/*     */   {
/* 850 */     return this.timeout_countdown;
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
/* 865 */     if ((System.getProperty("os.name").toLowerCase().startsWith("windows")) && 
/* 866 */       (!this.lt.windows)) {
/* 867 */       Locale.setDefault(Locale.US);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getInitialized()
/*     */   {
/* 876 */     return this.initialized;
/*     */   }
/*     */   
/*     */ 
/*     */   private void get_terminal_svcs_label(int paramInt)
/*     */   {
/*     */     String str;
/* 883 */     if (paramInt == 0) {
/* 884 */       str = "mstsc";
/* 885 */     } else if (paramInt == 1) {
/* 886 */       str = "vnc";
/*     */     } else {
/* 888 */       str = "type" + paramInt;
/*     */     }
/* 890 */     this.term_svcs_label = prop.getProperty(str + ".label", "Terminal Svcs");
/*     */   }
/*     */   
/*     */   static {
/* 894 */     prop = new Properties();
/*     */     try {
/* 896 */       prop.load(new FileInputStream(System.getProperty("user.home") + System.getProperty("file.separator") + ".java" + System.getProperty("file.separator") + "hp.properties"));
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (Exception localException)
/*     */     {
/*     */ 
/* 903 */       System.out.println("Exception: " + localException);
/*     */     }
/*     */   }
            public HashMap<String,String> params = new HashMap<String,String>();

            public void appletResize(int width, int height) {}
/*     */ }