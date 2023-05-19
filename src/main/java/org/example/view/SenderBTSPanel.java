package org.example.view;

import org.example.model.BTS;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

public class SenderBTSPanel extends JPanel {
    private BTS senderBTS;

    public SenderBTSPanel(BTS senderBTS) {
        super();
        this.senderBTS = senderBTS;
        this.setPreferredSize(new Dimension(70, 70));
        this.setBackground(Color.lightGray);
        this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        this.setFocusCycleRoot(true);
        this.setVisible(true);
        this.add(senderBTS);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        senderBTS.paintComponents(g);
    }

    public BTS getSenderBTS() {
        return senderBTS;
    }
}
