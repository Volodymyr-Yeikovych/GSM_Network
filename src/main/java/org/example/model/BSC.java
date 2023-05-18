package org.example.model;

import javax.swing.*;
import java.awt.*;

public class BSC extends JTextField {

    public static final int DEFAULT_SIDE_SIZE = 50;

    public BSC(String text) {
        super(text);
        this.setEditable(false);
        this.setPreferredSize(new Dimension(DEFAULT_SIDE_SIZE, DEFAULT_SIDE_SIZE));
        this.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawString(this.getText(), DEFAULT_SIDE_SIZE, DEFAULT_SIDE_SIZE);
    }

    @Override
    public String toString() {
        return this.getText();
    }
}
