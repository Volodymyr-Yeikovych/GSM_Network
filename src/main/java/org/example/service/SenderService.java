package org.example.service;

import org.example.controller.ProgramController;
import org.example.model.Message;
import org.example.model.Sender;

import java.util.List;

public class SenderService {

    public SenderService() {

    }

    public static void passMessageToBTS(Message message) {
        ProgramController.getSenderBTS().handle(message);
    }

    public List<Sender> getSenderPool() {
        return ProgramController.getSenderPool();
    }

    public void addSender(Sender sender) {
        ProgramController.addSender(sender);
    }
}
