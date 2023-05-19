package org.example.service;

import org.example.controller.ProgramController;
import org.example.model.BSC;
import org.example.model.Message;

import java.util.List;

public class BscService {

    public BscService() {

    }

    public static void passMessageToAvailableBsc(Message message) {
        ProgramController.sendMessageToBSC(message);
    }

    public void addBSCLayer() {
        ProgramController.addBSCLayer();
    }

    public List<BSC> getBSCLayer(int layer) {
        return ProgramController.getBSCLayer(layer);
    }
}
