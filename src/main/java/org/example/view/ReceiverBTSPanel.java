package org.example.view;

import org.example.model.BTS;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

public class ReceiverBTSPanel extends JPanel {
    private BTS receiverBTS;

    public ReceiverBTSPanel(BTS receiverBTS) {
        super();
        this.receiverBTS = receiverBTS;
        this.setPreferredSize(new Dimension(70, 70));
        this.setBackground(Color.lightGray);
        this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        this.setFocusCycleRoot(true);
        this.setVisible(true);
        this.add(receiverBTS);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        receiverBTS.paintComponents(g);
    }

    public BTS getReceiverBTS() {
        return receiverBTS;
    }
}
