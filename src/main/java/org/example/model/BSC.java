package org.example.model;

import javax.swing.*;
import java.awt.*;

public class BSC extends JTextField {

    public static final int DEFAULT_SIDE_SIZE = 50;
    private int x;
    private int y;

    public BSC(String text, int x, int y) {
        super(text);
        this.x = x;
        this.y = y;
        this.setEditable(false);
        this.setPreferredSize(new Dimension(DEFAULT_SIDE_SIZE, DEFAULT_SIDE_SIZE));
        this.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawString(this.getText(), DEFAULT_SIDE_SIZE, DEFAULT_SIDE_SIZE);
    }
}
