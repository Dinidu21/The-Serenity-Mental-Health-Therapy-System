package com.dinidu.lk.pmt.controller.dashboard;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.UserBO;
import com.dinidu.lk.pmt.controller.BaseController;
import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.custom.impl.QueryDAOImpl;
import com.dinidu.lk.pmt.dto.UserDTO;
import com.dinidu.lk.pmt.utils.*;
import com.dinidu.lk.pmt.utils.customAlerts.CustomAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.regex.Regex;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ProfileViewController extends BaseController {
    static String loggedInUsername = SessionUser.getLoggedInUsername();
    @FXML
    public TextField firstName;
    @FXML
    public TextField lastName;
    @FXML
    public TextField emailTextField;
    public Label changePw;
    public AnchorPane profileId;
    public Button logoutBtn;
    @FXML
    private ImageView profileImageView;
    @FXML
    private ComboBox<String> countryCodeComboBox;
    @FXML
    private TextField phoneNumberTextField;
    @FXML
    private Label errorLabel;

    UserBO userBO= (UserBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.USER);
    QueryDAO queryDAO = new QueryDAOImpl();

    private final ObservableList<String> countryCodes = FXCollections.observableArrayList(
            "LK - +94",
            "US - +1",
            "IN - +91",
            "UK - +44",
            "FR - +33",
            "DE - +49",
            "ES - +34",
            "IT - +39",
            "MX - +52",
            "BR - +55",
            "AU - +61",
            "CA - +1",
            "CN - +86",
            "JP - +81",
            "RU - +7"
    );

    public void initialize() {

        loggedInUsername = SessionUser.getLoggedInUsername();
        System.out.println("Username in initialize in Profile: " + loggedInUsername);
        SessionUser.initializeSession(loggedInUsername);

       Image userImage = SessionUser.getProfileImage();
        profileImageView.setImage(userImage != null ? userImage : new Image(getClass().getResourceAsStream("/asserts/images/user.jpg")));
        profileImageView.setClip(new Circle(175, 175, 175));

        countryCodeComboBox.setItems(countryCodes);
        countryCodeComboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            List<String> filteredCodes = countryCodes.stream()
                    .filter(code -> code.toLowerCase().contains(newValue.toLowerCase()))
                    .collect(Collectors.toList());
            countryCodeComboBox.getItems().setAll(filteredCodes);
            countryCodeComboBox.setValue(newValue);
        });

        countryCodeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                System.out.println("Selected country code: " + newValue);
            }
        });

        firstName.textProperty().addListener((observable, oldValue, newValue) -> validateName(firstName));
        lastName.textProperty().addListener((observable, oldValue, newValue) -> validateName(lastName));
        phoneNumberTextField.textProperty().addListener((observable, oldValue, newValue) -> validatePhoneNumber());
        emailTextField.textProperty().addListener((observable, oldValue, newValue) -> validateEmail());

        loadCurrentUserData();

        String username = SessionUser.getLoggedInUsername();
        UserRole userRoleByUsername;
        try {
            userRoleByUsername = queryDAO.getUserRoleByUsername(username);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (userRoleByUsername != UserRole.ADMIN) {
            System.out.println("Access denied: Only Admin");
            logoutBtn.setVisible(true);
        } else {
            logoutBtn.setVisible(false);
        }
    }

    private void loadCurrentUserData() {
        String loggedInUsername = SessionUser.getLoggedInUsername();
        System.out.println("Username in current Profile: " + loggedInUsername);
        if (loggedInUsername == null) {
            System.out.println("loggedInUsername is null");
            return;
        }

        UserDTO currentUser;

        try {
            currentUser = userBO.getUserDetailsByUsername(loggedInUsername);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (currentUser != null) {
            String[] fullNameParts = currentUser.getFullName().split(" ");
            if (fullNameParts.length > 0) {
                firstName.setText(fullNameParts[0]);
                if (fullNameParts.length > 1) {
                    lastName.setText(fullNameParts[1]);
                }
            }

            emailTextField.setText(currentUser.getEmail());

            String phoneNumber = currentUser.getPhoneNumber();
            System.out.println("Retrieved phone number: " + phoneNumber);

            String extractedCountryCode = PhoneUtil.extractCountryCode(phoneNumber);
            if (extractedCountryCode != null) {
                String formattedCode = countryCodes.stream()
                        .filter(code -> code.contains(extractedCountryCode))
                        .findFirst()
                        .orElse(null);

                if (formattedCode != null) {
                    System.out.println("Formatted code: " + formattedCode);
                    countryCodeComboBox.setValue(formattedCode);

                    String countryCode = formattedCode.split(" - ")[1];
                    int codeLength = countryCode.length();

                    String phoneNumberWithoutCode = phoneNumber.startsWith(countryCode)
                            ? phoneNumber.substring(codeLength) : phoneNumber;

                    System.out.println("Phone number without country code: " + phoneNumberWithoutCode);
                    phoneNumberTextField.setText(phoneNumberWithoutCode);
                } else {
                    System.out.println("Failed to find matching country code format in ComboBox.");
                    CustomErrorAlert.showAlert("Error", "Invalid phone number format. Unable to extract country code.");
                }
            } else {
                System.out.println("Failed to extract country code from phone number: " + phoneNumber);
                CustomErrorAlert.showAlert("Error", "Invalid phone number format. Unable to extract country code.");
            }
        } else {
            CustomErrorAlert.showAlert("Error", "Failed to load user data.");
            System.out.println("Failed to load user data. No users are found.");
        }
    }

    private void validatePhoneNumber() {
        String selectedCountryCode = getSelectedCountryCode();
        String phoneNumber = phoneNumberTextField.getText().replaceAll("[^0-9]", "");

        if (phoneNumber.isEmpty() || phoneNumber.startsWith("0")) {
            phoneNumberTextField.setStyle("-fx-border-color: red;");
            return;
        }

        boolean isValid = Regex.validatePhoneNumber(phoneNumber);
        if (!isValid) {
            phoneNumberTextField.setStyle("-fx-border-color: red;");
        } else {
            phoneNumberTextField.setStyle("");
        }

        if (phoneNumber.startsWith("0")) {
            errorLabel.setText("Phone number cannot start with zero.");
            phoneNumberTextField.setStyle("-fx-border-color: red;");
        } else {
            errorLabel.setText("");
        }
    }

    private String getSelectedCountryCode() {
        String selected = countryCodeComboBox.getValue();
        if (selected != null && selected.contains(" - ")) {
            return selected.split(" - ")[1];
        } else {
            System.out.println("Country code selection is invalid or empty.");
            return "";
        }
    }

    private void validateEmail() {
        String email = emailTextField.getText();
        boolean isValid = Regex.isEmailValid(email);
        if (!isValid) {
            emailTextField.setStyle("-fx-border-color: red;");
        } else {
            emailTextField.setStyle("");
        }
    }

    public void penOnClick(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(profileImageView.getScene().getWindow());
        if (selectedFile != null) {
            try {
                ProfileImageManager.saveProfileImage(selectedFile, loggedInUsername);
                Image newImage = new Image(selectedFile.toURI().toString());
                profileImageView.setImage(newImage);
                profileImageView.setClip(new Circle(175, 175, 175));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void validateName(TextField textField) {
        String value = textField.getText();
        if (!value.matches("[a-zA-Z]*")) {
            textField.setText(value.replaceAll("[^a-zA-Z]", ""));
            textField.setStyle("-fx-border-color: red;");
        } else {
            textField.setStyle("");
        }
    }

    public void changeOnClick(ActionEvent actionEvent) {
        //for dev purpose i set loggedInUsername = dinidu
        //loggedInUsername = "dinidu";
        /*loggedInUsername = "dinidu";*/
        System.out.println("Username in current Profile: " + loggedInUsername);
        boolean hasUpdates = false;
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(loggedInUsername); // Assuming the username remains the same

        if (!firstName.getText().isEmpty()) {
            if (firstName.getText().matches("[a-zA-Z]+")) {
                userDTO.setFullName(firstName.getText() + (userDTO.getFullName() != null ? " " + userDTO.getFullName().split(" ")[1] : ""));
                hasUpdates = true;
            } else {
                CustomErrorAlert.showAlert("Validation Error", "First name can only contain alphabetic characters.");
            }
        }

        if (!lastName.getText().isEmpty()) {
            if (lastName.getText().matches("[a-zA-Z]+")) {
                userDTO.setFullName((userDTO.getFullName() != null ? userDTO.getFullName().split(" ")[0] : "") + " " + lastName.getText());
                hasUpdates = true;
            } else {
                CustomErrorAlert.showAlert("Validation Error", "Last name can only contain alphabetic characters.");
            }
        }

        if (!emailTextField.getText().isEmpty()) {
            if (Regex.isEmailValid(emailTextField.getText())) {
                userDTO.setEmail(emailTextField.getText());
                hasUpdates = true;
            } else {
                CustomErrorAlert.showAlert("Validation Error", "Please enter a valid email.");
            }
        }

        if (!phoneNumberTextField.getText().isEmpty()) {
            String selectedCountryCode = getSelectedCountryCode();
            String phoneNumber = phoneNumberTextField.getText().replaceAll("[^0-9]", "");
            if (Regex.validatePhoneNumber(phoneNumber)) {
                userDTO.setPhoneNumber(selectedCountryCode + phoneNumber);
                hasUpdates = true;
            } else {
                CustomErrorAlert.showAlert("Validation Error", "Please enter a valid phone number.");
            }
        }

        if (hasUpdates) {
            boolean isUpdated;
            try {
                isUpdated = userBO.updateUser(userDTO);
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            if (isUpdated) {
                CustomAlert.showAlert("Success", "Profile updated successfully!");
                loadCurrentUserData();
            } else {
                CustomErrorAlert.showAlert("Error", "Failed to update profile. Please try again.");
            }
        } else {
            CustomErrorAlert.showAlert("No Updates", "No fields were updated.");
        }
    }

    public void changePwOnClick() {
        ModalLoaderUtil.showModal("/view/nav-buttons/change-pw.fxml", "/asserts/icons/PN.png", (Stage) profileId.getScene().getWindow());
    }

    public void logoutOnClick(ActionEvent actionEvent) {
        transitionToScene(profileId, "/view/login-view.fxml");
        SessionUser.clearSession();
    }
}