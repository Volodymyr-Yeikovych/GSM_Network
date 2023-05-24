package org.example.service;

import org.example.controller.ProgramController;
import org.example.exception.ReceiverOutOfReachException;
import org.example.model.Receiver;

import java.util.List;
import java.util.Random;

public class ReceiverService {

    public static void passMessageToReceiver(Byte[] message) {
        ProgramController.sendMessageToReceiver(message);
    }

    public static void removeReceiver(Receiver receiver) {
        ProgramController.removeReceiver(receiver);
    }

    public void addReceiver(Receiver receiver) {
        ProgramController.addReceiver(receiver);
    }

    public static String getRandomReceiverPhone() {
        List<Receiver> receiverPool = ProgramController.getReceiverPool();
        int size = receiverPool.size();
        if (size == 0) throw new ReceiverOutOfReachException("No available receivers");
        int rand = new Random().nextInt(0, size);
        return receiverPool.get(rand).getPhone();
    }

    public static String generateRandomPhoneNum() {
        return ProgramController.generatePhoneNum();
    }
}
