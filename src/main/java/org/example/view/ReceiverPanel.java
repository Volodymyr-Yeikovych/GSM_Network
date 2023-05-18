package org.example.view;

import org.example.model.Receiver;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.List;

public class ReceiverPanel extends JPanel {
    private List<Receiver> receiverPool;
    private JButton plusBut = new JButton("+");
    {
        plusBut.setSize(new Dimension(40, 40));
        plusBut.setVisible(true);
        plusBut.addActionListener(e -> {
            Receiver plus1 = new Receiver("R" + (receiverPool.size() + 1), 40, 40);
            receiverPool.add(plus1);
            removeAndRepaint();
        });
        this.add(plusBut);
    }
    private JButton minusBut = new JButton("-");
    {
        minusBut.setSize(new Dimension(40, 40));
        minusBut.setVisible(true);
        minusBut.addActionListener(e -> {
            if (receiverPool.isEmpty()) return;
            receiverPool.remove(receiverPool.size() - 1);
            removeAndRepaint();
        });
        this.add(minusBut);
    }
    public ReceiverPanel() {
        super();
        this.setPreferredSize(new Dimension(160, 160));
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        this.setFocusCycleRoot(true);
        this.setVisible(true);
    }

    private void removeAndRepaint() {
        removeAll();
        add(plusBut);
        add(minusBut);
        revalidate();
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        receiverPool.forEach(sender -> sender.paintComponents(g));
        plusBut.paintComponents(g);
        minusBut.paintComponents(g);
    }

    public synchronized void setReceiverPool(List<Receiver> senderList) {
        receiverPool = senderList;
        addReceiverPoolToPanel();
    }

    private void addReceiverPoolToPanel() {
        if (!receiverPool.isEmpty()) receiverPool.forEach(this::add);
    }

    public synchronized List<Receiver> getNewData() {
        return receiverPool;
    }
}
