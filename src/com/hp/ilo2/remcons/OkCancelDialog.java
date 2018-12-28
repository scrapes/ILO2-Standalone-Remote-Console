package com.hp.ilo2.remcons;

import java.awt.*;
import java.awt.event.*;


public class OkCancelDialog extends java.awt.Dialog implements ActionListener, WindowListener {
    private TextArea txt;
    private Button ok;
    private Button cancel;
    private boolean rc;

    public OkCancelDialog(Frame owner, String message) {
        super(owner, "Notice!", true);
        ui_init(message);
    }

    public OkCancelDialog(String message, boolean isModal) {
        super(new Frame(), "Notice!", isModal);
        ui_init(message);
    }

    private void ui_init(String message) {
        this.txt = new TextArea(message, 5, 40, 1);
        this.txt.setEditable(false);

        this.ok = new Button("    Ok    ");
        this.ok.addActionListener(this);

        this.cancel = new Button("Cancel");
        this.cancel.addActionListener(this);

        setBackground(Color.lightGray);
        setSize(360, 160);

        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();

        setLayout(gridBagLayout);

        constraints.fill = 2;
        constraints.anchor = 17;
        constraints.weightx = 100.0D;
        constraints.weighty = 100.0D;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        add(this.txt, constraints);

        Panel buttonsPanel = new Panel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.add(this.ok);
        buttonsPanel.add(this.cancel);

        constraints.fill = 0;
        constraints.anchor = 13;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;

        add(buttonsPanel, constraints);
        addWindowListener(this);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.ok) {
            dispose();
            this.rc = true;
        } else if (event.getSource() == this.cancel) {
            dispose();
            this.rc = false;
        }
    }

    public boolean result() {
        return this.rc;
    }

    public void append(String message) {
        this.txt.append(message);
        this.txt.repaint();
    }

    public void windowClosing(WindowEvent event) {
        dispose();
        this.rc = false;
    }

    public void windowOpened(WindowEvent event) {}

    public void windowDeiconified(WindowEvent event) {}

    public void windowIconified(WindowEvent event) {}

    public void windowActivated(WindowEvent event) {}

    public void windowClosed(WindowEvent event) {}

    public void windowDeactivated(WindowEvent event) {}
}