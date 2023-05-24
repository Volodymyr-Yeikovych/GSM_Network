package org.example.model;

import org.example.exception.InvalidGsmMessageFormatException;
import org.example.exception.ReceiverOutOfReachException;
import org.example.model.sms.SmsUtils;
import org.example.service.ReceiverService;
import org.example.service.SenderService;
import org.example.view.SenderSettingsWindow;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class Sender extends JButton implements Runnable, PausableProcess {
    private static final int DEFAULT_SIDE_SIZE = 60;
    private volatile boolean terminated = false;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();
    private SenderSettingsWindow window;
    private int timesSent;
    private String devNum;
    private String phone;
    private Byte[] msgTemp;
    private int messageDelay;
    private String message;

    public Sender(String devNum, String message) {
        super(devNum);
        this.devNum = devNum;
        this.phone = SenderService.generateRandomPhoneNum();
        this.message = message;
        this.messageDelay = 10;
        this.timesSent = 0;
        setUp();
    }

    public Sender(String devNum, String message, int messageDelay) {
        super(devNum);
        this.devNum = devNum;
        this.phone = SenderService.generateRandomPhoneNum();
        this.messageDelay = messageDelay;
        this.message = message;
        this.timesSent = 0;
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
        g.drawString(message, DEFAULT_SIDE_SIZE, DEFAULT_SIDE_SIZE);
    }

    @Override
    public String toString() {
        return "Message: {" + message + "} DevNum: {" + devNum + "} Delay: {" + messageDelay + "};";
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
                try {
                    msgTemp = getNewEncryptedMessage();
                    Thread.sleep(messageDelay * 1000L);
                    System.out.println(this.devNum + " SENT MESSAGE!!");
                    SenderService.passMessageToBTS(msgTemp);
                    if (timesSent >= 255) timesSent = 0;
                } catch (ReceiverOutOfReachException e) {
                    System.out.println("Unable to send sms - no receiver!!!");
                }
                timesSent++;
            } catch (InterruptedException e) {
                System.out.println(this.devNum + " Exception caught while sleeping.");
            }
        }
        System.out.println(this.devNum + " Terminated successfully");
    }

    private Byte[] getNewEncryptedMessage() {
        byte[] maxSmsC = new byte[255];
        String receiverPhone;
            receiverPhone = ReceiverService.getRandomReceiverPhone();
        byte[] senPhArr = toSemiOctet(this.phone);
        maxSmsC[1] = (byte) (0b1001_0000); // type of address

        int prevSize = 2;
        for (int i = prevSize; i < senPhArr.length + prevSize; i++) {
            maxSmsC[i] = senPhArr[i - prevSize];
        } // address value

        maxSmsC[0] = (byte) (senPhArr.length + 1); // address length

        byte[] smsC = Arrays.copyOfRange(maxSmsC, 0, 2 + senPhArr.length);

        byte[] maxTPDU = new byte[156];
        maxTPDU[0] = 0b0000_0001; // first-octet
        maxTPDU[1] = (byte) timesSent; // tp-mr
        maxTPDU[3] = (byte) 0b1001_0000; // tp da type of address

        byte[] recPhArr = toSemiOctet(receiverPhone);
        if (recPhArr.length > 10) throw new InvalidGsmMessageFormatException("Receiver phone cant be more than 20 digits");

        prevSize += 2;
        for (int i = prevSize; i < recPhArr.length + prevSize; i++) {
            maxTPDU[i] = recPhArr[i - prevSize];
        } // tp-da address value
        maxTPDU[2] = (byte) (receiverPhone.length()); // tp da address length

        maxTPDU[recPhArr.length + 4] = 0b00_0_00000; // tp pid
        maxTPDU[recPhArr.length + 5] = 0b0000_0100; // tp dcs
        maxTPDU[recPhArr.length + 6] = 0b0000_0000; // tp vp

        prevSize = recPhArr.length + 8;
        byte[] msgBytes = getByteMessage();
        for (int i = prevSize; i < msgBytes.length + prevSize; i++) {
            maxTPDU[i] = msgBytes[i - prevSize];
        } // tp ud

        maxTPDU[prevSize - 1] = (byte) msgBytes.length; // tp udl

        byte[] tpdu = Arrays.copyOfRange(maxTPDU, 0, prevSize + msgBytes.length);

        byte[] pdu = new byte[tpdu.length + smsC.length];
        System.arraycopy(smsC, 0, pdu, 0, smsC.length);
        System.arraycopy(tpdu, 0, pdu, smsC.length, tpdu.length);

        return SmsUtils.toObject(pdu);
    }

    private byte[] getByteMessage() {
        if (message.length() > 140) throw new InvalidGsmMessageFormatException("Message is exceeding 140 characters. Unable to send.");
        byte[] maxByteMsg = new byte[message.length()];
        for (int i = 0; i < message.length(); i++) {
            maxByteMsg[i] = (byte) message.charAt(i);
        }
        return Arrays.copyOfRange(maxByteMsg, 0, maxByteMsg.length);
    }

    private byte[] toSemiOctet(String phone) {
        String senPh = phone.length() % 2 == 1 ? phone + "F" : phone;
        if (senPh.length() > 510) throw new InvalidGsmMessageFormatException("Number length exceeds 510, impossible to parse");
        byte[] octet = new byte[senPh.length()];
        for (int i = 0; i < senPh.length(); i++) {
            byte save = SmsUtils.parseCharDigitToByte(senPh.charAt(i));
            if (i % 2 == 1) save = (byte) (save << 4);
            octet[i] = save;
        }
        byte[] semiOctet = new byte[senPh.length() / 2];
        for (int i = 0; i < semiOctet.length; i++) {
            byte first = octet[i * 2];
            byte second = octet[(2 * i) + 1];
            byte semi = (byte) (second ^ first);
            semiOctet[i] = semi;
        }
        return semiOctet;
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

    public void saveMessage() {
        SmsUtils.saveToFile(msgTemp);
    }
}
