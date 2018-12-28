package com.hp.ilo2.remcons;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class RC4 {

  private byte[] key = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
  private byte[] pre = new byte[16];
  private byte[] sBox = new byte[0x100];
  private byte[] keyBox = new byte[0x100];
  private int i = 0;
  private int j = 0;

  RC4(byte[] key) {
    System.arraycopy(key, 0, this.pre, 0, 16);
      try {
          update_key();
      } catch (NoSuchAlgorithmException e) {
          e.printStackTrace();
      }
  }

  void update_key() throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("MD5");
    md.update(this.pre);
    md.update(this.key);

    byte[] digest = md.digest();
    System.arraycopy(digest, 0, this.key, 0, this.key.length);

    for (int k = 0; k < 256; k++) {
      this.sBox[k] = ((byte)(k & 0xFF));
      this.keyBox[k] = this.key[(k % 16)];
    }

    this.j = 0;
    for (this.i = 0; this.i < 256; this.i += 1) {
      this.j = ((this.j & 0xFF) + (this.sBox[this.i] & 0xFF) + (this.keyBox[this.i] & 0xFF) & 0xFF);
      int m = this.sBox[this.i];
      this.sBox[this.i] = this.sBox[this.j];
      this.sBox[this.j] = (byte)m;
    }

    this.i = 0;
    this.j = 0;
  }

  int randomValue() {
    this.i = ((this.i & 0xFF) + 1 & 0xFF);
    this.j = ((this.j & 0xFF) + (this.sBox[this.i] & 0xFF) & 0xFF);
    int k = this.sBox[this.i];
    this.sBox[this.i] = this.sBox[this.j];
    this.sBox[this.j] = (byte)k;
    int m = (this.sBox[this.i] & 0xFF) + (this.sBox[this.j] & 0xFF) & 0xFF;
    return (int) this.sBox[m];
  }
}