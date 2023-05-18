package org.example.view;

import org.example.model.BSC;
import org.example.model.Sender;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class BSCPanel extends JPanel{
    private List<BSC> bscPool;
    private JButton plusBut;
    private JButton minusBut;
    private int layer;

    public BSCPanel(int layer) {
        super();
        this.layer = layer;
        this.setPreferredSize(new Dimension(160, 160));
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        this.setFocusCycleRoot(true);
        this.setVisible(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        bscPool.forEach(bsc -> bsc.paintComponents(g));
    }

    public synchronized void setBscPool(List<BSC> bscPool) {
        this.bscPool = bscPool;
        addBSCLayerToPanel();
    }

    private void addBSCLayerToPanel() {
        if (!bscPool.isEmpty()) bscPool.forEach(this::add);
    }

    public void addPlusButton(JButton plus) {
        plusBut = plus;
        plusBut.setSize(new Dimension(BSC.DEFAULT_SIDE_SIZE, BSC.DEFAULT_SIDE_SIZE));
        plusBut.setVisible(true);
        plusBut.addActionListener(e -> {
            String name = String.format("BSC%s:%s", layer, bscPool.size());
            BSC plus1 = new BSC(name, BSC.DEFAULT_SIDE_SIZE, BSC.DEFAULT_SIDE_SIZE);
            bscPool.add(plus1);
            update();
        });
        this.add(plusBut);
    }

    public void addMinusButton(JButton minus) {
        minusBut = minus;
        minusBut.setSize(new Dimension(BSC.DEFAULT_SIDE_SIZE, BSC.DEFAULT_SIDE_SIZE));
        minusBut.setVisible(true);
        minusBut.addActionListener(e -> {
            if (bscPool.isEmpty()) return;
            bscPool.remove(bscPool.size() - 1);
            update();
        });
        this.add(minusBut);
    }

    public synchronized List<BSC> getNewData() {
        return bscPool;
    }

    public int getLayer() {
        return layer;
    }

    private void update() {
        removeAll();
        add(plusBut);
        add(minusBut);
        revalidate();
        repaint();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof BSCPanel other)) return false;
        return this.bscPool.equals(other.bscPool) && this.layer == other.layer;
    }

}
