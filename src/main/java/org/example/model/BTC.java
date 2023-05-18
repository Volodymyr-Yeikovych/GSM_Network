package org.example.model;

import javax.swing.*;
import java.awt.*;

public class BTC extends JTextField {
    private static final String DEFAULT_NAME = "BTS";
    private static final int DEFAULT_SIDE_SIZE = 60;

    public BTC(String text) {
        super(DEFAULT_NAME + ": " + text);
        this.setEditable(false);
        this.setPreferredSize(new Dimension(DEFAULT_SIDE_SIZE, DEFAULT_SIDE_SIZE));
        this.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawString(toString(), DEFAULT_SIDE_SIZE, DEFAULT_SIDE_SIZE);
    }

    @Override
    public String toString() {
        return DEFAULT_NAME;
    }
}
