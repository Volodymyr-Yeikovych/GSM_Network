package org.example.view;

import org.example.model.Receiver;
import org.example.service.ReceiverService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.lang.reflect.InvocationTargetException;

public class ReceiverSettingsWindow extends JFrame {

    private Receiver receiver;
    private JButton terminateButton = new JButton("TERMINATE!");
    private JLabel msgCount = new JLabel();
    private JTextField devNumField = new JTextField();
    private JCheckBox timeOutCheck = new JCheckBox();

    public ReceiverSettingsWindow(Receiver receiver)  {
        super();
        this.receiver = receiver;

        terminateButton.setBounds(70, 220, 160, 50);
        terminateButton.addActionListener(e -> terminateSender());
        terminateButton.setVisible(true);

        msgCount.setBounds(70, 80, 160, 40);
        msgCount.setText("Messages count: {" + receiver.getReceivedMsgPoolSize() + "}");
        createThreadTimer();
        msgCount.setVisible(true);

        devNumField.setBounds(0, 0, 40, 40);
        devNumField.setEditable(false);
        devNumField.setText(receiver.getText());
        devNumField.setVisible(true);

        timeOutCheck.setBounds(225, 0, 100, 40);
        timeOutCheck.addActionListener(this::setReceiverTimeOut);
        timeOutCheck.setText("TimeOut");
        timeOutCheck.setSelected(receiver.hasTimeOut());
        timeOutCheck.setVisible(true);

        this.getContentPane().add(terminateButton);
        this.getContentPane().add(msgCount);
        this.getContentPane().add(devNumField);
        this.getContentPane().add(timeOutCheck);
        this.setAlwaysOnTop(true);
        this.setResizable(false);
        this.getContentPane().setBackground(Color.lightGray);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(340, 340);
        this.setLayout(null);
        this.setVisible(true);

    }

    private void createThreadTimer() {
        new Thread(() -> {
            while (!receiver.isTerminated()) {
                try {
                    msgCount.setText("Messages count: {" + receiver.getReceivedMsgPoolSize() + "}");
                    System.out.println("Setting counter");
                    removeAndRepaint();
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    System.out.println("Aborting msg count update timer.");
                    break;
                }
            }
            System.out.println("Aborting msg count update timer.");
        });
    }

    private void removeAndRepaint() {
        this.getContentPane().removeAll();
        this.getContentPane().add(terminateButton);
        this.getContentPane().add(msgCount);
        this.getContentPane().add(devNumField);
        this.getContentPane().add(timeOutCheck);
        this.getContentPane().revalidate();
        this.getContentPane().repaint();
    }

    private void setReceiverTimeOut(ActionEvent e) {
        receiver.setTimeOut(!receiver.hasTimeOut());
    }


    private void terminateSender() {
        ReceiverService.removeReceiver(receiver);
    }

}
