package org.example.listeners;

import org.example.controller.ProgramController;
import org.example.model.Sender;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreateSenderOnClickListener implements ActionListener {
    private JTextArea area;
    private JSlider slider;
    public CreateSenderOnClickListener(JTextArea area, JSlider slider) {
        this.area = area;
        this.slider = slider;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int devNum = ProgramController.getSenderPool().size() + 1;
        Sender sender = new Sender("S" + devNum, area.getText(), slider.getValue());
        System.out.println(sender);
        ProgramController.addSender(sender);
    }
}
