package com.hp.ilo2.remcons;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseSync implements MouseListener, MouseMotionListener, TimerListener {
    private static final int MOUSE_BUTTON_LEFT = 4;
    private static final int MOUSE_BUTTON_CENTER = 2;
    private static final int MOUSE_BUTTON_RIGHT = 1;

    private static final int CMD_START = 0;
    private static final int CMD_STOP = 1;
    private static final int CMD_SYNC = 2;
    private static final int CMD_SERVER_MOVE = 3;
    private static final int CMD_SERVER_SCREEN = 4;
    private static final int CMD_SERVER_DISABLE = 5;
    private static final int CMD_TIMEOUT = 6;
    private static final int CMD_CLICK = 7;
    private static final int CMD_ENTER = 8;
    private static final int CMD_EXIT = 9;
    private static final int CMD_PRESS = 10;
    private static final int CMD_RELEASE = 11;
    private static final int CMD_DRAG = 12;
    private static final int CMD_MOVE = 13;
    private static final int CMD_ALIGN = 14;
    enum Command {
        START, STOP,
        SYNC,
        SERVER_MOVE, SERVER_SCREEN, SERVER_DISABLE,
        TIMEOUT,
        CLICK,
        ENTER, EXIT,
        PRESS, RELEASE,
        DRAG, MOVE,
        ALIGN
    }

    private static final int STATE_INIT = 0;
    private static final int STATE_SYNC = 1;
    private static final int STATE_ENABLE = 2;
    private static final int STATE_DISABLE = 3;

    enum State {
        INIT, SYNC, ENABLE, DISABLE
    }
    private State state;

    private static final int SYNC_SUCCESS_COUNT = 2;
    private static final int SYNC_FAIL_COUNT = 4;

    private static final int TIMEOUT_DELAY = 5;
    private static final int TIMEOUT_MOVE = 200;
    private static final int TIMEOUT_SYNC = 2000;

    private MouseSyncListener listener;
    private int server_w;
    private int server_h;
    private int server_x;
    private int server_y;
    private int client_x;
    private int client_y;
    private int client_dx;
    private int client_dy;
    private int[] send_dx;
    private int[] send_dy;
    private int[] recv_dx;
    private int[] recv_dy;
    private int send_dx_index;
    private int send_dy_index;
    private int send_dx_count;
    private int send_dy_count;
    private int send_dx_success;
    private int send_dy_success;
    private boolean sync_successful;
    private Timer timer;
    private int pressed_button;
    private boolean dragging;
    private final Object mutex;
    private boolean debugMsgEnabled = false;


    public MouseSync(Object mutex) {
        this.mutex = mutex;
        this.state = State.INIT;
        stateMachine(CMD_START, null, 0, 0);
    }

    void setListener(MouseSyncListener listener) {
        this.listener = listener;
    }

    void enableDebug() {
        this.debugMsgEnabled = true;
    }

    void disableDebug() {
        this.debugMsgEnabled = false;
    }

    void restart() {
        goState(State.INIT);
    }

    void align() {
        stateMachine(CMD_ALIGN, null, 0, 0);
    }

    void sync() {
        stateMachine(CMD_SYNC, null, 0, 0);
    }

    void serverMoved(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        stateMachine(CMD_SERVER_MOVE, null, paramInt1, paramInt2);
    }

    void serverScreen(int paramX, int paramY) {
        stateMachine(CMD_SERVER_SCREEN, null, paramX, paramY);
    }

    void setServerScreenDimensions(int height, int width) {
        this.server_h = height;
        this.server_w = width;
    }

    void serverDisabled() {
        stateMachine(CMD_SERVER_DISABLE, null, 0, 0);
    }

    public void timeout(Object paramObject) {
        stateMachine(CMD_TIMEOUT, null, 0, 0);
    }

    public void mouseClicked(MouseEvent event) {
        stateMachine(CMD_CLICK, event, 0, 0);
    }

    public void mouseEntered(MouseEvent event) {
        //stateMachine(CMD_ENTER, event, 0, 0);
    }

    public void mouseExited(MouseEvent event) {
        stateMachine(CMD_EXIT, event, 0, 0);
    }

    public void mousePressed(MouseEvent event) {
        stateMachine(CMD_PRESS, event, 0, 0);
    }

    public void mouseReleased(MouseEvent event) {
        stateMachine(CMD_RELEASE, event, 0, 0);
    }

    public void mouseDragged(MouseEvent event) {
        stateMachine(CMD_DRAG, event, 0, 0);
        moveDelay();
    }

    public void mouseMoved(MouseEvent event) {
        stateMachine(CMD_MOVE, event, 0, 0);
        moveDelay();
    }

    private void moveDelay() {
        try {
            Thread.sleep(TIMEOUT_DELAY);
        } catch (InterruptedException ignored) {}
    }

    private void syncDefault() {
        int[] arrayOfInt = {1, 4, 6, 8, 12, 16, 32, 64};

        this.send_dx = new int[arrayOfInt.length];
        this.send_dy = new int[arrayOfInt.length];
        this.recv_dx = new int[arrayOfInt.length];
        this.recv_dy = new int[arrayOfInt.length];

        System.arraycopy(arrayOfInt, 0, this.send_dx, 0, this.send_dx.length);
        System.arraycopy(arrayOfInt, 0, this.send_dy, 0, this.send_dy.length);
        System.arraycopy(arrayOfInt, 0, this.recv_dx, 0, this.recv_dx.length);
        System.arraycopy(arrayOfInt, 0, this.recv_dy, 0, this.recv_dy.length);

        this.send_dx_index = 0;
        this.send_dy_index = 0;
        this.send_dx_count = 0;
        this.send_dy_count = 0;
        this.send_dx_success = 0;
        this.send_dy_success = 0;
        this.sync_successful = false;
    }

    private void syncContinue() {
        int i = 1;
        int j = 1;
        int k = 0;
        int m = 0;

        if (this.server_x > this.server_w / 2) {
            i = -1;
        }
        if (this.server_y < this.server_h / 2) {
            j = -1;
        }

        if (this.send_dx_index >= 0) {
            k = i * this.send_dx[this.send_dx_index];
        }
        if (this.send_dy_index >= 0) {
            m = j * this.send_dy[this.send_dy_index];
        }
        this.listener.serverMove(k, m, this.client_x, this.client_y);
        this.timer.start();
    }

    private void syncUpdate(int paramInt1, int paramInt2) {
        this.timer.pause();

        int i = paramInt1 - this.server_x;
        int j = this.server_y - paramInt2;

        this.server_x = paramInt1;
        this.server_y = paramInt2;

        if (i < 0) {
            i = -i;
        }
        if (j < 0) {
            j = -j;
        }

        if (this.send_dx_index >= 0) {
            if (this.recv_dx[this.send_dx_index] == i) {
                this.send_dx_success += 1;
            }
            this.recv_dx[this.send_dx_index] = i;
            this.send_dx_count += 1;
            if (this.send_dx_success >= SYNC_SUCCESS_COUNT) {
                this.send_dx_index -= 1;
                this.send_dx_success = 0;
                this.send_dx_count = 0;
            } else if (this.send_dx_count >= SYNC_FAIL_COUNT) {
                if (this.debugMsgEnabled) {
                    System.out.println("no x sync:" + this.send_dx[this.send_dx_index]);
                }
                goState(State.ENABLE);
                return;
            }
        }


        if (this.send_dy_index >= 0) {
            if (this.recv_dy[this.send_dy_index] == j) {
                this.send_dy_success += 1;
            }
            this.recv_dy[this.send_dy_index] = j;
            this.send_dy_count += 1;
            if (this.send_dy_success >= SYNC_SUCCESS_COUNT) {
                this.send_dy_index -= 1;
                this.send_dy_success = 0;
                this.send_dy_count = 0;
            } else if (this.send_dy_count >= SYNC_FAIL_COUNT) {
                if (this.debugMsgEnabled) {
                    System.out.println("no y sync:" + this.send_dy[this.send_dy_index]);
                }
                goState(State.ENABLE);
                return;
            }
        }
        if ((this.send_dx_index < 0) && (this.send_dy_index < 0)) {
            for (int k = this.send_dx.length - 1; k >= 0; k--) {
                if ((this.recv_dx[k] == 0) || (this.recv_dy[k] == 0)) {
                    if (this.debugMsgEnabled) {
                        System.out.println("no movement:" + this.send_dx[k]);
                    }
                    goState(State.ENABLE);
                    return;
                }
                if ((k != 0) && (
                        (this.recv_dx[k] < this.recv_dx[(k - 1)]) || (this.recv_dy[k] < this.recv_dy[(k - 1)]))) {
                    if (this.debugMsgEnabled) {
                        System.out.println("not linear:" + this.send_dx[k]);
                    }
                    goState(State.ENABLE);
                    return;
                }
            }


            this.sync_successful = true;
            this.send_dx_index = 0;
            this.send_dy_index = 0;
            goState(State.ENABLE);
        } else {
            syncContinue();
        }
    }


    private void initVars() {
        this.server_w = 640;
        this.server_h = 480;
        this.server_x = 0;
        this.server_y = 0;
        this.client_x = 0;
        this.client_y = 0;
        this.client_dx = 0;
        this.client_dy = 0;
        this.pressed_button = 0;
        this.dragging = false;

        syncDefault();
    }


    private void moveServer(boolean paramBoolean1, boolean paramBoolean2) {
        int step_dx = 0;
        int step_dy = 0;
        int total_dx_sent_abs = 0;
        int total_dy_sent_abs = 0;

        this.timer.pause();

        int dx_abs = this.client_dx;
        int dy_abs = this.client_dy;

        int sign_dx = 1;
        if (this.client_dx < 0) {
            sign_dx = -1;
            dx_abs = -this.client_dx;
        }

        int sign_dy = 1;
        if (this.client_dy < 0) {
            sign_dy = -1;
            dy_abs = -this.client_dy;
        }

        while ((dx_abs != 0) || (dy_abs != 0)) {
            if (dx_abs != 0) {
                int i;
                for (i = this.send_dx.length - 1; i >= this.send_dx_index; i--) {
                    if (this.recv_dx[i] <= dx_abs) {
                        step_dx = sign_dx * this.send_dx[i];
                        total_dx_sent_abs += this.recv_dx[i];
                        dx_abs -= this.recv_dx[i];
                        break;
                    }
                }
                if (i < this.send_dx_index) {
                    step_dx = 0;
                    total_dx_sent_abs += dx_abs;
                    dx_abs = 0;
                }
            } else {
                step_dx = 0;
            }


            if (dy_abs != 0) {
                int i;
                for (i = this.send_dy.length - 1; i >= this.send_dy_index; i--) {
                    if (this.recv_dy[i] <= dy_abs) {
                        step_dy = sign_dy * this.send_dy[i];
                        total_dy_sent_abs += this.recv_dy[i];
                        dy_abs -= this.recv_dy[i];
                        break;
                    }
                }
                if (i < this.send_dy_index) {
                    step_dy = 0;
                    total_dy_sent_abs += dy_abs;
                    dy_abs = 0;
                }
            } else {
                step_dy = 0;
            }


            if ((step_dx != 0) || (step_dy != 0)) {
                this.listener.serverMove(step_dx, step_dy, this.client_x, this.client_y);
            }
        }

        this.client_dx -= sign_dx * total_dx_sent_abs;
        this.client_dy -= sign_dy * total_dy_sent_abs;

        if (!paramBoolean2) {
            this.server_x += sign_dx * total_dx_sent_abs;
            this.server_y -= sign_dy * total_dy_sent_abs;
            if (this.debugMsgEnabled) {
                System.out.println("Server:" + this.server_x + "," + this.server_y);
            }
        }

        if ((this.client_dx != 0) || (this.client_dy != 0)) {
            this.timer.start();
        }
    }


    private void goState(State state) {
        synchronized (this.mutex) {
            stateMachine(CMD_STOP, null, 0, 0);
            this.state = state;
            stateMachine(CMD_START, null, 0, 0);
        }
    }


    private void stateMachine(int command, MouseEvent mouseEvent, int paramX, int paramY) {
        synchronized (this.mutex) {
            switch (this.state) {
                case INIT:
                    stateInit(command, mouseEvent, paramX, paramY);
                    break;
                case SYNC:
                    stateSync(command, mouseEvent, paramX, paramY);
                    break;
                case ENABLE:
                    stateEnable(command, mouseEvent, paramX, paramY);
                    break;
                case DISABLE:
                    stateDisable(command, mouseEvent, paramX, paramY);
                    break;
            }
        }
    }


    private void stateInit(int command, MouseEvent mouseEvent, int paramInt2, int paramInt3) {
        if (command == CMD_START) {
            initVars();
            goState(State.DISABLE);
        }
    }


    private void stateSync(int command, MouseEvent mouseEvent, int paramInt2, int paramInt3) {
        switch (command) {
            case CMD_START:
                this.timer = new Timer(TIMEOUT_SYNC, false, this.mutex);
                this.timer.setListener(this, null);
                syncDefault();
                this.send_dx_index = (this.send_dx.length - 1);
                this.send_dy_index = (this.send_dy.length - 1);
                syncContinue();
                break;
            case CMD_STOP:
                this.timer.stop();
                this.timer = null;
                if (!this.sync_successful) {
                    if (this.debugMsgEnabled) {
                        System.out.println("fail");
                    }
                    syncDefault();

                } else if (this.debugMsgEnabled) {
                    System.out.println("success");
                }

                if (this.debugMsgEnabled) {
                    for (int i = 0; i < this.send_dx.length; i++) {
                        System.out.println(this.recv_dx[i]);
                    }
                    for (int i = 0; i < this.send_dx.length; i++) {
                        System.out.println(this.recv_dy[i]);
                    }
                }
                break;
            case CMD_SYNC:
                goState(State.SYNC);
                break;
            case CMD_SERVER_MOVE:
                if ((paramInt2 > 2000) || (paramInt3 > 2000)) {
                    goState(State.DISABLE);
                } else {
                    syncUpdate(paramInt2, paramInt3);
                }
                break;
            case CMD_SERVER_SCREEN:
                setServerScreenDimensions(paramInt2, paramInt3);
                break;
            case CMD_SERVER_DISABLE:
                goState(State.DISABLE);
                break;
            case CMD_TIMEOUT:
                goState(State.ENABLE);
                break;
            case CMD_ENTER:
            case CMD_EXIT:
            case CMD_DRAG:
            case CMD_MOVE:
                this.client_x = mouseEvent.getX();
                this.client_y = mouseEvent.getY();
                break;
        }
    }


    private void stateEnable(int command, MouseEvent mouseEvent, int paramX, int paramY) {
        switch (command) {
            case CMD_START:
                if (this.debugMsgEnabled) {
                    System.out.println("enable");
                }
                this.timer = new Timer(TIMEOUT_MOVE, false, this.mutex);
                this.timer.setListener(this, null);
                break;

            case CMD_STOP:
                this.timer.stop();
                this.timer = null;
                break;

            case CMD_SYNC:
                goState(State.SYNC);
                break;

            case CMD_SERVER_MOVE:
                if (this.debugMsgEnabled) {
                    System.out.println("Server:" + paramX + "," + paramY);
                }
                if ((paramX > 2000) || (paramY > 2000)) {
                    goState(State.DISABLE);
                } else {
                    this.server_x = paramX;
                    this.server_y = paramY;
                }
                break;

            case CMD_SERVER_SCREEN:
                setServerScreenDimensions(paramX, paramY);
                break;

            case CMD_SERVER_DISABLE:
                goState(State.DISABLE);
                break;

            case CMD_ALIGN:
                this.client_dx = (this.client_x - this.server_x);
                this.client_dy = (this.server_y - this.client_y);
                moveServer(true, true);
                break;


            case CMD_TIMEOUT:
                moveServer(true, true);
                break;

            case CMD_ENTER:
            case CMD_EXIT:
                this.client_x = mouseEvent.getX();
                this.client_y = mouseEvent.getY();
                if (this.client_x < 0) {
                    this.client_x = 0;
                }
                if (this.client_x > this.server_w) {
                    this.client_x = this.server_w;
                }
                if (this.client_y < 0) {
                    this.client_y = 0;
                }
                if (this.client_y > this.server_h) {
                    this.client_y = this.server_h;
                }
                if (this.debugMsgEnabled) {
                    System.out.println("eClient:" + this.client_x + "," + this.client_y);
                }
                if ((this.pressed_button != MOUSE_BUTTON_RIGHT) && ((mouseEvent.getModifiers() & 0x2) == 0)) {

                    align();
                }

                break;
            case CMD_DRAG:
                if (this.pressed_button != MOUSE_BUTTON_RIGHT) {
                    if (this.pressed_button > 0) {
                        this.pressed_button = (-this.pressed_button);
                        this.listener.serverPress(this.pressed_button);
                    }
                    this.client_dx += mouseEvent.getX() - this.client_x;
                    this.client_dy += this.client_y - mouseEvent.getY();
                    moveServer(false, true);
                }
                this.client_x = mouseEvent.getX();
                this.client_y = mouseEvent.getY();
                if (this.debugMsgEnabled) {
                    System.out.println("Client:" + this.client_x + "," + this.client_y);
                }
                this.dragging = true;
                break;
            case CMD_MOVE:
                if ((mouseEvent.getModifiers() & InputEvent.CTRL_MASK) == 0) {
                    this.client_dx += mouseEvent.getX() - this.client_x;
                    this.client_dy += this.client_y - mouseEvent.getY();
                    moveServer(false, true);
                }
                this.client_x = mouseEvent.getX();
                this.client_y = mouseEvent.getY();
                if (this.debugMsgEnabled) {
                    System.out.println("Client:" + this.client_x + "," + this.client_y);
                }

                break;
            case CMD_PRESS:
                handleCmdPress(mouseEvent);
                break;
            case CMD_RELEASE:
                handleCmdRelease();
                this.pressed_button = 0;
                break;
            case CMD_CLICK:
                handleCmdClick(mouseEvent);
                break;
        }
    }


    private void stateDisable(int command, MouseEvent mouseEvent, int paramInt2, int paramInt3) {
        switch (command) {
            case CMD_START:
                if (this.debugMsgEnabled) {
                    System.out.println("disable");
                }
                this.timer = new Timer(TIMEOUT_MOVE, false, this.mutex);
                this.timer.setListener(this, null);
                break;

            case CMD_STOP:
                this.timer.stop();
                this.timer = null;
                break;

            case CMD_SYNC:
                syncDefault();
                break;

            case CMD_SERVER_MOVE:
                if (this.debugMsgEnabled) {
                    System.out.println("Server:" + paramInt2 + "," + paramInt3);
                }
                if ((paramInt2 < 2000) && (paramInt3 < 2000)) {
                    this.server_x = paramInt2;
                    this.server_y = paramInt3;
                    goState(State.ENABLE);
                }

                break;
            case CMD_SERVER_SCREEN:
                setServerScreenDimensions(paramInt2, paramInt3);
                break;
            case CMD_SERVER_DISABLE:
                break;
            case CMD_ALIGN:
                this.client_dx = (this.client_x - this.server_x);
                this.client_dy = (this.server_y - this.client_y);
                moveServer(true, false);
                break;
            case CMD_TIMEOUT:
                moveServer(true, false);
                break;
            case CMD_ENTER:
            case CMD_EXIT:
                this.client_x = mouseEvent.getX();
                this.client_y = mouseEvent.getY();
                if (this.client_x < 0) {
                    this.client_x = 0;
                }
                if (this.client_x > this.server_w) {
                    this.client_x = this.server_w;
                }
                if (this.client_y < 0) {
                    this.client_y = 0;
                }
                if (this.client_y > this.server_h) {
                    this.client_y = this.server_h;
                }
                if (this.debugMsgEnabled) {
                    System.out.println("eClient:" + this.client_x + "," + this.client_y);
                }
                if ((this.pressed_button != 1) && ((mouseEvent.getModifiers() & 0x2) == 0)) {

                    align();
                }
                break;
            case CMD_DRAG:
                if (this.pressed_button != MOUSE_BUTTON_RIGHT) {
                    if (this.pressed_button > 0) {
                        this.pressed_button = (-this.pressed_button);
                        this.listener.serverPress(this.pressed_button);
                    }
                    this.client_dx += mouseEvent.getX() - this.client_x;
                    this.client_dy += this.client_y - mouseEvent.getY();
                    moveServer(false, false);
                } else {
                    this.server_x = mouseEvent.getX();
                    this.server_y = mouseEvent.getY();
                }
                this.client_x = mouseEvent.getX();
                this.client_y = mouseEvent.getY();
                if (this.debugMsgEnabled) {
                    System.out.println("Client:" + this.client_x + "," + this.client_y);
                }
                this.dragging = true;
                break;
            case CMD_MOVE:
                if ((mouseEvent.getModifiers() & 0x2) == 0) {
                    this.client_dx += mouseEvent.getX() - this.client_x;
                    this.client_dy += this.client_y - mouseEvent.getY();
                    moveServer(false, false);
                } else {
                    this.server_x = mouseEvent.getX();
                    this.server_y = mouseEvent.getY();
                }
                this.client_x = mouseEvent.getX();
                this.client_y = mouseEvent.getY();
                if (this.debugMsgEnabled) {
                    System.out.println("Client:" + this.client_x + "," + this.client_y);
                }
                break;
            case CMD_PRESS:
                handleCmdPress(mouseEvent);
                break;
            case CMD_RELEASE:
                handleCmdRelease();
                this.pressed_button = 0;
                break;
            case CMD_CLICK:
                handleCmdClick(mouseEvent);
                break;
        }
    }

    private void handleCmdPress(MouseEvent mouseEvent) {
        if (this.pressed_button == 0) {
            if ((mouseEvent.getModifiers() & 0x4) != 0) {
                this.pressed_button = MOUSE_BUTTON_RIGHT;
            } else if ((mouseEvent.getModifiers() & 0x8) != 0) {
                this.pressed_button = MOUSE_BUTTON_CENTER;
            } else {
                this.pressed_button = MOUSE_BUTTON_LEFT;
            }
            this.dragging = false;
        }
    }

    private void handleCmdRelease() {
        if (this.pressed_button == -MOUSE_BUTTON_LEFT) {
            this.listener.serverRelease(MOUSE_BUTTON_LEFT);
        } else if (this.pressed_button == -MOUSE_BUTTON_CENTER) {
            this.listener.serverRelease(MOUSE_BUTTON_CENTER);
        } else if (this.pressed_button == -MOUSE_BUTTON_RIGHT) {
            this.listener.serverRelease(MOUSE_BUTTON_RIGHT);
        }
    }

    private void handleCmdClick(MouseEvent mouseEvent) {
        if (!this.dragging) {
            if ((mouseEvent.getModifiers() & 0x10) != 0) {
                this.listener.serverClick(MOUSE_BUTTON_LEFT, 1);
            } else if ((mouseEvent.getModifiers() & 0x8) != 0) {
                this.listener.serverClick(MOUSE_BUTTON_CENTER, 1);
            } else if ((mouseEvent.getModifiers() & 0x4) != 0) {
                this.listener.serverClick(MOUSE_BUTTON_RIGHT, 1);
            }
        }
    }
}