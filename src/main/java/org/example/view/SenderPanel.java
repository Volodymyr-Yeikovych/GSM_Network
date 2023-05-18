package org.example.view;

import org.example.model.Sender;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.List;

public class SenderPanel extends JPanel {

    private List<Sender> senderPool;
    private JButton minusBut = new JButton("-");
    {
        minusBut.setSize(new Dimension(40, 40));
        minusBut.setVisible(true);
        minusBut.addActionListener(e -> {
            if (senderPool.isEmpty()) return;
            senderPool.remove(senderPool.size() - 1);
            removeAndRepaint();
        });
        this.add(minusBut);
    }

    private JButton plusBut = new JButton("+");
    {
        plusBut.setSize(new Dimension(40, 40));
        plusBut.setVisible(true);
        plusBut.addActionListener(e -> {
            Sender plus1 = new Sender("S" + (senderPool.size() + 1), 40, 40);
            senderPool.add(plus1);
            removeAndRepaint();
        });
        this.add(plusBut);
    }

    public SenderPanel() {
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
        senderPool.forEach(sender -> sender.paintComponents(g));
        plusBut.paintComponents(g);
        minusBut.paintComponents(g);
    }

    public synchronized void addSenders(List<Sender> senderList) {
        senderPool = senderList;
        addSenderPoolToPanel();
    }

    private void addSenderPoolToPanel() {
        if (!senderPool.isEmpty()) senderPool.forEach(this::add);
    }

    public synchronized List<Sender> getNewData() {
        return senderPool;
    }

}

