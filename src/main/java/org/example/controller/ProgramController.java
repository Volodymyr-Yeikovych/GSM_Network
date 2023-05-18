package org.example.controller;

import org.example.model.BSC;
import org.example.model.BTC;
import org.example.model.Receiver;
import org.example.model.Sender;
import org.example.view.View;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ProgramController {

    private final View view;
    private List<Sender> senderPool = new CopyOnWriteArrayList<>();
    private List<Receiver> receiverPool = new CopyOnWriteArrayList<>();
    private final BTC senderStation = new BTC("S");
    private final BTC receiverStation = new BTC("R");
    private Map<Integer, List<BSC>> bscLayerPool = new ConcurrentHashMap<>();

    public ProgramController(View view) {
        this.view = view;
    }

    private void setUp() {
        List<BSC> firstLayer = new CopyOnWriteArrayList<>();
        firstLayer.add(new BSC("BSC1:1"));
        bscLayerPool.put(1, firstLayer);
        addSender(new Sender("S1", 40, 40));
        addSender(new Sender("S2", 40, 40));
        addSender(new Sender("S3", 40, 40));
        addSender(new Sender("S4", 40, 40));
        addSender(new Sender("S5", 40, 40));
        addSender(new Sender("S6", 40, 40));
        addReceiver(new Receiver("R1", 40, 40));
        addReceiver(new Receiver("R2", 40, 40));
    }

    public void start() throws InterruptedException {
        this.setUp();
        while (true) {
            view.displaySenders(senderPool);
            view.displaySenderBTS(senderStation);
            view.displayAddBSCButton();
            view.displayBSC(bscLayerPool);
            view.displayDeleteBSCButton();
            view.displayReceiverBTS(receiverStation);
            view.displayReceivers(receiverPool);
            view.setUp();
            receiverPool = view.updateReceivers();
            senderPool = view.updateSenders();
            bscLayerPool = view.updateBSCes();
            Thread.sleep(100);
        }
    }

    public void addSender(Sender sender) {
        senderPool.add(sender);
    }

    public void addReceiver(Receiver receiver) {
        receiverPool.add(receiver);
    }

    public void removeSender(Sender sender) {
        senderPool.remove(sender);
    }

    public void removeReceiver(Receiver receiver) {
        receiverPool.remove(receiver);
    }

    private void addBSCToLayer(int layer) {
        var bscPool = bscLayerPool.get(layer);
        bscPool.add(new BSC("BSC" + layer + ":" + bscPool.size()));
    }

    public void addBSCLayer() {
        List<BSC> bscPool = new CopyOnWriteArrayList<>();
        int lastKey = bscLayerPool.keySet().stream().max(Integer::compare).orElseThrow() + 1;
        bscPool.add(new BSC("BSC" + lastKey + ":1"));
        bscLayerPool.put(lastKey, bscPool);
    }

    public void removeBCSLayer(int layer) {
        bscLayerPool.remove(layer);
    }
}
