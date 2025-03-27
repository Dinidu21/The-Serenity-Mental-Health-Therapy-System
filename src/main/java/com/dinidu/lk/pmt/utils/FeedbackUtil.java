package com.dinidu.lk.pmt.utils;

import javafx.animation.FadeTransition;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class FeedbackUtil {

    public static void showFeedback(Label feedbackLabel, String message, Color color) {
        feedbackLabel.setText(message);
        feedbackLabel.setTextFill(color);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(3.5), feedbackLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(3.5), feedbackLabel);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        fadeIn.play();

        fadeIn.setOnFinished(event -> {
            fadeOut.play();
            fadeOut.setOnFinished(e -> feedbackLabel.setText(""));
        });
    }
}

