package com.dinidu.lk.pmt.controller.dashboard.task;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.ProgramsBO;
import com.dinidu.lk.pmt.bo.custom.TherapistsBO;
import com.dinidu.lk.pmt.bo.custom.UserBO;
import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.custom.impl.QueryDAOImpl;
import com.dinidu.lk.pmt.dto.TherapyProgramsDTO;
import com.dinidu.lk.pmt.dto.UserDTO;
import com.dinidu.lk.pmt.utils.*;
import com.dinidu.lk.pmt.utils.customAlerts.CustomAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomDeleteAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.listeners.TaskDeletionHandler;
import com.dinidu.lk.pmt.utils.listeners.TaskUpdateListener;
import com.dinidu.lk.pmt.utils.taskTypes.TaskPriority;
import com.dinidu.lk.pmt.utils.taskTypes.TaskStatus;
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
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class TaskEditViewController implements Initializable {
    public Button saveTaskBtn;
    public Button cancelTaskBtn;
    public Button deleteTaskBtn;
    public ComboBox<String> newMembersComboBox;
    @Setter
    private TaskDeletionHandler deletionHandler;
    @Setter
    private TaskUpdateListener updateListener;
    @FXML
    private Label backToMain;
    @FXML
    private AnchorPane TaskEdit;
    @FXML
    private TextField TaskNameField;
    @FXML
    private ComboBox<TaskStatus> TaskStatusCombo;
    @FXML
    private ComboBox<TaskPriority> TaskPriorityCombo;
    @FXML
    private TextField TaskDescriptionField;
    @FXML
    private DatePicker endDatePicker;
    private static TherapyProgramsDTO currentTask;

    UserBO userBO= (UserBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.USER);
    TherapistsBO projectBO =
            (TherapistsBO) BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.THERAPIST);
    ProgramsBO programsBO = (ProgramsBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.PROGRAM);
