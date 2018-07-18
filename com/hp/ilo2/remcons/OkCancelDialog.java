/*    */ package com.hp.ilo2.remcons;
/*    */ 
/*    */ import java.awt.Button;
/*    */ import java.awt.event.WindowEvent;
/*    */ 
/*    */ public class OkCancelDialog extends java.awt.Dialog implements java.awt.event.ActionListener, java.awt.event.WindowListener
/*    */ {
/*    */   java.awt.TextArea txt;
/*    */   Button ok;
/*    */   Button cancel;
/*    */   boolean rc;
/*    */   
/*    */   public OkCancelDialog(java.awt.Frame paramFrame, String paramString)
/*    */   {
/* 15 */     super(paramFrame, "Notice!", true);
/* 16 */     ui_init(paramString);
/*    */   }
/*    */   
/*    */   public OkCancelDialog(String paramString, boolean paramBoolean) {
/* 20 */     super(new java.awt.Frame(), "Notice!", paramBoolean);
/* 21 */     ui_init(paramString);
/*    */   }
/*    */   
/*    */   protected void ui_init(String paramString) {
/* 25 */     this.txt = new java.awt.TextArea(paramString, 5, 40, 1);
/*    */     
/* 27 */     this.txt.setEditable(false);
/* 28 */     this.ok = new Button("    Ok    ");
/* 29 */     this.ok.addActionListener(this);
/*    */     
/* 31 */     this.cancel = new Button("Cancel");
/* 32 */     this.cancel.addActionListener(this);
/*    */     
/* 34 */     setBackground(java.awt.Color.lightGray);
/* 35 */     setSize(360, 160);
/*    */     
/* 37 */     java.awt.GridBagLayout localGridBagLayout = new java.awt.GridBagLayout();
/* 38 */     java.awt.GridBagConstraints localGridBagConstraints = new java.awt.GridBagConstraints();
/*    */     
/* 40 */     setLayout(localGridBagLayout);
/*    */     
/* 42 */     localGridBagConstraints.fill = 2;
/* 43 */     localGridBagConstraints.anchor = 17;
/* 44 */     localGridBagConstraints.weightx = 100.0D;
/* 45 */     localGridBagConstraints.weighty = 100.0D;
/* 46 */     localGridBagConstraints.gridx = 0;
/* 47 */     localGridBagConstraints.gridy = 0;
/* 48 */     localGridBagConstraints.gridwidth = 1;
/* 49 */     localGridBagConstraints.gridheight = 1;
/* 50 */     add(this.txt, localGridBagConstraints);
/*    */     
/* 52 */     java.awt.Panel localPanel = new java.awt.Panel();
/* 53 */     localPanel.setLayout(new java.awt.FlowLayout(2));
/* 54 */     localPanel.add(this.ok);
/* 55 */     localPanel.add(this.cancel);
/*    */     
/* 57 */     localGridBagConstraints.fill = 0;
/* 58 */     localGridBagConstraints.anchor = 13;
/* 59 */     localGridBagConstraints.gridx = 0;
/* 60 */     localGridBagConstraints.gridy = 1;
/* 61 */     localGridBagConstraints.gridwidth = 1;
/*    */     
/* 63 */     add(localPanel, localGridBagConstraints);
/* 64 */     addWindowListener(this);
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 72 */     show();
/*    */   }
/*    */   
/*    */ 
/*    */   public void actionPerformed(java.awt.event.ActionEvent paramActionEvent)
/*    */   {
/* 78 */     if (paramActionEvent.getSource() == this.ok) {
/* 79 */       dispose();
/* 80 */       this.rc = true;
/* 81 */     } else if (paramActionEvent.getSource() == this.cancel) {
/* 82 */       dispose();
/* 83 */       this.rc = false;
/*    */     }
/*    */   }
/*    */   
/*    */   public boolean result() {
/* 88 */     return this.rc;
/*    */   }
/*    */   
/*    */   public void append(String paramString) {
/* 92 */     this.txt.append(paramString);
/* 93 */     this.txt.repaint();
/*    */   }
/*    */   
/*    */   public void windowClosing(WindowEvent paramWindowEvent) {
/* 97 */     dispose();
/* 98 */     this.rc = false;
/*    */   }
/*    */   
/*    */   public void windowOpened(WindowEvent paramWindowEvent) {}
/*    */   
/*    */   public void windowDeiconified(WindowEvent paramWindowEvent) {}
/*    */   
/*    */   public void windowIconified(WindowEvent paramWindowEvent) {}
/*    */   
/*    */   public void windowActivated(WindowEvent paramWindowEvent) {}
/*    */   
/*    */   public void windowClosed(WindowEvent paramWindowEvent) {}
/*    */   
/*    */   public void windowDeactivated(WindowEvent paramWindowEvent) {}
/*    */ }


/* Location:              C:\Users\anton\Documents\ILO2\rc175p10.jar!\com\hp\ilo2\remcons\OkCancelDialog.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */