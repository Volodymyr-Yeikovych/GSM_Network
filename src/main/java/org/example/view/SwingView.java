package org.example.view;

import org.example.model.BSC;
import org.example.model.BTS;
import org.example.model.Receiver;
import org.example.model.Sender;
import org.example.service.BscService;
import org.example.service.SenderService;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class SwingView extends JFrame implements View {

    private SenderService senderService;
    private BscService bscService;
    private SenderPanel senderPanel;
    private ReceiverPanel receiverPanel;
    private SenderBTSPanel senderBTSPanel;
    private ReceiverBTSPanel receiverBTSPanel;
    private List<BSCPanel> bscPanelLayers = new CopyOnWriteArrayList<>();
    private JButton addBSC = new JButton("+");
    private JButton deleteBSC = new JButton("-");
    public SwingView(SenderService senderService, BscService bscService) {
        super();
        this.senderService = senderService;
        this.bscService = bscService;
        this.senderPanel = new SenderPanel(senderService);
        this.receiverPanel  = new ReceiverPanel();

        addBSC.setVisible(true);
        addBSC.addActionListener(e -> {
            bscService.addBSCLayer();
            addNewBSCPanel();
            update();
        });

        deleteBSC.setVisible(true);
        deleteBSC.addActionListener(e -> removeBSCLayer());

        this.setSize(1080, 1080);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new GridLayout(1, 0));
        this.setVisible(true);
    }

    private void removeBSCLayer() {
        if (!bscPanelLayers.isEmpty()) {
            BSCPanel toRemove = bscPanelLayers.get(bscPanelLayers.size() - 1);
            toRemove.getBSCPool().forEach(BSC::terminate);
            bscPanelLayers.remove(toRemove);
            this.getContentPane().remove(toRemove);
            loadBSCToPanel();
            update();
        }
    }

    private void addNewBSCPanel() {
        int layer = bscPanelLayers.size() + 1;
        BSCPanel bscPanel = new BSCPanel(layer);
        List<BSC> bscPool = bscService.getBSCLayer(layer);
        bscPanel.setBscPool(bscPool);
        bscPanelLayers.add(bscPanel);
        loadBSCToPanel();
    }

    @Override
    public void displaySenders(List<Sender> senderList) {
        senderPanel.setSenders(senderList);
        this.getContentPane().add(senderPanel, 0);
    }

    @Override
    public void displayReceivers(List<Receiver> receiverList) {
        receiverPanel.setReceiverPool(receiverList);
        this.getContentPane().add(receiverPanel);
    }

    @Override
    public void displayBSC(Map<Integer, List<BSC>> bscLayerPool) {
        setBSCPanelList(bscLayerPool);
        loadBSCToPanel();
    }

    private void setBSCPanelList(Map<Integer, List<BSC>> bscLayerPool) {
        for (Map.Entry<Integer, List<BSC>> entry : bscLayerPool.entrySet()) {
            BSCPanel bscPanel = new BSCPanel(entry.getKey());
            bscPanel.setBscPool(entry.getValue());
            if (!bscPanelLayers.contains(bscPanel)) {
                System.out.println("added");
                bscPanelLayers.add(bscPanel);
            }
        }
    }

    private void loadBSCToPanel() {
        bscPanelLayers.forEach(panel -> {
            panel.addBSCLayerToPanel();
            getContentPane().add(panel, panel.getLayer() + 2);
        });
    }

    @Override
    public void displaySenderBTS(BTS bts) {
        if (senderBTSPanel == null) {
            this.senderBTSPanel = new SenderBTSPanel(bts);
            this.getContentPane().add(senderBTSPanel, 1);
        }
    }

    @Override
    public void displayReceiverBTS(BTS bts) {
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

    private void update() {
        this.getContentPane().revalidate();
        this.getContentPane().repaint();
    }

    @Override
    public void repaint() {
        update();
        senderPanel.removeAndRepaint();
        receiverPanel.removeAndRepaint();
    }
}
