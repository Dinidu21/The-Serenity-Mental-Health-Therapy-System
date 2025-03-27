package com.dinidu.lk.pmt.controller.dashboard;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class NotifyViewController {
    @FXML
    private AnchorPane notificationPane;
    @FXML
    private Button viewButton;
    @FXML
    private Button dismissButton;

    private static boolean isAnimating = false;

    @FXML
    public void initialize() {
        setupButtonHoverEffects(viewButton);
        setupButtonHoverEffects(dismissButton);

        notificationPane.parentProperty().addListener((obs, oldParent, newParent) -> {
            if (newParent != null) {
                positionNotification();
                playEntryAnimation();
            }
        });
    }

    private void positionNotification() {
        AnchorPane.setTopAnchor(notificationPane, 20.0);
        AnchorPane.setRightAnchor(notificationPane, 20.0);
    }

    private void setupButtonHoverEffects(Button button) {
        button.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), button);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();
        });

        button.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), button);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });
    }

    private void playEntryAnimation() {
        isAnimating = true;
        notificationPane.setTranslateY(-20);
        notificationPane.setOpacity(0);

        TranslateTransition translate = new TranslateTransition(Duration.millis(500), notificationPane);
        translate.setFromY(-20);
        translate.setToY(0);

        FadeTransition fade = new FadeTransition(Duration.millis(400), notificationPane);
        fade.setFromValue(0);
        fade.setToValue(1);

        ParallelTransition parallel = new ParallelTransition(translate, fade);
        parallel.setOnFinished(e -> isAnimating = false);
        parallel.play();
    }

    @FXML
    private void handleDismiss() {
        if (isAnimating) return;

        isAnimating = true;
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), notificationPane);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            if (notificationPane.getParent() != null) {
                ((AnchorPane) notificationPane.getParent()).getChildren().remove(notificationPane);
            }
            isAnimating = false;
        });
        fadeOut.play();
    }

    public static boolean isCurrentlyAnimating() {
        return isAnimating;
    }
}