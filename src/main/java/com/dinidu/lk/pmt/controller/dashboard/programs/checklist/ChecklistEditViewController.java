package com.dinidu.lk.pmt.controller.dashboard.programs.checklist;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.UserBO;
import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.custom.impl.QueryDAOImpl;

import com.dinidu.lk.pmt.dto.TherapySessionsDTO;
import com.dinidu.lk.pmt.dto.UserDTO;
import com.dinidu.lk.pmt.utils.*;
import com.dinidu.lk.pmt.utils.checklistTypes.ChecklistPriority;
import com.dinidu.lk.pmt.utils.checklistTypes.ChecklistStatus;
import com.dinidu.lk.pmt.utils.customAlerts.CustomAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomDeleteAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.listeners.ChecklistDeletionHandler;
import com.dinidu.lk.pmt.utils.listeners.ChecklistUpdateListener;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class ChecklistEditViewController implements Initializable {
    public Button saveTaskBtn;
    public Button cancelTaskBtn;
    public Button deleteTaskBtn;
    public ComboBox<String> newMembersComboBox;
    @Setter
    private ChecklistDeletionHandler deletionHandler;
    @Setter
    private ChecklistUpdateListener updateListener;
    @FXML
    private Label backToMain;
    @FXML
    private AnchorPane TaskEdit;
    @FXML
    private TextField TaskNameField;
    @FXML
    private ComboBox<ChecklistStatus> checklistStatusComboBox;
    @FXML
    private ComboBox<ChecklistPriority> checklistPriorityComboBox;
    @FXML
    private TextField TaskDescriptionField;
    @FXML
    private DatePicker endDatePicker;
    public TherapySessionsDTO currentChecklist;

    UserBO userBO= (UserBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.USER);
//    ChecklistBO checklistBO = (ChecklistBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.CHECKLISTS);


    QueryDAO queryDAO= new QueryDAOImpl();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<UserDTO> allActiveMembers;
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
        checklistStatusComboBox.getItems().setAll(ChecklistStatus.values());
        checklistPriorityComboBox.getItems().setAll(ChecklistPriority.values());

        if (currentChecklist == null) {
/*            List<ChecklistDTO> checklistDTOList = null;
            try {
                checklistDTOList = checklistBO.getAllChecklists();
            } catch (SQLException e) {
                System.out.println("Error while fetching checklistDTOList: " + e.getMessage());
                CustomErrorAlert.showAlert("Error", "Failed to fetch checklistDTOList.");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            assert checklistDTOList != null;
            if (!checklistDTOList.isEmpty()) {
                currentChecklist = checklistDTOList.get(0);
            } else {
                System.out.println("No Checklists available.");
                return;
            }*/
        }
        loadChecklistData();

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

    public void setChecklistData(TherapySessionsDTO checklist) {
        currentChecklist = checklist;
        loadChecklistData();
    }

    private boolean updateModifiedChecklistFields() {
        boolean isModified = false;
/*

        if (!TaskNameField.getText().trim().equals(currentChecklist.nameProperty().get())) {
            currentChecklist.nameProperty().set(TaskNameField.getText().trim());
            isModified = true;
            System.out.println("Checklist name updated to: " + TaskNameField.getText().trim());
        }

        if (!TaskDescriptionField.getText().trim().equals(currentChecklist.descriptionProperty().get())) {
            currentChecklist.descriptionProperty().set(TaskDescriptionField.getText().trim());
            isModified = true;
            System.out.println("Checklist description updated to: " + TaskDescriptionField.getText().trim());
        }

        ChecklistStatus newStatus = checklistStatusComboBox.getValue();
        if (newStatus != null && !newStatus.equals(currentChecklist.statusProperty().get())) {
            currentChecklist.statusProperty().set(newStatus);
            isModified = true;
            System.out.println("Checklist status updated to: " + newStatus);
        }

        ChecklistPriority newPriority = checklistPriorityComboBox.getValue();
        if (newPriority != null && !newPriority.equals(currentChecklist.priorityProperty().get())) {
            currentChecklist.priorityProperty().set(newPriority);
            isModified = true;
            System.out.println("Checklist priority updated to: " + newPriority);
        }

        LocalDate endDate = endDatePicker.getValue();
        if (endDate != null && (currentChecklist.dueDateProperty().get() == null ||
                !currentChecklist.dueDateProperty().get().toLocalDate().equals(endDate))) {
            currentChecklist.dueDateProperty().set(endDate.atStartOfDay());
            isModified = true;
            System.out.println("Checklist end date updated to: " + endDate);
        }
*/

        return isModified;
    }

    @FXML
    public void saveChecklist() {
        System.out.println("Saving checklist...");
        boolean isModified = updateModifiedChecklistFields();

        if (!isModified) {
            System.out.println("No changes detected, nothing to save.");
            return;
        }

        if (!validateFilledFields()) {
            return;
        }

/*        boolean isChecklistUpdated;
        try {
            isChecklistUpdated = checklistBO.updateChecklist(currentChecklist);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (!isChecklistUpdated) {
            CustomErrorAlert.showAlert("Error", "Failed to update checklist.");
            return;
        }*/

        System.out.println("Checklist updated successfully.");
        CustomAlert.showAlert("Success", "Changes saved successfully.");
        String selectedUser = newMembersComboBox.getValue();
        if (selectedUser != null) {
            handleMemberAssignment(selectedUser);
        }
/*
        if (updateListener != null) {
            updateListener.onChecklistUpdated(currentChecklist);
            System.out.println("updateListener is not null");
        } else {
            System.out.println("updateListener is null");
            CustomErrorAlert.showAlert("Error", "Failed to update Checklist.");
        }*/

        backToMain();
    }


    private void loadChecklistData() {
        System.out.println("Loading Checklist data...");

        if (currentChecklist == null) {
            System.out.println("currentChecklist is null");
            return;
        }

/*
        TaskNameField.setText(currentChecklist.nameProperty().get());
        TaskDescriptionField.setText(currentChecklist.descriptionProperty().get());
        checklistStatusComboBox.setValue(currentChecklist.statusProperty().get());
        checklistPriorityComboBox.setValue(currentChecklist.priorityProperty().get());

        if (currentChecklist.assignedToProperty().get() != 0) {
            String userFullNameById;
            try {
                userFullNameById = userBO.getUserFullNameById(currentChecklist.assignedToProperty().get());
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            newMembersComboBox.setValue(userFullNameById);
        }

        if (currentChecklist.dueDateProperty().get() != null) {
            endDatePicker.setValue(currentChecklist.dueDateProperty().get().toLocalDate());
        } else {
            endDatePicker.setValue(LocalDate.now());
        }
*/
    }

    private void handleMemberAssignment(String selectedUser) {
/*        Long selectedUserId;
        try {
            selectedUserId = userBO.getUserIdByFullName(selectedUser);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Long currentlyAssignedUserId = currentChecklist.assignedToProperty().get();

        if (currentlyAssignedUserId.equals(selectedUserId)) {
            notifyUserReassignment(selectedUser);
            return;
        }

        currentChecklist.assignedToProperty().set(selectedUserId);

        boolean isChecklistUpdated;
        try {
            isChecklistUpdated = checklistBO.updateChecklist(currentChecklist);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (isChecklistUpdated) {
            handleSuccessfulAssignment(selectedUser);
        } else {
            CustomErrorAlert.showAlert("Error", "Failed to assign the user to the checklist.");
        }*/
    }

    private boolean validateFilledFields() {
        boolean hasErrors = false;
        StringBuilder errorMessage = new StringBuilder();

        if (TaskNameField.getText().trim().isEmpty()) {
            errorMessage.append("Checklist name cannot be empty.\n");
            hasErrors = true;
        }

        if (TaskDescriptionField.getText().trim().isEmpty()) {
            errorMessage.append("Checklist description cannot be empty.\n");
            hasErrors = true;
        }

        if (checklistStatusComboBox.getValue() == null) {
            errorMessage.append("Checklist status must be selected.\n");
            hasErrors = true;
        }

        if (checklistPriorityComboBox.getValue() == null) {
            errorMessage.append("Checklist priority must be selected.\n");
            hasErrors = true;
        }

        if (hasErrors) {
            CustomErrorAlert.showAlert("Validation Error", errorMessage.toString());
            System.out.println("Error: " + errorMessage);
            return false;
        }
        return true;
    }

    private void handleSuccessfulAssignment(String selectedUser) {
        CustomAlert.showAlert("SUCCESS", "User successfully assigned.");
        System.out.println("New user assigned: " + selectedUser);
    }

    private void notifyUserReassignment(String selectedUser) {
        System.out.println("User already assigned: " + selectedUser);
    }

    private void validateFields() {
        boolean isValid = !TaskNameField.getText().isEmpty() &&
                !TaskDescriptionField.getText().isEmpty() &&
                endDatePicker.getValue() != null;
        saveTaskBtn.setDisable(!isValid);
    }

    @FXML
    public void deleteChecklist() {
        String username = SessionUser.getLoggedInUsername();
        System.out.println("Deleting checklist for user: " + username);

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
            System.out.println("Access denied: Only Admin, Project Manager, or Product Owner can delete projects.");
            CustomErrorAlert.showAlert("Access Denied", "You do not have permission to delete projects.");
            deleteTaskBtn.setVisible(false);
            return;
        }

        boolean confirmed = CustomDeleteAlert.showAlert(
                (Stage) TaskEdit.getScene().getWindow(),
                "Confirm Deletion",
                "Are you sure you want to delete this checklist? "
        );

/*        if (confirmed) {
            System.out.println("Deleting checklist...");
            try {
                if(checklistBO.deleteChecklist(currentChecklist.idProperty())) {
                    System.out.println("Checklist deleted successfully.");
                    CustomAlert.showAlert("Success", "Checklist deleted successfully.");

                    Stage currentStage = (Stage) TaskEdit.getScene().getWindow();
                    currentStage.close();

                    if (deletionHandler != null) {
                        deletionHandler.onChecklistDeleted();
                    }else {
                        System.out.println("deletionHandler is null");
                    }
                }else {
                    CustomErrorAlert.showAlert("Error", "Failed to delete checklist.");
                    System.out.println("Error while deleting checklist.");
                }
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Checklist deletion canceled by user.");
        }*/
    }

    @FXML
    public void editStatus() {
        System.out.println("Editing checklist status...");
    }

    @FXML
    public void editPriority() {
        System.out.println("Editing checklist priority...");
    }

    @FXML
    public void backToMain() {
        System.out.println("Returning to main...");
        ((Stage) backToMain.getScene().getWindow()).close();
    }

    @FXML
    public void cancelTaskBtnOnClick() {
        System.out.println("Cancelling checklist edit...");
        ((Stage) TaskEdit.getScene().getWindow()).close();
    }

    @FXML
    public void editName() {
        System.out.println("Editing checklist name...");
    }

    @FXML
    public void setEndDate() {
        System.out.println("Setting end date...");
    }

    @FXML
    public void editTaskDesc() {
        System.out.println("Editing checklist description...");
    }

    public void assignNewUserForCurrentTask() {
        System.out.println("Assigning new user for current checklist...");
    }
}
