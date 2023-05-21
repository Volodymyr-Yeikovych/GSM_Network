package org.example.service;

import org.example.controller.ProgramController;
import org.example.model.BSC;
import org.example.model.Message;
import org.example.view.BSCPanel;
import org.example.view.SwingView;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BscService {

    private static Map<String, Integer> msgCounter = new ConcurrentHashMap<>();
    public BscService() {

    }

    public static void passMessageToBTS(Message message) {
        String phone = message.getSenderPhone();
        Integer counter = msgCounter.get(phone);
        if (counter == null) counter = 0;
        msgCounter.put(phone, ++counter);
        if (msgCounter.get(phone) == ProgramController.getBSCSize()) {
            ProgramController.getReceiverBTS().handle(message);
            msgCounter.remove(phone);
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