//    TeamAssignmentBO teamAssignmentBO = (TeamAssignmentBO)
//            BOFactory.getInstance().
//                    getBO(BOFactory.BOTypes.TEAM_ASSIGNMENTS);

    QueryDAO queryDAO= new QueryDAOImpl();

    public static void setTask(TherapyProgramsDTO task) {
        currentTask = task;
    }

    private boolean validateFilledFields() {
        boolean hasErrors = false;
        StringBuilder errorMessage = new StringBuilder();

        if (!TaskNameField.getText().isEmpty() && TaskNameField.getText().trim().isEmpty()) {
            errorMessage.append("Task name cannot be empty.\n");
            hasErrors = true;
        }

        if (!TaskDescriptionField.getText().isEmpty() && TaskDescriptionField.getText().trim().isEmpty()) {
            errorMessage.append("Task description cannot be empty.\n");
            hasErrors = true;
        }

        if (TaskStatusCombo.getValue() != null && !isValidTaskStatus(TaskStatusCombo.getValue())) {
            errorMessage.append("Invalid status selected.\n");
            hasErrors = true;
        }

        if (TaskPriorityCombo.getValue() != null && !isValidTaskPriority(TaskPriorityCombo.getValue())) {
            errorMessage.append("Invalid priority selected.\n");
            hasErrors = true;
        }

        if (hasErrors) {
            CustomErrorAlert.showAlert("Validation Error", errorMessage.toString());
            return false;
        }
        return true;
    }

    private boolean isValidTaskStatus(TaskStatus status) {
        return status != null && Arrays.asList(TaskStatus.values()).contains(status);
    }

    private boolean isValidTaskPriority(TaskPriority priority) {
        return priority != null && Arrays.asList(TaskPriority.values()).contains(priority);
    }

    private boolean updateModifiedTaskFields() {
        boolean isModified = false;

        if (!TaskNameField.getText().isEmpty() &&
                !TaskNameField.getText().equals(currentTask.nameProperty().get())) {
            currentTask.nameProperty().set(TaskNameField.getText());
            isModified = true;
        }
        return isModified;
    }

    @FXML
    public void saveTask() {
        System.out.println("Saving task...");

        if (!validateFilledFields()) {
            return;
        }

        boolean isTaskModified = updateModifiedTaskFields();

        if (isTaskModified) {
            try {
                programsBO.updateProgram(currentTask);
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Task updated successfully.");
        }

        String selectedUser = newMembersComboBox.getValue();
        if (selectedUser != null) {
            handleTeamAssignment(selectedUser);
        }

        if (updateListener != null) {
            updateListener.onTaskUpdated(currentTask);
        }

        CustomAlert.showAlert("Success", "Changes saved successfully.");
        backToMain();
    }

    private void handleTeamAssignment(String selectedUser) {
/*        try {
            Long userIdByFullName;
            try {
                userIdByFullName = userBO.getUserIdByFullName(selectedUser);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            List<Long> existingUserIds = teamAssignmentBO.getTeamMemberIdsByTask(currentTask.idProperty().get());
            System.out.println("\n================= TASK EDIT VIEW =================\n");
            System.out.println("Existing user ids: " + existingUserIds);
            System.out.println("\n================= TASK EDIT VIEW =================\n");
            if (!existingUserIds.contains(userIdByFullName)) {
                TeamAssignmentDTO newAssignment = createTeamAssignment(userIdByFullName);
                if (teamAssignmentBO.saveAssignment(newAssignment)) {
                    System.out.println("\n===== Team assignment saved successfully.====\n");
                    handleSuccessfulAssignment(selectedUser);
                } else {
                    CustomErrorAlert.showAlert("Error", "Failed to save team assignment.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while handling team assignment: " + e.getMessage());
            CustomErrorAlert.showAlert("Error", "Database error while saving assignment.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }*/
    }




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

/*        List<UserDTO> allActiveMembers;
        try {
            allActiveMembers = queryDAO.getAllActiveMembersNames();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        ObservableList<String> memberNames = FXCollections.observableArrayList();
        for (UserDTO member : allActiveMembers) {
            memberNames.add(member.getFullName());
        }

        newMembersComboBox.setItems(memberNames);
        TaskStatusCombo.getItems().setAll(TaskStatus.values());
        TaskPriorityCombo.getItems().setAll(TaskPriority.values());*/

        if (currentTask == null) {
            List<TherapyProgramsDTO> tasks;
            try {
                tasks = programsBO.getAllPrograms();
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            if (!tasks.isEmpty()) {
                currentTask = tasks.get(0);
            } else {
                System.out.println("No tasks available.");
                return;
            }
        }
        loadTaskData();

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

        TaskNameField.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        TaskDescriptionField.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
    }

    private void loadTaskData() {
        System.out.println("Loading Task data...");

        if (currentTask == null) {
            System.out.println("currentTask is null");
            return;
        }

        TaskNameField.setText(currentTask.nameProperty().get() != null ? currentTask.nameProperty().get() : "");

    }

    private void validateFields() {
        boolean isValid = !TaskNameField.getText().isEmpty() &&
                !TaskDescriptionField.getText().isEmpty() &&
                endDatePicker.getValue() != null;
        saveTaskBtn.setDisable(!isValid);
    }

    @FXML
    public void backToMain() {
        System.out.println("Returning to main...");
        ((Stage) backToMain.getScene().getWindow()).close();
    }

    @FXML
    public void cancelTaskBtnOnClick(ActionEvent actionEvent) {
        System.out.println("Cancelling task edit...");
        ((Stage) TaskEdit.getScene().getWindow()).close();
    }

    @FXML
    public void editName(ActionEvent actionEvent) {
        System.out.println("Editing task name...");
    }

    @FXML
    public void deleteTask(ActionEvent actionEvent) {
        String username = SessionUser.getLoggedInUsername();
        System.out.println("Deleting task for user: " + username);

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
                userRoleByUsername != UserRole.RECEPTIONIST){
            System.out.println("Access denied: Only Admin, Project Manager, or Product Owner can delete projects.");
            CustomErrorAlert.showAlert("Access Denied", "You do not have permission to delete projects.");
            deleteTaskBtn.setVisible(false);
            return;
        }

        boolean confirmed = CustomDeleteAlert.showAlert(
                (Stage) TaskEdit.getScene().getWindow(),
                "Confirm Deletion",
                "Are you sure you want to delete this task? "
        );

        if (confirmed) {
            System.out.println("Deleting task...");
  /*          try {
                boolean b = programsBO.deleteProgram(currentTask.getName().get());
                if (!b) {
                    System.out.println("Task deletion failed.");
                    CustomErrorAlert.showAlert("Error", "Task deletion failed.");
                }
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }*/
            System.out.println("Task deleted successfully.");
            CustomAlert.showAlert("Success", "Task deleted successfully.");

            Stage currentStage = (Stage) TaskEdit.getScene().getWindow();
            currentStage.close();

            if (deletionHandler != null) {
                deletionHandler.onTaskDeleted();
            }
        } else {
            System.out.println("Task deletion canceled by user.");
        }
    }

    @FXML
    public void editStatus(ActionEvent actionEvent) {
        System.out.println("Editing task status...");
    }

    @FXML
    public void editPriority(ActionEvent actionEvent) {
        System.out.println("Editing task priority...");
    }

    @FXML
    public void setEndDate(ActionEvent actionEvent) {
        System.out.println("Setting end date...");
    }

    @FXML
    public void editTaskDesc(ActionEvent actionEvent) {
        System.out.println("Editing task description...");
    }

    public void assignNewUserForCurrentTask(ActionEvent actionEvent) {
        System.out.println("Assigning new user for current task...");
    }
}
