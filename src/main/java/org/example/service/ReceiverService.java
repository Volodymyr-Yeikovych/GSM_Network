package org.example.service;

import org.example.controller.ProgramController;
import org.example.exception.ReceiverOutOfReachException;
import org.example.model.Receiver;

import java.util.List;
import java.util.Random;

public class ReceiverService {

    public ReceiverService() {

    }

    public static void passMessageToReceiver(Byte[] message) {
        ProgramController.sendMessageToReceiver(message);
    }

    public static void removeReceiver(Receiver receiver) {
        receiver.terminate();
        ProgramController.removeReceiver(receiver);
    }

    public static String getRandomReceiverPhone() {
        List<Receiver> receiverPool = ProgramController.getReceiverPool();
        int size = receiverPool.size();
        int rand = new Random().nextInt(0, size);
        if (size == 0) throw new ReceiverOutOfReachException("No available receivers");
        return receiverPool.get(rand).getPhone();
    }

    public static String generateRandomPhoneNum() {
        return ProgramController.generatePhoneNum();
    }
}
