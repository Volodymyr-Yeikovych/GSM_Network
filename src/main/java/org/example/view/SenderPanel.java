package org.example.view;

import org.example.model.Sender;
import org.example.service.SenderService;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.List;

public class SenderPanel extends JPanel {
    private SenderService senderService;
    private List<Sender> senderPool;
    private SenderWindow window;
    private JButton plusBut = new JButton("+");

    public SenderPanel(SenderService senderService) {
        super();
        this.senderService = senderService;

        plusBut.setSize(new Dimension(50, 50));
        plusBut.addActionListener(e -> createMessageWindow());
        plusBut.setVisible(true);

        this.add(plusBut);
        this.setPreferredSize(new Dimension(160, 160));
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        this.setVisible(true);
    }

    private void createMessageWindow() {
        if (window != null) window.dispose();
        window = new SenderWindow(senderService);
    }

    public void removeAndRepaint() {
        removeAll();
        add(plusBut);
        addSenderPoolToPanel();
        revalidate();
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        senderPool.forEach(sender -> sender.paintComponents(g));
        plusBut.paintComponents(g);
    }

    public synchronized void setSenders(List<Sender> senderList) {
        senderPool = senderList;
        addSenderPoolToPanel();
    }

    private void addSenderPoolToPanel() {
        if (!senderPool.isEmpty()) senderPool.forEach(this::add);
    }

    public void saveMessages() {
        senderPool.forEach(Sender::saveMessage);
    }
}

