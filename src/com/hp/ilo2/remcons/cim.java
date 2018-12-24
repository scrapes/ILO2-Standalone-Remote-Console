package com.hp.ilo2.remcons;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.MemoryImageSource;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;


public class cim
        extends telnet
        implements MouseSyncListener {
    public static final int MOUSE_BUTTON_LEFT = 4;
    public static final int MOUSE_BUTTON_CENTER = 2;
    public static final int MOUSE_BUTTON_RIGHT = 1;
    static final int CMD_ENCRYPT = 192;
    private static final int CMD_MOUSE_MOVE = 208;
    private static final int CMD_BUTTON_PRESS = 209;
    private static final int CMD_BUTTON_RELEASE = 210;
    private static final int CMD_BUTTON_CLICK = 211;
    private static final int CMD_BYTE = 212;
    private static final int CMD_SET_MODE = 213;
    private static final char MOUSE_USBABS = '\001';
    private static final char MOUSE_USBREL = '\002';
    private static final int block_width = 16;
    private static final int block_height = 16;
    private static final int RESET = 0;
    private static final int START = 1;
    private static final int PIXELS = 2;
    private static final int PIXLRU1 = 3;
    private static final int PIXLRU0 = 4;
    private static final int PIXCODE1 = 5;
    private static final int PIXCODE2 = 6;
    private static final int PIXCODE3 = 7;
    private static final int PIXGREY = 8;
    private static final int PIXRGBR = 9;
    private static final int PIXRPT = 10;
    private static final int PIXRPT1 = 11;
    private static final int PIXRPTSTD1 = 12;
    private static final int PIXRPTSTD2 = 13;
    private static final int PIXRPTNSTD = 14;
    private static final int CMD = 15;
    private static final int CMD0 = 16;
    private static final int MOVEXY0 = 17;
    private static final int EXTCMD = 18;
    private static final int CMDX = 19;
    private static final int MOVESHORTX = 20;
    private static final int MOVELONGX = 21;
    private static final int BLKRPT = 22;
    private static final int EXTCMD1 = 23;
    private static final int FIRMWARE = 24;
    private static final int EXTCMD2 = 25;
    private static final int MODE0 = 26;
    private static final int TIMEOUT = 27;
    private static final int BLKRPT1 = 28;
    private static final int BLKRPTSTD = 29;
    private static final int BLKRPTNSTD = 30;
    private static final int PIXFAN = 31;
    private static final int PIXCODE4 = 32;
    private static final int PIXDUP = 33;
    private static final int BLKDUP = 34;
    private static final int PIXCODE = 35;
    private static final int PIXSPEC = 36;
    private static final int EXIT = 37;
    private static final int LATCHED = 38;
    private static final int MOVEXY1 = 39;
    private static final int MODE1 = 40;
    private static final int PIXRGBG = 41;
    private static final int PIXRGBB = 42;
    private static final int HUNT = 43;
    private static final int PRINT0 = 44;
    private static final int PRINT1 = 45;
    private static final int CORP = 46;
    private static final int MODE2 = 47;
    private static final int SIZE_OF_ALL = 48;
    private static final int B = -16777216;
    private static final int W = -8355712;
    private static final byte[] cursor_none = {0};
    private static final int[] cursor_outline = {-8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, -8355712, 0, 0, 0, 0, 0, 0, -8355712, -8355712, -8355712, -8355712, -8355712, -8355712, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, 0, 0, -8355712, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, -8355712, 0, 0, -8355712, -8355712, -8355712, 0, 0, -8355712, 0, 0, 0, -8355712, 0, -8355712, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, -8355712, -8355712, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, 0, 0, -8355712, 0, 0, 0, 0, 0, 0, 0, 0, 0, -8355712, -8355712, 0, 0};
    private static int[] bits_to_read = {0, 1, 1, 1, 1, 1, 2, 3, 4, 4, 1, 1, 3, 3, 8, 1, 1, 7, 1, 1, 3, 7, 1, 1, 8, 1, 7, 0, 1, 3, 7, 1, 4, 0, 0, 0, 1, 0, 1, 7, 7, 4, 4, 1, 8, 8, 1, 4};
    private static int[] next_0 = {1, 2, 31, 2, 2, 10, 10, 10, 10, 41, 2, 33, 2, 2, 2, 16, 19, 39, 22, 20, 1, 1, 34, 25, 46, 26, 40, 1, 29, 1, 1, 36, 10, 2, 1, 35, 8, 37, 38, 1, 47, 42, 10, 43, 45, 45, 1, 1};
    private static int[] next_1 = {1, 15, 3, 11, 11, 10, 10, 10, 10, 41, 11, 12, 2, 2, 2, 17, 18, 39, 23, 21, 1, 1, 28, 24, 46, 27, 40, 1, 30, 1, 1, 35, 10, 2, 1, 35, 9, 37, 38, 1, 47, 42, 10, 0, 45, 45, 24, 1};
    private static int dvc_cc_active = 0;
    private static int[] dvc_cc_color = new int[17];
    private static int[] dvc_cc_usage = new int[17];
    private static int[] dvc_cc_block = new int[17];
    private static int[] dvc_lru_lengths = {0, 0, 0, 1, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4};
    private static int[] dvc_getmask = {0, 1, 3, 7, 15, 31, 63, 127, 255};
    private static int[] dvc_reversal = new int['Ā'];
    private static int[] dvc_left = new int['Ā'];
    private static int[] dvc_right = new int['Ā'];
    private static int dvc_pixel_count;
    private static int dvc_size_x;
    private static int dvc_size_y;
    private static int dvc_y_clipped;
    private static int dvc_lastx;
    private static int dvc_lasty;
    private static int dvc_newx;
    private static int dvc_newy;
    private static int dvc_color;
    private static int dvc_last_color;
    private static int dvc_ib_acc = 0;
    private static int dvc_ib_bcnt = 0;
    private static int dvc_zero_count = 0;
    private static int dvc_decoder_state = 0;
    private static int dvc_next_state = 0;
    private static int dvc_pixcode = 38;
    private static int dvc_code;
    private static int[] block = new int['Ā'];
    private static int dvc_red;
    private static int dvc_green;
    private static int dvc_blue;
    private static int fatal_count;
    private static int printchan = 0;
    private static String printstring = "";
    private static long count_bytes = 0L;
    private static int[] cmd_p_buff = new int['Ā'];
    private static int cmd_p_count = 0;
    private static int cmd_last = 0;
    private static int framerate = 30;
    private static boolean debug_msgs = false;
    private static char last_bits = '\000';
    private static char last_bits2 = '\000';
    private static char last_bits3 = '\000';
    private static char last_bits4 = '\000';
    private static char last_bits5 = '\000';
    private static char last_bits6 = '\000';
    private static char last_bits7 = '\000';
    private static int last_len = 0;
    private static int last_len1 = 0;
    private static int last_len2 = 0;
    private static int last_len3 = 0;
    private static int last_len4 = 0;
    private static int last_len5 = 0;
    private static int last_len6 = 0;
    private static int last_len7 = 0;
    private static int last_len8 = 0;
    private static int last_len9 = 0;
    private static int last_len10 = 0;
    private static int last_len11 = 0;
    private static int last_len12 = 0;
    private static int last_len13 = 0;
    private static int last_len14 = 0;
    private static int last_len15 = 0;
    private static int last_len16 = 0;
    private static int last_len17 = 0;
    private static int last_len18 = 0;
    private static int last_len19 = 0;
    private static int last_len20 = 0;
    private static int last_len21 = 0;
    private static char dvc_new_bits = '\000';
    private static int debug_lastx = 0;
    private static int debug_lasty = 0;
    private static int debug_show_block = 0;
    private static long timeout_count = 0L;
    private static long dvc_counter_block = 0L;
    private static long dvc_counter_bits = 0L;
    private static boolean show_bitsblk_count = false;
    private static long show_slices = 0L;
    private static boolean dvc_process_inhibit = false;
    private static boolean video_detected = true;
    public int[] color_remap_table = new int['က'];
    public boolean UI_dirty = false;
    public byte[] encrypt_key = new byte[16];
    protected MouseSync mouse_sync = new MouseSync(this);
    protected Cursor current_cursor;
    private char prev_char = ' ';
    private boolean disable_kbd = false;
    private boolean altlock = false;
    private int scale_x = 1;
    private int scale_y = 1;
    private int screen_x = 1;
    private int screen_y = 1;
    private int mouse_protocol = 0;
    private boolean sending_encrypt_command = false;
    private RC4 RC4encrypter;
    private boolean encryption_active = false;
    private int key_index = 0;
    private boolean ignore_next_key = false;


    public cim() {
        dvc_reversal['ÿ'] = 0;
        this.current_cursor = Cursor.getDefaultCursor();
        this.screen.addMouseListener(this.mouse_sync);
        this.screen.addMouseMotionListener(this.mouse_sync);
        this.mouse_sync.setListener(this);
    }

    public static String byteToHex(byte paramByte) {
        StringBuffer localStringBuffer = new StringBuffer();
        localStringBuffer.append(toHexChar(paramByte >>> 4 & 0xF));
        localStringBuffer.append(toHexChar(paramByte & 0xF));
        return localStringBuffer.toString();
    }

    public static String intToHex(int paramInt) {
        byte b = (byte) paramInt;
        return byteToHex(b);
    }

    public static String intToHex4(int paramInt) {
        StringBuffer localStringBuffer = new StringBuffer();
        localStringBuffer.append(byteToHex((byte) (paramInt / 256)));
        localStringBuffer.append(byteToHex((byte) (paramInt & 0xFF)));
        return localStringBuffer.toString();
    }

    public static String charToHex(char paramChar) {
        byte b = (byte) paramChar;
        return byteToHex(b);
    }

    public static char toHexChar(int paramInt) {
        if ((0 <= paramInt) && (paramInt <= 9)) {
            return (char) (48 + paramInt);
        }

        return (char) (65 + (paramInt - 10));
    }

    public void setup_encryption(byte[] paramArrayOfByte, int paramInt) {
        System.arraycopy(paramArrayOfByte, 0, this.encrypt_key, 0, 16);

        this.RC4encrypter = new RC4(paramArrayOfByte);
        this.key_index = paramInt;
    }

    public void reinit_vars() {
        super.reinit_vars();

        this.prev_char = ' ';
        this.disable_kbd = false;
        this.altlock = false;

        dvc_reversal['ÿ'] = 0;

        this.scale_x = 1;
        this.scale_y = 1;

        this.mouse_sync.restart();

        dvc_process_inhibit = false;
    }

    public void enable_debug() {
        debug_msgs = true;
        super.enable_debug();
        this.mouse_sync.enableDebug();
    }

    public void disable_debug() {
        debug_msgs = false;
        super.disable_debug();
        this.mouse_sync.disableDebug();
    }

    public void sync_start() {
        this.mouse_sync.sync();
    }

    public void serverMove(int paramInt1, int paramInt2, int clientX, int clientY) {
        if (paramInt1 < -128) {
            paramInt1 = -128;
        } else if (paramInt1 > 127) {
            paramInt1 = 127;
        }
        if (paramInt2 < -128) {
            paramInt2 = -128;
        } else if (paramInt2 > 127) {
            paramInt2 = 127;
        }
        this.UI_dirty = true;

        if ((this.screen_x > 0) && (this.screen_y > 0)) {
            clientX = 3000 * clientX / this.screen_x;
            clientY = 3000 * clientY / this.screen_y;
        } else {
            clientX = 3000 * clientX / 1;
            clientY = 3000 * clientY / 1;
        }
        char c1 = (char) (clientX / 256);
        char c2 = (char) (clientX % 256);
        char c3 = (char) (clientY / 256);
        char c4 = (char) (clientY % 256);
        if (this.mouse_protocol == 0) {
            transmit("ÿÐ" + (char) paramInt1 + "" + (char) paramInt2);
        } else {
            transmit("ÿÐ" + (char) paramInt1 + "" + (char) paramInt2 + "" + c1 + "" + c2 + "" + c3 + "" + c4);
        }
    }

    public void mouse_mode_change(boolean paramBoolean) {
        char c = paramBoolean ? '\001' : '\002';
        transmit("ÿÕ" + c);
    }

    public void mouseEntered(MouseEvent paramMouseEvent) {
        this.UI_dirty = true;
        setCursor(this.current_cursor);
        super.mouseEntered(paramMouseEvent);
    }

    public void serverPress(int button) {
        this.UI_dirty = true;
        send_mouse_press(button);
    }

    public void serverRelease(int button) {
        this.UI_dirty = true;
        send_mouse_release(button);
    }

    public void serverClick(int button, int paramInt2) {
        this.UI_dirty = true;
        send_mouse_click(button, paramInt2);
    }

    public synchronized void mouseExited(MouseEvent paramMouseEvent) {
        super.mouseExited(paramMouseEvent);
        setCursor(Cursor.getDefaultCursor());
    }

    public void disable_keyboard() {
        this.disable_kbd = true;
    }

    public void enable_keyboard() {
        this.disable_kbd = false;
    }

    public void disable_altlock() {
        this.altlock = false;
    }

    public void enable_altlock() {
        this.altlock = true;
    }

    public synchronized void connect(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3) {
        char[] arrayOfChar = {'ÿ', 'À'};

        if (this.encryption_enabled) {
            this.encryption_active = true;
            paramString2 = "" + arrayOfChar[0] + "" + arrayOfChar[1] + "    " + paramString2;


            this.sending_encrypt_command = true;
        }

        super.connect(paramString1, paramString2, paramInt1, paramInt2, paramInt3);
    }

    public synchronized void transmit(String paramString) {
        if (this.out == null) {
            return;
        }
        if (paramString.length() != 0) {
            byte[] arrayOfByte = new byte[paramString.length()];

            int i;

            if (this.encryption_active) {
                if (this.sending_encrypt_command) {
                    arrayOfByte[0] = ((byte) paramString.charAt(0));
                    arrayOfByte[1] = ((byte) paramString.charAt(1));
                    arrayOfByte[2] = ((byte) ((this.key_index & 0xFF000000) >>> 24));
                    arrayOfByte[3] = ((byte) ((this.key_index & 0xFF0000) >>> 16));
                    arrayOfByte[4] = ((byte) ((this.key_index & 0xFF00) >>> 8));
                    arrayOfByte[5] = ((byte) ((this.key_index & 0xFF) >>> 0));


                    for (i = 6; i < paramString.length(); i++) {
                        arrayOfByte[i] = ((byte) (paramString.charAt(i) ^ this.RC4encrypter.randomValue()));
                    }
                    this.sending_encrypt_command = false;
                } else {
                    for (i = 0; i < paramString.length(); i++) {
                        arrayOfByte[i] = ((byte) (paramString.charAt(i) ^ this.RC4encrypter.randomValue()));
                    }

                }
            } else {
                for (i = 0; i < paramString.length(); i++) {
                    arrayOfByte[i] = ((byte) paramString.charAt(i));
                }
            }

            try {
                this.out.write(arrayOfByte, 0, arrayOfByte.length);
            } catch (IOException localIOException) {
            }
        }
    }

    protected String translate_key(KeyEvent paramKeyEvent) {
        String str = "";
        int i = paramKeyEvent.getKeyChar();
        int j = 0;
        int k = 1;

        if (this.disable_kbd) {
            return "";
        }

        if (this.ignore_next_key) {
            this.ignore_next_key = false;
            return "";
        }

        this.UI_dirty = true;
        if (paramKeyEvent.isShiftDown()) {
            j = 1;
        } else if (paramKeyEvent.isControlDown()) {
            j = 2;
        } else if ((this.altlock) || (paramKeyEvent.isAltDown())) {
            j = 3;
            if (paramKeyEvent.isAltDown()) {
                paramKeyEvent.consume();
            }
        }

        switch (i) {

            case 27:
                k = 0;
                break;


            case 10:
            case 13:
                switch (j) {
                    case 0:
                        str = "\r";
                        break;

                    case 1:
                        str = "\033[3\r";
                        break;

                    case 2:
                        str = "\n";
                        break;

                    case 3:
                        str = "\033[1\r";
                }

                k = 0;
                break;


            case 8:
                switch (j) {
                    case 0:
                        str = "\b";
                        break;

                    case 1:
                        str = "\033[3\b";
                        break;

                    case 2:
                        str = "";
                        break;

                    case 3:
                        str = "\033[1\b";
                }

                k = 0;
                break;

            default:
                str = super.translate_key(paramKeyEvent);
        }


        if ((k == 1) && (str.length() != 0) && (j == 3)) {
            str = "\033[1" + str;
        }
        return str;
    }

    protected String translate_special_key(KeyEvent paramKeyEvent) {
        String str = "";
        int i = 1;
        int j = 0;

        if (this.disable_kbd) {
            return "";
        }

        this.UI_dirty = true;
        if (paramKeyEvent.isShiftDown()) {
            j = 1;
        } else if (paramKeyEvent.isControlDown()) {
            j = 2;
        } else if ((this.altlock) || (paramKeyEvent.isAltDown())) {
            j = 3;
        }

        switch (paramKeyEvent.getKeyCode()) {

            case KeyEvent.VK_ESCAPE:
                str = "\033";
                break;

            case KeyEvent.VK_TAB:
                paramKeyEvent.consume();
                str = "\t";
                break;

            case KeyEvent.VK_DELETE:
                if ((paramKeyEvent.isControlDown()) && ((this.altlock) || (paramKeyEvent.isAltDown()))) {

                    send_ctrl_alt_del();
                    return "";
                }


                if (System.getProperty("java.version", "0").compareTo("1.4.2") < 0) {
                    str = "";
                }

                break;
            case 36:
                str = "\033[H";
                break;

            case 35:
                str = "\033[F";
                break;

            case 33:
                str = "\033[I";
                break;

            case 34:
                str = "\033[G";
                break;

            case 155:
                str = "\033[L";
                break;

            case 38:
                str = "\033[A";
                break;

            case 40:
                str = "\033[B";
                break;

            case 37:
                str = "\033[D";
                break;

            case 39:
                str = "\033[C";
                break;

            case 112:
                switch (j) {
                    case 0:
                        str = "\033[M";
                        break;

                    case 1:
                        str = "\033[Y";
                        break;

                    case 2:
                        str = "\033[k";
                        break;

                    case 3:
                        str = "\033[w";
                }

                paramKeyEvent.consume();
                i = 0;
                break;

            case 113:
                switch (j) {
                    case 0:
                        str = "\033[N";
                        break;

                    case 1:
                        str = "\033[Z";
                        break;

                    case 2:
                        str = "\033[l";
                        break;

                    case 3:
                        str = "\033[x";
                }

                paramKeyEvent.consume();
                i = 0;
                break;

            case 114:
                switch (j) {
                    case 0:
                        str = "\033[O";
                        break;

                    case 1:
                        str = "\033[a";
                        break;

                    case 2:
                        str = "\033[m";
                        break;

                    case 3:
                        str = "\033[y";
                }

                paramKeyEvent.consume();
                i = 0;
                break;

            case 115:
                switch (j) {
                    case 0:
                        str = "\033[P";
                        break;

                    case 1:
                        str = "\033[b";
                        break;

                    case 2:
                        str = "\033[n";
                        break;

                    case 3:
                        str = "\033[z";
                }

                paramKeyEvent.consume();
                i = 0;
                break;

            case 116:
                switch (j) {
                    case 0:
                        str = "\033[Q";
                        break;

                    case 1:
                        str = "\033[c";
                        break;

                    case 2:
                        str = "\033[o";
                        break;

                    case 3:
                        str = "\033[@";
                }

                paramKeyEvent.consume();
                i = 0;
                break;

            case 117:
                switch (j) {
                    case 0:
                        str = "\033[R";
                        break;

                    case 1:
                        str = "\033[d";
                        break;

                    case 2:
                        str = "\033[p";
                        break;

                    case 3:
                        str = "\033[[";
                }

                paramKeyEvent.consume();
                i = 0;
                break;

            case 118:
                switch (j) {
                    case 0:
                        str = "\033[S";
                        break;

                    case 1:
                        str = "\033[e";
                        break;

                    case 2:
                        str = "\033[q";
                        break;

                    case 3:
                        str = "\033[\\";
                }

                paramKeyEvent.consume();
                i = 0;
                break;

            case 119:
                switch (j) {
                    case 0:
                        str = "\033[T";
                        break;

                    case 1:
                        str = "\033[f";
                        break;

                    case 2:
                        str = "\033[r";
                        break;

                    case 3:
                        str = "\033[]";
                }

                paramKeyEvent.consume();
                i = 0;
                break;

            case 120:
                switch (j) {
                    case 0:
                        str = "\033[U";
                        break;

                    case 1:
                        str = "\033[g";
                        break;

                    case 2:
                        str = "\033[s";
                        break;

                    case 3:
                        str = "\033[^";
                }

                paramKeyEvent.consume();
                i = 0;
                break;

            case 121:
                switch (j) {
                    case 0:
                        str = "\033[V";
                        break;

                    case 1:
                        str = "\033[h";
                        break;

                    case 2:
                        str = "\033[t";
                        break;

                    case 3:
                        str = "\033[_";
                }

                paramKeyEvent.consume();
                i = 0;
                break;

            case 122:
                switch (j) {
                    case 0:
                        str = "\033[W";
                        break;

                    case 1:
                        str = "\033[i";
                        break;

                    case 2:
                        str = "\033[u";
                        break;

                    case 3:
                        str = "\033[`";
                }

                paramKeyEvent.consume();
                i = 0;
                break;

            case 123:
                switch (j) {
                    case 0:
                        str = "\033[X";
                        break;

                    case 1:
                        str = "\033[j";
                        break;

                    case 2:
                        str = "\033[v";
                        break;

                    case 3:
                        str = "\033['";
                }

                paramKeyEvent.consume();
                i = 0;
                break;

            default:
                i = 0;
                str = super.translate_special_key(paramKeyEvent);
        }


        if (str.length() != 0) {
            if (i == 1) {
                switch (j) {
                    case 1:
                        str = "\033[3" + str;
                        break;

                    case 2:
                        str = "\033[2" + str;
                        break;

                    case 3:
                        str = "\033[1" + str;
                }

            }
        }

        return str;
    }

    protected String translate_special_key_release(KeyEvent paramKeyEvent) {
        String str = "";
        int i = 0;

        if (paramKeyEvent.isShiftDown()) {
            i = 1;
        }

        if ((this.altlock) || (paramKeyEvent.isAltDown())) {
            i += 2;
        }

        if (paramKeyEvent.isControlDown()) {
            i += 4;
        }

        switch (paramKeyEvent.getKeyCode()) {
            case 243:
            case 244:
            case 263:
                i += 128;
                break;
            case 29:
                i += 136;
                break;
            case 28:
            case 256:
            case 257:
                i += 144;
                break;
            case 241:
            case 242:
            case 245:
                i += 152;
        }


        if (i > 127) {
            str = "" + (char) i;

        } else {
            str = "";
        }

        return str;
    }

    public void send_ctrl_alt_del() {
        transmit("\033[2\033[");
    }

    public void send_mouse_press(int paramInt) {
        transmit("ÿÑ" + (char) paramInt);
    }

    public void send_mouse_release(int paramInt) {
        transmit("ÿÒ" + (char) paramInt);
    }

    public void send_mouse_click(int paramInt1, int paramInt2) {
        transmit("ÿÓ" + (char) paramInt1 + "" + (char) paramInt2);
    }

    public void send_mouse_byte(int paramInt) {
        transmit("ÿÔ" + (char) paramInt);
    }

    public void refresh_screen() {
        transmit("\033[~");
    }

    public void send_keep_alive_msg() {
        transmit("\033[(");
    }

    protected synchronized void set_framerate(int paramInt) {
        framerate = paramInt;
        this.screen.set_framerate(paramInt);
        set_status(3, "" + framerate);
    }

    protected void show_error(String paramString) {
        System.out.println("dvc:" + paramString + ": state " + dvc_decoder_state + " code " + dvc_code);
        System.out.println("dvc:error at byte count " + count_bytes);
    }

    final void cache_reset() {
        dvc_cc_active = 0;
    }

    final int cache_lru(int paramInt) {
        int k = dvc_cc_active;
        int j = 0;
        int n = 0;


        for (int i = 0; i < k; i++) {
            if (paramInt == dvc_cc_color[i]) {
                j = i;
                n = 1;
                break;
            }
            if (dvc_cc_usage[i] == k - 1) {
                j = i;
            }
        }

        int m = dvc_cc_usage[j];

        if (n == 0) {

            if (k < 17) {

                j = k;
                m = k;
                k++;
                dvc_cc_active = k;

                if (dvc_cc_active < 2) {
                    dvc_pixcode = 38;
                } else if (dvc_cc_active == 2) {
                    dvc_pixcode = 4;
                } else if (dvc_cc_active == 3) {
                    dvc_pixcode = 5;
                } else if (dvc_cc_active < 6) {
                    dvc_pixcode = 6;
                } else if (dvc_cc_active < 10) {
                    dvc_pixcode = 7;
                } else
                    dvc_pixcode = 32;
                next_1[31] = dvc_pixcode;
            }
            dvc_cc_color[j] = paramInt;
        }

        dvc_cc_block[j] = 1;

        for (int i = 0; i < k; i++) {
            if (dvc_cc_usage[i] < m) {
                dvc_cc_usage[i] += 1;
            }
        }
        dvc_cc_usage[j] = 0;
        return n;
    }

    final int cache_find(int paramInt) {
        int i = dvc_cc_active;

        for (int j = 0; j < i; j++) {
            if (paramInt == dvc_cc_usage[j]) {

                int m = dvc_cc_color[j];
                int k = j;

                for (j = 0; j < i; j++) {
                    if (dvc_cc_usage[j] < paramInt) {
                        dvc_cc_usage[j] += 1;
                    }
                }
                dvc_cc_usage[k] = 0;
                dvc_cc_block[k] = 1;
                return m;
            }
        }
        return -1;
    }

    final void cache_prune() {
        int j = dvc_cc_active;

        for (int i = 0; i < j; ) {
            int k = dvc_cc_block[i];
            if (k == 0) {

                j--;
                dvc_cc_block[i] = dvc_cc_block[j];
                dvc_cc_color[i] = dvc_cc_color[j];
                dvc_cc_usage[i] = dvc_cc_usage[j];
            } else {
                dvc_cc_block[i] -= 1;
                i++;
            }
        }
        dvc_cc_active = j;
        if (dvc_cc_active < 2) {
            dvc_pixcode = 38;
        } else if (dvc_cc_active == 2) {
            dvc_pixcode = 4;
        } else if (dvc_cc_active == 3) {
            dvc_pixcode = 5;
        } else if (dvc_cc_active < 6) {
            dvc_pixcode = 6;
        } else if (dvc_cc_active < 10) {
            dvc_pixcode = 7;
        } else
            dvc_pixcode = 32;
        next_1[31] = dvc_pixcode;
    }

    protected void next_block(int paramInt) {
        int k = 1;
        if (!video_detected) {
            k = 0;
        }

        if (dvc_pixel_count != 0) {
            if ((dvc_y_clipped > 0) && (dvc_lasty == dvc_size_y)) {
                int m = this.color_remap_table[0];
                for (int j = dvc_y_clipped; j < 256; j++) {
                    block[j] = m;
                }
            }
        }
        dvc_pixel_count = 0;
        dvc_next_state = 1;

        int i = dvc_lastx * 16;
        int j = dvc_lasty * 16;
        while (paramInt != 0) {
            if (k != 0) {
                this.screen.paste_array(block, i, j, 16);
            }

            dvc_lastx += 1;
            i += 16;

            if (dvc_lastx >= dvc_size_x)
                break;
            paramInt--;
        }
    }

    protected void init_reversal() {
        for (int i = 0; i < 256; i++) {
            int i1 = 8;
            int n = 8;
            int k = i;
            int m = 0;
            for (int j = 0; j < 8; j++) {
                m <<= 1;
                if ((k & 0x1) == 1) {
                    if (i1 > j)
                        i1 = j;
                    m |= 0x1;
                    n = 7 - j;
                }
                k >>= 1;
            }
            dvc_reversal[i] = m;
            dvc_right[i] = i1;
            dvc_left[i] = n;
        }
    }

    final int add_bits(char paramChar) {
        dvc_zero_count += dvc_right[paramChar];


        int i = paramChar;
        dvc_ib_acc |= i << dvc_ib_bcnt;

        dvc_ib_bcnt += 8;

        if (dvc_zero_count > 30) {

            if (debug_msgs) {
                if ((dvc_decoder_state == 38) && (fatal_count < 40) && (fatal_count > 0)) {
                    System.out.println("reset caused a false alarm");
                } else {
                    System.out.println("Reset sequence detected at " + count_bytes);
                }
            }
            dvc_next_state = 43;
            dvc_decoder_state = 43;
            return 4;
        }

        if (paramChar != 0) {
            dvc_zero_count = dvc_left[paramChar];
        }
        return 0;
    }

    final int get_bits(int paramInt) {
        if (paramInt == 1) {
            dvc_code = dvc_ib_acc & 0x1;
            dvc_ib_acc >>= 1;
            dvc_ib_bcnt -= 1;
            return 0;
        }


        if (paramInt == 0) {
            return 0;
        }

        int i = dvc_ib_acc & dvc_getmask[paramInt];


        dvc_ib_bcnt -= paramInt;


        dvc_ib_acc >>= paramInt;


        i = dvc_reversal[i];


        i >>= 8 - paramInt;

        dvc_code = i;


        return 0;
    }

    int process_bits(char paramChar) {
        int n = 1;

        int m = 0;


        add_bits(paramChar);
        dvc_new_bits = paramChar;
        count_bytes += 1L;
        int k = 0;

        label2353:
        while (m == 0) {
            k = bits_to_read[dvc_decoder_state];

            if (k > dvc_ib_bcnt) {

                m = 0;
                break;
            }


            int i = get_bits(k);
            dvc_counter_bits += k;


            if (dvc_code == 0) {
                dvc_next_state = next_0[dvc_decoder_state];
            } else {
                dvc_next_state = next_1[dvc_decoder_state];
            }

            int j;
            switch (dvc_decoder_state) {


                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 32:
                    if (dvc_cc_active == 1) {
                        dvc_code = dvc_cc_usage[0];
                    } else if (dvc_decoder_state == 4) {
                        dvc_code = 0;
                    } else if (dvc_decoder_state == 3) {
                        dvc_code = 1;
                    } else if (dvc_code != 0) {
                        dvc_code += 1;
                    }
                    dvc_color = cache_find(dvc_code);
                    if (dvc_color == -1) {

                        show_error("could not find color for LRU " + dvc_code + ", cache has " + dvc_cc_active + " colors");
                        dvc_next_state = 38;
                    } else {
                        dvc_last_color = this.color_remap_table[dvc_color];

                        if (dvc_pixel_count < 256) {
                            block[dvc_pixel_count] = dvc_last_color;
                        } else {
                            System.out.println("dvc:too many block0");
                            dvc_next_state = 38;
                            break label2353;
                        }
                        dvc_pixel_count += 1;
                    }
                    break;

                case 12:
                    if (dvc_code == 7) {
                        dvc_next_state = 14;
                    } else if (dvc_code == 6) {
                        dvc_next_state = 13;
                    } else {
                        dvc_code += 2;
                        for (j = 0; j < dvc_code; j++) {

                            if (dvc_pixel_count < 256) {
                                block[dvc_pixel_count] = dvc_last_color;
                            } else {
                                show_error("too many pixels in a block2");
                                dvc_next_state = 38;
                                break;
                            }
                            dvc_pixel_count += 1;
                        }
                    }
                    break;
                case 13:
                    dvc_code += 8;

                case 14:
                    if ((dvc_decoder_state == 14) && (dvc_code < 16)) {

                        if (debug_msgs) {
                            System.out.println("dvc:non-std repeat misused");
                        }
                    }
                    for (j = 0; j < dvc_code; j++) {

                        if (dvc_pixel_count < 256) {
                            block[dvc_pixel_count] = dvc_last_color;
                        } else {
                            show_error("too many pixels in a block3");
                            dvc_next_state = 38;
                            break;
                        }
                        dvc_pixel_count += 1;
                    }
                    break;

                case 33:
                    if (dvc_pixel_count < 256) {
                        block[dvc_pixel_count] = dvc_last_color;
                    } else {
                        show_error("too many pixels in a block4");
                        dvc_next_state = 38;
                        break label2353;
                    }
                    dvc_pixel_count += 1;
                    break;

                case 1:
                case 2:
                case 10:
                case 11:
                case 22:
                case 28:
                case 31:
                case 36:
                    break;

                case 35:
                    dvc_next_state = dvc_pixcode;
                    break;
                case 9:
                    dvc_red = dvc_code << 8;
                    break;
                case 41:
                    dvc_green = dvc_code << 4;
                    break;
                case 8:
                    dvc_red = dvc_code << 8;
                    dvc_green = dvc_code << 4;

                case 42:
                    dvc_blue = dvc_code;
                    dvc_color = dvc_red | dvc_green | dvc_blue;
                    i = cache_lru(dvc_color);
                    if (i != 0) {
                        if (debug_msgs) {
                            if (count_bytes > 6L) {
                                show_error("unexpected hit: color " + intToHex4(dvc_color));
                            } else {
                                show_error("possible reset underway: color " + intToHex4(dvc_color));
                            }
                        }
                        dvc_next_state = 38;
                    } else {
                        dvc_last_color = this.color_remap_table[dvc_color];

                        if (dvc_pixel_count < 256) {
                            block[dvc_pixel_count] = dvc_last_color;
                        } else {
                            System.out.println("dvc:too many block1");
                            dvc_next_state = 38;
                            break label2353;
                        }
                        dvc_pixel_count += 1;
                    }
                    break;
                case 17:
                case 26:
                    dvc_newx = dvc_code;
                    if ((dvc_decoder_state == 17) && (dvc_newx > dvc_size_x)) {
                        if (debug_msgs) {
                            System.out.print("dvc:movexy moves x beyond screen " + dvc_newx);
                            System.out.println(" byte count " + count_bytes);
                        }
                        dvc_newx = 0;
                    }

                    break;
                case 39:
                    dvc_newy = dvc_code & 0x7F;

                    dvc_lastx = dvc_newx;
                    dvc_lasty = dvc_newy;

                    if (dvc_lasty > dvc_size_y) {
                        if (debug_msgs) {
                            System.out.print("dvc:movexy moves y beyond screen " + dvc_lasty);
                            System.out.println(" byte count " + count_bytes);
                        }
                        dvc_lasty = 0;
                    }
                    this.screen.repaint_it(1);
                    break;


                case 20:
                    dvc_code = dvc_lastx + dvc_code + 1;
                    if (dvc_code > dvc_size_x) {
                        if (debug_msgs) {
                            System.out.print("dvc:short x moves beyond screen " + dvc_code + " lastx " + dvc_lastx);
                            System.out.println(" byte count " + count_bytes);
                        }
                    }

                case 21:
                    dvc_lastx = dvc_code & 0x7F;
                    if (dvc_lastx > dvc_size_x) {
                        if (debug_msgs) {
                            System.out.print("dvc:long x moves beyond screen " + dvc_lastx);
                            System.out.println(" byte count " + count_bytes);
                        }
                        dvc_lastx = 0;
                    }


                    break;
                case 27:
                    if (timeout_count == count_bytes - 1L) {
                        show_error("double timeout at " + count_bytes + ", remaining bits " + (dvc_ib_bcnt & 0x7));
                        dvc_next_state = 38;
                    }


                    if ((dvc_ib_bcnt & 0x7) != 0)
                        get_bits(dvc_ib_bcnt & 0x7);
                    timeout_count = count_bytes;

                    this.screen.repaint_it(1);
                    break;


                case 24:
                    if (cmd_p_count != 0)
                        cmd_p_buff[(cmd_p_count - 1)] = cmd_last;
                    cmd_p_count += 1;

                    cmd_last = dvc_code;
                    break;

                case 46:
                    if (dvc_code == 0) {


                        switch (cmd_last) {
                            case 1:
                                dvc_next_state = 37;
                                break;


                            case 2:
                                dvc_next_state = 44;
                                break;


                            case 3:
                                if (cmd_p_count != 0) {
                                    set_framerate(cmd_p_buff[0]);
                                } else
                                    set_framerate(0);
                                break;
                            case 4:
                            case 5:
                                break;
                            case 6:
                                this.screen.show_text("Video suspended");
                                set_status(2, "Video_suspended");
                                this.screen_x = 640;
                                this.screen_y = 100;
                                break;
                            case 7:
                                this.ts_type = cmd_p_buff[0];
                                startRdp();
                                break;
                            case 8:
                                stop_rdp();
                                break;


                            case 9:
                                if ((dvc_ib_bcnt & 0x7) != 0) {
                                    get_bits(dvc_ib_bcnt & 0x7);
                                }
                                change_key();
                                break;
                            case 10:
                                seize();
                                break;
                            default:
                                System.out.println("dvc: unknown firmware command " + cmd_last);
                        }

                        cmd_p_count = 0;
                    }
                    break;
                case 44:
                    printchan = dvc_code;
                    printstring = "";
                    break;
                case 45:
                    if (dvc_code != 0) {
                        printstring += (char) dvc_code;
                    } else {

                        switch (printchan) {
                            case 1:
                            case 2:
                                set_status(2 + printchan, printstring);
                                break;
                            case 3:
                                System.out.println(printstring);
                                break;

                            case 4:
                                this.screen.show_text(printstring);
                        }


                        dvc_next_state = 1;
                    }
                    break;
                case 15:
                case 16:
                case 18:
                case 19:
                case 23:
                case 25:
                    break;
                case 0:
                    cache_reset();
                    dvc_pixel_count = 0;
                    dvc_lastx = 0;
                    dvc_lasty = 0;
                    dvc_red = 0;
                    dvc_green = 0;
                    dvc_blue = 0;
                    fatal_count = 0;
                    timeout_count = -1L;

                    cmd_p_count = 0;
                    break;
                case 38:
                    if (fatal_count == 0) {
                        debug_lastx = dvc_lastx;
                        debug_lasty = dvc_lasty;
                        debug_show_block = 1;
                    }
                    if (fatal_count == 40) {
                        System.out.print("Latched: byte count " + count_bytes);
                        System.out.println(" current block at " + dvc_lastx + " " + dvc_lasty);
                    }
                    if (fatal_count == 11680) {
                        refresh_screen();
                    }
                    fatal_count += 1;
                    if (fatal_count == 120000) {
                        System.out.println("Requesting refresh1");
                        refresh_screen();
                    }
                    if (fatal_count == 12000000) {
                        System.out.println("Requesting refresh2");
                        refresh_screen();
                        fatal_count = 41;
                    }
                    break;
                case 34:
                    next_block(1);
                    break;
                case 29:
                    dvc_code += 2;
                case 30:
                    next_block(dvc_code);
                    break;
                case 40:
                    dvc_size_x = dvc_newx;
                    dvc_size_y = dvc_code;
                    break;
                case 47:
                    dvc_lastx = 0;
                    dvc_lasty = 0;
                    dvc_pixel_count = 0;
                    cache_reset();
                    this.scale_x = 1;
                    this.scale_y = 1;
                    this.screen_x = (dvc_size_x * 16);
                    this.screen_y = (dvc_size_y * 16 + dvc_code);

                    if ((this.screen_x == 0) || (this.screen_y == 0)) {
                        video_detected = false;
                    } else {
                        video_detected = true;
                    }

                    if (dvc_code > 0) {
                        dvc_y_clipped = 256 - 16 * dvc_code;
                    } else {
                        dvc_y_clipped = 0;
                    }

                    if (!video_detected) {
                        this.screen.show_text("No Video");
                        set_status(2, "No Video");
                        this.screen_x = 640;
                        this.screen_y = 100;

                    } else {
                        this.screen.set_abs_dimensions(this.screen_x, this.screen_y);
                        this.mouse_sync.serverScreen(this.screen_x, this.screen_y);
                        set_status(2, " Video:" + this.screen_x + "x" + this.screen_y);
                    }
                    break;
                case 43:
                    if (dvc_next_state != dvc_decoder_state) {
                        dvc_ib_bcnt = 0;
                        dvc_ib_acc = 0;
                        dvc_zero_count = 0;
                        count_bytes = 0L;
                    }
                    break;
                case 37:
                    return 1;
            }

            if ((dvc_next_state == 2) && (dvc_pixel_count == 256)) {
                next_block(1);
                cache_prune();
            }

            if ((dvc_decoder_state == dvc_next_state) && (dvc_decoder_state != 45) && (dvc_decoder_state != 38) && (dvc_decoder_state != 43)) {
                System.out.println("Machine hung in state " + dvc_decoder_state);
                m = 6;
            } else {
                dvc_decoder_state = dvc_next_state;
            }
        }
        return m;
    }

    boolean process_dvc(char paramChar) {
        if (dvc_reversal['ÿ'] == 0) {
            System.out.println(" Version 20050808154652 ");
            init_reversal();
            cache_reset();
            dvc_decoder_state = 0;
            dvc_next_state = 0;
            dvc_zero_count = 0;
            dvc_ib_acc = 0;
            dvc_ib_bcnt = 0;
            for (int j = 0; j < 4096; j++) {
                this.color_remap_table[j] = ((j & 0xF00) * 4352 + (j & 0xF0) * 272 + (j & 0xF) * 17);
            }
        }

        int i;
        if (!dvc_process_inhibit) {
            i = process_bits(paramChar);
        } else
            i = 0;
        boolean bool;
        if (i == 0) {
            bool = true;
        } else {
            System.out.println("Exit from DVC mode status =" + i);
            System.out.println("Current block at " + dvc_lastx + " " + dvc_lasty);
            System.out.println("Byte count " + count_bytes);
            bool = true;

            dvc_decoder_state = 38;
            dvc_next_state = 38;

            fatal_count = 0;
            refresh_screen();
        }
        return bool;
    }

    public void set_sig_colors(int[] paramArrayOfInt) {
    }

    public void change_key() {
        try {
            this.RC4encrypter.update_key();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        super.change_key();
    }

    public void set_mouse_protocol(int paramInt) {
        this.mouse_protocol = paramInt;
    }

    Cursor customCursor(Image paramImage, Point paramPoint, String paramString) {
        Cursor localCursor = null;
        try {
            Class localClass = Toolkit.class;
            Method localMethod = localClass.getMethod("createCustomCursor", new Class[]{Image.class, Point.class, String.class});

            Toolkit localToolkit = Toolkit.getDefaultToolkit();
            if (localMethod != null) {
                localCursor = (Cursor) localMethod.invoke(localToolkit, new Object[]{paramImage, paramPoint, paramString});
            }
        } catch (Exception localException) {
            System.out.println("This JVM cannot create custom cursors");
        }
        return localCursor;
    }

    Cursor createCursor(int paramInt) {
        String javaVersion = System.getProperty("java.version", "0");

        Toolkit localToolkit = Toolkit.getDefaultToolkit();
        Image localImage;
        int[] arrayOfInt;
        MemoryImageSource localMemoryImageSource;
        switch (paramInt) {
            case 0:
                return Cursor.getDefaultCursor();
            case 1:
                return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
            case 2:
                localImage = localToolkit.createImage(cursor_none);
                break;
            case 3:
                arrayOfInt = new int['Ѐ'];
                arrayOfInt[0] = (arrayOfInt[1] = arrayOfInt[32] = arrayOfInt[33] = -8355712);

                localMemoryImageSource = new MemoryImageSource(32, 32, arrayOfInt, 0, 32);
                localImage = createImage(localMemoryImageSource);
                break;

            case 4:
                arrayOfInt = new int['Ѐ'];
                for (int i = 0; i < 21; i++) {
                    for (int j = 0; j < 12; j++) {
                        arrayOfInt[(j + i * 32)] = cursor_outline[(j + i * 12)];
                    }
                }
                localMemoryImageSource = new MemoryImageSource(32, 32, arrayOfInt, 0, 32);
                localImage = createImage(localMemoryImageSource);
                break;
            default:
                System.out.println("createCursor: unknown cursor " + paramInt);
                return Cursor.getDefaultCursor();
        }

        Cursor localCursor = null;
        if (javaVersion.compareTo("1.2") < 0) {
            System.out.println("This JVM cannot create custom cursors");
        } else {
            localCursor = customCursor(localImage, new Point(), "rcCursor");
        }
        return localCursor != null ? localCursor : Cursor.getDefaultCursor();
    }

    public void set_cursor(int paramInt) {
        this.current_cursor = createCursor(paramInt);
        setCursor(this.current_cursor);
    }
}