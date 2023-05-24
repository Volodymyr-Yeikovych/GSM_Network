package org.example.controller;

import org.example.exception.InvalidBscException;
import org.example.exception.ReceiverOutOfReachException;
import org.example.model.*;
import org.example.view.View;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ProgramController {
    private final View view;
    private static Set<String> phoneNumbersPool = ConcurrentHashMap.newKeySet();
    private static List<Sender> senderPool = new CopyOnWriteArrayList<>();
    private static List<Receiver> receiverPool = new CopyOnWriteArrayList<>();
    private static final BTS senderBTS = new BTS("S", true);
    private static final BTS receiverBTS = new BTS("R", false);
    private static Map<Integer, List<BSC>> bscLayerPool = new ConcurrentHashMap<>();

    public ProgramController(View view) {
        this.view = view;
    }

    public synchronized static void createBSC() {
        List<Integer> layersSize = new ArrayList<>();
        for (int i = 1; i <= bscLayerPool.size(); i++) {
            layersSize.add(bscLayerPool.get(i).size());
        }
        int min = layersSize.stream()
                .min(Integer::compareTo)
                .orElseThrow(() -> new InvalidBscException("No BSC in pool to pass data."));
        int layer = layersSize.indexOf(min) + 1;
        addBSCToLayer(layer);
    }

    private void setUp() {
        List<BSC> firstLayer = new CopyOnWriteArrayList<>();
        firstLayer.add(new BSC("BSC1:1"));
        bscLayerPool.put(1, firstLayer);
        addReceiver(new Receiver("R1"));
        addSender(new Sender("S1", "m1"));
    }

    public void start() throws InterruptedException {
        this.setUp();
        view.displaySenders(senderPool);
        view.displaySenderBTS(senderBTS);
        view.displayAddBSCButton();
        view.displayBSC(bscLayerPool);
        view.displayDeleteBSCButton();
        view.displayReceiverBTS(receiverBTS);
        view.displayReceivers(receiverPool);
        threadsStart();
        while (true) {
            view.repaint();
            Thread.sleep(400);
        }
    }

    private void threadsStart() {
        senderPool.forEach(sender -> new Thread(sender).start());
        new Thread(senderBTS).start();
        for (int i = 1; i <= bscLayerPool.size(); i++) {
            for (BSC bsc : bscLayerPool.get(i)) new Thread(bsc).start();
        }
        new Thread(receiverBTS).start();
        receiverPool.forEach(receiver -> new Thread(receiver).start());
    }

    public synchronized static List<Sender> getSenderPool() {
        return senderPool;
    }
    public synchronized static List<Receiver> getReceiverPool() {
        return receiverPool;
    }

    public synchronized static Set<String> getPhoneNumbersPool() {
        return phoneNumbersPool;
    }

    public static void addSender(Sender sender) {
        senderPool.add(sender);
    }

    public void addReceiver(Receiver receiver) {
        receiverPool.add(receiver);
    }

    public synchronized static void removeSender(Sender sender) {
        senderPool.remove(sender);
    }

    public synchronized static void removeReceiver(Receiver receiver) {
        receiverPool.remove(receiver);
    }

    private synchronized static void addBSCToLayer(int layer) {
        var bscPool = bscLayerPool.get(layer);
        BSC toAdd = new BSC("BSC" + layer + ":" + bscPool.size());
        new Thread(toAdd).start();
        bscPool.add(toAdd);
    }

    public synchronized static void removeBCSLayer(int layer) {
        bscLayerPool.remove(layer);
    }

    public synchronized static void addBSCLayer() {
        int layer = (bscLayerPool.size() + 1);
        List<BSC> nextLayer = new CopyOnWriteArrayList<>();
        BSC bsc = new BSC("BSC" + layer + ":1");
        new Thread(bsc).start();
        nextLayer.add(bsc);
        bscLayerPool.put(layer, nextLayer);
    }

    public synchronized static void removeLastBSCLayer() {
        removeBCSLayer(bscLayerPool.size() - 1);
    }

    public synchronized static List<BSC> getBSCLayer(int layer) {
        return bscLayerPool.get(layer);
    }

    public synchronized static BTS getSenderBTS() {
        return senderBTS;
    }

    public synchronized static BTS getReceiverBTS() {
        return receiverBTS;
    }

    public synchronized static void sendMessageToBSC(Byte[] message) {
        for (int i = 1; i <= bscLayerPool.size(); i++) {
            BSC bsc = getLeastBusyBSCFromLayer(i);
            bsc.handle(message);
        }
    }
    public synchronized static void sendMessageToReceiver(Byte[] message) {
        receiverPool.stream()
                .filter(receiver -> receiver.getPhone().equals(Receiver.peekMessagePhone(message)))
                .min(Comparator.comparing(Receiver::getPhone))
                .orElseThrow(() -> new ReceiverOutOfReachException("Receiver was not found"))
                .handle(message);
    }

    private static BSC getLeastBusyBSCFromLayer(int layer) {
        BSC min = bscLayerPool.get(layer).get(0);
        for (BSC bsc : bscLayerPool.get(layer)) {
            if (bsc.getQueueSize() < min.getQueueSize()) min = bsc;
        }
        return min;
    }

    public synchronized static int getBSCSize() {
        return bscLayerPool.size();
    }

    public synchronized static String generatePhoneNum() {
        StringBuilder builder = new StringBuilder();
        boolean freeNum = false;
        while (!freeNum) {
            for (int i = 0; i < 9; i++) {
                int rand = new Random().nextInt(0, 10);
                builder.append(rand);
            }
            if (phoneNumbersPool.contains(builder.toString())) {
                continue;
            } else {
                freeNum = true;
                phoneNumbersPool.add(builder.toString());
            }
        }
        return builder.toString();
    }

}
