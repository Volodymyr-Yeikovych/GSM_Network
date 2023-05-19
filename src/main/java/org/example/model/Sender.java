package org.example.model;

import org.example.service.SenderService;

import javax.swing.*;
import java.awt.*;

public class Sender extends JTextField implements Runnable, PausableProcess {

    private volatile boolean terminated = false;
    private volatile boolean paused = true;
    private final Object pauseLock = new Object();
    private static final int DEFAULT_SIDE_SIZE = 40;
    private String devNum;
    private int messageDelay;
    private Message message;

    public Sender(String devNum, String message) {
        super(devNum);
        this.devNum = devNum;
        this.message = new Message(message);
        this.messageDelay = 10;
        setUp();
    }

    public Sender(String devNum, String message, int messageDelay) {
        super(devNum);
        this.devNum = devNum;
        this.messageDelay = messageDelay;
        this.message = new Message(message);
        setUp();
    }

    private void setUp() {
        this.setEditable(false);
        this.setPreferredSize(new Dimension(DEFAULT_SIDE_SIZE, DEFAULT_SIDE_SIZE));
        this.setVisible(true);
        run();
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

    @Override
    public void run() {
        while (!terminated) {
            synchronized (pauseLock) {
                if (terminated) break;
                if (paused) {
                    try {
                        pauseLock.wait();
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                if (terminated) break;
            }
            try {
                if (paused || terminated) break;
                SenderService.passMessageToBTS(message);
                System.out.println(this.devNum + " SENT MESSAGE!!");
                Thread.sleep(messageDelay * 1000L);
            } catch (InterruptedException e) {
                System.out.println(this.devNum + " Exception caught while sleeping.");
            }
        }
        System.out.println(this.devNum + " Terminated successfully");
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void unPause() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }
    }

    @Override
    public void terminate() {
        terminated = true;
        if (paused) unPause();
    }
}
