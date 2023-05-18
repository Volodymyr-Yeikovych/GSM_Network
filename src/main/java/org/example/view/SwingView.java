package org.example.view;

import org.example.model.BSC;
import org.example.model.BTC;
import org.example.model.Receiver;
import org.example.model.Sender;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SwingView extends JFrame implements View {

    private ReceiverPanel receiverPanel = new ReceiverPanel();
    private SenderPanel senderPanel = new SenderPanel();
    private SenderBTSPanel senderBTSPanel;
    private ReceiverBTSPanel receiverBTSPanel;

    private List<BSCPanel> bscPanelLayers = new CopyOnWriteArrayList<>();

    public SwingView() {
        super();
    }

    @Override
    public void displaySenders(List<Sender> senderList) {
        senderPanel.addSenders(senderList);
        this.getContentPane().add(senderPanel, 0);
    }

    @Override
    public void displayReceivers(List<Receiver> receiverList) {
        receiverPanel.setReceiverPool(receiverList);
        this.getContentPane().add(receiverPanel, 4);
    }

    @Override
    public void displayAddSenderButton() {
        senderPanel.addPlusButton(new JButton("+"));
    }

    @Override
    public void displayDeleteSenderButton() {
        senderPanel.addMinusButton(new JButton("-"));
    }

    @Override
    public void displayAddReceiverButton() {
        receiverPanel.addPlusButton(new JButton("+"));
    }

    @Override
    public void displayDeleteReceiverButton() {
        receiverPanel.addMinusButton(new JButton("-"));
    }

    @Override
    public void displayBaseStationController(Map<Integer, List<BSC>> bscLayerPool) {
        int index = 2;
        for (Map.Entry<Integer, List<BSC>> entry : bscLayerPool.entrySet()) {
            BSCPanel bscPanel = new BSCPanel(entry.getKey());
            bscPanel.setBscPool(entry.getValue());
            if (!bscPanelLayers.contains(bscPanel)) {
                System.out.println("added");
                bscPanelLayers.add(bscPanel);
                this.getContentPane().add(bscPanel, index);
                index++;
            }
        }

    }

    @Override
    public void displaySenderBaseTransceiverStation(BTC bts) {
        if (senderBTSPanel == null) {
            this.senderBTSPanel = new SenderBTSPanel(bts);
            this.getContentPane().add(senderBTSPanel);
        }
    }

    @Override
    public void displayReceiverBaseTransceiverStation(BTC bts) {
        if (receiverBTSPanel == null) {
            this.receiverBTSPanel = new ReceiverBTSPanel(bts);
            this.getContentPane().add(receiverBTSPanel);
        }
    }

    @Override
    public void displayAddBSCButton() {
        bscPanelLayers.forEach(bcs -> bcs.addPlusButton(new JButton("+")));
    }

    @Override
    public void displayDeleteBSCButton() {
        bscPanelLayers.forEach(bcs -> bcs.addMinusButton(new JButton("-")));
    }

    @Override
    public void display() {

    }

    @Override
    public void setUp() {
        this.setSize(640, 640);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new GridLayout(1, 5));
        this.setVisible(true);
    }

    @Override
    public List<Receiver> updateReceivers() {
        return receiverPanel.getNewData();
    }

    @Override
    public List<Sender> updateSenders() {
        return senderPanel.getNewData();
    }

    @Override
    public Map<Integer, List<BSC>> updateBSCes() {
        Map<Integer, List<BSC>> bscLayers = new ConcurrentHashMap<>();
        for (BSCPanel panel : bscPanelLayers) {
            bscLayers.put(panel.getLayer(), panel.getNewData());
        }
        return bscLayers;
    }
}
