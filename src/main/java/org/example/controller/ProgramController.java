package org.example.controller;

import org.example.model.*;
import org.example.view.View;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ProgramController {
    private final View view;
    private static List<Sender> senderPool = new CopyOnWriteArrayList<>();
    private static List<Receiver> receiverPool = new CopyOnWriteArrayList<>();
    private static final BTS senderBTS = new BTS("S", true);
    private static final BTS receiverBTS = new BTS("R", false);
    private static Map<Integer, List<BSC>> bscLayerPool = new ConcurrentHashMap<>();

    public ProgramController(View view) {
        this.view = view;
    }

    private void setUp() {
        List<BSC> firstLayer = new CopyOnWriteArrayList<>();
        firstLayer.add(new BSC("BSC1:1"));
        bscLayerPool.put(1, firstLayer);
        addSender(new Sender("S1", "m1"));
        addSender(new Sender("S2", "m2"));
        addSender(new Sender("S3", "m3"));
        addSender(new Sender("S4", "m4"));
        addSender(new Sender("S5", "m5"));
        addSender(new Sender("S6", "m6"));
        addReceiver(new Receiver("R1"));
        addReceiver(new Receiver("R2"));
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
        while (true) {
            view.repaint();
            Thread.sleep(400);
        }
    }

    public synchronized static List<Sender> getSenderPool() {
        return senderPool;
    }

    public synchronized static void addSender(Sender sender) {
        senderPool.add(sender);
    }

    public synchronized void addReceiver(Receiver receiver) {
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
        bscPool.add(new BSC("BSC" + layer + ":" + bscPool.size()));
    }

    public synchronized static void removeBCSLayer(int layer) {
        bscLayerPool.remove(layer);
    }

    public synchronized static void addBSCLayer() {
        int layer = (bscLayerPool.size() + 1);
        List<BSC> nextLayer = new CopyOnWriteArrayList<>();
        nextLayer.add(new BSC("BSC" + layer + ":1"));
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

    public synchronized static void sendMessageToBSC(Message message) {
        // processing message, passing through all bsc layers ->>,<<-
    }
}
