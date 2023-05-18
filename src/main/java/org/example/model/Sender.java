package org.example.model;

import javax.swing.*;
import java.awt.*;

public class Sender extends JTextField {

    private static final int DEFAULT_SIDE_SIZE = 40;
    private String devNum;
    private int messageDelay;
    private Message message;

    public Sender(String devNum, String message) {
        super(devNum);
        setUp();
        this.devNum = devNum;
        this.message = new Message(message);
        this.messageDelay = 10;
    }

    public Sender(String devNum, String message, int messageDelay) {
        super(devNum);
        setUp();
        this.devNum = devNum;
        this.messageDelay = messageDelay;
        this.message = new Message(message);
    }

    private void setUp() {
        this.setEditable(false);
        this.setPreferredSize(new Dimension(DEFAULT_SIDE_SIZE, DEFAULT_SIDE_SIZE));
        this.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawString(message.getMessage(), DEFAULT_SIDE_SIZE, DEFAULT_SIDE_SIZE);
    }

    @Override
    public String toString() {
        return "Message: {" + message.getMessage() + "} DevNum: {" + devNum + "} Delay: {" + messageDelay + "};";
    }
}
