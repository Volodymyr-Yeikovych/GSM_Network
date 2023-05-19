package org.example.model;

import org.example.service.BscService;

import javax.swing.*;
import java.awt.*;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class BTS extends JTextField implements Runnable, PausableProcess {

    private volatile boolean terminated = false;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();
    private final Queue<Message> messages = new ArrayBlockingQueue<>(999999);
    private static final String DEFAULT_NAME = "BTS";
    private static final int DEFAULT_SIDE_SIZE = 60;
    private static final long DEFAULT_SLEEPING_TIME = 1000L;

    public BTS(String text) {
        super(DEFAULT_NAME + ": " + text);
        this.setEditable(false);
        this.setPreferredSize(new Dimension(DEFAULT_SIDE_SIZE, DEFAULT_SIDE_SIZE));
        this.setVisible(true);
        run();
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

    public void handle(Message message) {
        boolean handled = messages.offer(message);
        if (!handled) {
            for (int i = 0; i < 3; i++) {
                if (handled) break;
                try {
                    Thread.sleep(1000);
                    handled = messages.offer(message);
                } catch (InterruptedException e) {
                    System.out.println(DEFAULT_NAME + " Exception caught while sleeping in handling");
                }
            }
        }
        if (!handled) System.out.println("Error: {" + message.getMessage() + "} ->>> wasn't processed!!!");
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
                while (!messages.isEmpty()) {
                    if (paused || terminated) break;
                    Message toProcess = messages.poll();
                    if (toProcess == null) break;
                    BscService.passMessageToAvailableBsc(toProcess);
                    System.out.println(DEFAULT_NAME + " MessageProcessed{" + toProcess.getMessage() + "}");
                    Thread.sleep(DEFAULT_SLEEPING_TIME);
                }
            } catch (InterruptedException e) {
                System.out.println(DEFAULT_NAME + " Exception caught while sleeping");
            }
        }
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
