package com.hp.ilo2.remcons;

import java.applet.Applet;
import java.applet.AppletStub;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;



public class remcons
  extends Applet
  implements ActionListener, ItemListener, TimerListener, Runnable, AppletStub
{
            public remcons (HashMap<String, String> hmm)
            {
                params = hmm;
            }
  private static final int SESSION_TIMEOUT_DEFAULT = 900;
  private static final int KEEP_ALIVE_INTERVAL = 30;
  private static final int INFINITE_TIMEOUT = 2147483640;
  private int session_timeout = SESSION_TIMEOUT_DEFAULT;

  private cim session;

  private Panel session_window;

  private Panel tool_bar;
  private Button refresh;
  private Button send_ctrl_alt_del;
  private Button term_svcs;
  private String term_svcs_label = "Terminal Svcs";

  private Checkbox alt_lock;

  private Label alt_lock_label;
  private Checkbox hp_mouse;
  private Label hp_mouse_label;
  private boolean hp_mouse_state;
  private boolean hp_mouse_once = false;
  String hp_mouse_warning = "The High Performance Mouse is supported natively on Microsoft Windows Server 2000 SP3 or later and Windows 2003 or later. Linux users should enable the High-Performance Mouse option once the HP iLO2 High-Performance Mouse for Linux driver is installed.";

  private Choice local_cursor;

  private Label local_cursor_label;
  private Choice kbd_locale = null;
  private Label kbd_locale_label = null;


  private String login;


  private Timer timer;


  public int timeout_countdown;

  private int port_num = 23;
  private boolean translate = false;
  private boolean debug_msg = false;
  private String session_ip = null;
  private int num_cursors = 0;
  private int mouse_mode = 0;

  private Frame parent_frame;

  public int[] rndm_nums = new int[12];

  private int terminalServicesPort = 3389;
  private boolean launchTerminalServices = false;
  private int ts_param = 0;

  public boolean session_encryption_enabled = false;
  public byte[] session_encrypt_key = new byte[16];
  public byte[] session_decrypt_key = new byte[16];
  public int session_key_index = 0;

  private LocaleTranslator lt = new LocaleTranslator();

  public static Properties prop;

  private static final char[] base64 = { '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '>', '\000', '\000', '\000', '?', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\000', '\001', '\002', '\003', '\004', '\005', '\006', '\007', '\b', '\t', '\n', '\013', '\f', '\r', '\016', '\017', '\020', '\021', '\022', '\023', '\024', '\025', '\026', '\027', '\030', '\031', '\000', '\000', '\000', '\000', '\000', '\000', '\032', '\033', '\034', '\035', '\036', '\037', ' ', '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '\000', '\000', '\000', '\000', '\000' };

  public int initialized = 0;



  public void init()
  {
    Thread localThread = new Thread(this);
    localThread.start();

    setBackground(Color.white);

    init_params();

    this.refresh = new Button("");
    this.refresh.addActionListener(this);

    this.send_ctrl_alt_del = new Button("");
    this.send_ctrl_alt_del.addActionListener(this);

    this.term_svcs = new Button("");
    this.term_svcs.addActionListener(this);

    if ((this.ts_param & 0x1) == 0) {
      this.term_svcs.setEnabled(false);
    }

    this.alt_lock = new Checkbox("", null, false);
    this.alt_lock.addItemListener(this);
    this.alt_lock.setBackground(Color.white);

    this.alt_lock_label = new Label("", 2);

    this.hp_mouse = new Checkbox("", null, this.hp_mouse_state);
    this.hp_mouse.addItemListener(this);
    this.hp_mouse.setBackground(Color.white);
    this.hp_mouse_label = new Label("", 2);

    this.local_cursor = new Choice();
    this.local_cursor.add("Default");
    this.local_cursor.add("Crosshairs");
    if (System.getProperty("java.version", "0").compareTo("1.2") > 0) {
      this.local_cursor.add("Hidden");
      this.local_cursor.add("Dot");
      this.local_cursor.add("Outline");
    }
    this.local_cursor.addItemListener(this);
    this.local_cursor_label = new Label("", 2);

    String str = this.lt.getSelected();
    if (this.lt.showgui) {
      this.kbd_locale = new Choice();
      String[] localObject = this.lt.getLocales();
      for (int i = 0; i < localObject.length; i++) {
        this.kbd_locale.add(localObject[i]);
      }
      if (str != null)
        this.kbd_locale.select(str);
      this.kbd_locale.addItemListener(this);
    }
    this.kbd_locale_label = new Label("", 2);


    this.session = new cim();

    if (this.session_encryption_enabled)
    {
      this.session.setup_encryption(this.session_encrypt_key, this.session_key_index);
      this.session.setup_decryption(this.session_decrypt_key);
    }

    this.session.set_mouse_protocol(this.mouse_mode);



    for (int i = 0; i < 12; i++)
      this.rndm_nums[i] = ((int)(Math.random() * 4.0D) * 85);
    this.session.set_sig_colors(this.rndm_nums);

    if (this.debug_msg) {
      this.session.enable_debug();
    }
    else {
      this.session.disable_debug();
    }

    this.tool_bar = new Panel(new FlowLayout(1, 1, 7));

    this.tool_bar.add(this.refresh);
    this.tool_bar.add(this.term_svcs);
    this.tool_bar.add(this.send_ctrl_alt_del);


    this.tool_bar.add(this.alt_lock_label);
    this.tool_bar.add(this.alt_lock);

    this.tool_bar.add(this.hp_mouse_label);
    this.tool_bar.add(this.hp_mouse);

    this.tool_bar.add(this.local_cursor_label);
    this.tool_bar.add(this.local_cursor);

    if (this.kbd_locale != null) {
      this.tool_bar.add(this.kbd_locale_label);
      this.tool_bar.add(this.kbd_locale);
    }

    this.session.enable_keyboard();

    update_strings();

    this.session_window = new Panel();


    this.session_window.setLayout(new GridBagLayout());
    GridBagConstraints localObject = new GridBagConstraints();
    localObject.fill = 0;
    localObject.anchor = 17;
    localObject.weightx = (localObject.weighty = 100.0D);
    localObject.gridx = (localObject.gridy = 0);
    localObject.gridwidth = (localObject.gridheight = 1);
    this.session_window.add(this.tool_bar, localObject);
    localObject.gridy = 1;
    this.session_window.add(this.session, localObject);


    setLayout(new FlowLayout(0));
    add(this.session_window);
    System.out.println("Applet initialized...");

    this.initialized = 1;
  }


  public void start()
  {
    update_strings();
    System.out.println("Applet started...");

    this.timeout_countdown = this.session_timeout;
    start_session();
    if (this.session_timeout == INFINITE_TIMEOUT)
      System.out.println("Remote Console inactivity timeout = infinite."); else {
      System.out.println("Remote Console inactivity timeout = " + this.session_timeout / 60 + " minutes.");
    }
  }


  public void stop()
  {
    stop_session();
    System.out.println("Applet stopped...");
  }


  public void timeout(Object paramObject)
  {
    if (this.session.UI_dirty)
    {
      this.session.UI_dirty = false;
      this.timeout_countdown = this.session_timeout;

      this.session.send_keep_alive_msg();
    }
    else
    {
      this.session.send_auto_alive_msg();
      this.timeout_countdown -= KEEP_ALIVE_INTERVAL;

      if (this.timeout_countdown <= 0)
      {
        if (System.getProperty("java.version", "0").compareTo("1.2") < 0) {
          stop_session();
        }
      }
    }
  }


  private void update_strings()
  {
    if (!this.translate)
    {

      this.alt_lock_label.setText("Alt Lock");
      this.local_cursor_label.setText("Local Cursor");
      this.kbd_locale_label.setText("Locale");
      this.hp_mouse_label.setText("High Performance Mouse");
      this.refresh.setLabel("Refresh");
      this.send_ctrl_alt_del.setLabel("Ctrl-Alt-Del");
      this.term_svcs.setLabel(this.term_svcs_label);

    }
    else
    {

      this.alt_lock_label.setText("Altキーロック");
      this.refresh.setLabel("リフレッシュ");
      this.send_ctrl_alt_del.setLabel("Ctrl-Alt-Del");
      this.term_svcs.setLabel(this.term_svcs_label);
    }
  }


  public void itemStateChanged(ItemEvent paramItemEvent)
  {
    if (paramItemEvent.getSource() == this.alt_lock)
    {
      if (this.alt_lock.getState()) {
        this.session.enable_altlock();
      }
      else {
        this.session.disable_altlock();
      }
      this.session.requestFocus();
    } else if (paramItemEvent.getSource() == this.hp_mouse) {
      int i = 1;
      boolean bool = this.hp_mouse.getState();
      if (!this.hp_mouse_once) {
        this.hp_mouse_once = true;
        OkCancelDialog localOkCancelDialog = new OkCancelDialog(this.hp_mouse_warning, true);
        if (!localOkCancelDialog.result()) {
          i = 0;
          this.hp_mouse.setState(this.hp_mouse_state);
        }
      }
      if (i != 0) {
        this.hp_mouse_state = bool;
        this.session.mouse_mode_change(bool);
      }
    } else if (paramItemEvent.getSource() == this.local_cursor) {
      String str = this.local_cursor.getSelectedItem();
      if (str.equals("Default")) {
        this.session.set_cursor(0);
      } else if (str.equals("Crosshairs")) {
        this.session.set_cursor(1);
      } else if (str.equals("Hidden")) {
        this.session.set_cursor(2);
      } else if (str.equals("Dot")) {
        this.session.set_cursor(3);
      } else if (str.equals("Outline")) {
        this.session.set_cursor(4);
      }
    } else if (paramItemEvent.getSource() == this.kbd_locale) {
      this.session.setLocale(this.kbd_locale.getSelectedItem());
    }
  }


  private void start_session()
  {
    if (this.session_ip == null) {
      this.session.connect(huust, this.login, this.port_num, this.ts_param, this.terminalServicesPort);
    }
    else {
      this.session.connect(this.session_ip, this.login, this.port_num, this.ts_param, this.terminalServicesPort);
    }
    this.timer = new Timer(30000, false, this.session);
    this.timer.setListener(this, null);
    this.timer.start();
    if (this.launchTerminalServices) {
      this.session.startRdp();
    }
  }


  private void stop_session()
  {
    if (this.timer != null) {
      this.timer.stop();
      this.timer = null;
    }

    this.session.disconnect();
  }


  private String huust = "";


  public void SetHost(String hst)
            {
                huust = hst;
            }


  public void actionPerformed(ActionEvent paramActionEvent)
  {
    if (paramActionEvent.getSource() == this.refresh) {
      this.session.refresh_screen();
      this.session.requestFocus();
    }
    else if (paramActionEvent.getSource() == this.send_ctrl_alt_del) {
      this.session.send_ctrl_alt_del();
      this.session.requestFocus();
    }
    else if (paramActionEvent.getSource() == this.term_svcs) {
      this.session.startRdp();
    }
  }


  public String getParameter(String name) {
                return params.get(name);
            }


  public void addParameter(String name, String value) {
                params.put(name, value);
            }


  protected void init_params()
  {
    this.login = parse_login(getParameter("INFO0"));


    if (this.login.length() != 0) {
      if (getParameter("INFO1") != null)
      {
        this.login = ("\033[4" + this.login);
      }


      this.login = ("\033[7\033[9" + this.login);
    }

    String str = getParameter("INFO6");
    if (str != null) {
      try {
        this.port_num = Integer.parseInt(str);
      }
      catch (NumberFormatException localNumberFormatException1) {
        this.port_num = 23;
      }
    }




    str = getParameter("INFOM");
    if (str != null) {
      try {
        this.mouse_mode = Integer.parseInt(str);
      }
      catch (NumberFormatException localNumberFormatException2) {
        this.mouse_mode = 0;
      }
    }




    str = getParameter("INFOMM");
    if (str != null) {
      try {
        this.hp_mouse_state = (Integer.parseInt(str) == 1);
      }
      catch (NumberFormatException localNumberFormatException3) {
        this.hp_mouse_state = false;
      }
    }

    str = getParameter("INFO7");
    if (str != null) {
      try {
        this.session_timeout = Integer.parseInt(str);
        this.session_timeout *= 60;
      }
      catch (NumberFormatException localNumberFormatException4) {
        this.session_timeout = 900;
      }

    } else {
      this.session_timeout = 900;
    }


    str = getParameter("INFOA");
    int i; if (str != null)
    {
      i = 0;
      try {
        i = Integer.parseInt(str);
      }
      catch (NumberFormatException localNumberFormatException8) {
        i = 0;
      }

      if (i == 1) {
        this.session_encryption_enabled = true;
      } else {
        this.session_encryption_enabled = false;
      }
    } else {
      this.session_encryption_enabled = false;
    }


    if (this.session_encryption_enabled) {
      str = getParameter("INFOB");
      if (str != null) {
        try
        {
          for (i = 0; i < 16; i++) {
            this.session_decrypt_key[i] = ((byte)Integer.parseInt(str.substring(2 * i, 2 * i + 2), 16));
          }
        }
        catch (NumberFormatException localNumberFormatException5) {
          System.out.println("Couldn't parse INFOB: " + localNumberFormatException5);
        }

      } else {
        this.session_decrypt_key = null;
      }


      str = getParameter("INFOC");
      if (str != null) {
        try
        {
          for (int j = 0; j < 16; j++) {
            this.session_encrypt_key[j] = ((byte)Integer.parseInt(str.substring(2 * j, 2 * j + 2), 16));
          }
        }
        catch (NumberFormatException localNumberFormatException6) {
          System.out.println("Couldn't parse INFOC: " + localNumberFormatException6);
        }

      } else {
        this.session_encrypt_key = null;
      }


      str = getParameter("INFOD");
      if (str != null) {
        try {
          this.session_key_index = Integer.parseInt(str);
        }
        catch (NumberFormatException localNumberFormatException7) {
          this.session_key_index = 0;
        }

      } else {
        this.session_key_index = 0;
      }
    }

    str = getParameter("INFON");
    int k = 0;

    if (str != null) {
      try {
        k = Integer.parseInt(str);
      }
      catch (NumberFormatException localNumberFormatException9) {
        k = 0;
      }
    }


    this.ts_param = (k & 0xFF00);
    get_terminal_svcs_label(this.ts_param >> 8);
    k &= 0xFF;


    if (k == 0)
    {
      this.launchTerminalServices = false;
      this.ts_param |= 0x1;
    }
    else if (k == 1)
    {
      this.launchTerminalServices = false;
    }
    else
    {
      this.launchTerminalServices = true;
      this.ts_param |= 0x1;
    }

    str = getParameter("INFOO");

    if (str != null) {
      try {
        this.terminalServicesPort = Integer.parseInt(str);
      }
      catch (NumberFormatException localNumberFormatException10) {
        this.terminalServicesPort = 0;
      }
    }

    str = getParameter("DEBUG");
    if ((str != null) && (str.length() > 0)) {
      this.debug_msg = true;
    }
    else {
      this.debug_msg = false;
    }

    str = getParameter("IPADDR");
    if (str != null) {
      this.session_ip = str;
    }

    str = getParameter("cursors");
    if (str != null) {
      this.num_cursors = Integer.parseInt(str);
    }
  }


  private String parse_login(String paramString)
  {
    if (paramString.startsWith("Compaq-RIB-Login=")) {
      String str = "\033[!";
      try
      {
        str = str + paramString.substring(17, 73);
        str = str + '\r';
        str = str + paramString.substring(74, 106);
        str = str + '\r';
      }
      catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException) {
        return null;
      }

      return str;
    }

    return base64_decode(paramString);
  }


  private String base64_decode(String paramString)
  {
    int n = 0;
    int i1 = 0;
    String str = "";

    while ((n + 3 < paramString.length()) && (i1 == 0)) {
      int i = base64[(paramString.charAt(n) & 0x7F)];
      int j = base64[(paramString.charAt(n + 1) & 0x7F)];
      int k = base64[(paramString.charAt(n + 2) & 0x7F)];
      int m = base64[(paramString.charAt(n + 3) & 0x7F)];

      char c1 = (char)((i << 2) + (j >> 4));
      char c2 = (char)((j << 4) + (k >> 2));
      char c3 = (char)((k << 6) + m);

      c1 = (char)(c1 & 0xFF);
      c2 = (char)(c2 & 0xFF);
      c3 = (char)(c3 & 0xFF);

      if (c1 == ':') {
        c1 = '\r';
      }
      if (c2 == ':') {
        c2 = '\r';
      }
      if (c3 == ':') {
        c3 = '\r';
      }
      str = str + c1;


      if (paramString.charAt(n + 2) == '=') {
        i1++;
      }
      else {
        str = str + c2;
      }
      if (paramString.charAt(n + 3) == '=') {
        i1++;
      }
      else {
        str = str + c3;
      }
      n += 4;
    }
    if (str.length() != 0) {
      str = str + '\r';
    }
    return str;
  }


  public void paint(Graphics paramGraphics) {}


  public int getTimeoutValue()
  {
    return this.timeout_countdown;
  }


  public void run()
  {
    if ((System.getProperty("os.name").toLowerCase().startsWith("windows")) &&
      (!this.lt.windows)) {
      Locale.setDefault(Locale.US);
    }
  }


  public int getInitialized()
  {
    return this.initialized;
  }


  private void get_terminal_svcs_label(int paramInt)
  {
    String str;
    if (paramInt == 0) {
      str = "mstsc";
    } else if (paramInt == 1) {
      str = "vnc";
    } else {
      str = "type" + paramInt;
    }
    this.term_svcs_label = prop.getProperty(str + ".label", "Terminal Svcs");
  }


  static {
    prop = new Properties();
    try {
      prop.load(new FileInputStream(System.getProperty("user.home") + System.getProperty("file.separator") + ".java" + System.getProperty("file.separator") + "hp.properties"));


    }
    catch (Exception localException)
    {

      System.out.println("Exception: " + localException);
    }
  }

  public HashMap<String,String> params = new HashMap<String,String>();

  public void appletResize(int width, int height) {}
}