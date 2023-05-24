package org.example.view;

import org.example.exception.InvalidComboBoxParamException;
import org.example.model.Sender;
import org.example.service.SenderService;

import javax.swing.*;
import java.awt.*;

public class SenderSettingsWindow extends JFrame {
    private final Sender sender;
    private final JSlider msgFreq = new JSlider();
    private final JButton terminateButton = new JButton("TERMINATE!");
    private final JTextField devNumField = new JTextField();
    private final JComboBox<String> senderState = new JComboBox<>();

    public SenderSettingsWindow(Sender sender) throws HeadlessException {
        super();
        this.sender = sender;

        msgFreq.setMajorTickSpacing(10);
        msgFreq.setMinorTickSpacing(5);
        msgFreq.setPaintTrack(true);
        msgFreq.setPaintTicks(true);
        msgFreq.setPaintLabels(true);
        msgFreq.setSnapToTicks(true);
        msgFreq.setMinimum(5);
        msgFreq.setMaximum(85);
        msgFreq.setBounds(50, 160, 200, 50);
        msgFreq.setValue(sender.getMessageDelay());
        msgFreq.addChangeListener(e -> changeSenderMessageDelay());
        msgFreq.setVisible(true);

        terminateButton.setBounds(70, 220, 160, 50);
        terminateButton.addActionListener(e -> terminateSender());
        terminateButton.setVisible(true);

        devNumField.setBounds(0, 0, 40, 40);
        devNumField.setEditable(false);
        devNumField.setText(sender.getText());
        devNumField.setVisible(true);

        senderState.setBounds(225, 0, 100, 40);
        String first = sender.isPaused() ? "WAITING" : "ACTIVE";
        String second = sender.isPaused() ? "ACTIVE" : "WAITING";
        senderState.addItem(first);
        senderState.addItem(second);
        senderState.addActionListener(e -> {
            Object source = ((JComboBox<?>) e.getSource()).getSelectedItem();
            if (source == null) throw new InvalidComboBoxParamException("Combo box source was null.");
            String selectedItem = (String) source;
            changeStateOnClick(selectedItem);
        });
        senderState.setEditable(false);
        senderState.setVisible(true);

        this.getContentPane().add(terminateButton);
        this.getContentPane().add(devNumField);
        this.getContentPane().add(msgFreq);
        this.getContentPane().add(senderState);
        this.setAlwaysOnTop(true);
        this.setResizable(false);
        this.getContentPane().setBackground(Color.lightGray);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(340, 340);
        this.setLayout(null);
        this.setVisible(true);
    }

    private void changeStateOnClick(String selectedItem) {
        if (selectedItem.equalsIgnoreCase("active")) sender.unPause();
        else if (selectedItem.equalsIgnoreCase("waiting")) sender.pause();
        else throw new InvalidComboBoxParamException("Param was {" + selectedItem + "}. Expected: {WAITING/ACTIVE}");
    }

    private void terminateSender() {
        SenderService.removeSender(sender);
    }

    private void changeSenderMessageDelay() {
        sender.setMessageDelay(msgFreq.getValue());
    }

}
