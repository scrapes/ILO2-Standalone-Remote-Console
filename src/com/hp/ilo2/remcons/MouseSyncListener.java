package com.hp.ilo2.remcons;

interface MouseSyncListener {
    void serverMove(int paramInt1, int paramInt2, int clientX, int clientY);

    void serverPress(int button);

    void serverRelease(int button);

    void serverClick(int button, int paramInt2);
}