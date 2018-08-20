package com.hp.ilo2.remcons;

import java.awt.Button;
import java.awt.event.WindowEvent;

public class OkCancelDialog extends java.awt.Dialog implements java.awt.event.ActionListener, java.awt.event.WindowListener
{
  java.awt.TextArea txt;
  Button ok;
  Button cancel;
  boolean rc;

  public OkCancelDialog(java.awt.Frame paramFrame, String paramString)
  {
    super(paramFrame, "Notice!", true);
    ui_init(paramString);
  }

  public OkCancelDialog(String paramString, boolean paramBoolean) {
    super(new java.awt.Frame(), "Notice!", paramBoolean);
    ui_init(paramString);
  }

  protected void ui_init(String paramString) {
    this.txt = new java.awt.TextArea(paramString, 5, 40, 1);

    this.txt.setEditable(false);
    this.ok = new Button("    Ok    ");
    this.ok.addActionListener(this);

    this.cancel = new Button("Cancel");
    this.cancel.addActionListener(this);

    setBackground(java.awt.Color.lightGray);
    setSize(360, 160);

    java.awt.GridBagLayout localGridBagLayout = new java.awt.GridBagLayout();
    java.awt.GridBagConstraints localGridBagConstraints = new java.awt.GridBagConstraints();

    setLayout(localGridBagLayout);

    localGridBagConstraints.fill = 2;
    localGridBagConstraints.anchor = 17;
    localGridBagConstraints.weightx = 100.0D;
    localGridBagConstraints.weighty = 100.0D;
    localGridBagConstraints.gridx = 0;
    localGridBagConstraints.gridy = 0;
    localGridBagConstraints.gridwidth = 1;
    localGridBagConstraints.gridheight = 1;
    add(this.txt, localGridBagConstraints);

    java.awt.Panel localPanel = new java.awt.Panel();
    localPanel.setLayout(new java.awt.FlowLayout(2));
    localPanel.add(this.ok);
    localPanel.add(this.cancel);

    localGridBagConstraints.fill = 0;
    localGridBagConstraints.anchor = 13;
    localGridBagConstraints.gridx = 0;
    localGridBagConstraints.gridy = 1;
    localGridBagConstraints.gridwidth = 1;

    add(localPanel, localGridBagConstraints);
    addWindowListener(this);







    setVisible(true);
  }


  public void actionPerformed(java.awt.event.ActionEvent paramActionEvent)
  {
    if (paramActionEvent.getSource() == this.ok) {
      dispose();
      this.rc = true;
    } else if (paramActionEvent.getSource() == this.cancel) {
      dispose();
      this.rc = false;
    }
  }

  public boolean result() {
    return this.rc;
  }

  public void append(String paramString) {
    this.txt.append(paramString);
    this.txt.repaint();
  }

  public void windowClosing(WindowEvent paramWindowEvent) {
    dispose();
    this.rc = false;
  }

  public void windowOpened(WindowEvent paramWindowEvent) {}

  public void windowDeiconified(WindowEvent paramWindowEvent) {}

  public void windowIconified(WindowEvent paramWindowEvent) {}

  public void windowActivated(WindowEvent paramWindowEvent) {}

  public void windowClosed(WindowEvent paramWindowEvent) {}

  public void windowDeactivated(WindowEvent paramWindowEvent) {}
}


/* Location:              C:\Users\anton\Documents\ILO2\rc175p10.jar!\com\hp\ilo2\remcons\OkCancelDialog.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */