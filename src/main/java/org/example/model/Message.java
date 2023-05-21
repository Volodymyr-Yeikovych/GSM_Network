package org.example.model;

import org.example.enc.SmsEncryptionManager;
import org.example.service.ReceiverService;

public class Message {
    private String encryptedMessage;
    private String senderPhone;
    private String receiverPhone;

    public Message(String message, String senderPhone) {
        this.encryptedMessage = SmsEncryptionManager.encrypt(message);
        this.senderPhone = senderPhone;
        this.receiverPhone = ReceiverService.getRandomReceiverPhone();
    }

    public String getEncryptedMessage() {
        return SmsEncryptionManager.decrypt(encryptedMessage);
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    @Override
    public String toString() {
        return "Msg{" + getEncryptedMessage() + "}";
    }
}
