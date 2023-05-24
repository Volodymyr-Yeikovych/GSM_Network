package org.example.model;

import org.example.service.ReceiverService;
import org.example.view.ReceiverSettingsWindow;
import org.example.model.sms.SmsUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Receiver extends JButton implements Runnable, PausableProcess {
    private static final int DEFAULT_SIDE_SIZE = 60;
    private static final int DEFAULT_TIME_OUT_S = 10;
    private volatile boolean terminated = false;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();
    private ReceiverSettingsWindow window;
    private String devNum;
    private boolean hasTimeOut;
    private String phone;
    private int timesReceived;
    private Queue<Byte[]> receivedMessages = new ArrayBlockingQueue<>(999999);
    private Map<Long, Byte[]> processedMessages = new ConcurrentHashMap<>();

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

    public static String peekMessagePhone(Byte[] message) {
        byte[] primitiveMessage = SmsUtils.toPrimitive(message);
        String result = getReceiverPhone(primitiveMessage);
        if (result.endsWith("F")) result = result.substring(0, result.length() - 1);
        System.out.println(result);
        return result;
    }

    private static String getReceiverPhone(byte[] message) {
        byte[] receiverPhoneBytes = getReceiverPhoneOctets(message);
        return SmsUtils.getStringFromBytes(receiverPhoneBytes);
    }

    private static byte[] getReceiverPhoneOctets(byte[] message) {
        byte[] semiOctets = getReceiverPhoneSemiOctets(message);
        System.out.println(Arrays.toString(semiOctets));
        byte[] octets = new byte[semiOctets.length * 2];
        for (int i = 0; i < semiOctets.length; i++) {
            byte first = (byte) (semiOctets[i] & 0x0f);
            byte second = (byte) (semiOctets[i] >> 4);
            octets[2 * i] = first;
            octets[1 + (2 * i)] = second;
        }
        System.out.println(Arrays.toString(octets));
        return octets;
    }

    private static byte[] getReceiverPhoneSemiOctets(byte[] message) {
        int senderPhOctet = getSmsCLength(message);
        int recPhoneIndex = senderPhOctet + 5;
        int receiverOctetLen = getReceiverOctetLength(message);
        byte[] semiOctets = new byte[receiverOctetLen];

        for (int i = 0; i < semiOctets.length; i++) {
            semiOctets[i] = message[i + recPhoneIndex];
        }

        return semiOctets;
    }


    private static int getReceiverOctetLength(byte[] message) {
        int senderPhOctet = getSmsCLength(message);
        int recPhLenIndex = message[senderPhOctet + 3];
        return recPhLenIndex - 1;
    }

    private static int getSmsCLength(byte[] message) {
        return message[0];
    }

    private void setUp() {
        this.addActionListener(e -> openSettingsWindow());
        this.setPreferredSize(new Dimension(DEFAULT_SIDE_SIZE, DEFAULT_SIDE_SIZE));
        this.setVisible(true);
    }

    private void openSettingsWindow() {
        if (window != null) window.dispose();
        window = new ReceiverSettingsWindow(this);

    }

    public void handle(Byte[] message) {
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
        if (!handled) System.out.println("Error: {" + decrypt(message) + "} ->>> wasn't processed!!!");
    }

    private String decrypt(Byte[] message) {
        return "decrypted";
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
                Byte[] toProcess = receivedMessages.poll();
                if (toProcess == null) break;
                processedMessages.put(System.currentTimeMillis() / 1000, toProcess);
                System.out.println(devNum + " MessageProcessed{" + decrypt(toProcess) + "}");
                timesReceived++;
            }
            checkTimeOutTime();
        }
    }

    private synchronized void checkTimeOutTime() {
        if (!hasTimeOut) return;
        if (processedMessages.isEmpty()) return;
        for (Map.Entry<Long, Byte[]> entry : processedMessages.entrySet()) {
            if ((System.currentTimeMillis() / 1000) - entry.getKey() >= DEFAULT_TIME_OUT_S)
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

    public int getReceivedMsgPoolSize() {
        return processedMessages.size();
    }

    public void setTimeOut(boolean hasTimeOut) {
        this.hasTimeOut = hasTimeOut;
    }

    public boolean hasTimeOut() {
        return hasTimeOut;
    }

    public boolean isTerminated() {
        return terminated;
    }
}
