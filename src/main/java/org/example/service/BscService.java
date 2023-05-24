package org.example.service;

import org.example.controller.ProgramController;
import org.example.model.BSC;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BscService {

    private static Map<Byte[], Integer> msgCounter = new ConcurrentHashMap<>();
    public BscService() {

    }

    public static void passMessageToBTS(Byte[] message) {
        Integer counter = msgCounter.get(message);
        if (counter == null) counter = 0;
        msgCounter.put(message, ++counter);
        if (msgCounter.get(message) == ProgramController.getBSCSize()) {
            ProgramController.getReceiverBTS().handle(message);
            msgCounter.remove(message);
        }
    }

    public static void createBSC() {
        ProgramController.createBSC();
    }

    public static void removeBscLayer(int layer) {
        ProgramController.removeBCSLayer(layer);
    }

    public void addBSCLayer() {
        ProgramController.addBSCLayer();
    }

    public List<BSC> getBSCLayer(int layer) {
        return ProgramController.getBSCLayer(layer);
    }
}
