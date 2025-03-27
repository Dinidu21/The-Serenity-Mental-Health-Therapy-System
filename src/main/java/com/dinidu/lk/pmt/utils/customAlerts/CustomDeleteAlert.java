package com.dinidu.lk.pmt.utils.customAlerts;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.scene.shape.SVGPath;

public class CustomDeleteAlert {
    private static boolean result;
    private static Stage dialogStage;
    private static double xOffset = 0;
    private static double yOffset = 0;

    public static boolean showAlert(Stage parentStage, String title, String message) {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.initOwner(parentStage);

        SVGPath warningIcon = new SVGPath();
        warningIcon.setContent("M12 5.99L19.53 19H4.47L12 5.99M12 2L1 21h22L12 2zm1 14h-2v2h2v-2zm0-6h-2v4h2v-4z");
        warningIcon.setFill(Color.web("#FF7675"));
        warningIcon.setScaleX(1.0);
        warningIcon.setScaleY(1.0);

        VBox mainContainer = new VBox(25);
        mainContainer.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.95);" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 30;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 20, 0, 0, 10)," +
                        "           innershadow(gaussian, rgba(255, 255, 255, 0.8), 10, 0, 0, 0);" +
                        "-fx-border-color: rgba(255, 255, 255, 0.8);" +
                        "-fx-border-radius: 20;" +
                        "-fx-border-width: 1;"
        );

        StackPane iconContainer = new StackPane(warningIcon);
        iconContainer.setStyle(
                "-fx-background-color: rgba(255, 118, 117, 0.1);" +
                        "-fx-padding: 15;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
                "-fx-font-family: 'Segoe UI';" +
                        "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #2D3436;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.1), 1, 0, 0, 1);"
        );

        Label messageLabel = new Label(message);
        messageLabel.setStyle(
                "-fx-font-family: 'Segoe UI';" +
                        "-fx-font-size: 15px;" +
                        "-fx-text-fill: #636E72;" +
                        "-fx-wrap-text: true;" +
                        "-fx-line-spacing: 5px;"
        );

        HBox buttonContainer = new HBox(20);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setStyle("-fx-padding: 10 0 0 0;");

        Button noButton = new Button("Cancel");
        noButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #636E72;" +
                        "-fx-font-family: 'Segoe UI';" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 12 30;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: #E0E0E0;" +
                        "-fx-border-radius: 8;" +
                        "-fx-cursor: hand;"
        );

        Button yesButton = new Button("Delete");
        yesButton.setStyle(
                "-fx-background-color: linear-gradient(to right, #FF7675, #D63031);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-family: 'Segoe UI';" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 12 30;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(255, 118, 117, 0.3), 8, 0, 0, 4);"
        );

        setupButtonHoverEffects(noButton, yesButton);
        setupButtonActions(noButton, yesButton);
        buttonContainer.getChildren().addAll(noButton, yesButton);
        mainContainer.getChildren().addAll(iconContainer, titleLabel, messageLabel, buttonContainer);
        mainContainer.setAlignment(Pos.CENTER);
        StackPane overlay = new StackPane(mainContainer);
        overlay.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.3);" +
                        "-fx-background-radius: 20;"
        );

        Scene scene = new Scene(overlay, 450, 300);
        scene.setFill(null);

        setupDragging(overlay);

        dialogStage.setScene(scene);

        playOpeningAnimation(overlay, mainContainer);

        dialogStage.showAndWait();
        return result;
    }

    private static void setupButtonHoverEffects(Button noButton, Button yesButton) {
        noButton.setOnMouseEntered(e -> {
            noButton.setStyle(
                    "-fx-background-color: #F0F0F0;" +
                            "-fx-text-fill: #2D3436;" +
                            "-fx-font-family: 'Segoe UI';" +
                            "-fx-font-size: 15px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 12 30;" +
                            "-fx-background-radius: 8;" +
                            "-fx-border-color: #E0E0E0;" +
                            "-fx-border-radius: 8;" +
                            "-fx-cursor: hand;"
            );
        });

        noButton.setOnMouseExited(e -> {
            noButton.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-text-fill: #636E72;" +
                            "-fx-font-family: 'Segoe UI';" +
                            "-fx-font-size: 15px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 12 30;" +
                            "-fx-background-radius: 8;" +
                            "-fx-border-color: #E0E0E0;" +
                            "-fx-border-radius: 8;" +
                            "-fx-cursor: hand;"
            );
        });

        yesButton.setOnMouseEntered(e -> {
            yesButton.setStyle(
                    "-fx-background-color: linear-gradient(to right, #D63031, #FF7675);" +
                            "-fx-text-fill: white;" +
                            "-fx-font-family: 'Segoe UI';" +
                            "-fx-font-size: 15px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 12 30;" +
                            "-fx-background-radius: 8;" +
                            "-fx-cursor: hand;" +
                            "-fx-effect: dropshadow(gaussian, rgba(255, 118, 117, 0.4), 10, 0, 0, 6);"
            );
        });

        yesButton.setOnMouseExited(e -> {
            yesButton.setStyle(
                    "-fx-background-color: linear-gradient(to right, #FF7675, #D63031);" +
                            "-fx-text-fill: white;" +
                            "-fx-font-family: 'Segoe UI';" +
                            "-fx-font-size: 15px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 12 30;" +
                            "-fx-background-radius: 8;" +
                            "-fx-cursor: hand;" +
                            "-fx-effect: dropshadow(gaussian, rgba(255, 118, 117, 0.3), 8, 0, 0, 4);"
            );
        });
    }

    private static void setupButtonActions(Button noButton, Button yesButton) {
        yesButton.setOnAction(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), yesButton);
            st.setToX(0.95);
            st.setToY(0.95);
            st.setOnFinished(event -> {
                ScaleTransition st2 = new ScaleTransition(Duration.millis(100), yesButton);
                st2.setToX(1);
                st2.setToY(1);
                st2.setOnFinished(event2 -> {
                    result = true;
                    closeWithAnimation();
                });
                st2.play();
            });
            st.play();
        });

        noButton.setOnAction(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(100), noButton);
            st.setToX(0.95);
            st.setToY(0.95);
            st.setOnFinished(event -> {
                ScaleTransition st2 = new ScaleTransition(Duration.millis(100), noButton);
                st2.setToX(1);
                st2.setToY(1);
                st2.setOnFinished(event2 -> {
                    result = false;
                    closeWithAnimation();
                });
                st2.play();
            });
            st.play();
        });
    }

    private static void setupDragging(StackPane overlay) {
        overlay.setOnMousePressed(mouseEvent -> {
            xOffset = mouseEvent.getSceneX();
            yOffset = mouseEvent.getSceneY();
        });

        overlay.setOnMouseDragged(mouseEvent -> {
            dialogStage.setX(mouseEvent.getScreenX() - xOffset);
            dialogStage.setY(mouseEvent.getScreenY() - yOffset);
        });
    }

    private static void playOpeningAnimation(StackPane overlay, VBox mainContainer) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), overlay);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), mainContainer);
        scaleIn.setFromX(0.8);
        scaleIn.setFromY(0.8);
        scaleIn.setToX(1);
        scaleIn.setToY(1);
        ParallelTransition pt = new ParallelTransition(fadeIn, scaleIn);
        pt.play();
    }

    private static void closeWithAnimation() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), dialogStage.getScene().getRoot());
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200),
                ((StackPane) dialogStage.getScene().getRoot()).getChildren().get(0));
        scaleOut.setToX(0.8);
        scaleOut.setToY(0.8);

        ParallelTransition pt = new ParallelTransition(fadeOut, scaleOut);
        pt.setOnFinished(e -> dialogStage.close());
        pt.play();
    }
}