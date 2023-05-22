package org.example.model;

import org.example.enc.SmsEncryptionManager;
import org.example.service.ReceiverService;

public class Message {
    private volatile String message;
    private String senderPhone;
    private String receiverPhone;
    private int timesPassed;

    public Message(String message, String senderPhone) {
        this.senderPhone = senderPhone;
        this.receiverPhone = ReceiverService.getRandomReceiverPhone();
        this.message = message;
        this.timesPassed = 0;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public int getTimesPassed() {
        return timesPassed;
    }

    @Override
    public String toString() {
        return "Msg{" + getMessage() + "}";
    }

    public synchronized void setMessage(String message) {
        this.message = message;
    }

    public synchronized void incrementTimesPassed() {
        timesPassed++;
    }

}
