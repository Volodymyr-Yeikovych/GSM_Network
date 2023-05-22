package org.example.model;

import org.example.enc.SmsEncryptionManager;
import org.example.service.BscService;

import javax.swing.*;
import java.awt.*;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class BSC extends JTextField implements Runnable, PausableProcess {

    private static final int DEFAULT_SIDE_SIZE = 50;
    private volatile boolean terminated = false;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();
    private String name;
    private Queue<Message> processingPool = new ArrayBlockingQueue<>(999999);

    public BSC(String name) {
        super(name);
        this.name = name;
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

    public void handle(Message message) {
        boolean handled = processingPool.offer(message);
        if (!handled) {
            for (int i = 0; i < 3; i++) {
                if (handled) break;
                try {
                    Thread.sleep(1000);
                    handled = processingPool.offer(message);
                } catch (InterruptedException e) {
                    System.out.println(name + " Exception caught while sleeping in handling");
                }
            }
        }
        if (!handled) System.out.println("Error: {" + message.getMessage() + "} ->>> wasn't processed!!!");
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
                while (!processingPool.isEmpty()) {
                    if (paused || terminated) break;
                    Message toProcess = processingPool.poll();
                    if (toProcess == null) break;
                    SmsEncryptionManager.translateToSmsDeliverMessage(toProcess);
                    BscService.passMessageToBTS(toProcess);
                    System.out.println(name + " MessageProcessed{" + toProcess.getMessage() + "}");
                    Thread.sleep(3000);
                }
            } catch (InterruptedException e) {
                System.out.println(name + " Exception caught while sleeping");
                break;
            }
        }
    }

    public int getQueueSize() {
        return processingPool.size();
    }
}
