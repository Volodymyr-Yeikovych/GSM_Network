package org.example.service;

import org.example.controller.ProgramController;
import org.example.model.BTS;

public class BtsService {

    public static void passMessageToBsc(Byte[] message) {
        ProgramController.sendMessageToBSC(message);
    }

    public static void removeBts(BTS bts) {
        ProgramController.removeBts(bts);
    }
}
