package com.hp.ilo2.remcons;

interface MouseSyncListener
{
  void serverMove(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  void serverPress(int paramInt);
  
  void serverRelease(int paramInt);
  
  void serverClick(int paramInt1, int paramInt2);
}


/* Location:              C:\Users\anton\Documents\ILO2\rc175p10.jar!\com\hp\ilo2\remcons\MouseSyncListener.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */