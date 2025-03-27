package com.dinidu.lk.pmt.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
public class LoadingScreenController {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private ProgressBar progressBar;
    public void initialize() {
        new Thread(() -> {
            try {
                for (int i = 0; i <= 100; i++) {
                    final int progress = i;
                    Thread.sleep(50);
                    javafx.application.Platform.runLater(() -> {
                        progressBar.setProgress(progress / 100.0);
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}

