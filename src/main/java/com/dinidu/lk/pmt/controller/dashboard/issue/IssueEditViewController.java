package com.dinidu.lk.pmt.controller.dashboard.issue;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.PatientBO;
import com.dinidu.lk.pmt.bo.custom.UserBO;
import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.custom.impl.QueryDAOImpl;
import com.dinidu.lk.pmt.dto.PatientsDTO;
import com.dinidu.lk.pmt.utils.*;
import com.dinidu.lk.pmt.utils.customAlerts.CustomAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomDeleteAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.issuesTypes.IssuePriority;
import com.dinidu.lk.pmt.utils.issuesTypes.IssueStatus;
import com.dinidu.lk.pmt.utils.listeners.IssueDeletionHandler;
import com.dinidu.lk.pmt.utils.listeners.IssueUpdateListener;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.Setter;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class IssueEditViewController implements Initializable {
    public Button saveIssueBtn;
    public Button cancelProjectBtn;
    public Button deleteIssueBtn;
    public ComboBox<String> selectTaskNameComboBox;
    public ComboBox<String> selectMemberNameComboBox;
    public ComboBox<String> selectProjectNameComboBox;
    public DatePicker dueDate;
    @Setter
    private IssueDeletionHandler deletionHandler;
    @Setter
    private IssueUpdateListener updateListener;
    @FXML
    private Label backToMain;
    @FXML
    private AnchorPane projectEdit;
    @FXML
    private ComboBox<IssueStatus> issueStatusComboBox;
    @FXML
    private ComboBox<IssuePriority> issuePriorityComboBox;
    @FXML
    private TextField issueDescriptionField;
    private static PatientsDTO currentIssue;

    UserBO userBO= (UserBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.USER);
    PatientBO issuesBO =
            (PatientBO) BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.PATIENTS);

    QueryDAO queryDAO = new QueryDAOImpl();


    public static void setIssue(PatientsDTO patientsDTO) {
        currentIssue = patientsDTO;
    }

    @FXML
    public void saveIssue() {
        System.out.println("Saving Issue...");

        if (issueDescriptionField.getText().isEmpty()) {
            CustomErrorAlert.showAlert("Error", "Issue description cannot be empty.");
            return;
        }

/*        currentIssue.setDescription(issueDescriptionField.getText());
        currentIssue.setStatus(issueStatusComboBox.getValue());
        currentIssue.setPriority(issuePriorityComboBox.getValue());

        try {
            currentIssue.setProjectId(issuesBO.getTherapistsIdByName(selectProjectNameComboBox.getValue()));
        } catch (SQLException e) {
            System.out.println("Error retrieving project ID: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            String selectedTaskName = selectTaskNameComboBox.getValue();
            if (selectedTaskName != null) {
                Long taskId = issuesBO.getProgramIdByName(selectedTaskName);
                if (taskId != null) {
                    currentIssue.setTaskId(taskId);
                } else {
                    CustomErrorAlert.showAlert("Error", "Selected task does not exist.");
                    return;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving task ID: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            currentIssue.setAssignedTo(userBO.getUserIdByFullName(selectMemberNameComboBox.getValue()));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (dueDate.getValue() != null) {
            currentIssue.setDueDate(Date.valueOf(dueDate.getValue()));
        } else {
            currentIssue.setDueDate(null);
        }

        currentIssue.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        currentIssue.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        try {
            boolean issueUpdated = issuesBO.updatePatient(currentIssue);

            if (issueUpdated) {
                System.out.println("Issue saved successfully.");

                if (updateListener != null) {
                    System.out.println("Updating listener...");
                    updateListener.onIssueUpdated(currentIssue);
                }

                System.out.println("Saving issue to database...");
                CustomAlert.showAlert("Success", "Issue saved successfully.");
                System.out.println("Returning to main...");
                backToMain();
            } else {
                System.out.println("Failed to save issue.");
                CustomErrorAlert.showAlert("Error", "Failed to save issue.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }*/
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeProjectComboBox();
        initializeTaskComboBox();
        initializeMemberComboBox();
        issueStatusComboBox.getItems().setAll(IssueStatus.values());
        issuePriorityComboBox.getItems().setAll(IssuePriority.values());

        if (currentIssue == null) {
            List<PatientsDTO> issues = null;

            try {
                issues = issuesBO.getAllPatients();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            if (issues != null && !issues.isEmpty()) {
                currentIssue = issues.get(0);
            } else {
                System.out.println("No issues available.");
                return;
            }
        }
        loadIssueData();

        dueDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date != null && (date.isBefore(LocalDate.now()) || date.isEqual(LocalDate.now()))) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });

        issueDescriptionField.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
    }

    private void initializeProjectComboBox() {
        ObservableList<String> projectNames = FXCollections.observableArrayList();
        try {
            List<String> projects = issuesBO.getActiveTherapistsNames();  // Assuming getActiveProjectNames is refactored to return List<String>
            projectNames.addAll(projects);
            selectProjectNameComboBox.setItems(projectNames);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    private void initializeTaskComboBox() {
        selectProjectNameComboBox.setOnAction(event -> {
            String selectedProject = selectProjectNameComboBox.getValue();
            if (selectedProject != null) {
                ObservableList<String> taskNames = FXCollections.observableArrayList();
                try {
                    List<String> tasks = issuesBO.getProgramsByTherapist(selectedProject);  // Assuming getTasksByProject is refactored to return List<String>
                    taskNames.addAll(tasks);
                    selectTaskNameComboBox.setItems(taskNames);
                } catch (SQLException | ClassNotFoundException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        });
    }

    private void initializeMemberComboBox() {
        ObservableList<String> memberNames = FXCollections.observableArrayList();
        try {
            List<String> members = issuesBO.getActivePatients();  // This now returns a List<String>
            memberNames.addAll(members);  // Add all member names to the ObservableList
            selectMemberNameComboBox.setItems(memberNames);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    private void loadIssueData() {
        System.out.println("Loading Issue data...");

        if (currentIssue == null) {
            System.out.println("currentIssue is null");
            return;
        }

/*        issueDescriptionField.setText(currentIssue.getDescription() != null ? currentIssue.getDescription() : "");
        issueStatusComboBox.setValue(currentIssue.getStatus());
        issuePriorityComboBox.setValue(currentIssue.getPriority());

        try {
            String projectId = currentIssue.getProjectId();
            if (projectId != null) {
                String projectName = issuesBO.getTherapistNameById(projectId);
                selectProjectNameComboBox.setValue(projectName);
            }

            long taskId = currentIssue.getTaskId();
            if (taskId != 0) {
                String taskName = issuesBO.getProgramNameById(taskId);
                selectTaskNameComboBox.setValue(taskName);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving project or task data: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        long assignedTo = currentIssue.getAssignedTo();
        if (assignedTo != 0) {
            String assigneeName;
            try {
                assigneeName = userBO.getUserFullNameById(assignedTo);
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            selectMemberNameComboBox.setValue(assigneeName);
        }

        if (currentIssue.getDueDate() != null) {
            dueDate.setValue(currentIssue.getDueDate().toLocalDate());
        } else {
            dueDate.setValue(null);
        }*/
    }


    private void validateFields() {
        boolean isValid = !issueDescriptionField.getText().isEmpty();
        saveIssueBtn.setDisable(!isValid);
    }

    @FXML
    public void backToMain() {
        System.out.println("Returning to main...");
        ((Stage) backToMain.getScene().getWindow()).close();
    }

    @FXML
    public void cancelIssueBtnOnClick(ActionEvent actionEvent) {
        System.out.println("Cancelling project edit...");
        ((Stage) projectEdit.getScene().getWindow()).close();
    }

    @FXML
    public void deleteIssue() {
        String username = SessionUser.getLoggedInUsername();
        System.out.println("Deleting Issue for user: " + username);

        if (username == null) {
            System.out.println("User not logged in. username: " + null);
        }

        UserRole userRoleByUsername;
        try {
            userRoleByUsername = queryDAO.getUserRoleByUsername(username);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (userRoleByUsername == null) {
            System.out.println("User not logged in. userRoleByUsername: " + null);
            return;
        }

        System.out.println("User role: " + userRoleByUsername);

        if (userRoleByUsername != UserRole.ADMIN &&
                userRoleByUsername != UserRole.RECEPTIONIST) {
            System.out.println("Access denied: Only Admin, Project Manager, or Product Owner can delete Issue.");
            CustomErrorAlert.showAlert("Access Denied", "You do not have permission to delete Issue.");
            deleteIssueBtn.setVisible(false);
            return;
        }

        boolean confirmed = CustomDeleteAlert.showAlert(
                (Stage) projectEdit.getScene().getWindow(),
                "Confirm Deletion",
                "Are you sure you want to delete this issue? "
        );

        if (confirmed) {
            System.out.println("Deleting issue...");
            boolean isDeleted;
            try {
                isDeleted = issuesBO.deletePatient(currentIssue.getId());
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            if (isDeleted) {
                System.out.println("Issue deleted successfully.");
                CustomAlert.showAlert("Success", "Issue deleted successfully.");

                Stage currentStage = (Stage) projectEdit.getScene().getWindow();
                currentStage.close();

                if (deletionHandler != null) {
                    deletionHandler.onIssueDeleted();
                }
            } else {
                System.out.println("Issue deletion failed.");
                CustomErrorAlert.showAlert("Error", "Failed to delete issue.");
            }
        } else {
            System.out.println("Issue deletion canceled by user.");
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
    public void editIssueDesc(ActionEvent actionEvent) {
        System.out.println("Editing project description...");
    }

    public void editTaskName(ActionEvent actionEvent) {
        System.out.println("Editing task name...");
    }

    public void editMember(ActionEvent actionEvent) {
        System.out.println("Editing member...");
    }

    public void editProjectName(ActionEvent actionEvent) {
        System.out.println("Editing project name...");
    }

    public void setDueDate(ActionEvent actionEvent) {
        System.out.println("Setting due date...");
    }
}
