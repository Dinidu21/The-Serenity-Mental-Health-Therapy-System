package com.dinidu.lk.pmt.controller.dashboard.project;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.TherapistsBO;
import com.dinidu.lk.pmt.bo.custom.UserBO;
import com.dinidu.lk.pmt.controller.dashboard.TherapistsViewController;
import com.dinidu.lk.pmt.dto.ProjectDTO;
import com.dinidu.lk.pmt.utils.Auth;
import com.dinidu.lk.pmt.utils.projectTypes.TherapistStatus;
import com.dinidu.lk.pmt.utils.customAlerts.CustomAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.SessionUser;
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
                    getBO(BOFactory.BOTypes.TherapistsBO);

    public void initialize() {

    }

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
            ProjectDTO projectDTO = new ProjectDTO();
            projectDTO.setId(emailField.getText() + "-00" + phoneNumberField.getText());
            projectDTO.setName(TherapistsNameField.getText());
            projectDTO.setDescription(addressField.getText());
            projectDTO.setStartDate(new Date());
            projectDTO.setEndDate(null);
            projectDTO.setStatus(TherapistStatus.AVAILABLE);
            projectDTO.setCreatedBy(userIdByUsername);
            projectDTO.setCreatedAt(new Date());
            projectDTO.setUpdatedAt(new Date());

            boolean isSaved;
            try {
                isSaved = projectsBO.insert(projectDTO);
                if (isSaved) {
                    System.out.println("Project created successfully!");
                    CustomAlert.showAlert("Project created", "Project created successfully!");
                    TherapistsViewController.bindNavigation(projectCreatePg, "/view/nav-buttons/therapist-view.fxml");
                    clearContent();
                } else {
                    System.out.println("Error saving project.");
                    CustomErrorAlert.showAlert("Error saving project", "Failed to save the project.");
                }
            } catch (Exception e) {
                System.out.println("Error saving project: " + e.getMessage());
                CustomErrorAlert.showAlert("Error saving project", "Error saving project: " + e.getMessage());
            }
        } else {
            System.out.println("Please fill all the required fields.");
            CustomErrorAlert.showAlert("Invalid Input", "Please fill all the required fields.");
        }
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
