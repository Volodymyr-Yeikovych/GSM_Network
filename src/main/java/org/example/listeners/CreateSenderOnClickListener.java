package org.example.listeners;

import org.example.model.Sender;
import org.example.service.SenderService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreateSenderOnClickListener implements ActionListener {
    private final SenderService senderService;
    private final JTextArea area;
    private final JSlider slider;
    public CreateSenderOnClickListener(JTextArea area, JSlider slider, SenderService senderService) {
        this.area = area;
        this.slider = slider;
        this.senderService = senderService;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int devNum = senderService.getSenderPool().size() + 1;
        Sender sender = new Sender("S" + devNum, area.getText(), slider.getValue());
        System.out.println(sender);
        senderService.addSender(sender);
    }
}
