package org.example.model;

import org.example.enc.SmsEncryptionManager;
import org.example.service.BtsService;
import org.example.service.ReceiverService;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class BTS extends JTextField implements Runnable, PausableProcess {

    private static final String DEFAULT_NAME = "BTS";
    private static final int DEFAULT_SIDE_SIZE = 60;
    private static final long DEFAULT_SLEEPING_TIME = 3000L;
    private volatile boolean terminated = false;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();
    private final boolean isSenderBTS;
    private final Queue<Byte[]> messages = new ArrayBlockingQueue<>(999999);

    public BTS(String text, boolean isSenderBTS) {
        super(DEFAULT_NAME + ":" + text);
        this.isSenderBTS = isSenderBTS;
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

    public void handle(Byte[] message) {
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
        if (!handled) System.out.println("Error: {" + Arrays.toString(message) + "} ->>> wasn't processed!!!");
        else {
            System.out.println(Arrays.toString(message) + " Handled by Sender BTS");
        }
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
                    Byte[] toProcess = messages.poll();
                    if (toProcess == null) break;
                    System.out.println(DEFAULT_NAME + " MessageProcessed{" + Arrays.toString(toProcess) + "}");
                    if (isSenderBTS) {
                        BtsService.passMessageToBsc(toProcess);
                    } else {
                        ReceiverService.passMessageToReceiver(toProcess);
                    }
                    Thread.sleep(DEFAULT_SLEEPING_TIME);
                }
            } catch (InterruptedException e) {
                System.out.println(DEFAULT_NAME + " Exception caught while sleeping");
                break;
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
