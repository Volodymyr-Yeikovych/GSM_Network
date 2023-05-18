package org.example.model;

import javax.swing.*;
import java.awt.*;

public class Receiver extends JTextField {
    private static final int DEFAULT_SIDE_SIZE = 40;
    private Message message;
    public Receiver(String text) {
        super(text);
        this.setEditable(false);
        this.setPreferredSize(new Dimension(DEFAULT_SIDE_SIZE, DEFAULT_SIDE_SIZE));
        this.setVisible(true);
        message = new Message(text);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawString(message.getMessage(), DEFAULT_SIDE_SIZE, DEFAULT_SIDE_SIZE);
    }

    @Override
    public String toString() {
        return "Message: {" + message.getMessage() + "};";
    }
}
