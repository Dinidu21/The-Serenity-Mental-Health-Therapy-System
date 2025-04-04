package com.dinidu.lk.pmt.controller.dashboard.project;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.TherapistsBO;
import com.dinidu.lk.pmt.bo.custom.UserBO;
import com.dinidu.lk.pmt.controller.dashboard.TherapistsViewController;
import com.dinidu.lk.pmt.dto.ProjectDTO;
import com.dinidu.lk.pmt.utils.Auth;
import com.dinidu.lk.pmt.utils.projectTypes.ProjectPriority;
import com.dinidu.lk.pmt.utils.projectTypes.ProjectStatus;
import com.dinidu.lk.pmt.utils.projectTypes.ProjectVisibility;
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
        phoneNumberField.setText("1");
        phoneNumberField.setDisable(true);
        emailField.setDisable(true);
        TherapistsNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            generateProjectId(newValue);
            updateFullProjectId();
        });

        phoneNumberField.textProperty().addListener((observable, oldValue, newValue) -> updateFullProjectId());
    }

    private void generateProjectId(String name) {
        StringBuilder projectId = new StringBuilder();

        if (!name.isEmpty()) {
            projectId.append(Character.toUpperCase(name.charAt(0)));

            for (int i = 1; i < name.length(); i++) {
                char currentChar = name.charAt(i);
                if (Character.isUpperCase(currentChar)) {
                    projectId.append(currentChar);
                }
            }
        }

        emailField.setText(projectId.length() > 2 ? projectId.substring(0, 2) : projectId.toString());
    }

    private void updateFullProjectId() {
        String projectId = emailField.getText();
        System.out.println("Project ID: " + projectId);
        String startingId = phoneNumberField.getText();
        String fullProjectId = projectId + "-00" + startingId;

        try {
            if (projectsBO.isTherapistIdTaken(fullProjectId).isPresent()) {
                System.out.println("Project ID already exists: " + fullProjectId);
                int newStartingId = Integer.parseInt(startingId) + 1;
                phoneNumberField.setText(String.valueOf(newStartingId));
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Full Project ID: " + fullProjectId);
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
            projectDTO.setStatus(ProjectStatus.PLANNED);
            projectDTO.setPriority(ProjectPriority.MEDIUM);
            projectDTO.setVisibility(ProjectVisibility.PUBLIC);
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
                addressField.getText().isEmpty();
    }

    private void clearContent() {
        TherapistsNameField.clear();
        emailField.clear();
        addressField.clear();
        phoneNumberField.setText("1");
    }

    private boolean validateInputFields() {
        return !TherapistsNameField.getText().isEmpty() &&
                !emailField.getText().isEmpty() &&
                !addressField.getText().isEmpty();
    }
}
