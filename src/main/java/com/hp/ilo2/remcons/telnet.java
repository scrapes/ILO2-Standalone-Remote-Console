package com.hp.ilo2.remcons;

import java.awt.*;
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
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;


public class telnet extends Panel implements Runnable, MouseListener, FocusListener, KeyListener {
    private static final int TELNET_PORT = 23;

    public static final byte TELNET_ENCRYPT = (byte) 0xc0;
    public static final byte TELNET_CHG_ENCRYPT_KEYS = (byte) 0xc1;
    public static final byte TELNET_SE = (byte) 0xf0;
    public static final byte TELNET_NOP = (byte) 0xf1;
    public static final byte TELNET_DM = (byte) 0xf2;
    public static final byte TELNET_BRK = (byte) 0xf3;
    public static final byte TELNET_IP = (byte) 0xf4;
    public static final byte TELNET_AO = (byte) 0xf5;
    public static final byte TELNET_AYT = (byte) 0xf6;
    public static final byte TELNET_EC = (byte) 0xf7;
    public static final byte TELNET_EL = (byte) 0xf8;
    public static final byte TELNET_GA = (byte) 0xf9;
    public static final byte TELNET_SB = (byte) 0xfa;
    public static final byte TELNET_WILL = (byte) 0xfb;
    public static final byte TELNET_WONT = (byte) 0xfc;
    public static final byte TELNET_DO = (byte) 0xfd;
    public static final byte TELNET_DONT = (byte) 0xfe;
    public static final byte TELNET_IAC = (byte) 0xff;

    private static final byte CMD_TS_AVAIL = (byte) 0xc2;
    private static final byte CMD_TS_NOT_AVAIL = (byte) 0xc3;
    private static final byte CMD_TS_STARTED = (byte) 0xc4;
    private static final byte CMD_TS_STOPPED = (byte) 0xc5;

    dvcwin screen;

    private TextField statusBox;
    private String[] statusFields = new String[5];

    private Thread receiver;
    private Socket s;
    private DataInputStream in;
    protected DataOutputStream out;
    private String login = "";

    private String host = "";
    private int port = TELNET_PORT;

    private int connected = 0;

    private RC4 RC4decrypter;
    private byte[] decrypt_key = new byte[16];
    private boolean decryption_active = false;
    protected boolean encryption_enabled = false;
    private Process rdpProc = null;
    private boolean enable_terminal_services = false;
    private int terminalServicesPort = 3389;

    int ts_type;
    private boolean dvc_mode = false;
    private boolean dvc_encryption = false;

    private boolean seized = false;

    private LocaleTranslator translator = new LocaleTranslator();

    public telnet() {
        this.statusBox = new TextField(60);

        this.screen = new dvcwin(1600, 1200);
        this.statusBox.setEditable(false);

        this.screen.addMouseListener(this);

        addFocusListener(this);
        this.screen.addFocusListener(this);
        this.screen.addKeyListener(this);


        focusTraversalKeysDisable(this.screen);
        focusTraversalKeysDisable(this);


        setLayout(new BorderLayout());
        add("South", this.statusBox);
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

    public void setLocale(String paramString) {
        this.translator.selectLocale(paramString);
    }

    public void enable_debug() {}


    public void disable_debug() {}


    public void startRdp() {
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
                    transmit(new byte[] {TELNET_IAC, CMD_TS_STARTED});
                } catch (SecurityException e) {
                    System.out.println("SecurityException: " + e.getMessage() + ":: Attempting to launch " + str2);
                } catch (IOException e) {
                    System.out.println("IOException: " + e.getMessage() + ":: " + str2);
                }
                return;
            }

