package org.example.view;

import org.example.model.BTS;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.List;

public class SenderBTSPanel extends JPanel {
    private List<BTS> senderBTS;

    public SenderBTSPanel(List<BTS> senderBTS) {
        super();
        this.senderBTS = senderBTS;
        this.setBackground(Color.lightGray);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        this.setFocusCycleRoot(true);
        this.setVisible(true);
        addAllBts();
    }

    private void addAllBts() {
        if (!senderBTS.isEmpty()) senderBTS.forEach(this::add);
    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    public void removeAndRepaint() {
        removeAll();
        addAllBts();
        revalidate();
        repaint();
    }

}
