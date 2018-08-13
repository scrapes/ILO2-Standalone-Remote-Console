package com.hp.ilo2.remcons;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RC4
{










  byte[] key = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
  byte[] pre = new byte[16];
  byte[] sBox = new byte['Ā'];
  byte[] keyBox = new byte['Ā'];
  int i = 0;
  int j = 0;

  RC4(byte[] paramArrayOfByte)
  {
    System.arraycopy(paramArrayOfByte, 0, this.pre, 0, 16);
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

    byte[] arrayOfByte = md.digest();
    System.arraycopy(arrayOfByte, 0, this.key, 0, this.key.length);






    for (int k = 0; k < 256; k++)
    {
      this.sBox[k] = ((byte)(k & 0xFF));
      this.keyBox[k] = this.key[(k % 16)];
    }


    this.j = 0;
    for (this.i = 0; this.i < 256; this.i += 1)
    {
      this.j = ((this.j & 0xFF) + (this.sBox[this.i] & 0xFF) + (this.keyBox[this.i] & 0xFF) & 0xFF);
      int m = this.sBox[this.i];
      this.sBox[this.i] = this.sBox[this.j];
      this.sBox[this.j] = (byte)m;
    }

    this.i = 0;
    this.j = 0;
  }

  int randomValue()
  {
    this.i = ((this.i & 0xFF) + 1 & 0xFF);
    this.j = ((this.j & 0xFF) + (this.sBox[this.i] & 0xFF) & 0xFF);
    int k = this.sBox[this.i];
    this.sBox[this.i] = this.sBox[this.j];
    this.sBox[this.j] = (byte)k;
    int m = (this.sBox[this.i] & 0xFF) + (this.sBox[this.j] & 0xFF) & 0xFF;
    int n = this.sBox[m];
    return n;
  }
}


/* Location:              C:\Users\anton\Documents\ILO2\rc175p10.jar!\com\hp\ilo2\remcons\RC4.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */