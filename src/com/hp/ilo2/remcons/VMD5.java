package com.hp.ilo2.remcons;


public final class VMD5
  implements Cloneable
{
  private byte[] digestBits;

  private String algorithm;

  private int[] state;

  private long count;

  private byte[] buffer;

  private int[] transformBuffer;

  private static final int S11 = 7;

  private static final int S12 = 12;

  private static final int S13 = 17;

  private static final int S14 = 22;

  private static final int S21 = 5;

  private static final int S22 = 9;

  private static final int S23 = 14;

  private static final int S24 = 20;

  private static final int S31 = 4;

  private static final int S32 = 11;

  private static final int S33 = 16;

  private static final int S34 = 23;
  private static final int S41 = 6;
  private static final int S42 = 10;
  private static final int S43 = 15;
  private static final int S44 = 21;

  public VMD5()
  {
    init();
  }

  private VMD5(VMD5 paramVMD5)
  {
    this();
    this.state = new int[paramVMD5.state.length];

    System.arraycopy(paramVMD5.state, 0, this.state, 0, paramVMD5.state.length);
    this.transformBuffer = new int[paramVMD5.transformBuffer.length];

    System.arraycopy(paramVMD5.transformBuffer, 0, this.transformBuffer, 0, paramVMD5.transformBuffer.length);

    this.buffer = new byte[paramVMD5.buffer.length];

    System.arraycopy(paramVMD5.buffer, 0, this.buffer, 0, paramVMD5.buffer.length);

    this.digestBits = new byte[paramVMD5.digestBits.length];

    System.arraycopy(paramVMD5.digestBits, 0, this.digestBits, 0, paramVMD5.digestBits.length);

    this.count = paramVMD5.count;
  }

  private int F(int paramInt1, int paramInt2, int paramInt3)
  {
    return paramInt1 & paramInt2 | (paramInt1 ^ 0xFFFFFFFF) & paramInt3;
  }

  private int G(int paramInt1, int paramInt2, int paramInt3)
  {
    return paramInt1 & paramInt3 | paramInt2 & (paramInt3 ^ 0xFFFFFFFF);
  }

  private int H(int paramInt1, int paramInt2, int paramInt3)
  {
    return paramInt1 ^ paramInt2 ^ paramInt3;
  }

  private int I(int paramInt1, int paramInt2, int paramInt3)
  {
    return paramInt2 ^ (paramInt1 | paramInt3 ^ 0xFFFFFFFF);
  }

  private int rotateLeft(int paramInt1, int paramInt2)
  {
    return paramInt1 << paramInt2 | paramInt1 >>> 32 - paramInt2;
  }

  private int FF(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
  {
    paramInt1 += F(paramInt2, paramInt3, paramInt4) + paramInt5 + paramInt7;
    paramInt1 = rotateLeft(paramInt1, paramInt6);
    paramInt1 += paramInt2;
    return paramInt1;
  }

  private int GG(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
  {
    paramInt1 += G(paramInt2, paramInt3, paramInt4) + paramInt5 + paramInt7;
    paramInt1 = rotateLeft(paramInt1, paramInt6);
    paramInt1 += paramInt2;
    return paramInt1;
  }

  private int HH(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
  {
    paramInt1 += H(paramInt2, paramInt3, paramInt4) + paramInt5 + paramInt7;
    paramInt1 = rotateLeft(paramInt1, paramInt6);
    paramInt1 += paramInt2;
    return paramInt1;
  }

  private int II(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
  {
    paramInt1 += I(paramInt2, paramInt3, paramInt4) + paramInt5 + paramInt7;
    paramInt1 = rotateLeft(paramInt1, paramInt6);
    paramInt1 += paramInt2;
    return paramInt1;
  }

  void transform(byte[] paramArrayOfByte, int paramInt)
  {
    int[] arrayOfInt = this.transformBuffer;
    int i = this.state[0];
    int j = this.state[1];
    int k = this.state[2];
    int m = this.state[3];
    for (int n = 0; n < 16; n++)
    {
      arrayOfInt[n] = (paramArrayOfByte[(n * 4 + paramInt)] & 0xFF);
      for (int i1 = 1; i1 < 4; i1++)
        arrayOfInt[n] += ((paramArrayOfByte[(n * 4 + i1 + paramInt)] & 0xFF) << i1 * 8);
    }
    i = FF(i, j, k, m, arrayOfInt[0], 7, -680876936);
    m = FF(m, i, j, k, arrayOfInt[1], 12, -389564586);
    k = FF(k, m, i, j, arrayOfInt[2], 17, 606105819);
    j = FF(j, k, m, i, arrayOfInt[3], 22, -1044525330);
    i = FF(i, j, k, m, arrayOfInt[4], 7, -176418897);
    m = FF(m, i, j, k, arrayOfInt[5], 12, 1200080426);
    k = FF(k, m, i, j, arrayOfInt[6], 17, -1473231341);
    j = FF(j, k, m, i, arrayOfInt[7], 22, -45705983);
    i = FF(i, j, k, m, arrayOfInt[8], 7, 1770035416);
    m = FF(m, i, j, k, arrayOfInt[9], 12, -1958414417);
    k = FF(k, m, i, j, arrayOfInt[10], 17, -42063);
    j = FF(j, k, m, i, arrayOfInt[11], 22, -1990404162);
    i = FF(i, j, k, m, arrayOfInt[12], 7, 1804603682);
    m = FF(m, i, j, k, arrayOfInt[13], 12, -40341101);
    k = FF(k, m, i, j, arrayOfInt[14], 17, -1502002290);
    j = FF(j, k, m, i, arrayOfInt[15], 22, 1236535329);
    i = GG(i, j, k, m, arrayOfInt[1], 5, -165796510);
    m = GG(m, i, j, k, arrayOfInt[6], 9, -1069501632);
    k = GG(k, m, i, j, arrayOfInt[11], 14, 643717713);
    j = GG(j, k, m, i, arrayOfInt[0], 20, -373897302);
    i = GG(i, j, k, m, arrayOfInt[5], 5, -701558691);
    m = GG(m, i, j, k, arrayOfInt[10], 9, 38016083);
    k = GG(k, m, i, j, arrayOfInt[15], 14, -660478335);
    j = GG(j, k, m, i, arrayOfInt[4], 20, -405537848);
    i = GG(i, j, k, m, arrayOfInt[9], 5, 568446438);
    m = GG(m, i, j, k, arrayOfInt[14], 9, -1019803690);
    k = GG(k, m, i, j, arrayOfInt[3], 14, -187363961);
    j = GG(j, k, m, i, arrayOfInt[8], 20, 1163531501);
    i = GG(i, j, k, m, arrayOfInt[13], 5, -1444681467);
    m = GG(m, i, j, k, arrayOfInt[2], 9, -51403784);
    k = GG(k, m, i, j, arrayOfInt[7], 14, 1735328473);
    j = GG(j, k, m, i, arrayOfInt[12], 20, -1926607734);
    i = HH(i, j, k, m, arrayOfInt[5], 4, -378558);
    m = HH(m, i, j, k, arrayOfInt[8], 11, -2022574463);
    k = HH(k, m, i, j, arrayOfInt[11], 16, 1839030562);
    j = HH(j, k, m, i, arrayOfInt[14], 23, -35309556);
    i = HH(i, j, k, m, arrayOfInt[1], 4, -1530992060);
    m = HH(m, i, j, k, arrayOfInt[4], 11, 1272893353);
    k = HH(k, m, i, j, arrayOfInt[7], 16, -155497632);
    j = HH(j, k, m, i, arrayOfInt[10], 23, -1094730640);
    i = HH(i, j, k, m, arrayOfInt[13], 4, 681279174);
    m = HH(m, i, j, k, arrayOfInt[0], 11, -358537222);
    k = HH(k, m, i, j, arrayOfInt[3], 16, -722521979);
    j = HH(j, k, m, i, arrayOfInt[6], 23, 76029189);
    i = HH(i, j, k, m, arrayOfInt[9], 4, -640364487);
    m = HH(m, i, j, k, arrayOfInt[12], 11, -421815835);
    k = HH(k, m, i, j, arrayOfInt[15], 16, 530742520);
    j = HH(j, k, m, i, arrayOfInt[2], 23, -995338651);
    i = II(i, j, k, m, arrayOfInt[0], 6, -198630844);
    m = II(m, i, j, k, arrayOfInt[7], 10, 1126891415);
    k = II(k, m, i, j, arrayOfInt[14], 15, -1416354905);
    j = II(j, k, m, i, arrayOfInt[5], 21, -57434055);
    i = II(i, j, k, m, arrayOfInt[12], 6, 1700485571);
    m = II(m, i, j, k, arrayOfInt[3], 10, -1894986606);
    k = II(k, m, i, j, arrayOfInt[10], 15, -1051523);
    j = II(j, k, m, i, arrayOfInt[1], 21, -2054922799);
    i = II(i, j, k, m, arrayOfInt[8], 6, 1873313359);
    m = II(m, i, j, k, arrayOfInt[15], 10, -30611744);
    k = II(k, m, i, j, arrayOfInt[6], 15, -1560198380);
    j = II(j, k, m, i, arrayOfInt[13], 21, 1309151649);
    i = II(i, j, k, m, arrayOfInt[4], 6, -145523070);
    m = II(m, i, j, k, arrayOfInt[11], 10, -1120210379);
    k = II(k, m, i, j, arrayOfInt[2], 15, 718787259);
    j = II(j, k, m, i, arrayOfInt[9], 21, -343485551);
    this.state[0] += i;
    this.state[1] += j;
    this.state[2] += k;
    this.state[3] += m;
  }

  public void init()
  {
    this.state = new int[4];
    this.transformBuffer = new int[16];
    this.buffer = new byte[64];
    this.digestBits = new byte[16];
    this.count = 0L;
    this.state[0] = 1732584193;
    this.state[1] = -271733879;
    this.state[2] = -1732584194;
    this.state[3] = 271733878;
    for (int i = 0; i < this.digestBits.length; i++) {
      this.digestBits[i] = 0;
    }
  }

  public void engineReset() {
    init();
  }

  public synchronized void engineUpdate(byte paramByte)
  {
    int i = (int)(this.count >>> 3 & 0x3F);
    this.count += 8L;
    this.buffer[i] = paramByte;
    if (i >= 63) {
      transform(this.buffer, 0);
    }
  }

  public synchronized void engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    int i = paramInt1;
    while (paramInt2 > 0)
    {
      int j = (int)(this.count >>> 3 & 0x3F);
      if ((j == 0) && (paramInt2 > 64))
      {
        this.count += 512L;
        transform(paramArrayOfByte, i);
        paramInt2 -= 64;
        i += 64;
      }
      else
      {
        this.count += 8L;
        this.buffer[j] = paramArrayOfByte[i];
        if (j >= 63)
          transform(this.buffer, 0);
        i++;
        paramInt2--;
      }
    }
  }

  private void finish()
  {
    byte[] arrayOfByte1 = new byte[8];
    int i = 0;
    int j = 0;
    int k = 0;
    for (i = 0; i < 8; i++)
      arrayOfByte1[i] = ((byte)(int)(this.count >>> i * 8 & 0xFF));
    k = (int)(this.count >> 3) & 0x3F;
    i = k < 56 ? 56 - k : 120 - k;
    byte[] arrayOfByte2 = new byte[i];
    arrayOfByte2[0] = Byte.MIN_VALUE;
    engineUpdate(arrayOfByte2, 0, arrayOfByte2.length);
    engineUpdate(arrayOfByte1, 0, arrayOfByte1.length);
    for (i = 0; i < 4; i++)
    {
      for (j = 0; j < 4; j++)
      {
        this.digestBits[(i * 4 + j)] = ((byte)(this.state[i] >>> j * 8 & 0xFF));
      }
    }
  }


  public byte[] engineDigest()
  {
    finish();
    byte[] arrayOfByte = new byte[16];
    System.arraycopy(this.digestBits, 0, arrayOfByte, 0, 16);
    init();
    return arrayOfByte;
  }

  public Object clone()
  {
    return new VMD5(this);
  }




  public void reset()
  {
    engineReset();
  }







  public void update(byte paramByte)
  {
    engineUpdate(paramByte);
  }












  public void update(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    engineUpdate(paramArrayOfByte, paramInt1, paramInt2);
  }






  public void update(byte[] paramArrayOfByte)
  {
    engineUpdate(paramArrayOfByte, 0, paramArrayOfByte.length);
  }








  public byte[] digest()
  {
    this.digestBits = engineDigest();
    return this.digestBits;
  }
}


/* Location:              C:\Users\anton\Documents\ILO2\rc175p10.jar!\com\hp\ilo2\remcons\VMD5.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */