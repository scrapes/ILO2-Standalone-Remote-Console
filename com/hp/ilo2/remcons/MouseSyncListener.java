package com.hp.ilo2.remcons;

abstract interface MouseSyncListener
{
  public abstract void serverMove(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void serverPress(int paramInt);
  
  public abstract void serverRelease(int paramInt);
  
  public abstract void serverClick(int paramInt1, int paramInt2);
}


/* Location:              C:\Users\anton\Documents\ILO2\rc175p10.jar!\com\hp\ilo2\remcons\MouseSyncListener.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */