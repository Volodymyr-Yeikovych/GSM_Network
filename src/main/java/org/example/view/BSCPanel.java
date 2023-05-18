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

    public void addBSCLayerToPanel() {
        if (!bscPool.isEmpty()) bscPool.forEach(this::add);
    }

    public synchronized List<BSC> getNewData() {
        return bscPool;
    }

    public int getLayer() {
        return layer;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof BSCPanel other)) return false;
        return this.bscPool.equals(other.bscPool) && this.layer == other.layer;
    }

}
