package org.example.view;

import org.example.model.BSC;
import org.example.model.BTC;
import org.example.model.Receiver;
import org.example.model.Sender;

import java.util.List;
import java.util.Map;

public interface View{

    void displaySenders(List<Sender> senderList);
    void displayReceivers(List<Receiver> receiverList);
    void displayAddSenderButton();
    void displayDeleteSenderButton();
    void displayAddReceiverButton();
    void displayDeleteReceiverButton();
    void displayBaseStationController(Map<Integer, List<BSC>> bscLayerPool);
    void displaySenderBaseTransceiverStation(BTC bts);
    void displayReceiverBaseTransceiverStation(BTC bts);
    void displayAddBSCButton();
    void displayDeleteBSCButton();
    void display();
    void setUp();

    List<Receiver> updateReceivers();

    List<Sender> updateSenders();

    Map<Integer, List<BSC>> updateBSCes();
}
