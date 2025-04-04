package com.dinidu.lk.pmt.controller.dashboard;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.List;

public class DashboardBTNViewController {

    public AnchorPane businessOverview;
    @FXML
    private ComboBox<String> dashboardComboBox;

    @FXML
    private AnchorPane card1;
    @FXML
    private AnchorPane card2;
    @FXML
    private AnchorPane card3;
    @FXML
    private AnchorPane card4;
    @FXML
    private AnchorPane card5;

    public void initialize() {
        dashboardComboBox.setValue("Business Overview");
        dashboardComboBox.getItems().addAll("Business Overview", "My Dashboard");

        dashboardComboBox.setOnAction(event -> {
            String selectedDashboard = dashboardComboBox.getValue();
            if ("Business Overview".equals(selectedDashboard)) {
                TherapistsViewController.bindNavigation(
                        businessOverview,
                        "/view/nav-buttons/dashboard-btn-view.fxml"
                );
            } else if ("My Dashboard".equals(selectedDashboard)) {
                TherapistsViewController.bindNavigation(
                        businessOverview,
                        "/view/nav-buttons/dashboard-expand-view.fxml"
                );
            }
        });

        cardAnimation();
    }

    private void cardAnimation() {
        List<AnchorPane> cards = Arrays.asList(card1, card2, card3, card4, card5);
        applySequentialFadeIn(cards);
        setupHoverEffect(card1);
        setupHoverEffect(card2);
        setupHoverEffect(card3);
        setupHoverEffect(card4);
        setupHoverEffect(card5);
    }

    private void setupHoverEffect(AnchorPane card) {
        card.setOnMouseEntered(event -> scaleCard(card, 1.05));
        card.setOnMouseExited(event -> scaleCard(card, 1.0));
    }

    private void scaleCard(AnchorPane card, double scale) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), card);
        scaleTransition.setToX(scale);
        scaleTransition.setToY(scale);
        scaleTransition.play();
    }

    private void applySequentialFadeIn(List<AnchorPane> cards) {
        Duration delayBetweenCards = Duration.millis(500);
        Timeline timeline = new Timeline();

        for (int i = 0; i < cards.size(); i++) {
            AnchorPane card = cards.get(i);
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(900), card);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);
            fadeTransition.setCycleCount(1);
            KeyFrame keyFrame = new KeyFrame(delayBetweenCards.multiply(i), event -> fadeTransition.play());
            timeline.getKeyFrames().add(keyFrame);
            card.setOpacity(0);
        }

        timeline.play();
    }
}
