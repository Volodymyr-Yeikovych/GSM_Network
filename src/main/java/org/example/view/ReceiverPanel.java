package org.example.view;

import org.example.model.Receiver;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.List;

public class ReceiverPanel extends JPanel {
    private List<Receiver> receiverPool;
    private JButton plusBut;
    private JButton minusBut;
    public ReceiverPanel() {
        super();
        this.setPreferredSize(new Dimension(160, 160));
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        this.setFocusCycleRoot(true);
        this.setVisible(true);
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

    public void addPlusButton(JButton plus) {
        plusBut = plus;
        plusBut.setSize(new Dimension(40, 40));
        plusBut.setVisible(true);
        plusBut.addActionListener(e -> {
                Receiver plus1 = new Receiver("S" + receiverPool.size(), 40, 40);
                receiverPool.add(plus1);
                removeAll();
                add(plusBut);
                add(minusBut);
                revalidate();
                repaint();
        });
        this.add(plusBut);
    }

    public void addMinusButton(JButton minus) {
        minusBut = minus;
        minusBut.setSize(new Dimension(40, 40));
        minusBut.setVisible(true);
        minusBut.addActionListener(e -> {
            if (receiverPool.isEmpty()) return;
            receiverPool.remove(receiverPool.size() - 1);
            removeAll();
            add(plusBut);
            add(minusBut);
            revalidate();
            repaint();
        });
        this.add(minusBut);
    }

    public synchronized List<Receiver> getNewData() {
        return receiverPool;
    }
}
