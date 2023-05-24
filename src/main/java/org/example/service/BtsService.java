package org.example.service;

import org.example.controller.ProgramController;

public class BtsService {
    public BtsService() {
    }

    public static void passMessageToBsc(Byte[] message) {
        ProgramController.sendMessageToBSC(message);
    }
}
