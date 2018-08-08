package com.hp.ilo2.remcons;

import java.awt.BorderLayout;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Properties;

























































public class telnet
  extends Panel
  implements Runnable, MouseListener, FocusListener, KeyListener
{
  public static final int TELNET_PORT = 23;
  public static final int TELNET_ENCRYPT = 192;
  public static final int TELNET_CHG_ENCRYPT_KEYS = 193;
  public static final int TELNET_SE = 240;
  public static final int TELNET_NOP = 241;
  public static final int TELNET_DM = 242;
  public static final int TELNET_BRK = 243;
  public static final int TELNET_IP = 244;
  public static final int TELNET_AO = 245;
  public static final int TELNET_AYT = 246;
  public static final int TELNET_EC = 247;
  public static final int TELNET_EL = 248;
  public static final int TELNET_GA = 249;
  public static final int TELNET_SB = 250;
  public static final int TELNET_WILL = 251;
  public static final int TELNET_WONT = 252;
  public static final int TELNET_DO = 253;
  public static final int TELNET_DONT = 254;
  public static final int TELNET_IAC = 255;
  private static final int CMD_TS_AVAIL = 194;
  private static final int CMD_TS_NOT_AVAIL = 195;
  private static final int CMD_TS_STARTED = 196;
  private static final int CMD_TS_STOPPED = 197;
  protected dvcwin screen;
  protected TextField status_box;
  protected Thread receiver;
  protected Socket s;
  protected DataInputStream in;
  protected DataOutputStream out;
  protected String login = "";


  protected String host = "";


  protected int port = 23;


  protected int connected = 0;


  protected int fore;


  protected int back;


  protected int hi_fore;


  protected int hi_back;


  protected String escseq;


  protected String curr_num;


  protected int[] escseq_val = new int[10];


  protected int escseq_val_count = 0;

  private boolean crlf_enabled = false;

  public boolean mirror = false;

  private RC4 RC4decrypter;
  protected byte[] decrypt_key = new byte[16];
  private boolean decryption_active = false;
  protected boolean encryption_enabled = false;
  private Process rdpProc = null;
  private boolean enable_terminal_services = false;
  private int terminalServicesPort = 3389;

  int ts_type;
  private boolean tbm_mode = false;
  private boolean dvc_mode = false;
  private boolean dvc_encryption = false;
  private int total_count = 0;

  private String st_fld1 = "";
  private String st_fld2 = "";
  private String st_fld3 = "";
  private String st_fld4 = "";

  private boolean seized = false;

  LocaleTranslator translator = new LocaleTranslator();

  public void setLocale(String paramString)
  {
    this.translator.selectLocale(paramString);
  }









  public telnet()
  {
    this.status_box = new TextField(60);

    this.screen = new dvcwin(1600, 1200);
    this.status_box.setEditable(false);

    this.screen.addMouseListener(this);

    addFocusListener(this);
    this.screen.addFocusListener(this);
    this.screen.addKeyListener(this);



    focusTraversalKeysDisable(this.screen);
    focusTraversalKeysDisable(this);


    setLayout(new BorderLayout());
    add("South", this.status_box);
    add("North", this.screen);

    set_status(1, "Offline");
    set_status(2, "");
    set_status(3, "");
    set_status(4, "");




    if ((System.getProperty("os.name").toLowerCase().startsWith("windows")) &&
      (!this.translator.windows)) {
      this.translator.selectLocale("en_US");
    }
  }







  public void enable_debug() {}






  public void disable_debug() {}






  public void startRdp()
  {
    if (this.rdpProc == null) {
      Runtime localRuntime = Runtime.getRuntime();

      String str1;
      if (this.ts_type == 0) {
        str1 = "mstsc";
      } else if (this.ts_type == 1) {
        str1 = "vnc";
      } else {
        str1 = "type" + this.ts_type;
      }

      String str2 = remcons.prop.getProperty(str1 + ".program");
      System.out.println(str1 + " = " + str2);
      if (str2 != null) {
        str2 = percent_sub(str2);
        System.out.println("exec: " + str2);
        try {
          this.rdpProc = localRuntime.exec(str2);
          transmit("ÿÄ");
        } catch (SecurityException localSecurityException1) {
          System.out.println("SecurityException: " + localSecurityException1.getMessage() + ":: Attempting to launch " + str2);
        } catch (IOException localIOException1) {
          System.out.println("IOException: " + localIOException1.getMessage() + ":: " + str2);
        }
        return;
      }


      int i = 0;
      try
      {
        System.out.println("Executing mstsc. Port is " + this.terminalServicesPort);

        this.rdpProc = localRuntime.exec("mstsc /f /console /v:" + this.host + ":" + this.terminalServicesPort);


        transmit("ÿÄ");
      }
      catch (SecurityException localSecurityException2) {
        System.out.println("SecurityException: " + localSecurityException2.getMessage() + ":: Attempting to launch mstsc.");
      }
      catch (IOException localIOException2) {
        System.out.println("IOException: " + localIOException2.getMessage() + ":: mstsc not found in system directory. Looking in \\Program Files\\Remote Desktop.");
        i = 1;
      }
      String[] arrayOfString;
      if (i != 0) {
        i = 0;
        arrayOfString = new String[] { "\\Program Files\\Remote Desktop\\mstsc /f /console /v:" + this.host + ":" + this.terminalServicesPort };
        try
        {
          this.rdpProc = localRuntime.exec(arrayOfString);


          transmit("ÿÄ");
        }
        catch (SecurityException localSecurityException3) {
          System.out.println("SecurityException: " + localSecurityException3.getMessage() + ":: Attempting to launch mstsc.");
        }
        catch (IOException localIOException3) {
          System.out.println("IOException: " + localIOException3.getMessage() + ":: Unable to find mstsc. Verify that Terminal Services client is installed.");
          i = 1;
        }
      }
      if (i != 0) {
        arrayOfString = new String[] { "\\Program Files\\Terminal Services Client\\mstsc" };
        try
        {
          this.rdpProc = localRuntime.exec(arrayOfString);


          transmit("ÿÄ");
        }
        catch (SecurityException localSecurityException4) {
          System.out.println("SecurityException: " + localSecurityException4.getMessage() + ":: Attempting to launch mstsc.");
        }
        catch (IOException localIOException4) {
          System.out.println("IOException: " + localIOException4.getMessage() + ":: Unable to find mstsc. Verify that Terminal Services client is installed.");
        }
      }
    }
  }








  public void keyTyped(KeyEvent paramKeyEvent)
  {
    transmit(translate_key(paramKeyEvent));
  }






  public void keyPressed(KeyEvent paramKeyEvent)
  {
    transmit(translate_special_key(paramKeyEvent));
  }







  public void keyReleased(KeyEvent paramKeyEvent)
  {
    transmit(translate_special_key_release(paramKeyEvent));
  }





  public void send_auto_alive_msg()
  {
    transmit("\033[&");
  }







  public synchronized void focusGained(FocusEvent paramFocusEvent)
  {
    if (paramFocusEvent.getComponent() != this.screen)
    {


      this.screen.requestFocus();
    }
  }







  public synchronized void focusLost(FocusEvent paramFocusEvent)
  {
    if (paramFocusEvent.getComponent() == this.screen) {}
  }










  public synchronized void mouseClicked(MouseEvent paramMouseEvent)
  {
    super.requestFocus();
  }









  public synchronized void mousePressed(MouseEvent paramMouseEvent) {}









  public synchronized void mouseReleased(MouseEvent paramMouseEvent) {}









  public synchronized void mouseEntered(MouseEvent paramMouseEvent) {}









  public synchronized void mouseExited(MouseEvent paramMouseEvent) {}








  public synchronized void addNotify()
  {
    super.addNotify();
  }












  public synchronized void set_status(int paramInt, String paramString)
  {
    switch (paramInt)
    {
    case 1:
      this.st_fld1 = paramString;
      break;
    case 2:
      this.st_fld2 = paramString;
      break;
    case 3:
      this.st_fld3 = paramString;
      break;
    case 4:
      this.st_fld4 = paramString;
    }

    this.status_box.setText(this.st_fld1 + " " + this.st_fld2 + " " + this.st_fld3 + " " + this.st_fld4);
  }






  public void reinit_vars() {}





  public void setup_decryption(byte[] paramArrayOfByte)
  {
    System.arraycopy(paramArrayOfByte, 0, this.decrypt_key, 0, 16);

    this.RC4decrypter = new RC4(paramArrayOfByte);
    this.encryption_enabled = true;
  }









  public synchronized void connect(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3)
  {
    this.enable_terminal_services = ((paramInt2 & 0x1) == 1);
    this.ts_type = (paramInt2 >> 8);

    if (paramInt3 != 0) {
      this.terminalServicesPort = paramInt3;
    }

    if (this.connected == 0) {
      this.screen.start_updates();

      this.connected = 1;
      this.host = paramString1;
      this.login = paramString2;
      this.port = paramInt1;

      requestFocus();
      try {
        set_status(1, "Connecting");
        this.s = new Socket(this.host, this.port);
        try {
          this.s.setSoLinger(true, 0);
        }
        catch (SocketException localSocketException1) {
          System.out.println("telnet.connect() linger SocketException: " + localSocketException1);
        }

        this.in = new DataInputStream(this.s.getInputStream());
        this.out = new DataOutputStream(this.s.getOutputStream());
        set_status(1, "Online");


        this.receiver = new Thread(this);

        this.receiver.setName("telnet_rcvr");
        this.receiver.start();

        transmit(this.login);
      }
      catch (SocketException localSocketException2) {
        System.out.println("telnet.connect() SocketException: " + localSocketException2);
        set_status(1, localSocketException2.toString());
        this.s = null;
        this.in = null;
        this.out = null;
        this.receiver = null;
        this.connected = 0;
      }
      catch (UnknownHostException localUnknownHostException) {
        System.out.println("telnet.connect() UnknownHostException: " + localUnknownHostException);
        set_status(1, localUnknownHostException.toString());
        this.s = null;
        this.in = null;
        this.out = null;
        this.receiver = null;
        this.connected = 0;
      }
      catch (IOException localIOException) {
        System.out.println("telnet.connect() IOException: " + localIOException);
        set_status(1, localIOException.toString());
        this.s = null;
        this.in = null;
        this.out = null;
        this.receiver = null;
        this.connected = 0;
      }
    }
    else {
      requestFocus();
    }
  }








  public void connect(String paramString1, String paramString2, int paramInt1, int paramInt2)
  {
    connect(paramString1, paramString2, this.port, paramInt1, paramInt2);
  }








  public void connect(String paramString, int paramInt1, int paramInt2)
  {
    connect(paramString, this.login, this.port, paramInt1, paramInt2);
  }






  public synchronized void disconnect()
  {
    if (this.connected == 1) {
      this.screen.stop_updates();
      this.connected = 0;

      if ((this.receiver != null) && (this.receiver.isAlive())) {
        this.receiver.interrupt();
      }
      this.receiver = null;

      if (this.s != null) {
        try {
          System.out.println("Closing socket");
          this.s.close();
        }
        catch (IOException localIOException) {
          System.out.println("telnet.disconnect() IOException: " + localIOException);
          set_status(1, localIOException.toString());
        }
      }
      this.s = null;
      this.in = null;
      this.out = null;
      set_status(1, "Offline");
      reinit_vars();

      this.decryption_active = false;
    }
  }







  public synchronized void transmit(String paramString)
  {
    if (this.out == null) {
      return;
    }
    if (paramString.length() != 0) {
      byte[] arrayOfByte = new byte[paramString.length()];








      for (int i = 0; i < paramString.length(); i++) {
        arrayOfByte[i] = ((byte)paramString.charAt(i));
      }
      try
      {
        this.out.write(arrayOfByte, 0, arrayOfByte.length);
      }
      catch (IOException localIOException) {
        System.out.println("telnet.transmit() IOException: " + localIOException);
      }
    }
  }









  protected synchronized String translate_key(KeyEvent paramKeyEvent)
  {
    char c = paramKeyEvent.getKeyChar();
    String str;
    switch (c) {
    case '\n':
    case '\r':
      if (paramKeyEvent.isShiftDown()) {
        str = "\n";
      }
      else {
        str = "\r";
      }
      break;

    case '\t':
      str = "";
      break;
    case '\013': case '\f':
    default:
      str = this.translator.translate(c);
    }

    return str;
  }










  protected synchronized String translate_special_key(KeyEvent paramKeyEvent)
  {
    String str = "";

    switch (paramKeyEvent.getKeyCode()) {
    case 9:
      paramKeyEvent.consume();
      str = "\t";
    }


    return str;
  }



  protected synchronized String translate_special_key_release(KeyEvent paramKeyEvent)
  {
    String str = "";
    return str;
  }




  boolean process_dvc(char paramChar)
  {
    return true;
  }






  public void run()
  {
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    byte[] arrayOfByte = new byte['Ѐ'];





    this.screen.show_text("Connecting");
    try
    {
      for (;;)
      {
        if (this.rdpProc != null) {
          try {
            this.rdpProc.exitValue();
            this.rdpProc.destroy();
            this.rdpProc = null;


            transmit("ÿÅ");
          }
          catch (IllegalThreadStateException localIllegalThreadStateException) {}
        }


        int n;

        try
        {
          if ((this.s == null) || (this.in == null))
          {
            System.out.println("telnet.run() s or in is null");
            break;
          }
          this.s.setSoTimeout(1000);
          n = this.in.read(arrayOfByte);

        }
        catch (InterruptedIOException localInterruptedIOException)
        {
          continue;
        }
        catch (Exception localException2)
        {
          System.out.println("telnet.run().read Exception, class:" + localException2.getClass() + "  msg:" + localException2.getMessage());
          localException2.printStackTrace();
          n = -1;
        }

        if (n < 0) {
          break;
        }




        for (int i1 = 0; i1 < n; i1++)
        {
          char c1 = (char)arrayOfByte[i1];
          c1 = (char)(c1 & 0xFF);

          if (this.dvc_mode)
          {


            if (this.dvc_encryption)
            {
              char c2 = (char)(this.RC4decrypter.randomValue() & 0xFF);
              c1 = (char)(c1 ^ c2);
              c1 = (char)(c1 & 0xFF);
            }

            this.dvc_mode = process_dvc(c1);
            if (!this.dvc_mode)
            {
              System.out.println("DVC mode turned off");
              set_status(1, "DVC Mode off at run");

            }



          }
          else if (c1 == '\033') {
            j = 1;
          } else if ((j == 1) && (c1 == '[')) {
            j = 2;
          } else if ((j == 2) && (c1 == 'R'))
          {

            this.dvc_mode = true;
            this.dvc_encryption = true;
            set_status(1, "DVC Mode (RC4-128 bit)");
          }
          else if ((j == 2) && (c1 == 'r'))
          {

            this.dvc_mode = true;
            this.dvc_encryption = false;
            set_status(1, "DVC Mode (no encryption)");
          }
          else {
            j = 0;
          }

        }
      }
    }
    catch (Exception localException1)
    {
      System.out.println("telnet.run() Exception, class:" + localException1.getClass() + "  msg:" + localException1.getMessage());
      localException1.printStackTrace();

    }
    finally
    {

      if (!this.seized) {
        this.screen.show_text("Offline");
        set_status(1, "Offline");
        set_status(2, "");
        set_status(3, "");
        set_status(4, "");
        disconnect();
      }
    }
  }


  public void change_key()
  {
    this.RC4decrypter.update_key();
  }

  void focusTraversalKeysDisable(Object paramObject)
  {
    Class[] arrayOfClass = { Boolean.TYPE };
    Object[] arrayOfObject1 = { Boolean.TRUE };
    Object[] arrayOfObject2 = { Boolean.FALSE };
    try
    {
      paramObject.getClass().getMethod("setFocusTraversalKeysEnabled", arrayOfClass).invoke(paramObject, arrayOfObject2);
    }
    catch (Throwable localThrowable1) {}

    try
    {
      paramObject.getClass().getMethod("setFocusCycleRoot", arrayOfClass).invoke(paramObject, arrayOfObject1);
    }
    catch (Throwable localThrowable2) {}
  }



  public void stop_rdp()
  {
    if (this.rdpProc != null)
    {
      try
      {
        this.rdpProc.exitValue();
      }
      catch (IllegalThreadStateException localIllegalThreadStateException)
      {
        System.out.println("IllegalThreadStateException thrown. Destroying TS.");
        this.rdpProc.destroy();
      }
      this.rdpProc = null;
      transmit("ÿÅ");
    }
    System.out.println("TS stop.");
  }


  public void seize()
  {
    this.seized = true;
    this.screen.show_text("Session Acquired by another user.");
    set_status(1, "Offline");
    set_status(2, "");
    set_status(3, "");
    set_status(4, "");
    disconnect();
  }



  public String percent_sub(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer();

    for (int i = 0; i < paramString.length(); i++) {
      char c = paramString.charAt(i);
      if (c == '%') {
        c = paramString.charAt(++i);
        if (c == 'h') {
          localStringBuffer.append(this.host);
        } else if (c == 'p') {
          localStringBuffer.append(this.terminalServicesPort);
        } else {
          localStringBuffer.append(c);
        }
      }
      else {
        localStringBuffer.append(c);
      } }
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Users\anton\Documents\ILO2\rc175p10.jar!\com\hp\ilo2\remcons\telnet.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */