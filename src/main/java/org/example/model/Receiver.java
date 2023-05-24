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

public class Receiver extends JButton implements Runnable, PausableProcess, Process {
    private static final int DEFAULT_SIDE_SIZE = 60;
    private static final int DEFAULT_TIME_OUT_S = 10;
    private volatile boolean terminated = false;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();
    private ReceiverSettingsWindow window;
    private final String devNum;
    private boolean hasTimeOut;
    private final String phone;
    private final Queue<Byte[]> receivedMessages = new ArrayBlockingQueue<>(999999);
    private final Map<Long, Byte[]> processedMessages = new ConcurrentHashMap<>();

    public Receiver(String devNum) {
        super(devNum);
        this.devNum = devNum;
        this.phone = ReceiverService.generateRandomPhoneNum();
        this.hasTimeOut = true;
        setUp();
    }

    public static String peekMessagePhone(Byte[] message) {
        byte[] primitiveMessage = SmsUtils.toPrimitive(message);
        String result = getReceiverPhone(primitiveMessage);
        if (result.endsWith("F")) result = result.substring(0, result.length() - 1);
        return result;
    }

    private static String getReceiverPhone(byte[] message) {
        byte[] receiverPhoneBytes = getReceiverPhoneOctets(message);
        return SmsUtils.getStringFromBytes(receiverPhoneBytes);
    }

    private static byte[] getReceiverPhoneOctets(byte[] message) {
        byte[] semiOctets = getReceiverPhoneSemiOctets(message);
        byte[] octets = new byte[semiOctets.length * 2];
        for (int i = 0; i < semiOctets.length; i++) {
            byte first = (byte) (semiOctets[i] & 0x0f);
            byte second = (byte) ((byte) (semiOctets[i] >> 4) & 0x0f);
            octets[2 * i] = first;
            octets[1 + (2 * i)] = second;
        }
        return octets;
    }

    private static byte[] getReceiverPhoneSemiOctets(byte[] message) {
        int senderPhOctet = getSmsCLength(message);
        int recPhoneIndex = senderPhOctet + 5;
        int receiverOctetLen = getReceiverOctetLength(message);
        byte[] semiOctets = new byte[receiverOctetLen];

        System.arraycopy(message, recPhoneIndex, semiOctets, 0, semiOctets.length);

        return semiOctets;
    }


    private static int getReceiverOctetLength(byte[] message) {
        int senderPhOctet = getSmsCLength(message);
        int recPhLen = message[senderPhOctet + 3];
        if (recPhLen % 2 == 1) recPhLen++;
        recPhLen /= 2;
        return recPhLen;
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

    @Override
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
        byte[] msg = SmsUtils.toPrimitive(message);
        return getDecryptedMessage(msg);
    }

    private String getDecryptedMessage(byte[] message) {
        int msgLen = getMessageLength(message);
        byte[] msg = Arrays.copyOfRange(message, message.length - msgLen, message.length);
        StringBuilder decrypted = new StringBuilder();
        for (byte b : msg) {
            decrypted.append((char) b);
        }
        return decrypted.toString();
    }

    private int getMessageLength(byte[] message) {
        int smsClen = getSmsCLength(message);
        int recPhoneLen = getReceiverOctetLength(message);
        int offset = smsClen + 4 + recPhoneLen + 4;
        return message[offset];
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
