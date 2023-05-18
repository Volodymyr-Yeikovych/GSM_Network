package org.example.model;

import javax.swing.*;
import java.awt.*;

public class Sender extends JTextField {
    private Message message;
    private int x;
    private int y;

    public Sender(String text, int x, int y) {
        super(text);
        this.x = x;
        this.y = y;
        this.setEditable(false);
        this.setPreferredSize(new Dimension(x, y));
        this.setVisible(true);
        message = new Message(text);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawString(message.getMessage(), x, y);
    }

    @Override
    public String toString() {
        return "Message: {" + message.getMessage() + "} x{" + x + "} y{" + y + "};";
    }
}
