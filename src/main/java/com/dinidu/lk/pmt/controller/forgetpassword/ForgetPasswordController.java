package com.dinidu.lk.pmt.controller.forgetpassword;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.UserBO;
import com.dinidu.lk.pmt.controller.BaseController;
import com.dinidu.lk.pmt.execeptions.LoginException;
import com.dinidu.lk.pmt.utils.customAlerts.CustomAlert;
import com.dinidu.lk.pmt.utils.ModalLoaderUtil;
import com.dinidu.lk.pmt.utils.regex.Regex;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.FeedbackUtil;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Getter;

import java.security.SecureRandom;
import java.sql.SQLException;

@Getter

public class ForgetPasswordController extends BaseController {
    @FXML
    public TextField userEmailForgetPW;
    @FXML
    public AnchorPane forgetPage;
    public Button OTPButton;
    public TextField usernameField;
    public Label feedbackLabel;
    @FXML
    private ProgressIndicator loadingIndicator;
    public static String userEmail;

    UserBO userBO= (UserBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.USER);

    @FXML
    public void initialize() {
        loadingIndicator.setVisible(false);
        feedbackLabel.setText("");
        animateBackground();

        userEmailForgetPW.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                userEmailForgetPW.setScaleX(1.05);
                userEmailForgetPW.setScaleY(1.05);
            } else {
                userEmailForgetPW.setScaleX(1.0);
                userEmailForgetPW.setScaleY(1.0);
            }
        });
    }

    private void animateBackground() {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(4), forgetPage);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.9);
        fadeTransition.setCycleCount(FadeTransition.INDEFINITE);
        fadeTransition.setAutoReverse(true);
        fadeTransition.play();
    }

    @FXML
    private void handleSendResetEmail() {
        userEmail = userEmailForgetPW.getText();
        ResetPwViewController rst = new ResetPwViewController();
        rst.setUserEmail(userEmail);

        if (userEmail == null || userEmail.isEmpty()) {
            FeedbackUtil.showFeedback(feedbackLabel, "Please enter your Email.", Color.RED);
            return;
        }

        if (!Regex.isEmailValid(userEmail)) {
            FeedbackUtil.showFeedback(feedbackLabel, "Please Enter Valid Email", Color.RED);
            return;
        }

        try {
            if (!userBO.isEmailRegistered(userEmail)) {
                FeedbackUtil.showFeedback(feedbackLabel, "Email is not registered.", Color.RED);
                return;
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new LoginException("Error occurred while checking email: " + e.getMessage());
        }

        loadingIndicator.setVisible(true);
        int otp = generateOTP();
        new Thread(() -> {
            try {
                Thread.sleep(2000);

                Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    FeedbackUtil.showFeedback(feedbackLabel, "OTP sent to your email!", Color.GREEN);
                    CustomAlert.showAlert("Success", "OTP sent to your email!");
                    loadOTPView(otp);
                    System.out.println("Your otp is : " + otp);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    FeedbackUtil.showFeedback(feedbackLabel, "Failed to send OTP.", Color.RED);
                });
            }
        }).start();
    }

    private int generateOTP() {
        SecureRandom random = new SecureRandom();
        return 100000 + random.nextInt(900000);
    }

    @FXML
    private void handleBackToLogin() {
        transitionToScene(forgetPage, "/view/login-view.fxml");
    }

    private void loadOTPView(int otp) {
        System.out.println("OTP Loaded: " + otp);
        try {
            Stage currentStage = (Stage) forgetPage.getScene().getWindow();
            ModalLoaderUtil.showModal("/view/forgetpassword/otp-view.fxml", "/asserts/icons/PN.png", currentStage);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/forgetpassword/otp-view.fxml"));
            loader.load();
            OTPViewController.generatedOTP = otp;
        } catch (Exception e) {
            CustomErrorAlert.showAlert("ERROR", "Error: Loading OTP View");
            System.out.println(e.getMessage());
        }
    }
}
