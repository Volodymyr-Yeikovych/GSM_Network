package org.example;

import org.example.controller.ProgramController;
import org.example.service.BscService;
import org.example.service.SenderService;
import org.example.view.SwingView;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ProgramController controller = new ProgramController(new SwingView(new SenderService(), new BscService()));
        controller.start();
    }
}