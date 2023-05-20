package org.example.model;

import org.example.service.ReceiverService;

public class Message {
    private String message;
    private String senderPhone;
    private String receiverPhone;

    public Message(String message, String senderPhone) {
        this.message = message;
        this.senderPhone = senderPhone;
        this.receiverPhone = ReceiverService.getRandomReceiverPhone();
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

    @Override
    public String toString() {
        return "Msg{" + getMessage() + "}";
    }
}
