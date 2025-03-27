package com.dinidu.lk.pmt.controller.dashboard.project;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.ProjectsBO;
import com.dinidu.lk.pmt.bo.custom.UserBO;
import com.dinidu.lk.pmt.controller.dashboard.ProjectViewController;
import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.custom.impl.QueryDAOImpl;
import com.dinidu.lk.pmt.dto.ProjectDTO;
import com.dinidu.lk.pmt.utils.projectTypes.ProjectPriority;
import com.dinidu.lk.pmt.utils.projectTypes.ProjectStatus;
import com.dinidu.lk.pmt.utils.projectTypes.ProjectVisibility;
import com.dinidu.lk.pmt.utils.customAlerts.CustomAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.SessionUser;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.sql.SQLException;
import java.util.Date;

public class CreateProjectViewController {
    @FXML
    private AnchorPane projectCreatePg;

    @FXML
    private TextField projectNameField;

    @FXML
    private TextField projectIdField;

    @FXML
    private TextField startingIdField;

    @FXML
    private TextArea descriptionIdField;

    UserBO userBO =
            (UserBO) BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.USER);

    ProjectsBO projectsBO =
            (ProjectsBO) BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.PROJECTS);

    QueryDAO queryDAO = new QueryDAOImpl();


    public void initialize() {
        startingIdField.setText("1");
        startingIdField.setDisable(true);
        projectIdField.setDisable(true);
        projectNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            generateProjectId(newValue);
            updateFullProjectId();
        });

        startingIdField.textProperty().addListener((observable, oldValue, newValue) -> updateFullProjectId());
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

        projectIdField.setText(projectId.length() > 2 ? projectId.substring(0, 2) : projectId.toString());
    }

    private void updateFullProjectId() {
        String projectId = projectIdField.getText();
        System.out.println("Project ID: " + projectId);
        String startingId = startingIdField.getText();
        String fullProjectId = projectId + "-00" + startingId;

        try {
            if (projectsBO.isProjectIdTaken(fullProjectId).isPresent()) {
                System.out.println("Project ID already exists: " + fullProjectId);
                int newStartingId = Integer.parseInt(startingId) + 1;
                startingIdField.setText(String.valueOf(newStartingId));
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Full Project ID: " + fullProjectId);
    }


    public void createProjectClick(ActionEvent actionEvent) {
        String username = SessionUser.getLoggedInUsername();
        System.out.println("Logged in username Inside Create Project: " + username);
        UserRole userRole;
        try {
            userRole = queryDAO.getUserRoleByUsername(username);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if ((userRole != UserRole.ADMIN &&
                userRole != UserRole.RECEPTIONIST)) {
            CustomErrorAlert.showAlert("Access Denied", "You do not have permission to create projects.");
            return;
        }

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
            projectDTO.setId(projectIdField.getText() + "-00" + startingIdField.getText());
            projectDTO.setName(projectNameField.getText());
            projectDTO.setDescription(descriptionIdField.getText());
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
                    ProjectViewController.bindNavigation(projectCreatePg, "/view/nav-buttons/project-view.fxml");
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
            ProjectViewController.bindNavigation(projectCreatePg, "/view/nav-buttons/project-view.fxml");
        } else {
            clearContent();
        }
    }

    private boolean areFieldsCleared() {
        return projectNameField.getText().isEmpty() &&
                projectIdField.getText().isEmpty() &&
                descriptionIdField.getText().isEmpty();
    }

    private void clearContent() {
        projectNameField.clear();
        projectIdField.clear();
        descriptionIdField.clear();
        startingIdField.setText("1");
    }

    private boolean validateInputFields() {
        return !projectNameField.getText().isEmpty() &&
                !projectIdField.getText().isEmpty() &&
                !descriptionIdField.getText().isEmpty();
    }
}
