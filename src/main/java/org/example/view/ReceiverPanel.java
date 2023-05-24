package org.example.view;

import org.example.model.Receiver;
import org.example.service.ReceiverService;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.List;

public class ReceiverPanel extends JPanel {
    private final ReceiverService receiverService;
    private List<Receiver> receiverPool;
    private final JButton plusBut = new JButton("+");
    public ReceiverPanel(ReceiverService receiverService) {
        super();
        this.receiverService = receiverService;

        plusBut.setSize(new Dimension(40, 40));
        plusBut.setVisible(true);
        plusBut.addActionListener(e -> {
            Receiver plus1 = new Receiver("R" + (receiverPool.size() + 1));
            receiverService.addReceiver(plus1);
            removeAndRepaint();
        });

        this.add(plusBut);
        this.setPreferredSize(new Dimension(160, 160));
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        this.setFocusCycleRoot(true);
        this.setVisible(true);
    }

    public void removeAndRepaint() {
        removeAll();
        add(plusBut);
        addReceiverPoolToPanel();
        revalidate();
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        receiverPool.forEach(sender -> sender.paintComponents(g));
        plusBut.paintComponents(g);
    }

    public synchronized void setReceiverPool(List<Receiver> senderList) {
        receiverPool = senderList;
        addReceiverPoolToPanel();
    }

    private void addReceiverPoolToPanel() {
        if (!receiverPool.isEmpty()) receiverPool.forEach(this::add);
    }
}
