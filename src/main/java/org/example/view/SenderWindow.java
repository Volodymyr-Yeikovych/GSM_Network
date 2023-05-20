package org.example.view;

import org.example.listeners.CreateSenderOnClickListener;
import org.example.service.SenderService;

import javax.swing.*;
import java.awt.*;

public class SenderWindow extends JFrame{
    private SenderService senderService;
    private JTextArea messageField = new JTextArea();
    private JButton okBut = new JButton("OK");
    private JSlider msgFreq = new JSlider();
    public SenderWindow(SenderService senderService) {
        this.senderService = senderService;

        msgFreq.setMajorTickSpacing(10);
        msgFreq.setMinorTickSpacing(5);
        msgFreq.setPaintTrack(true);
        msgFreq.setPaintTicks(true);
        msgFreq.setPaintLabels(true);
        msgFreq.setSnapToTicks(true);
        msgFreq.setMinimum(5);
        msgFreq.setMaximum(85);
        msgFreq.setBounds(15, 30, 200, 50);
        msgFreq.setValue(30);
        msgFreq.setVisible(true);

        messageField.setBounds(15, 110, 200, 20);
        messageField.setEditable(true);
        messageField.setVisible(true);

        okBut.setBounds(100, 140, 60, 60);
        okBut.addActionListener(new CreateSenderOnClickListener(messageField, msgFreq, senderService));
        okBut.setVisible(true);

        this.getContentPane().add(messageField);
        this.getContentPane().add(okBut);
        this.getContentPane().add(msgFreq);
        this.setAlwaysOnTop(true);
        this.setResizable(false);
        this.getContentPane().setBackground(Color.lightGray);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(240, 240);
        this.setLayout(null);
        this.setVisible(true);
    }
}
