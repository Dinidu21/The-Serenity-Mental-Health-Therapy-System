package com.dinidu.lk.pmt.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.Objects;

public class ModalLoaderUtil {
    private static double xOffset = 0;
    private static double yOffset = 0;

    public static void showModal(String fxmlPath, String iconPath, Stage ownerStage) {
        try {
            if(iconPath == null) {
                iconPath = "/asserts/icons/PN.png";
            }

            Stage modalStage = new Stage();
            modalStage.initStyle(StageStyle.TRANSPARENT);
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(ownerStage);

            FXMLLoader loader = new FXMLLoader(ModalLoaderUtil.class.getResource(fxmlPath));
            Parent root = loader.load();

            root.setOnMousePressed(mouseEvent -> {
                xOffset = mouseEvent.getSceneX();
                yOffset = mouseEvent.getSceneY();
            });
            root.setOnMouseDragged(mouseEvent -> {
                modalStage.setX(mouseEvent.getScreenX() - xOffset);
                modalStage.setY(mouseEvent.getScreenY() - yOffset);
            });

            Scene scene = new Scene(root);
            scene.setFill(null);
            modalStage.setScene(scene);
            modalStage.getIcons().add(new Image(Objects.requireNonNull(ModalLoaderUtil.class.getResourceAsStream(iconPath))));
            modalStage.centerOnScreen();
            modalStage.show();
        } catch (Exception e) {
            System.out.println("Error while loading modal: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
