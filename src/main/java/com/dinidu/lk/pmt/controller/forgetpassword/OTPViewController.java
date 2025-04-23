package com.dinidu.lk.pmt.controller.forgetpassword;

import com.dinidu.lk.pmt.controller.BaseController;
import com.dinidu.lk.pmt.execeptions.LoginException;
import com.dinidu.lk.pmt.utils.customAlerts.CustomAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.ModalLoaderUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class OTPViewController extends BaseController implements Initializable {
    private static final int MAX_ATTEMPTS = 3;
    private int attemptCounter = 0;

    @FXML
    public AnchorPane otpPg;

    @FXML
    public TextField otpField1;

    @FXML
    public TextField otpField2;

    @FXML
    public TextField otpField3;

    @FXML
    public TextField otpField4;

    @FXML
    public TextField otpField5;

    @FXML
    public TextField otpField6;

    @FXML
    public Button submitBtn;

    public static int generatedOTP;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setNumericField(otpField1);
        setNumericField(otpField2);
        setNumericField(otpField3);
        setNumericField(otpField4);
        setNumericField(otpField5);
        setNumericField(otpField6);
        setFieldListeners();
    }

    private void setNumericField(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("\\D", ""));
            }
            if (newValue.length() > 1) {
                textField.setText(newValue.substring(0, 1));
            }
        });
    }

    private void setFieldListeners() {
        otpField1.setOnKeyReleased(event -> moveToNextField(otpField1, otpField2));
        otpField2.setOnKeyReleased(event -> moveToNextField(otpField2, otpField3));
        otpField3.setOnKeyReleased(event -> moveToNextField(otpField3, otpField4));
        otpField4.setOnKeyReleased(event -> moveToNextField(otpField4, otpField5));
        otpField5.setOnKeyReleased(event -> moveToNextField(otpField5, otpField6));
        otpField6.setOnKeyReleased(event -> moveToNextField(otpField6, null));
    }

    private void moveToNextField(TextField currentField, TextField nextField) {
        if (currentField.getText().length() == 1 && nextField != null) {
            nextField.requestFocus();
        }
    }

    public void handleSubmitOTP(ActionEvent actionEvent) {
        String enteredOTP = otpField1.getText() + otpField2.getText() + otpField3.getText() +
                otpField4.getText() + otpField5.getText() + otpField6.getText();
        if (enteredOTP.length() != 6) {
            resetOtpFieldBorders();
            CustomErrorAlert.showAlert("Error", "Please enter the complete OTP.");
            return;
        }
        System.out.println("Entered OTP: " + enteredOTP);
        System.out.println("Generated OTP: " + generatedOTP);
        if (Integer.parseInt(enteredOTP) == generatedOTP) {
            resetOtpFieldBorders();
            backToLogin(null);
            CustomAlert.showAlert("Confirmation", "OTP Verified! Password reset can proceed.");
            loadPasswordResetScreen();
        } else {
            attemptCounter++;
            if (attemptCounter >= MAX_ATTEMPTS) {
                CustomErrorAlert.showAlert("Error", "Invalid OTP. Too many attempts. Please try again later.");
                redirectToLogin();
            } else {
                CustomErrorAlert.showAlert("Error", "Invalid OTP. Please try again.");
                setInvalidOtpFieldBorders();
            }
        }
    }

    private void setInvalidOtpFieldBorders() {
        otpField1.setStyle("-fx-border-color: red;");
        otpField2.setStyle("-fx-border-color: red;");
        otpField3.setStyle("-fx-border-color: red;");
        otpField4.setStyle("-fx-border-color: red;");
        otpField5.setStyle("-fx-border-color: red;");
        otpField6.setStyle("-fx-border-color: red;");
    }

    private void resetOtpFieldBorders() {
        otpField1.setStyle("");
        otpField2.setStyle("");
        otpField3.setStyle("");
        otpField4.setStyle("");
        otpField5.setStyle("");
        otpField6.setStyle("");
    }

    private void redirectToLogin() {
        try {
            Stage otpStage = (Stage) otpPg.getScene().getWindow();
            otpStage.close();
            Stage parentStage = (Stage) otpStage.getOwner();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login-view.fxml"));
            Parent root = loader.load();
            parentStage.getScene().setRoot(root);
            parentStage.centerOnScreen();

        } catch (Exception e) {
            CustomErrorAlert.showAlert("ERROR", "Error while redirecting to login: ");
            System.out.println(e.getMessage());
            throw  new LoginException(""+e);
        }
    }


    private void loadPasswordResetScreen() {
        try {
            Stage currentStage = (Stage) otpPg.getScene().getWindow();
            Stage parentStage = (Stage) currentStage.getOwner();

            if (parentStage == null) {
                parentStage = currentStage;
            }

            ModalLoaderUtil.showModal("/view/forgetpassword/reset-pw.fxml", "/asserts/icons/PN.png", parentStage);
            parentStage.centerOnScreen();

        } catch (Exception e) {
            CustomErrorAlert.showAlert("ERROR", "Error while loading password reset screen ");
            e.printStackTrace();
        }
    }


    public void backToLogin(MouseEvent mouseEvent) {
        Stage currentStage = (Stage) otpPg.getScene().getWindow();
        currentStage.close();
    }
}
