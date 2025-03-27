package com.dinidu.lk.pmt.controller.dashboard.task.checklist;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.ChecklistBO;
import com.dinidu.lk.pmt.bo.custom.UserBO;
import com.dinidu.lk.pmt.controller.dashboard.ProjectViewController;
import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.custom.impl.QueryDAOImpl;
import com.dinidu.lk.pmt.dto.ChecklistDTO;
import com.dinidu.lk.pmt.dto.UserDTO;
import com.dinidu.lk.pmt.utils.customAlerts.CustomAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.SessionUser;
import com.dinidu.lk.pmt.utils.checklistTypes.ChecklistPriority;
import com.dinidu.lk.pmt.utils.checklistTypes.ChecklistStatus;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;
import javafx.beans.property.LongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ChecklistCreateViewController {
    private static LongProperty taskIdForChecklist;
    public ComboBox<ChecklistPriority> checklistPriorityComboBox;
    public DatePicker deadlineForChecklist;
    @FXML
    private AnchorPane taskChecklistCreatePage;
    public ComboBox<String> selectMemberNameComboBox;
    @FXML
    private TextField checkListNameField;
    @FXML
    private TextArea descriptionIdField;

    UserBO userBO= (UserBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.USER);

    QueryDAO queryDAO= new QueryDAOImpl();
    ChecklistBO checklistBO = (ChecklistBO)
            BOFactory.getInstance().getBO(BOFactory.BOTypes.CHECKLISTS);



    public static void setTaskId(LongProperty id) {
        ChecklistCreateViewController.taskIdForChecklist = id;
    }

    public void initialize() {
        ObservableList<ChecklistPriority> priorityList = FXCollections.observableArrayList(ChecklistPriority.values());
        checklistPriorityComboBox.setItems(priorityList);

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

        selectMemberNameComboBox.setItems(memberNames);

        deadlineForChecklist.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date != null && (date.isBefore(LocalDate.now()) || date.isEqual(LocalDate.now()))) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });
    }

    public void createChecklistClick(ActionEvent actionEvent) {
        userAuthorityCheck();

        if (validateInputFields()) {
            ChecklistDTO checklistDTO = new ChecklistDTO();
            checklistDTO.nameProperty().set(checkListNameField.getText());
            checklistDTO.descriptionProperty().set(descriptionIdField.getText());
            String selectedMemberName = selectMemberNameComboBox.getValue();
            Long assignedTo;
            try {
                assignedTo = userBO.getUserIdByFullName(selectedMemberName);
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            checklistDTO.assignedToProperty().set(assignedTo);
            checklistDTO.taskIdProperty().set(taskIdForChecklist.get());
            checklistDTO.statusProperty().set(ChecklistStatus.PENDING);
            checklistDTO.priorityProperty().set(checklistPriorityComboBox.getValue());

            if (deadlineForChecklist.getValue() != null) {
                checklistDTO.dueDateProperty().set(deadlineForChecklist.getValue().atStartOfDay());
            }

            boolean isSaved;
            try {
                isSaved = checklistBO.insertChecklist(checklistDTO);
                if (isSaved) {
                    System.out.println("Checklist created successfully!");
                    CustomAlert.showAlert("Checklist created", "Checklist created successfully!");
                    ProjectViewController.bindNavigation(taskChecklistCreatePage, "/view/nav-buttons/task/task-create-success-view.fxml");
                    clearContent();
                } else {
                    System.out.println("Error saving checklist.");
                    CustomErrorAlert.showAlert("Error saving Checklist", "Failed to save the Checklist.");
                }
            } catch (Exception e) {
                System.out.println("Error saving Checklist: " + e.getMessage());
                CustomErrorAlert.showAlert("Error saving Checklist", "Error saving Checklist: " + e.getMessage());
            }
        } else {
            System.out.println("Please fill all the required fields.");
            CustomErrorAlert.showAlert("Invalid Input", "Please fill all the required fields.");
        }
    }

    private void userAuthorityCheck() {
        String username = SessionUser.getLoggedInUsername();
        System.out.println("Logged in username Inside Create Checklist: " + username);

        UserRole userRole;
        try {
            userRole = queryDAO.getUserRoleByUsername(username);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if ((userRole != UserRole.ADMIN &&
                userRole != UserRole.RECEPTIONIST )) {
            CustomErrorAlert.showAlert("Access Denied", "You do not have permission to create projects.");

            return;
        }

        String loggedInUsername = SessionUser.getLoggedInUsername();
        Long userIdByUsername;
        try {
            userIdByUsername = userBO.getUserIdByUsername(loggedInUsername);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (userIdByUsername == null) {
            System.out.println("User ID is null for username: " + loggedInUsername);
            CustomErrorAlert.showAlert("User not found", "User not found. Please login again.");
        }
    }

    public void cancelOnClick(ActionEvent actionEvent) {
        if (areFieldsCleared()) {
            ProjectViewController.bindNavigation(taskChecklistCreatePage, "/view/nav-buttons/task-view.fxml");
        } else {
            clearContent();
        }
    }

    private boolean areFieldsCleared() {
        return checkListNameField.getText().isEmpty() &&
                descriptionIdField.getText().isEmpty() &&
                selectMemberNameComboBox.getSelectionModel().isEmpty() &&
                deadlineForChecklist.getValue() == null;
    }

    private void clearContent() {
        checkListNameField.clear();
        descriptionIdField.clear();
        selectMemberNameComboBox.getSelectionModel().clearSelection();
        deadlineForChecklist.setValue(null);
    }

    private boolean validateInputFields() {
        return !checkListNameField.getText().isEmpty() &&
                !descriptionIdField.getText().isEmpty() &&
                !selectMemberNameComboBox.getSelectionModel().isEmpty() &&
                deadlineForChecklist.getValue() != null;
    }
}

