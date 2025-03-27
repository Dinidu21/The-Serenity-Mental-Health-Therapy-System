package com.dinidu.lk.pmt.utils.customAlerts;

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

import java.util.Objects;

public class CustomErrorAlert {
    public static void showAlert(String title, String message) {
        Stage alertStage = new Stage();
        alertStage.initModality(Modality.APPLICATION_MODAL);
        alertStage.setTitle(title);

        Image icon = new Image(Objects.requireNonNull(
                CustomErrorAlert.class.getResourceAsStream("/asserts/icons/error.jpeg")
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

        Image gifImage = new Image(Objects.requireNonNull(
                CustomErrorAlert.class.getResourceAsStream("/asserts/gifs/error.gif")
        ));
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
        alertStage.showAndWait();
    }
}
