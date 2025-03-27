package com.dinidu.lk.pmt.controller;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public abstract class BaseController {
    protected void transitionToScene(AnchorPane currentPane, String fxmlPath) {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), currentPane);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent newRoot = loader.load();

                newRoot.setOpacity(0);

                Scene currentScene = currentPane.getScene();
                currentScene.setRoot(newRoot);

                Stage currentStage = (Stage) currentScene.getWindow();
                currentStage.centerOnScreen();
                currentStage.setMaximized(true);

                FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), newRoot);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        fadeOut.play();
    }
}

