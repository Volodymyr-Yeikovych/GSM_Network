package org.example.view;

import org.example.model.BSC;
import org.example.model.BTS;
import org.example.model.Receiver;
import org.example.model.Sender;

import java.util.List;
import java.util.Map;

public interface View{

    void displaySenders(List<Sender> senderList);
    void displayReceivers(List<Receiver> receiverList);
    void displayBSC(Map<Integer, List<BSC>> bscLayerPool);
    void displaySenderBTS(BTS bts);
    void displayReceiverBTS(BTS bts);
    void displayAddBSCButton();
    void displayDeleteBSCButton();
    void repaint();
}
