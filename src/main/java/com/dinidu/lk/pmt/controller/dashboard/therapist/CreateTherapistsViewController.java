package com.dinidu.lk.pmt.controller.dashboard.therapist;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.TherapistsBO;
import com.dinidu.lk.pmt.bo.custom.UserBO;
import com.dinidu.lk.pmt.controller.dashboard.TherapistsViewController;
import com.dinidu.lk.pmt.dto.TherapistDTO;
import com.dinidu.lk.pmt.utils.Auth;
import com.dinidu.lk.pmt.utils.projectTypes.TherapistStatus;
import com.dinidu.lk.pmt.utils.customAlerts.CustomAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.SessionUser;
import com.dinidu.lk.pmt.utils.regex.Regex;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.sql.SQLException;
import java.util.Date;

public class CreateTherapistsViewController {
    @FXML
    private AnchorPane projectCreatePg;

    @FXML
    private TextField TherapistsNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private TextArea addressField;

    UserBO userBO =
            (UserBO) BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.USER);

    TherapistsBO projectsBO =
            (TherapistsBO) BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.THERAPIST);

    public void createTherapistsClick(ActionEvent actionEvent) {
        Auth.userAccessLevelCheck();
        String loggedInUsername = SessionUser.getLoggedInUsername();
        Long userIdByUsername = null;
        try {
            userIdByUsername = userBO.getUserIdByUsername(loggedInUsername);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (userIdByUsername == null) {
            System.out.println("User ID is null for username: " + loggedInUsername);
            CustomErrorAlert.showAlert("User not found", "User not found. Please login again.");
            return;
        }

        if (validateInputFields()) {
            TherapistDTO therapistDTO = getTherapistDTO(userIdByUsername);

            boolean isSaved;
            try {
                isSaved = projectsBO.insert(therapistDTO);
                if (isSaved) {
                    System.out.println("Therapist created successfully!");
                    CustomAlert.showAlert("Therapist created", "Therapist created successfully!");
                    TherapistsViewController.bindNavigation(projectCreatePg, "/view/nav-buttons/therapist-view.fxml");
                    clearContent();
                } else {
                    System.out.println("Error saving Therapist.");
                    CustomErrorAlert.showAlert("Error saving Therapist", "Failed to save the Therapist.");
                }
            } catch (Exception e) {
                System.out.println("Error saving Therapist: " + e.getMessage());
                CustomErrorAlert.showAlert("Error saving Therapist", "Error saving Therapist: " + e.getMessage());
            }
        } else {
            System.out.println("Please fill all the required fields.");
            CustomErrorAlert.showAlert("Invalid Input", "Please fill all the required fields.");
        }
    }

    private TherapistDTO getTherapistDTO(Long userIdByUsername) {
        System.out.println("Name from input field: " + TherapistsNameField.getText());
        TherapistDTO therapistDTO = new TherapistDTO();
        String name = TherapistsNameField.getText();
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Error: Therapist's name cannot be empty!");
            return null;
        }

        if (!Regex.isEmailValid(emailField.getText())) {
            CustomErrorAlert.showAlert("Invalid Email", "Please enter a valid email address.");
            return null;
        }

        if (!Regex.validatePhoneNumber(phoneNumberField.getText())) {
            CustomErrorAlert.showAlert("Invalid Phone Number", "Please enter a valid phone number.");
            return null;
        }

        if (!Regex.isAlphabetic(name)) {
            CustomErrorAlert.showAlert("Invalid Name", "Please enter a valid name.");
            return null;
        }

        therapistDTO.setFullName(name);
        therapistDTO.setEmail(emailField.getText());
        therapistDTO.setPhoneNumber(phoneNumberField.getText());
        therapistDTO.setAddress(addressField.getText());
        therapistDTO.setStatus(TherapistStatus.AVAILABLE);
        therapistDTO.setCreatedBy(userIdByUsername);
        therapistDTO.setCreatedAt(new Date());
        therapistDTO.setUpdatedAt(new Date());
        System.out.println("THIS IS THE THERAPIST DTO BEFORE INSERTING: ");
        System.out.println("TherapistDTO: " + therapistDTO);
        System.out.println("Controller: " + this);
        return therapistDTO;
    }


    public void cancelOnClick(ActionEvent actionEvent) {
        if (areFieldsCleared()) {
            TherapistsViewController.bindNavigation(projectCreatePg, "/view/nav-buttons/therapist-view.fxml");
        } else {
            clearContent();
        }
    }

    private boolean areFieldsCleared() {
        return TherapistsNameField.getText().isEmpty() &&
                emailField.getText().isEmpty() &&
                addressField.getText().isEmpty()
                && phoneNumberField.getText().isEmpty();
    }

    private void clearContent() {
        TherapistsNameField.clear();
        emailField.clear();
        addressField.clear();
        phoneNumberField.clear();
    }

    private boolean validateInputFields() {
        return !TherapistsNameField.getText().isEmpty() &&
                !emailField.getText().isEmpty() &&
                !addressField.getText().isEmpty() &&
                !phoneNumberField.getText().isEmpty();
    }
}
