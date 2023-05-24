package org.example.model;

import org.example.service.BscService;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class BSC extends JPanel implements Runnable, PausableProcess, Process {
    private volatile boolean terminated = false;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();
    private final JTextField textField;
    private final JButton terminateButton;
    private final String name;
    private final Random randomTime = new Random();
    private final Queue<Byte[]> processingPool = new ArrayBlockingQueue<>(999999);

    public BSC(String name) {
        super();
        this.name = name;
        this.textField = new JTextField(name);
        this.terminateButton = new JButton("TERMINATE");

        terminateButton.addActionListener(e -> terminateBsc());
        terminateButton.setVisible(true);

        textField.setEditable(false);
        textField.setVisible(true);

        this.add(textField);
        this.add(terminateButton);
        this.setMaximumSize(new Dimension(160, 60));
        this.setMinimumSize(new Dimension(160, 60));
        this.setVisible(true);
    }

    private void terminateBsc() {
        BscService.removeBsc(this);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        textField.paintComponents(g);
        terminateButton.paintComponents(g);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public void handle(Byte[] message) {
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
        if (!handled) System.out.println("Error: {" + Arrays.toString(message) + "} ->>> wasn't processed!!!");
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
                    Byte[] toProcess = processingPool.poll();
                    if (toProcess == null) break;
                    Thread.sleep(randomTime.nextInt(5000, 15000));
                    System.out.println(name + " MessageProcessed{" + Arrays.toString(toProcess) + "}");
                    BscService.passMessageToBTS(toProcess);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BSC bsc = (BSC) o;
        return terminated == bsc.terminated && paused == bsc.paused && Objects.equals(pauseLock, bsc.pauseLock)
                && Objects.equals(textField, bsc.textField) && Objects.equals(terminateButton, bsc.terminateButton)
                && Objects.equals(name, bsc.name) && Objects.equals(randomTime, bsc.randomTime) && Objects.equals(processingPool, bsc.processingPool);
    }

    @Override
    public int hashCode() {
        return Objects.hash(terminated, paused, pauseLock, textField, terminateButton, name, randomTime, processingPool);
    }
}
