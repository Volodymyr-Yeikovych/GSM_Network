package org.example.view;

import org.example.model.BTS;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.List;

public class ReceiverBTSPanel extends JPanel {
    private final List<BTS> receiverBTS;

    public ReceiverBTSPanel(List<BTS> receiverBTS) {
        super();
        this.receiverBTS = receiverBTS;
        this.setBackground(Color.lightGray);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        this.setFocusCycleRoot(true);
        this.setVisible(true);
        addAllBts();
    }

    private void addAllBts() {
        if (!receiverBTS.isEmpty()) receiverBTS.forEach(this::add);
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
