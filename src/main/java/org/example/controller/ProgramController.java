package org.example.controller;

import org.example.exception.InvalidBscException;
import org.example.exception.NoAvailableBtsException;
import org.example.exception.ReceiverOutOfReachException;
import org.example.model.*;
import org.example.view.View;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ProgramController {
    private final View view;
    private static final Set<String> phoneNumbersPool = ConcurrentHashMap.newKeySet();
    private static final List<Sender> senderPool = new CopyOnWriteArrayList<>();
    private static final List<Receiver> receiverPool = new CopyOnWriteArrayList<>();
    private static final List<BTS> senderBTSPool = new CopyOnWriteArrayList<>();
    private static final List<BTS> receiverBTSPool = new CopyOnWriteArrayList<>();
    private static final Map<Integer, List<BSC>> bscLayerPool = new ConcurrentHashMap<>();

    public ProgramController(View view) {
        this.view = view;
    }

    private void setUp() {
        List<BSC> firstLayer = new CopyOnWriteArrayList<>();
        bscLayerPool.put(1, firstLayer);
        addBSCToLayer(1);
        senderBTSPool.add(new BTS("S", true));
        receiverBTSPool.add(new BTS("R", false));
        addReceiver(new Receiver("R1"));
        addSender(new Sender("S1", "m1"));
    }

    public void start() throws InterruptedException {
        this.setUp();
        view.displaySenders(senderPool);
        view.displaySenderBTS(senderBTSPool);
        view.displayAddBSCButton();
        view.displayBSC(bscLayerPool);
        view.displayDeleteBSCButton();
        view.displayReceiverBTS(receiverBTSPool);
        view.displayReceivers(receiverPool);
        threadsStart();
        while (true) {
            view.repaint();
            Thread.sleep(400);
        }
    }

    private void threadsStart() {
        senderBTSPool.forEach(bts -> new Thread(bts).start());
        receiverBTSPool.forEach(bts -> new Thread(bts).start());
    }

    public synchronized static List<Sender> getSenderPool() {
        return senderPool;
    }

    public synchronized static List<Receiver> getReceiverPool() {
        return receiverPool;
    }

    public synchronized static void addSender(Sender sender) {
        new Thread(sender).start();
        senderPool.add(sender);
    }

    public synchronized static void addReceiver(Receiver receiver) {
        new Thread(receiver).start();
        receiverPool.add(receiver);
    }

    public synchronized static void removeSender(Sender sender) {
        sender.terminate();
        senderPool.remove(sender);
    }

    public synchronized static void removeReceiver(Receiver receiver) {
        receiver.terminate();
        receiverPool.remove(receiver);
    }

    private synchronized static void addBSCToLayer(int layer) {
        var bscPool = bscLayerPool.get(layer);
        BSC toAdd = new BSC("BSC" + layer + ":" + (bscPool.size() + 1));
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

    public synchronized static List<BSC> getBSCLayer(int layer) {
        return bscLayerPool.get(layer);
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

    private synchronized static BSC getLeastBusyBSCFromLayer(int layer) {
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

    public static BTS getAvailableReceiverBTS() {
        return receiverBTSPool.stream()
                .min(Comparator.comparingInt(BTS::getMessagePoolSize))
                .orElseThrow(() -> new NoAvailableBtsException("Available receiver bts was not found."));
    }

    public static BTS getAvailableSenderBTS() {
        return senderBTSPool.stream()
                .min(Comparator.comparingInt(BTS::getMessagePoolSize))
                .orElseThrow(() -> new NoAvailableBtsException("Available sender bts was not found."));
    }

    public synchronized static void createSenderBTS() {
        if (bscSizeMoreThanSenderBts()) {
            BTS newSenderBts = new BTS("S", true);
            new Thread(newSenderBts).start();
            senderBTSPool.add(newSenderBts);
        }
    }

    private static int getBscMaxPoolSize() {
        int max = 0;
        for (int i = 1; i <= bscLayerPool.size(); i++) {
            if (bscLayerPool.get(i).size() > max) max = bscLayerPool.get(i).size();
        }
        return max;
    }

    private static boolean bscSizeMoreThanSenderBts() {
        int bscSize = getBscMaxPoolSize();
        return bscSize > senderBTSPool.size();
    }

    public synchronized static void createReceiverBTS() {
        if (bscSizeMoreThanReceiverBts()) {
            BTS newReceiverBts = new BTS("R", false);
            new Thread(newReceiverBts).start();
            receiverBTSPool.add(newReceiverBts);
        }
    }

    private static boolean bscSizeMoreThanReceiverBts() {
        int bscSize = getBscMaxPoolSize();
        return bscSize > receiverBTSPool.size();
    }

    public synchronized static void removeBsc(BSC bsc) {
        for (int i = 1; i <= bscLayerPool.size(); i++) {
            List<BSC> layer = bscLayerPool.get(i);
            if (layer.size() < 2) continue;
            for (BSC other : layer) {
                if (other.equals(bsc)) {
                    bsc.terminate();
                    layer.remove(bsc);
                    return;
                }
            }
        }
    }

    public synchronized static void removeBts(BTS bts) {
        boolean isSenderBts = bts.isSenderBTS();
        int btsPoolSize = isSenderBts ? senderBTSPool.size() : receiverBTSPool.size();
        if (btsPoolSize > 1) {
            bts.terminate();
            if (isSenderBts) senderBTSPool.remove(bts);
            else receiverBTSPool.remove(bts);
        }
    }
}
