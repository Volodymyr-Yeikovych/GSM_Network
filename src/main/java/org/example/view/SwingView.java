package org.example.view;

import org.example.model.BSC;
import org.example.model.BTC;
import org.example.model.Receiver;
import org.example.model.Sender;

import javax.swing.*;
import java.awt.*;
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
    private JButton addBSC = new JButton("+");

    {
        addBSC.setVisible(true);
        addBSC.addActionListener(e -> {
            int layer = bscPanelLayers.size() + 1;
            BSCPanel bscPanel = new BSCPanel(layer);
            List<BSC> bscPool = new CopyOnWriteArrayList<>();
            bscPool.add(new BSC("BSC" + layer + ":" + 1));
            bscPanel.setBscPool(bscPool);
            bscPanelLayers.add(bscPanel);
            getContentPane().revalidate();
            getContentPane().repaint();
        });
    }

    private JButton deleteBSC = new JButton("-");

    {
        deleteBSC.setVisible(true);
        deleteBSC.addActionListener(e -> {
            if (!bscPanelLayers.isEmpty()) {
                BSCPanel toRemove = bscPanelLayers.get(bscPanelLayers.size() - 1);
                bscPanelLayers.remove(toRemove);
                this.getContentPane().remove(toRemove);
                getContentPane().revalidate();
                getContentPane().repaint();
            }
        });
    }

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
        this.getContentPane().add(receiverPanel);
    }

    @Override
    public void displayBSC(Map<Integer, List<BSC>> bscLayerPool) {
        for (Map.Entry<Integer, List<BSC>> entry : bscLayerPool.entrySet()) {
            BSCPanel bscPanel = new BSCPanel(entry.getKey());
            bscPanel.setBscPool(entry.getValue());
            if (!bscPanelLayers.contains(bscPanel)) {
                System.out.println("added");
                bscPanelLayers.add(bscPanel);
            }
        }
        bscPanelLayers.forEach(panel -> {
            panel.addBSCLayerToPanel();
            getContentPane().add(panel, panel.getLayer() + 2);
        });
    }

    @Override
    public void displaySenderBTS(BTC bts) {
        if (senderBTSPanel == null) {
            this.senderBTSPanel = new SenderBTSPanel(bts);
            this.getContentPane().add(senderBTSPanel, 1);
        }
    }

    @Override
    public void displayReceiverBTS(BTC bts) {
        if (receiverBTSPanel == null) {
            this.receiverBTSPanel = new ReceiverBTSPanel(bts);
            this.getContentPane().add(receiverBTSPanel, 4 + bscPanelLayers.size());
        }
    }

    @Override
    public void displayAddBSCButton() {
        this.getContentPane().add(addBSC, 2);
    }

    @Override
    public void displayDeleteBSCButton() {
        this.getContentPane().add(deleteBSC, 3 + bscPanelLayers.size());
    }

    @Override
    public void display() {

    }

    @Override
    public void setUp() {
        this.setSize(1080, 1080);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new GridLayout(1, 0));
        this.setFocusCycleRoot(true);
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
