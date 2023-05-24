package org.example.model;

import org.example.service.BtsService;
import org.example.service.ReceiverService;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class BTS extends JPanel implements Runnable, PausableProcess, Process {
    private volatile boolean terminated = false;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();
    private final JTextField textField;
    private final JButton terminateButton;
    private final String name;
    private final boolean isSenderBTS;
    private final Queue<Byte[]> messages = new ArrayBlockingQueue<>(999999);

    public BTS(String text, boolean isSenderBTS) {
        super();
        this.name = "BTS :" + text;
        this.isSenderBTS = isSenderBTS;

        this.textField = new JTextField(name);
        this.terminateButton = new JButton("TERMINATE");

        terminateButton.addActionListener(e -> terminateBts());
        terminateButton.setVisible(true);

        textField.setEditable(false);
        textField.setVisible(true);

        this.add(textField);
        this.add(terminateButton);
        this.setMaximumSize(new Dimension(160, 60));
        this.setMinimumSize(new Dimension(160, 40));
        this.setVisible(true);
    }

    private void terminateBts() {
        BtsService.removeBts(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public void handle(Byte[] message) {
        boolean handled = messages.offer(message);
        if (!handled) {
            for (int i = 0; i < 3; i++) {
                if (handled) break;
                try {
                    Thread.sleep(1000);
                    handled = messages.offer(message);
                } catch (InterruptedException e) {
                    System.out.println(name + " Exception caught while sleeping in handling");
                }
            }
        }
        if (!handled) System.out.println("Error: {" + Arrays.toString(message) + "} ->>> wasn't processed!!!");
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
                    Thread.sleep(3000);
                    System.out.println(name + " MessageProcessed{" + Arrays.toString(toProcess) + "}");
                    if (isSenderBTS) {
                        BtsService.passMessageToBsc(toProcess);
                    } else {
                        ReceiverService.passMessageToReceiver(toProcess);
                    }
                }
            } catch (InterruptedException e) {
                System.out.println(name + " Exception caught while sleeping");
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

    public int getMessagePoolSize() {
        return messages.size();
    }

    public boolean isSenderBTS() {
        return isSenderBTS;
    }
}
