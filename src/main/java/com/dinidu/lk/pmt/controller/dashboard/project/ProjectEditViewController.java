package com.dinidu.lk.pmt.controller.dashboard.project;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.TherapistsBO;
import com.dinidu.lk.pmt.bo.custom.UserBO;
import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.custom.impl.QueryDAOImpl;
import com.dinidu.lk.pmt.dto.ProjectDTO;
import com.dinidu.lk.pmt.utils.*;
import com.dinidu.lk.pmt.utils.customAlerts.CustomAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomDeleteAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.listeners.ProjectDeletionHandler;
import com.dinidu.lk.pmt.utils.listeners.ProjectUpdateListener;
import com.dinidu.lk.pmt.utils.projectTypes.ProjectPriority;
import com.dinidu.lk.pmt.utils.projectTypes.ProjectStatus;
import com.dinidu.lk.pmt.utils.projectTypes.ProjectVisibility;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.Setter;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class ProjectEditViewController implements Initializable {
    public Button saveProjectBtn;
    @Setter
    private ProjectDeletionHandler deletionHandler;
    @Setter
    private ProjectUpdateListener updateListener;
    @FXML
    private Label backToMain;
    @FXML
    private AnchorPane projectEdit;
    @FXML
    private TextField projectNameField;
    @FXML
    private ComboBox<ProjectStatus> projectStatusCombo;
    @FXML
    private ComboBox<ProjectPriority> projectPriorityCombo;
    @FXML
    private ComboBox<ProjectVisibility> projectVisibilityCombo;
    @FXML
    private TextField projectDescriptionField;
    @FXML
    private DatePicker endDatePicker;
    private static ProjectDTO currentProject;

    UserBO userBO= (UserBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.USER);

    TherapistsBO therapistsBO = (TherapistsBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.TherapistsBO);

    QueryDAO queryDAO = new QueryDAOImpl();


    public static void setProject(ProjectDTO project) {
        currentProject = project;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        projectStatusCombo.getItems().setAll(ProjectStatus.values());
        projectPriorityCombo.getItems().setAll(ProjectPriority.values());
        projectVisibilityCombo.getItems().setAll(ProjectVisibility.values());

        if (currentProject == null) {
            List<ProjectDTO> projects;
            try {
                projects = therapistsBO.getAllTherapists();
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            if (projects != null && !projects.isEmpty()) {
                currentProject = projects.get(0);
            } else {
                System.out.println("No projects available.");
                return;
            }
        }

        loadProjectData();

        endDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date != null && (date.isBefore(LocalDate.now()) || date.isEqual(LocalDate.now()))) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });

        projectNameField.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        projectDescriptionField.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
    }

    private void loadProjectData() {
        System.out.println("Loading project data...");

        if (currentProject == null) {
            System.out.println("currentProject is null");
            return;
        }

        projectNameField.setText(currentProject.getName() != null ? currentProject.getName() : "");
        projectDescriptionField.setText(currentProject.getDescription() != null ? currentProject.getDescription() : "");
        projectStatusCombo.setValue(currentProject.getStatus());
        projectPriorityCombo.setValue(currentProject.getPriority());
        projectVisibilityCombo.setValue(currentProject.getVisibility());

        if (currentProject.getEndDate() != null) {
            endDatePicker.setValue(new java.sql.Date(currentProject.getEndDate().getTime())
                    .toLocalDate());
        } else {
            System.out.println("endDate is null");
            endDatePicker.setValue(null);
        }
    }

    @FXML
    public void saveProject() {
        System.out.println("Saving project...");

        if (projectNameField.getText().isEmpty() || projectDescriptionField.getText().isEmpty()) {
            CustomErrorAlert.showAlert("Error", "Project name and description cannot be empty.");
            return;
        }

        currentProject.setName(projectNameField.getText());
        currentProject.setDescription(projectDescriptionField.getText());
        currentProject.setStatus(projectStatusCombo.getValue());
        currentProject.setPriority(projectPriorityCombo.getValue());
        currentProject.setVisibility(projectVisibilityCombo.getValue());

        LocalDate endDate = endDatePicker.getValue();
        if (endDate != null) {
            currentProject.setEndDate(java.sql.Date.valueOf(endDate));
        } else {
            currentProject.setEndDate(null);
        }

        try {
            therapistsBO.updateTherapist(currentProject);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Project saved successfully.");

        if (updateListener != null) {
            updateListener.onProjectUpdated(currentProject);
        }

        CustomAlert.showAlert("Success", "Project saved successfully.");
        backToMain();
    }

    private void validateFields() {
        boolean isValid = !projectNameField.getText().isEmpty() && !projectDescriptionField.getText().isEmpty();
        saveProjectBtn.setDisable(!isValid);
    }

    @FXML
    public void backToMain() {
        System.out.println("Returning to main...");
        ((Stage) backToMain.getScene().getWindow()).close();
    }

    @FXML
    public void cancelProjectBtnOnClick(ActionEvent actionEvent) {
        System.out.println("Cancelling project edit...");
        ((Stage) projectEdit.getScene().getWindow()).close();
    }

    @FXML
    public void editName(ActionEvent actionEvent) {
        System.out.println("Editing project name...");
    }


    @FXML
    public void deleteProject(ActionEvent actionEvent) {
        String username = SessionUser.getLoggedInUsername();
        System.out.println("Deleting project for user: " + username);

        if (username == null) {
            System.out.println("User not logged in. username: " + username);
        }

        UserRole userRoleByUsername;
        try {
            userRoleByUsername = queryDAO.getUserRoleByUsername(username);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (userRoleByUsername == null) {
            System.out.println("User not logged in. userRoleByUsername: " + userRoleByUsername);
            return;
        }

        System.out.println("User role: " + userRoleByUsername);

        if (userRoleByUsername != UserRole.ADMIN &&
                userRoleByUsername != UserRole.RECEPTIONIST) {
            System.out.println("Access denied: Only Admin, Project Manager, or Product Owner can delete projects.");
            CustomErrorAlert.showAlert("Access Denied", "You do not have permission to delete projects.");
            return;
        }

        boolean confirmed = CustomDeleteAlert.showAlert(
                (Stage) projectEdit.getScene().getWindow(),
                "Confirm Deletion",
                "Are you sure you want to delete this project? "
        );

        if (confirmed) {
            System.out.println("Deleting project...");
            try {
                boolean b = therapistsBO.deleteTherapists(currentProject.getId());
                if(!b){
                    System.out.println("Deletion failed.");
                    CustomErrorAlert.showAlert("Failed","Project deletion failed.");
                }
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Project deleted successfully.");
            CustomAlert.showAlert("Success", "Project deleted successfully.");

            Stage currentStage = (Stage) projectEdit.getScene().getWindow();
            currentStage.close();

            if (deletionHandler != null) {
                deletionHandler.onProjectDeleted();
            }
        } else {
            System.out.println("Project deletion canceled by user.");
        }
    }

    @FXML
    public void editStatus(ActionEvent actionEvent) {
        System.out.println("Editing project status...");
    }

    @FXML
    public void editPriority(ActionEvent actionEvent) {
        System.out.println("Editing project priority...");
    }

    @FXML
    public void editVisibility(ActionEvent actionEvent) {
        System.out.println("Editing project visibility...");
    }

    @FXML
    public void setEndDate(ActionEvent actionEvent) {
        System.out.println("Setting end date...");
    }

    @FXML
    public void editProjectDesc(ActionEvent actionEvent) {
        System.out.println("Editing project description...");
    }
}
