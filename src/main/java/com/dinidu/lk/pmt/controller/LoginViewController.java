package com.dinidu.lk.pmt.controller;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.UserBO;
import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.custom.impl.QueryDAOImpl;
import com.dinidu.lk.pmt.execeptions.LoginException;
import com.dinidu.lk.pmt.utils.regex.Regex;
import com.dinidu.lk.pmt.utils.customAlerts.CustomAlert;
import com.dinidu.lk.pmt.utils.FeedbackUtil;
import com.dinidu.lk.pmt.utils.SessionUser;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.animation.ScaleTransition;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;

public class LoginViewController extends BaseController {

    @FXML
    public AnchorPane loginPg;
    @FXML
    public Label feedbackLabelForPW;
    public Button signInButton;
    public TextField newEmailField;
    public Button signUpButton;
    public TextField newFullName;
    public Label labelOnSignUp;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ImageView googleIcon;
    @FXML
    private ImageView githubIcon;
    @FXML
    private ImageView appleIcon;
    @FXML
    private ImageView wechatIcon;
    @FXML
    private ImageView gitlabIcon;
    @FXML
    private ImageView spaceIcon;

    UserBO userBO = (UserBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.USER);
    QueryDAO queryDAO = new QueryDAOImpl();

    @FXML
    private void handleLogin() {
        String loggedInUsername = SessionUser.getLoggedInUsername();
        System.out.println("Username after user logged out: " + loggedInUsername);
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            FeedbackUtil.showFeedback(feedbackLabelForPW, "Please enter both username and password.", Color.RED);
            return;
        }
        Regex regex = new Regex();

        if (!Regex.isMinLength(password)) {
            FeedbackUtil.showFeedback(feedbackLabelForPW, "Password must be at least 8 characters long.", Color.RED);
        } else if (!Regex.containsUpperCase(password)) {
            FeedbackUtil.showFeedback(feedbackLabelForPW, "Password must contain at least one uppercase letter.", Color.RED);
        } else if (!Regex.containsLowerCase(password)) {
            FeedbackUtil.showFeedback(feedbackLabelForPW, "Password must contain at least one lowercase letter.", Color.RED);
        } else if (!Regex.containsDigit(password)) {
            FeedbackUtil.showFeedback(feedbackLabelForPW, "Password must contain at least one digit.", Color.RED);
        } else if (!regex.containsSpecialChar(password)) {
            FeedbackUtil.showFeedback(feedbackLabelForPW, "Password must contain at least one special character.", Color.RED);
        } else {
            String result;
            try {
                result = userBO.verifyUser(username, password);
            } catch (SQLException | ClassNotFoundException e) {
                throw new LoginException("Error occurred while verifying user: " + e.getMessage());
            }

            switch (result) {
                case "SUCCESS":
                    CustomAlert.showAlert("Confirmation", "Login successful!");
                    SessionUser.setLoggedInUsername(username);
                    System.out.println("Username: " + username + " set in session");
                    UserRole userRole;
                    try {
                        userRole = queryDAO.getUserRoleByUsername(username);
                    } catch (SQLException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("User role: " + userRole);
                    SessionUser.setLoggedInUserRole(userRole);
                    Set<String> userPermissions;
                    try {
                        userPermissions = queryDAO.getUserPermissionsByRole(userRole);
                    } catch (SQLException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    SessionUser.setPermissions(userPermissions);

                    System.out.println(userRole + " Logged in");
                    System.out.println("User Can:\n" + userPermissions);
                    System.out.println("Username is logged in Now in Dashboard: " + username);
                    transitionToScene(loginPg, "/view/dashboard-view.fxml");
                    clearFields();
                    break;
                case "INVALID_USERNAME":
                    FeedbackUtil.showFeedback(feedbackLabelForPW, "Invalid username. Please check your username.", Color.RED);
                    break;
                case "INVALID_PASSWORD":
                    FeedbackUtil.showFeedback(feedbackLabelForPW, "Incorrect password. Please try again.", Color.RED);
                    break;
                case "ERROR":
                    FeedbackUtil.showFeedback(feedbackLabelForPW, "An error occurred. Please try again later.", Color.RED);
                    break;
                default:
                    FeedbackUtil.showFeedback(feedbackLabelForPW, "An unknown error occurred.", Color.RED);
                    break;
            }
        }
    }

    @FXML
    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
    }

    @FXML
    public void initialize() {
        addHoverEffect(googleIcon);
        addHoverEffect(githubIcon);
        addHoverEffect(appleIcon);
        addHoverEffect(wechatIcon);
        addHoverEffect(gitlabIcon);
        addHoverEffect(spaceIcon);
    }

    private void addHoverEffect(ImageView icon) {
        ScaleTransition hoverIn = new ScaleTransition(Duration.millis(200), icon);
        hoverIn.setToX(1.2);
        hoverIn.setToY(1.2);

        ScaleTransition hoverOut = new ScaleTransition(Duration.millis(200), icon);
        hoverOut.setToX(1.0);
        hoverOut.setToY(1.0);

        icon.setOnMouseEntered(event -> hoverIn.playFromStart());
        icon.setOnMouseExited(event -> hoverOut.playFromStart());
    }

    @FXML
    private void handleForgotPassword() {
        transitionToScene(loginPg, "/view/forgetpassword/forget_password-view.fxml");
    }

    public void onSignUp() {
        boolean isValid = true;

        String fullName = newFullName.getText();
        String email = newEmailField.getText();

        if (fullName.isEmpty() || email.isEmpty()) {
            FeedbackUtil.showFeedback(labelOnSignUp, "Please enter both name and email.", Color.RED);
            return;
        }

        if (!Regex.isAlphabetic(fullName)) {
            FeedbackUtil.showFeedback(labelOnSignUp, "Enter a valid name (alphabetic only).", Color.RED);
            isValid = false;
        }

        if (!Regex.isEmailValid(email)) {
            FeedbackUtil.showFeedback(labelOnSignUp, "Enter a valid email.", Color.RED);
            isValid = false;
        }

        if (isValid) {
            System.out.println("isValid");
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), loginPg);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/signUp-view.fxml"));
                    Parent newRoot = loader.load();

                    SignUpViewController signUpController = loader.getController();
                    signUpController.initData(fullName, newEmailField.getText());

                    Scene newScene = new Scene(newRoot);
                    Stage currentStage = (Stage) loginPg.getScene().getWindow();
                    currentStage.setScene(newScene);
                    currentStage.setMaximized(true);
                    currentStage.centerOnScreen();

                    newRoot.setOpacity(0);
                    FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), newRoot);
                    fadeIn.setFromValue(0.0);
                    fadeIn.setToValue(1.0);

                    currentStage.show();
                    fadeIn.play();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            fadeOut.play();
        }
    }
}