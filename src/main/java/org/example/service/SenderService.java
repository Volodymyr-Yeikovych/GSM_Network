package org.example.service;

import org.example.controller.ProgramController;
import org.example.model.Sender;

import java.util.List;

public class SenderService {

    public SenderService() {

    }

    public static void passMessageToBTS(Byte[] message) {
        ProgramController.getSenderBTS().handle(message);
    }

    public static String generateRandomPhoneNum() {
        return ProgramController.generatePhoneNum();
    }

    public List<Sender> getSenderPool() {
        return ProgramController.getSenderPool();
    }

    public void addSender(Sender sender) {
        new Thread(sender).start();
        ProgramController.addSender(sender);
    }

    public static void removeSender(Sender sender) {
        sender.terminate();
        ProgramController.removeSender(sender);
    }

}
