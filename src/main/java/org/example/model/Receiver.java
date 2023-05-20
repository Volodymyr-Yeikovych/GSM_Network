package org.example.model;

import org.example.service.ReceiverService;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Receiver extends JTextField implements Runnable, PausableProcess {
    private static final int DEFAULT_SIDE_SIZE = 40;
    private static final int DEFAULT_TIME_OUT_S = 10;
    private volatile boolean terminated = false;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();
    private String devNum;
    private boolean hasTimeOut;
    private String phone;
    private Queue<Message> receivedMessages = new ArrayBlockingQueue<>(999999);
    private Map<Long, Message> processedMessages = new ConcurrentHashMap<>();

    public Receiver(String devNum) {
        super(devNum);
        this.devNum = devNum;
        this.phone = ReceiverService.generateRandomPhoneNum();
        this.hasTimeOut = true;
        setUp();
    }

    public Receiver(String devNum, boolean hasTimeOut) {
        super(devNum);
        this.devNum = devNum;
        this.phone = ReceiverService.generateRandomPhoneNum();
        this.hasTimeOut = hasTimeOut;
        setUp();
    }

    private void setUp() {
        this.setEditable(false);
        this.setPreferredSize(new Dimension(DEFAULT_SIDE_SIZE, DEFAULT_SIDE_SIZE));
        this.setVisible(true);
    }

    public void handle(Message message) {
        boolean handled = receivedMessages.offer(message);
        if (!handled) {
            for (int i = 0; i < 3; i++) {
                if (handled) break;
                try {
                    Thread.sleep(1000);
                    handled = receivedMessages.offer(message);
                } catch (InterruptedException e) {
                    System.out.println(devNum + " Exception caught while sleeping in handling");
                }
            }
        }
        if (!handled) System.out.println("Error: {" + message.getMessage() + "} ->>> wasn't processed!!!");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawString(devNum, DEFAULT_SIDE_SIZE, DEFAULT_SIDE_SIZE);
    }

    @Override
    public String toString() {
        return "Message: {" + receivedMessages + "};";
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
            while (!receivedMessages.isEmpty()) {
                if (paused || terminated) break;
                Message toProcess = receivedMessages.poll();
                if (toProcess == null) break;
                processedMessages.put(System.currentTimeMillis(), toProcess);
                System.out.println(devNum + " MessageProcessed{" + toProcess.getMessage() + "}");
                checkTimeOutTime();
            }
        }
    }

    private void checkTimeOutTime() {
        if (!hasTimeOut) return;
        for (Map.Entry<Long, Message> entry : processedMessages.entrySet()) {
            if (System.currentTimeMillis() - entry.getKey() >= DEFAULT_TIME_OUT_S * 1000)
                processedMessages.remove(entry.getKey());
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

    public String getPhone() {
        return phone;
    }

    public int getMessagePoolSize() {
        return receivedMessages.size();
    }
}
