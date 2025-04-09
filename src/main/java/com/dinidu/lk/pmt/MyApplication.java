package com.dinidu.lk.pmt;

import com.dinidu.lk.pmt.controller.LoginViewController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

public class MyApplication extends Application {
    private static double xOffset = 0;
    private static double yOffset = 0;


    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.initStyle(StageStyle.TRANSPARENT);

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/login-view.fxml")));
            setupDraggable(root, primaryStage);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            setupStage(primaryStage, scene);

            launchMainApplication(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupDraggable(Parent root, Stage stage) {
        root.setOnMousePressed(mouseEvent -> {
            xOffset = mouseEvent.getSceneX();
            yOffset = mouseEvent.getSceneY();
        });
        root.setOnMouseDragged(mouseEvent -> {
            stage.setX(mouseEvent.getScreenX() - xOffset);
            stage.setY(mouseEvent.getScreenY() - yOffset);
        });
    }

    private void setupStage(Stage stage, Scene scene) {
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/asserts/icons/PN.png")));
        stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    private void launchMainApplication(Stage loadingStage) {
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                Platform.runLater(() -> loadMainStage(loadingStage));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadMainStage(Stage loadingStage) {
        try {
            FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/view/login-view.fxml"));
            AnchorPane mainPane = mainLoader.load();
            LoginViewController controller = mainLoader.getController();
            Scene mainScene = new Scene(mainPane);
            Stage mainStage = new Stage();
            mainStage.setTitle("Project Nexus");
            mainStage.setScene(mainScene);
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/asserts/icons/PN.png")));
            mainStage.getIcons().add(icon);
            mainStage.setOnCloseRequest(event -> {
                event.consume();
                handleApplicationClose();
            });
            mainStage.centerOnScreen();
            mainStage.setMaximized(true);
            mainStage.show();
            loadingStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void handleApplicationClose() {
        try {
            Platform.exit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        handleApplicationClose();
    }

    public static void main(String[] args) {
        launch(args);
    }
}