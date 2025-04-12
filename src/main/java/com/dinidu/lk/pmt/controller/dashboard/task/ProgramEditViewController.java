package com.dinidu.lk.pmt.controller.dashboard.task;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.ProgramsBO;
import com.dinidu.lk.pmt.bo.custom.TherapistsBO;
import com.dinidu.lk.pmt.bo.custom.UserBO;
import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.custom.impl.QueryDAOImpl;
import com.dinidu.lk.pmt.dto.TherapyProgramsDTO;
import com.dinidu.lk.pmt.utils.*;
import com.dinidu.lk.pmt.utils.customAlerts.CustomAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomDeleteAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.listeners.TaskDeletionHandler;
import com.dinidu.lk.pmt.utils.listeners.TaskUpdateListener;
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
import java.util.List;
import java.util.ResourceBundle;

public class ProgramEditViewController implements Initializable {
    public Button saveTaskBtn;
    public Button cancelTaskBtn;
    public Button deleteTaskBtn;
    public ComboBox<String> DurationCombo;
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
    private TextField TaskDescriptionField;
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

    QueryDAO queryDAO= new QueryDAOImpl();

    public static void setTask(TherapyProgramsDTO task) {
        currentTask = task;
    }

    private boolean updateModifiedTaskFields() {
        boolean isModified = false;

        // Check Task Name
        String newName = TaskNameField.getText();
        if (newName != null && !newName.equals(currentTask.nameProperty().get())) {
            currentTask.nameProperty().set(newName);
            isModified = true;
        }

        // Check Task Description (Fee)
        String newFee = TaskDescriptionField.getText();
        if (newFee != null && !newFee.equals(currentTask.feeProperty().get())) {
            currentTask.feeProperty().set(newFee);
            isModified = true;
        }

        // Check Duration
        String newDuration = DurationCombo.getValue();
        if (newDuration != null && !newDuration.equals(currentTask.durationProperty().get())) {
            currentTask.durationProperty().set(newDuration);
            isModified = true;
        }

        return isModified;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (currentTask == null) {
            List<TherapyProgramsDTO> tasks;
            try {
                tasks = programsBO.getAllPrograms();
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            if (!tasks.isEmpty()) {
                currentTask = tasks.get(0);
                System.out.println("\nNo task selected. Defaulting to first task: " + currentTask.getProgramId());
                System.out.println("Task ID: " + currentTask.getId());
            } else {
                System.out.println("No tasks available.");
                return;
            }
        }
        loadTaskData();
        System.out.println("Program :  " + currentTask);
        TaskNameField.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        TaskDescriptionField.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        DurationCombo.getItems().addAll("1 weeks", "2 weeks", "3 weeks", "1 Month", "2 Months", "3 Months", "4 Months", "5 Months", "6 Months");
    }

    private void loadTaskData() {
        System.out.println("Loading Task data...");

        if (currentTask == null) {
            System.out.println("currentTask is null");
            return;
        }

        TaskNameField.setText(currentTask.nameProperty().get() != null ? currentTask.nameProperty().get() : "");
        TaskDescriptionField.setText(currentTask.feeProperty().get() != null ? currentTask.feeProperty().get() : "");
        DurationCombo.setValue(currentTask.durationProperty().get() != null ? currentTask.durationProperty().get() : "");
        System.out.println("Program Name: " + TaskNameField.getText());
        System.out.println("Program ID: " + currentTask.getProgramId());
        System.out.println("Program Fee: " + TaskDescriptionField.getText());
        System.out.println("Program Duration: " + DurationCombo.getValue());
        System.out.println("Program data loaded successfully.");
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
                if( currentTask == null) {
                    System.out.println("currentTask is null");
                    return;
                }

                System.out.println("\nUpdating task: " + currentTask.getName());
                System.out.println("Task Name: " + currentTask.getName());
                System.out.println("Task ID: " + currentTask.getId());
                System.out.println("Task Fee: " + currentTask.getFee()+"\n");

                currentTask.setId(currentTask.getId());
                programsBO.updateProgram(currentTask);
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Task updated successfully.");
        }

        if (updateListener != null) {
            updateListener.onTaskUpdated(currentTask);
        }

        CustomAlert.showAlert("Success", "Changes saved successfully.");
        backToMain();
    }

    private void validateFields() {
        boolean isValid = !TaskNameField.getText().isEmpty() &&
                !TaskDescriptionField.getText().isEmpty();
        saveTaskBtn.setDisable(!isValid);
    }

    private boolean validateFilledFields() {
        StringBuilder errorMessage = new StringBuilder();
        boolean hasErrors = false;

        String taskName = TaskNameField.getText();
        String taskDescription = TaskDescriptionField.getText();
        String duration = DurationCombo.getValue();

        if (taskName == null || taskName.trim().isEmpty()) {
            errorMessage.append("Task name cannot be empty.\n");
            hasErrors = true;
        }

        if (taskDescription == null || taskDescription.trim().isEmpty()) {
            errorMessage.append("Task description cannot be empty.\n");
            hasErrors = true;
        }

        if (duration == null || duration.trim().isEmpty()) {
            errorMessage.append("Please select a duration.\n");
            hasErrors = true;
        }

        if (hasErrors) {
            CustomErrorAlert.showAlert("Validation Error", errorMessage.toString());
            return false;
        }

        return true;
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
    public void editName(ActionEvent actionEvent) {
        System.out.println("Editing task name...");
    }
    @FXML
    public void editPriority(ActionEvent actionEvent) {
        System.out.println("Editing task priority...");
    }
    @FXML
    public void editTaskDesc(ActionEvent actionEvent) {
        System.out.println("Editing task description...");
    }

}
