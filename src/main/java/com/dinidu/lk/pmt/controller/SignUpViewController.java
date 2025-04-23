package com.dinidu.lk.pmt.controller;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.UserBO;
import com.dinidu.lk.pmt.dto.UserDTO;
import com.dinidu.lk.pmt.execeptions.RegistrationException;
import com.dinidu.lk.pmt.utils.regex.Regex;
import com.dinidu.lk.pmt.utils.customAlerts.CustomAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.FeedbackUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.sql.SQLException;

public class SignUpViewController extends BaseController {
    private final Regex regex = new Regex();

    public Button registerButton;
    public TextField phoneField;
    public TextField emailField;
    public PasswordField passwordField;
    public TextField usernameField;
    public AnchorPane registerPg;
    public TextField fullNameField;
    public Label feedbackPw;
    public ProgressIndicator loadingIndicator;

    UserBO userBO= (UserBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.USER);

    @FXML
    private void initialize() {
        loadingIndicator.setVisible(false);
        setUpEnterKeyPress();
    }

    private void setUpEnterKeyPress() {
        fullNameField.setOnAction(e -> usernameField.requestFocus());
        usernameField.setOnAction(e -> emailField.requestFocus());
        emailField.setOnAction(e -> passwordField.requestFocus());
        passwordField.setOnAction(e -> phoneField.requestFocus());
        phoneField.setOnAction(e -> registerButton.fire());
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        feedbackPw.setText("");
        ValidationResult validation = validateForm();

        if (validation.isValid) {
            processRegistration();
        } else {
            FeedbackUtil.showFeedback(feedbackPw, validation.message, Color.RED);
        }
    }

    private static class ValidationResult {
        boolean isValid;
        String message;

        ValidationResult(boolean isValid, String message) {
            this.isValid = isValid;
            this.message = message;
        }
    }

    private ValidationResult validateForm() {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        try {
            if(userBO.isEmailRegistered(email)){
                return new ValidationResult(false, "Email already registered.");
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (fullName.isEmpty()) {
            return new ValidationResult(false, "Full Name cannot be empty.");
        }
        if (!Regex.isAlphabetic(fullName)) {
            return new ValidationResult(false, "Full Name must contain only alphabetic characters.");
        }

        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            return new ValidationResult(false, "Username cannot be empty.");
        }

        if (email.isEmpty()) {
            return new ValidationResult(false, "Email cannot be empty.");
        }
        if (!Regex.isEmailValid(email)) {
            return new ValidationResult(false, "Invalid email format.");
        }

        String password = passwordField.getText();
        if (password.isEmpty()) {
            return new ValidationResult(false, "Password cannot be empty.");
        }
        if (!Regex.isMinLength(password)) {
            return new ValidationResult(false, "Password must be at least 8 characters long.");
        }
        if (!Regex.containsUpperCase(password)) {
            return new ValidationResult(false, "Password must contain at least one uppercase letter.");
        }
        if (!Regex.containsLowerCase(password)) {
            return new ValidationResult(false, "Password must contain at least one lowercase letter.");
        }
        if (!Regex.containsDigit(password)) {
            return new ValidationResult(false, "Password must contain at least one number.");
        }
        if (!regex.containsSpecialChar(password)) {
            return new ValidationResult(false, "Password must contain at least one special character.");
        }

        String phoneNumber = phoneField.getText().trim();
        if (phoneNumber.isEmpty()) {
            return new ValidationResult(false, "Phone number cannot be empty.");
        }
        if (!Regex.validatePhoneNumber(phoneNumber)) {
            return new ValidationResult(false, "Invalid phone number format.");
        }

        return new ValidationResult(true, "");
    }

    private void processRegistration() {
        UserDTO userDTO = new UserDTO(
                usernameField.getText().trim(),
                fullNameField.getText().trim(),
                passwordField.getText(),
                emailField.getText().trim(),
                phoneField.getText().trim()
        );

        try {
            System.out.println("UserDTO: " + userDTO);
            boolean isSaved = userBO.saveUser(userDTO); //Working
            loadingIndicator.setVisible(isSaved);

            if (isSaved) {
                handleSuccessfulRegistration();
            } else {
                CustomErrorAlert.showAlert("ERROR", "Registration failed! Please try again.");
                System.out.println("Failed to save user! Please try again.");
            }
        } catch (Exception e) {
            CustomErrorAlert.showAlert("Error", "An unexpected error occurred");
            System.out.println(e.getMessage());
            throw new RegistrationException("Registration failed! Please try again.");
        }
    }

    private void handleSuccessfulRegistration() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    clearContent();
                    System.out.println("User has been saved successfully!");
                    CustomAlert.showAlert("SUCCESS", "Registration successful!");
                    transitionToScene(registerPg, "/view/login-view.fxml");
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    CustomErrorAlert.showAlert("ERROR", "An unexpected error occurred");
                    System.out.println("An unexpected error occurred: " + e.getMessage());
                    clearContent();
                });
            }
        }).start();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        clearContent();
        feedbackPw.setText("");
    }

    public void initData(String fullName, String email) {
        fullNameField.setText(fullName);
        emailField.setText(email);
        System.out.println("Email: " + email);
        System.out.println("Full Name: " + fullName);
    }

    @FXML
    private void handleLogin() {
        transitionToScene(registerPg, "/view/login-view.fxml");
    }

    private void clearContent() {
        usernameField.clear();
        passwordField.clear();
        emailField.clear();
        phoneField.clear();
        fullNameField.clear();
    }
}
