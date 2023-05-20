package org.example.service;

import org.example.controller.ProgramController;
import org.example.model.Message;

public class BtsService {
    public BtsService() {
    }

    public static void passMessageToBsc(Message message) {
        ProgramController.sendMessageToBSC(message);
    }
}
