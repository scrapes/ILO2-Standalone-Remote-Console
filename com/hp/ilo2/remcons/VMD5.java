/*     */ package com.hp.ilo2.remcons;
/*     */ 
/*     */ 
/*     */ public final class VMD5
/*     */   implements Cloneable
/*     */ {
/*     */   private byte[] digestBits;
/*     */   
/*     */   private String algorithm;
/*     */   
/*     */   private int[] state;
/*     */   
/*     */   private long count;
/*     */   
/*     */   private byte[] buffer;
/*     */   
/*     */   private int[] transformBuffer;
/*     */   
/*     */   private static final int S11 = 7;
/*     */   
/*     */   private static final int S12 = 12;
/*     */   
/*     */   private static final int S13 = 17;
/*     */   
/*     */   private static final int S14 = 22;
/*     */   
/*     */   private static final int S21 = 5;
/*     */   
/*     */   private static final int S22 = 9;
/*     */   
/*     */   private static final int S23 = 14;
/*     */   
/*     */   private static final int S24 = 20;
/*     */   
/*     */   private static final int S31 = 4;
/*     */   
/*     */   private static final int S32 = 11;
/*     */   
/*     */   private static final int S33 = 16;
/*     */   
/*     */   private static final int S34 = 23;
/*     */   private static final int S41 = 6;
/*     */   private static final int S42 = 10;
/*     */   private static final int S43 = 15;
/*     */   private static final int S44 = 21;
/*     */   
/*     */   public VMD5()
/*     */   {
/*  49 */     init();
/*     */   }
/*     */   
/*     */   private VMD5(VMD5 paramVMD5)
/*     */   {
/*  54 */     this();
/*  55 */     this.state = new int[paramVMD5.state.length];
/*     */     
/*  57 */     System.arraycopy(paramVMD5.state, 0, this.state, 0, paramVMD5.state.length);
/*  58 */     this.transformBuffer = new int[paramVMD5.transformBuffer.length];
/*     */     
/*  60 */     System.arraycopy(paramVMD5.transformBuffer, 0, this.transformBuffer, 0, paramVMD5.transformBuffer.length);
/*     */     
/*  62 */     this.buffer = new byte[paramVMD5.buffer.length];
/*     */     
/*  64 */     System.arraycopy(paramVMD5.buffer, 0, this.buffer, 0, paramVMD5.buffer.length);
/*     */     
/*  66 */     this.digestBits = new byte[paramVMD5.digestBits.length];
/*     */     
/*  68 */     System.arraycopy(paramVMD5.digestBits, 0, this.digestBits, 0, paramVMD5.digestBits.length);
/*     */     
/*  70 */     this.count = paramVMD5.count;
/*     */   }
/*     */   
/*     */   private int F(int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/*  75 */     return paramInt1 & paramInt2 | (paramInt1 ^ 0xFFFFFFFF) & paramInt3;
/*     */   }
/*     */   
/*     */   private int G(int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/*  80 */     return paramInt1 & paramInt3 | paramInt2 & (paramInt3 ^ 0xFFFFFFFF);
/*     */   }
/*     */   
/*     */   private int H(int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/*  85 */     return paramInt1 ^ paramInt2 ^ paramInt3;
/*     */   }
/*     */   
/*     */   private int I(int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/*  90 */     return paramInt2 ^ (paramInt1 | paramInt3 ^ 0xFFFFFFFF);
/*     */   }
/*     */   
/*     */   private int rotateLeft(int paramInt1, int paramInt2)
/*     */   {
/*  95 */     return paramInt1 << paramInt2 | paramInt1 >>> 32 - paramInt2;
/*     */   }
/*     */   
/*     */   private int FF(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
/*     */   {
/* 100 */     paramInt1 += F(paramInt2, paramInt3, paramInt4) + paramInt5 + paramInt7;
/* 101 */     paramInt1 = rotateLeft(paramInt1, paramInt6);
/* 102 */     paramInt1 += paramInt2;
/* 103 */     return paramInt1;
/*     */   }
/*     */   
/*     */   private int GG(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
/*     */   {
/* 108 */     paramInt1 += G(paramInt2, paramInt3, paramInt4) + paramInt5 + paramInt7;
/* 109 */     paramInt1 = rotateLeft(paramInt1, paramInt6);
/* 110 */     paramInt1 += paramInt2;
/* 111 */     return paramInt1;
/*     */   }
/*     */   
/*     */   private int HH(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
/*     */   {
/* 116 */     paramInt1 += H(paramInt2, paramInt3, paramInt4) + paramInt5 + paramInt7;
/* 117 */     paramInt1 = rotateLeft(paramInt1, paramInt6);
/* 118 */     paramInt1 += paramInt2;
/* 119 */     return paramInt1;
/*     */   }
/*     */   
/*     */   private int II(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
/*     */   {
/* 124 */     paramInt1 += I(paramInt2, paramInt3, paramInt4) + paramInt5 + paramInt7;
/* 125 */     paramInt1 = rotateLeft(paramInt1, paramInt6);
/* 126 */     paramInt1 += paramInt2;
/* 127 */     return paramInt1;
/*     */   }
/*     */   
/*     */   void transform(byte[] paramArrayOfByte, int paramInt)
/*     */   {
/* 132 */     int[] arrayOfInt = this.transformBuffer;
/* 133 */     int i = this.state[0];
/* 134 */     int j = this.state[1];
/* 135 */     int k = this.state[2];
/* 136 */     int m = this.state[3];
/* 137 */     for (int n = 0; n < 16; n++)
/*     */     {
/* 139 */       arrayOfInt[n] = (paramArrayOfByte[(n * 4 + paramInt)] & 0xFF);
/* 140 */       for (int i1 = 1; i1 < 4; i1++)
/* 141 */         arrayOfInt[n] += ((paramArrayOfByte[(n * 4 + i1 + paramInt)] & 0xFF) << i1 * 8);
/*     */     }
/* 143 */     i = FF(i, j, k, m, arrayOfInt[0], 7, -680876936);
/* 144 */     m = FF(m, i, j, k, arrayOfInt[1], 12, -389564586);
/* 145 */     k = FF(k, m, i, j, arrayOfInt[2], 17, 606105819);
/* 146 */     j = FF(j, k, m, i, arrayOfInt[3], 22, -1044525330);
/* 147 */     i = FF(i, j, k, m, arrayOfInt[4], 7, -176418897);
/* 148 */     m = FF(m, i, j, k, arrayOfInt[5], 12, 1200080426);
/* 149 */     k = FF(k, m, i, j, arrayOfInt[6], 17, -1473231341);
/* 150 */     j = FF(j, k, m, i, arrayOfInt[7], 22, -45705983);
/* 151 */     i = FF(i, j, k, m, arrayOfInt[8], 7, 1770035416);
/* 152 */     m = FF(m, i, j, k, arrayOfInt[9], 12, -1958414417);
/* 153 */     k = FF(k, m, i, j, arrayOfInt[10], 17, -42063);
/* 154 */     j = FF(j, k, m, i, arrayOfInt[11], 22, -1990404162);
/* 155 */     i = FF(i, j, k, m, arrayOfInt[12], 7, 1804603682);
/* 156 */     m = FF(m, i, j, k, arrayOfInt[13], 12, -40341101);
/* 157 */     k = FF(k, m, i, j, arrayOfInt[14], 17, -1502002290);
/* 158 */     j = FF(j, k, m, i, arrayOfInt[15], 22, 1236535329);
/* 159 */     i = GG(i, j, k, m, arrayOfInt[1], 5, -165796510);
/* 160 */     m = GG(m, i, j, k, arrayOfInt[6], 9, -1069501632);
/* 161 */     k = GG(k, m, i, j, arrayOfInt[11], 14, 643717713);
/* 162 */     j = GG(j, k, m, i, arrayOfInt[0], 20, -373897302);
/* 163 */     i = GG(i, j, k, m, arrayOfInt[5], 5, -701558691);
/* 164 */     m = GG(m, i, j, k, arrayOfInt[10], 9, 38016083);
/* 165 */     k = GG(k, m, i, j, arrayOfInt[15], 14, -660478335);
/* 166 */     j = GG(j, k, m, i, arrayOfInt[4], 20, -405537848);
/* 167 */     i = GG(i, j, k, m, arrayOfInt[9], 5, 568446438);
/* 168 */     m = GG(m, i, j, k, arrayOfInt[14], 9, -1019803690);
/* 169 */     k = GG(k, m, i, j, arrayOfInt[3], 14, -187363961);
/* 170 */     j = GG(j, k, m, i, arrayOfInt[8], 20, 1163531501);
/* 171 */     i = GG(i, j, k, m, arrayOfInt[13], 5, -1444681467);
/* 172 */     m = GG(m, i, j, k, arrayOfInt[2], 9, -51403784);
/* 173 */     k = GG(k, m, i, j, arrayOfInt[7], 14, 1735328473);
/* 174 */     j = GG(j, k, m, i, arrayOfInt[12], 20, -1926607734);
/* 175 */     i = HH(i, j, k, m, arrayOfInt[5], 4, -378558);
/* 176 */     m = HH(m, i, j, k, arrayOfInt[8], 11, -2022574463);
/* 177 */     k = HH(k, m, i, j, arrayOfInt[11], 16, 1839030562);
/* 178 */     j = HH(j, k, m, i, arrayOfInt[14], 23, -35309556);
/* 179 */     i = HH(i, j, k, m, arrayOfInt[1], 4, -1530992060);
/* 180 */     m = HH(m, i, j, k, arrayOfInt[4], 11, 1272893353);
/* 181 */     k = HH(k, m, i, j, arrayOfInt[7], 16, -155497632);
/* 182 */     j = HH(j, k, m, i, arrayOfInt[10], 23, -1094730640);
/* 183 */     i = HH(i, j, k, m, arrayOfInt[13], 4, 681279174);
/* 184 */     m = HH(m, i, j, k, arrayOfInt[0], 11, -358537222);
/* 185 */     k = HH(k, m, i, j, arrayOfInt[3], 16, -722521979);
/* 186 */     j = HH(j, k, m, i, arrayOfInt[6], 23, 76029189);
/* 187 */     i = HH(i, j, k, m, arrayOfInt[9], 4, -640364487);
/* 188 */     m = HH(m, i, j, k, arrayOfInt[12], 11, -421815835);
/* 189 */     k = HH(k, m, i, j, arrayOfInt[15], 16, 530742520);
/* 190 */     j = HH(j, k, m, i, arrayOfInt[2], 23, -995338651);
/* 191 */     i = II(i, j, k, m, arrayOfInt[0], 6, -198630844);
/* 192 */     m = II(m, i, j, k, arrayOfInt[7], 10, 1126891415);
/* 193 */     k = II(k, m, i, j, arrayOfInt[14], 15, -1416354905);
/* 194 */     j = II(j, k, m, i, arrayOfInt[5], 21, -57434055);
/* 195 */     i = II(i, j, k, m, arrayOfInt[12], 6, 1700485571);
/* 196 */     m = II(m, i, j, k, arrayOfInt[3], 10, -1894986606);
/* 197 */     k = II(k, m, i, j, arrayOfInt[10], 15, -1051523);
/* 198 */     j = II(j, k, m, i, arrayOfInt[1], 21, -2054922799);
/* 199 */     i = II(i, j, k, m, arrayOfInt[8], 6, 1873313359);
/* 200 */     m = II(m, i, j, k, arrayOfInt[15], 10, -30611744);
/* 201 */     k = II(k, m, i, j, arrayOfInt[6], 15, -1560198380);
/* 202 */     j = II(j, k, m, i, arrayOfInt[13], 21, 1309151649);
/* 203 */     i = II(i, j, k, m, arrayOfInt[4], 6, -145523070);
/* 204 */     m = II(m, i, j, k, arrayOfInt[11], 10, -1120210379);
/* 205 */     k = II(k, m, i, j, arrayOfInt[2], 15, 718787259);
/* 206 */     j = II(j, k, m, i, arrayOfInt[9], 21, -343485551);
/* 207 */     this.state[0] += i;
/* 208 */     this.state[1] += j;
/* 209 */     this.state[2] += k;
/* 210 */     this.state[3] += m;
/*     */   }
/*     */   
/*     */   public void init()
/*     */   {
/* 215 */     this.state = new int[4];
/* 216 */     this.transformBuffer = new int[16];
/* 217 */     this.buffer = new byte[64];
/* 218 */     this.digestBits = new byte[16];
/* 219 */     this.count = 0L;
/* 220 */     this.state[0] = 1732584193;
/* 221 */     this.state[1] = -271733879;
/* 222 */     this.state[2] = -1732584194;
/* 223 */     this.state[3] = 271733878;
/* 224 */     for (int i = 0; i < this.digestBits.length; i++) {
/* 225 */       this.digestBits[i] = 0;
/*     */     }
/*     */   }
/*     */   
/*     */   public void engineReset() {
/* 230 */     init();
/*     */   }
/*     */   
/*     */   public synchronized void engineUpdate(byte paramByte)
/*     */   {
/* 235 */     int i = (int)(this.count >>> 3 & 0x3F);
/* 236 */     this.count += 8L;
/* 237 */     this.buffer[i] = paramByte;
/* 238 */     if (i >= 63) {
/* 239 */       transform(this.buffer, 0);
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized void engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
/* 244 */     int i = paramInt1;
/* 245 */     while (paramInt2 > 0)
/*     */     {
/* 247 */       int j = (int)(this.count >>> 3 & 0x3F);
/* 248 */       if ((j == 0) && (paramInt2 > 64))
/*     */       {
/* 250 */         this.count += 512L;
/* 251 */         transform(paramArrayOfByte, i);
/* 252 */         paramInt2 -= 64;
/* 253 */         i += 64;
/*     */       }
/*     */       else
/*     */       {
/* 257 */         this.count += 8L;
/* 258 */         this.buffer[j] = paramArrayOfByte[i];
/* 259 */         if (j >= 63)
/* 260 */           transform(this.buffer, 0);
/* 261 */         i++;
/* 262 */         paramInt2--;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void finish()
/*     */   {
/* 269 */     byte[] arrayOfByte1 = new byte[8];
/* 270 */     int i = 0;
/* 271 */     int j = 0;
/* 272 */     int k = 0;
/* 273 */     for (i = 0; i < 8; i++)
/* 274 */       arrayOfByte1[i] = ((byte)(int)(this.count >>> i * 8 & 0xFF));
/* 275 */     k = (int)(this.count >> 3) & 0x3F;
/* 276 */     i = k < 56 ? 56 - k : 120 - k;
/* 277 */     byte[] arrayOfByte2 = new byte[i];
/* 278 */     arrayOfByte2[0] = Byte.MIN_VALUE;
/* 279 */     engineUpdate(arrayOfByte2, 0, arrayOfByte2.length);
/* 280 */     engineUpdate(arrayOfByte1, 0, arrayOfByte1.length);
/* 281 */     for (i = 0; i < 4; i++)
/*     */     {
/* 283 */       for (j = 0; j < 4; j++)
/*     */       {
/* 285 */         this.digestBits[(i * 4 + j)] = ((byte)(this.state[i] >>> j * 8 & 0xFF));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] engineDigest()
/*     */   {
/* 293 */     finish();
/* 294 */     byte[] arrayOfByte = new byte[16];
/* 295 */     System.arraycopy(this.digestBits, 0, arrayOfByte, 0, 16);
/* 296 */     init();
/* 297 */     return arrayOfByte;
/*     */   }
/*     */   
/*     */   public Object clone()
/*     */   {
/* 302 */     return new VMD5(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/* 310 */     engineReset();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void update(byte paramByte)
/*     */   {
/* 321 */     engineUpdate(paramByte);
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
/*     */   public void update(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */   {
/* 337 */     engineUpdate(paramArrayOfByte, paramInt1, paramInt2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void update(byte[] paramArrayOfByte)
/*     */   {
/* 347 */     engineUpdate(paramArrayOfByte, 0, paramArrayOfByte.length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] digest()
/*     */   {
/* 359 */     this.digestBits = engineDigest();
/* 360 */     return this.digestBits;
/*     */   }
/*     */ }


/* Location:              C:\Users\anton\Documents\ILO2\rc175p10.jar!\com\hp\ilo2\remcons\VMD5.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */