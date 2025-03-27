package com.dinidu.lk.pmt.controller.dashboard;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.UserBO;
import com.dinidu.lk.pmt.controller.BaseController;
import com.dinidu.lk.pmt.utils.regex.Regex;
import com.dinidu.lk.pmt.utils.customAlerts.CustomAlert;
import com.dinidu.lk.pmt.utils.FeedbackUtil;
import com.dinidu.lk.pmt.utils.SessionUser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.SQLException;

public class ChangePwViewController extends BaseController {

    public AnchorPane pwPage;
    public Label backToProfile;
    @FXML
    private TextField newPasswordField;

    @FXML
    private TextField confirmPasswordField;

    @FXML
    private Button resetPasswordBtn;

    @FXML
    private Label passwordFeedback;

    private final Regex regex = new Regex();

    public static String loggedInUsername = SessionUser.getLoggedInUsername();

    UserBO userBO= (UserBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.USER);


    @FXML
    public void initialize() {
        newPasswordField.textProperty().addListener((observable, oldValue, newValue) -> validatePassword(newValue));
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> validatePasswordMatch(newValue));

        resetPasswordBtn.setOnAction(e -> {
            try {
                handleChangePassword();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void handleChangePassword() throws SQLException {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            FeedbackUtil.showFeedback(passwordFeedback, "Please fill out both fields.", Color.RED);
            return;
        }

        if (!Regex.isMinLength(newPassword)) {
            FeedbackUtil.showFeedback(passwordFeedback, "Password must meet the required criteria.", Color.RED);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            FeedbackUtil.showFeedback(passwordFeedback, "Passwords do not match.", Color.RED);
            return;
        }

        System.out.println("Updating password for username: " + loggedInUsername);
        boolean updateSuccessful;
        try {
            updateSuccessful = userBO.updatePassword(loggedInUsername, newPassword);
        } catch (ClassNotFoundException | SQLException e ) {
            throw new RuntimeException(e);
        }
        if (updateSuccessful) {
            new Thread(() -> {
                try {
                    String userEmail = userBO.getUserEmail(loggedInUsername);
                    System.out.println("Email for username in change password modal: " + userEmail);

                    Thread.sleep(2000);

                    Platform.runLater(() -> {
                        FeedbackUtil.showFeedback(passwordFeedback, "Password changed successfully!", Color.GREEN);
                        CustomAlert.showAlert("Success", "Your password has been updated.");
                        backToProfile();
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        } else {
            FeedbackUtil.showFeedback(passwordFeedback, "Failed to change password. Please try again.", Color.RED);
        }
    }

    private void validatePassword(String password) {
        if (password.isEmpty()) {
            FeedbackUtil.showFeedback(passwordFeedback, "Password cannot be empty.", Color.RED);
            return;
        }

        if (!Regex.containsUpperCase(password)) {
            FeedbackUtil.showFeedback(passwordFeedback, "Must contain at least one uppercase letter.", Color.RED);
        } else if (!Regex.containsLowerCase(password)) {
            FeedbackUtil.showFeedback(passwordFeedback, "Must contain at least one lowercase letter.", Color.RED);
        } else if (!Regex.containsDigit(password)) {
            FeedbackUtil.showFeedback(passwordFeedback, "Must contain at least one digit.", Color.RED);
        } else if (!Regex.isMinLength(password)) {
            FeedbackUtil.showFeedback(passwordFeedback, "Must be at least 8 characters long.", Color.RED);
        } else if (!regex.containsSpecialChar(password)) {
            FeedbackUtil.showFeedback(passwordFeedback, "Must contain at least one special character.", Color.RED);
        } else {
            FeedbackUtil.showFeedback(passwordFeedback, "Password looks good!", Color.GREEN);
        }
    }

    private void validatePasswordMatch(String confirmPassword) {
        String newPassword = newPasswordField.getText();

        if (confirmPassword.isEmpty()) {
            FeedbackUtil.showFeedback(passwordFeedback, "Please confirm your new password.", Color.RED);
        } else if (!confirmPassword.equals(newPassword)) {
            FeedbackUtil.showFeedback(passwordFeedback, "Passwords do not match.", Color.RED);
        } else {
            FeedbackUtil.showFeedback(passwordFeedback, "Passwords match!", Color.GREEN);
        }
    }

    @FXML
    public void backToProfile() {
        Stage stage = (Stage) backToProfile.getScene().getWindow();
        stage.close();
    }
}
