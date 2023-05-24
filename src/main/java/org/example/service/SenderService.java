package org.example.service;

import org.example.controller.ProgramController;
import org.example.model.Sender;

import java.util.List;

public class SenderService {

    public static void passMessageToBTS(Byte[] message) {
        ProgramController.getAvailableSenderBTS().handle(message);
    }

    public static String generateRandomPhoneNum() {
        return ProgramController.generatePhoneNum();
    }

    public List<Sender> getSenderPool() {
        return ProgramController.getSenderPool();
    }

    public void addSender(Sender sender) {
        ProgramController.addSender(sender);
    }

    public static void removeSender(Sender sender) {
        ProgramController.removeSender(sender);
    }

}
