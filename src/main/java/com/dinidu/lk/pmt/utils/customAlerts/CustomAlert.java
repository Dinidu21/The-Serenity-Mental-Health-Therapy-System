package com.dinidu.lk.pmt.utils.customAlerts;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;

public class CustomAlert {
    public static void showAlert(String title, String message) {
        Stage alertStage = new Stage();
        alertStage.initModality(Modality.APPLICATION_MODAL);
        alertStage.setTitle(title);

        Image icon = new Image(Objects.requireNonNull(
                CustomAlert.class.getResourceAsStream("/asserts/icons/confirm.png")
        ));
        alertStage.getIcons().add(icon);

        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle(
                "-fx-padding: 20; " +
                        "-fx-background-color: #ffffff; " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-radius: 10; " +
                        "-fx-border-color: #ff318c; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);"
        );

        Image gifImage = new Image("file:D:/Java/PMT/src/main/resources/asserts/gifs/CONFIRM.gif");
        ImageView gifImageView = new ImageView(gifImage);
        gifImageView.setFitHeight(100);
        gifImageView.setFitWidth(100);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.web("#ff318c"));

        Label messageLabel = new Label(message);
        messageLabel.setFont(Font.font("Poppins", 14));
        messageLabel.setTextFill(Color.web("#333333"));
        messageLabel.setWrapText(true);

        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER);

        Button closeButton = new Button("Close");
        closeButton.setStyle(
                "-fx-background-color: #ff318c; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 5; " +
                        "-fx-border-radius: 5; " +
                        "-fx-padding: 5 15 5 15;"
        );
        closeButton.setOnAction(e -> alertStage.close());

        buttonContainer.getChildren().add(closeButton);
        vbox.getChildren().addAll(gifImageView, titleLabel, messageLabel, buttonContainer);
        Scene scene = new Scene(vbox);
        alertStage.setScene(scene);
        alertStage.centerOnScreen();
        alertStage.setWidth(350);
        alertStage.setHeight(300);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.7), event -> alertStage.close())
        );
        timeline.setCycleCount(1);
        timeline.play();

        alertStage.show();
    }
}