            boolean i = false;
            try {
                System.out.println("Executing mstsc. Port is " + this.terminalServicesPort);

                this.rdpProc = localRuntime.exec("mstsc /f /console /v:" + this.host + ":" + this.terminalServicesPort);


                transmit(new byte[] {TELNET_IAC, CMD_TS_STARTED});
            } catch (SecurityException e) {
                System.out.println("SecurityException: " + e.getMessage() + ":: Attempting to launch mstsc.");
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage() + ":: mstsc not found in system directory. Looking in \\Program Files\\Remote Desktop.");
                i = true;
            }
            String[] arrayOfString;
            if (i) {
                i = false;
                arrayOfString = new String[]{"\\Program Files\\Remote Desktop\\mstsc /f /console /v:" + this.host + ":" + this.terminalServicesPort};
                try {
                    this.rdpProc = localRuntime.exec(arrayOfString);


                    transmit(new byte[] {TELNET_IAC, CMD_TS_STARTED});
                } catch (SecurityException e) {
                    System.out.println("SecurityException: " + e.getMessage() + ":: Attempting to launch mstsc.");
                } catch (IOException e) {
                    System.out.println("IOException: " + e.getMessage() + ":: Unable to find mstsc. Verify that Terminal Services client is installed.");
                    i = true;
                }
            }
            if (i) {
                arrayOfString = new String[]{"\\Program Files\\Terminal Services Client\\mstsc"};
                try {
                    this.rdpProc = localRuntime.exec(arrayOfString);


                    transmit(new byte[] {TELNET_IAC, CMD_TS_STARTED});
                } catch (SecurityException e) {
                    System.out.println("SecurityException: " + e.getMessage() + ":: Attempting to launch mstsc.");
                } catch (IOException e) {
                    System.out.println("IOException: " + e.getMessage() + ":: Unable to find mstsc. Verify that Terminal Services client is installed.");
                }
            }
        }
    }


    public void keyTyped(KeyEvent event) {
        transmit(translate_key(event));
    }


    public void keyPressed(KeyEvent event) {
        transmit(translate_special_key(event));
    }


    public void keyReleased(KeyEvent event) {
        transmit(translate_special_key_release(event));
    }


    public void send_auto_alive_msg() {
        transmit("\033[&");
    }


    public synchronized void focusGained(FocusEvent paramFocusEvent) {
        if (paramFocusEvent.getComponent() != this.screen) {
            this.screen.requestFocus();
        }
    }


    public synchronized void focusLost(FocusEvent paramFocusEvent) {
        if (paramFocusEvent.getComponent() == this.screen) {
        }
    }


    public synchronized void mouseClicked(MouseEvent paramMouseEvent) {
        super.requestFocus();
    }


    public synchronized void mousePressed(MouseEvent paramMouseEvent) {
    }


    public synchronized void mouseReleased(MouseEvent paramMouseEvent) {
    }


    public synchronized void mouseEntered(MouseEvent paramMouseEvent) {
    }


    public synchronized void mouseExited(MouseEvent paramMouseEvent) {
    }


    public synchronized void addNotify() {
        super.addNotify();
    }


    public synchronized void set_status(int fieldIndex, String message) {
        this.statusFields[fieldIndex] = message;

        this.statusBox.setText(this.statusFields[0] + " " + this.statusFields[1] + " " + this.statusFields[2] + " " + this.statusFields[3]);
    }


    public void reinit_vars() {
    }


    public void setup_decryption(byte[] paramArrayOfByte) {
        System.arraycopy(paramArrayOfByte, 0, this.decrypt_key, 0, 16);

        this.RC4decrypter = new RC4(paramArrayOfByte);
        this.encryption_enabled = true;
    }


    public synchronized void connect(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3) {
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
                } catch (SocketException e) {
                    System.out.println("telnet.connect() linger SocketException: " + e);
                }

                this.in = new DataInputStream(this.s.getInputStream());
                this.out = new DataOutputStream(this.s.getOutputStream());
                set_status(1, "Online");


                this.receiver = new Thread(this);

                this.receiver.setName("telnet_rcvr");
                this.receiver.start();

                transmit(this.login);
            } catch (SocketException e) {
                System.out.println("telnet.connect() SocketException: " + e);
                setErrorStatus(e.toString());
            } catch (UnknownHostException e) {
                System.out.println("telnet.connect() UnknownHostException: " + e);
                setErrorStatus(e.toString());
            } catch (IOException e) {
                System.out.println("telnet.connect() IOException: " + e);
                setErrorStatus(e.toString());
            }
        } else {
            requestFocus();
        }
    }

    private void setErrorStatus(String s) {
        set_status(1, s);
        this.s = null;
        this.in = null;
        this.out = null;
        this.receiver = null;
        this.connected = 0;
    }


    public void connect(String paramString1, String paramString2, int paramInt1, int paramInt2) {
        connect(paramString1, paramString2, this.port, paramInt1, paramInt2);
    }


    public void connect(String paramString, int paramInt1, int paramInt2) {
        connect(paramString, this.login, this.port, paramInt1, paramInt2);
    }


    public synchronized void disconnect() {
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
                } catch (IOException localIOException) {
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


    // TAKE EXTRA CARE TO CONVERT INTEGERS TO BYTES PROPERLY WHEN USING THIS
    public synchronized void transmit(String paramString) {
        if (this.out == null) {
            return;
        }
        if (paramString.length() != 0) {
            byte[] arrayOfByte = new byte[paramString.length()];

            for (int i = 0; i < paramString.length(); i++) {
                arrayOfByte[i] = ((byte) paramString.charAt(i));
            }
            transmit(arrayOfByte);
        }
    }

    public synchronized void transmit(byte[] data) {
        if (this.out == null) {
            return;
        }
        if (data.length != 0) {
            try {
                this.out.write(data, 0, data.length);
            } catch (IOException localIOException) {
                System.out.println("telnet.transmit() IOException: " + localIOException);
            }
        }
    }


    protected synchronized String translate_key(KeyEvent keyEvent) {
        char c = keyEvent.getKeyChar();
        String str;
        switch (c) {
            case '\n':
            case '\r':
                if (keyEvent.isShiftDown()) {
                    str = "\n";
                } else {
                    str = "\r";
                }
                break;

            case '\t':
                str = "";
                break;
            case 11:
            case '\f':
            default:
                str = this.translator.translate(c);
        }

        return str;
    }


    protected synchronized String translate_special_key(KeyEvent paramKeyEvent) {
        String str = "";

        if (paramKeyEvent.getKeyCode() == '\t') {
            paramKeyEvent.consume();
            str = "\t";
        }
        return str;
    }


    protected synchronized String translate_special_key_release(KeyEvent paramKeyEvent) {
        return "";
    }


    boolean process_dvc(char paramChar) {
        return true;
    }

    public void run() {
        int i = 0;
        int j = 0;
        int k = 0;
        int m = 0;
        byte[] arrayOfByte = new byte[1024];


        this.screen.show_text("Connecting");
        try {
            while (true) {
                if (this.rdpProc != null) {
                    try {
                        this.rdpProc.exitValue();
                        this.rdpProc.destroy();
                        this.rdpProc = null;


                        transmit(new byte[] {TELNET_IAC, CMD_TS_STOPPED});
                    } catch (IllegalThreadStateException ignored) {}
                }


                int n;

                try {
                    if ((this.s == null) || (this.in == null)) {
                        System.out.println("telnet.run() s or in is null");
                        break;
                    }
                    this.s.setSoTimeout(1000);
                    n = this.in.read(arrayOfByte);

                } catch (InterruptedIOException e) {
                    continue;
                } catch (Exception e) {
                    System.out.println("telnet.run().read Exception, class:" + e.getClass() + "  msg:" + e.getMessage());
                    e.printStackTrace();
                    n = -1;
                }

                if (n < 0) {
                    break;
                }


                for (int i1 = 0; i1 < n; i1++) {
                    char c1 = (char) arrayOfByte[i1];
                    c1 = (char) (c1 & 0xFF);

                    if (this.dvc_mode) {
                        if (this.dvc_encryption) {
                            char c2 = (char) (this.RC4decrypter.randomValue() & 0xFF);
                            c1 = (char) (c1 ^ c2);
                            c1 = (char) (c1 & 0xFF);
                        }

                        this.dvc_mode = process_dvc(c1);
                        if (!this.dvc_mode) {
                            System.out.println("DVC mode turned off");
                            set_status(1, "DVC Mode off at run");
                        }


                    } else if (c1 == 27) { // this sequence has to happen before anything else - it gates the above if block
                        j = 1;
                    } else if ((j == 1) && (c1 == '[')) {
                        j = 2;
                    } else if ((j == 2) && (c1 == 'R')) {

                        this.dvc_mode = true;
                        this.dvc_encryption = true;
                        set_status(1, "DVC Mode (RC4-128 bit)");
                    } else if ((j == 2) && (c1 == 'r')) {

                        this.dvc_mode = true;
                        this.dvc_encryption = false;
                        set_status(1, "DVC Mode (no encryption)");
                    } else {
                        j = 0;
                    }

                }
            }
        } catch (Exception e) {
            System.out.println("telnet.run() Exception, class:" + e.getClass() + "  msg:" + e.getMessage());
            e.printStackTrace();
        } finally {
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


    public void change_key() {
        try {
            this.RC4decrypter.update_key();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void focusTraversalKeysDisable(Component paramObject) {
        paramObject.setFocusTraversalKeysEnabled(false);

        if (paramObject instanceof Container)
            ((Container)paramObject).setFocusCycleRoot(true);
    }


    public void stop_rdp() {
        if (this.rdpProc != null) {
            try {
                this.rdpProc.exitValue();
            } catch (IllegalThreadStateException e) {
                System.out.println("IllegalThreadStateException thrown. Destroying TS.");
                this.rdpProc.destroy();
            }
            this.rdpProc = null;

            transmit(new byte[] {TELNET_IAC, CMD_TS_STOPPED});
        }
        System.out.println("TS stop.");
    }


    public void seize() {
        this.seized = true;
        this.screen.show_text("Session Acquired by another user.");
        set_status(1, "Offline");
        set_status(2, "");
        set_status(3, "");
        set_status(4, "");
        disconnect();
    }


    public String percent_sub(String paramString) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < paramString.length(); i++) {
            char c = paramString.charAt(i);
            if (c == '%') {
                c = paramString.charAt(++i);
                if (c == 'h') {
                    builder.append(this.host);
                } else if (c == 'p') {
                    builder.append(this.terminalServicesPort);
                } else {
                    builder.append(c);
                }
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }
}