package org.example.model;

import org.example.service.SenderService;
import org.example.view.SenderSettingsWindow;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class Sender extends JButton implements Runnable, PausableProcess {
    private static final int DEFAULT_SIDE_SIZE = 50;
    private volatile boolean terminated = false;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();
    private SenderSettingsWindow window;
    private String devNum;
    private int messageDelay;
    private Message message;
    private String phone;

    public Sender(String devNum, String message) {
        super(devNum);
        this.devNum = devNum;
        this.phone = SenderService.generateRandomPhoneNum();
        this.message = new Message(message, phone);
        this.messageDelay = 10;
        setUp();
    }

    public Sender(String devNum, String message, int messageDelay) {
        super(devNum);
        this.devNum = devNum;
        this.phone = SenderService.generateRandomPhoneNum();
        this.messageDelay = messageDelay;
        this.message = new Message(message, phone);
        setUp();
    }

    private void setUp() {
        this.addActionListener(e -> openSenderSettingsWindow());
        this.setPreferredSize(new Dimension(DEFAULT_SIDE_SIZE, DEFAULT_SIDE_SIZE));
        this.setVisible(true);
    }

    private void openSenderSettingsWindow() {
        if (window != null) window.dispose();
        window = new SenderSettingsWindow(this);

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
                if (terminated) break;
                if (paused) continue;
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
        System.out.println("Pausing sender {" + devNum + "}");
        paused = true;
    }

    @Override
    public void unPause() {
        synchronized (pauseLock) {
            System.out.println("Unpausing sender {" + devNum + "}");
            paused = false;
            pauseLock.notifyAll();
        }
    }

    @Override
    public void terminate() {
        terminated = true;
        if (paused) unPause();
    }

    public int getMessageDelay() {
        return messageDelay;
    }

    public void setMessageDelay(int messageDelay) {
        this.messageDelay = messageDelay;
    }

    public boolean isPaused() {
        return paused;
    }
}
