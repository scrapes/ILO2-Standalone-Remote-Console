/*    */ package com.hp.ilo2.remcons;
/*    */
/*    */import java.security.MessageDigest;
/*    */import java.security.NoSuchAlgorithmException;
/*    */
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RC4
/*    */ {
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 26 */   byte[] key = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/* 27 */   byte[] pre = new byte[16];
/* 28 */   byte[] sBox = new byte['Ā'];
/* 29 */   byte[] keyBox = new byte['Ā'];
/* 30 */   int i = 0;
/* 31 */   int j = 0;
/*    */   
/*    */   RC4(byte[] paramArrayOfByte)
/*    */   {
/* 35 */     System.arraycopy(paramArrayOfByte, 0, this.pre, 0, 16);
/* 37 */
/*    */     try {
/*    */         update_key();
/*    */     } catch (NoSuchAlgorithmException e) {
/*    */         e.printStackTrace();
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   void update_key() throws NoSuchAlgorithmException
/*    */   {
/*    */     MessageDigest digest = MessageDigest.getInstance("MD5");
/*    */     digest.update(this.pre);
/*    */     digest.update(this.key);
/*    */     byte[] arrayOfByte = digest.digest();
/*    */
/* 48 */     System.arraycopy(arrayOfByte, 0, this.key, 0, this.key.length);
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 55 */     for (int k = 0; k < 256; k++)
/*    */     {
/* 57 */       this.sBox[k] = ((byte)(k & 0xFF));
/* 58 */       this.keyBox[k] = this.key[(k % 16)];
/*    */     }
/*    */     
/*    */ 
/* 62 */     this.j = 0;
/* 63 */     for (this.i = 0; this.i < 256; this.i += 1)
/*    */     {
/* 65 */       this.j = ((this.j & 0xFF) + (this.sBox[this.i] & 0xFF) + (this.keyBox[this.i] & 0xFF) & 0xFF);
/* 66 */       int m = this.sBox[this.i];
/* 67 */       this.sBox[this.i] = this.sBox[this.j];
/* 68 */       this.sBox[this.j] = (byte)m;
/*    */     }
/*    */     
/* 71 */     this.i = 0;
/* 72 */     this.j = 0;
/*    */   }
/*    */   
/*    */   int randomValue()
/*    */   {
/* 77 */     this.i = ((this.i & 0xFF) + 1 & 0xFF);
/* 78 */     this.j = ((this.j & 0xFF) + (this.sBox[this.i] & 0xFF) & 0xFF);
/* 79 */     int k = this.sBox[this.i];
/* 80 */     this.sBox[this.i] = this.sBox[this.j];
/* 81 */     this.sBox[this.j] = (byte)k;
/* 82 */     int m = (this.sBox[this.i] & 0xFF) + (this.sBox[this.j] & 0xFF) & 0xFF;
/* 83 */     int n = this.sBox[m];
/* 84 */     return n;
/*    */   }
/*    */ }


/* Location:              C:\Users\anton\Documents\ILO2\rc175p10.jar!\com\hp\ilo2\remcons\RC4.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */