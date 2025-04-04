package com.dinidu.lk.pmt.controller.dashboard.task;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.ProgramsBO;
import com.dinidu.lk.pmt.bo.custom.TherapistsBO;
import com.dinidu.lk.pmt.bo.custom.UserBO;
import com.dinidu.lk.pmt.controller.dashboard.TherapistsViewController;
import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.custom.impl.QueryDAOImpl;
import com.dinidu.lk.pmt.dto.ProjectDTO;
import com.dinidu.lk.pmt.dto.ProgramsDTO;
import com.dinidu.lk.pmt.dto.UserDTO;
import com.dinidu.lk.pmt.utils.*;
import com.dinidu.lk.pmt.utils.customAlerts.CustomAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.taskTypes.TaskPriority;
import com.dinidu.lk.pmt.utils.taskTypes.TaskStatus;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class CreateTaskViewController {

    @FXML
    private AnchorPane taskCreatePage;
    public ComboBox<String> selectMemberNameComboBox;
    public ComboBox<String> selectProjectNameComboBox;
    @FXML
    private DatePicker taskDeadline;

    @FXML
    private TextField taskNameField;

    @FXML
    private TextArea descriptionIdField;

    UserBO userBO= (UserBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.USER);
    TherapistsBO therapistsBO =
            (TherapistsBO) BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.TherapistsBO);
    ProgramsBO programsBO =
            (ProgramsBO) BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.ProgramsBO);
    QueryDAO queryDAO= new QueryDAOImpl();


    public void initialize() {
        List<UserDTO> allActiveMembers;
        try {
            allActiveMembers = queryDAO.getAllActiveMembersNames();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        List<ProjectDTO> allProjects;
        try {
            allProjects = therapistsBO.getAllTherapists();
            System.out.println("All projects: " + allProjects);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        ObservableList<String> memberNames = FXCollections.observableArrayList();
        ObservableList<String> projectNames = FXCollections.observableArrayList();

        for (UserDTO member : allActiveMembers) {
            memberNames.add(member.getFullName());
        }

        assert allProjects != null;
        for (ProjectDTO project : allProjects) {
            System.out.println("Project name: " + project.getName());
            projectNames.add(project.getName());
        }

        selectMemberNameComboBox.setItems(memberNames);
        selectProjectNameComboBox.setItems(projectNames);

        taskDeadline.setDayCellFactory(picker -> new DateCell() {
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

    public void createTaskClick() {
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
        Long userIdByUsername;
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
            ProgramsDTO programsDTO = new ProgramsDTO();
            programsDTO.nameProperty().set(taskNameField.getText());
            programsDTO.descriptionProperty().set(descriptionIdField.getText());

            String selectedProjectName = selectProjectNameComboBox.getValue();
            System.out.println("===========Create Task View===========");
            System.out.println("Selected Project Name: " + selectedProjectName);

            if (selectedProjectName == null) {
                CustomErrorAlert.showAlert("Project not found", "Selected Project Name is null");
                return;
            }

            String projectId;
            try {
                projectId = therapistsBO.getTherapistIdByName(selectedProjectName);
                if(projectId == null){
                    CustomErrorAlert.showAlert("Project not found", "Project Id is null");
                    return;
                }

            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            programsDTO.projectIdProperty().set(projectId);

            String selectedMemberName = selectMemberNameComboBox.getValue();
            Long assignedTo;
            try {
                assignedTo = userBO.getUserIdByFullName(selectedMemberName);
                System.out.println("Assigned  Task View: " + assignedTo);
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            programsDTO.assignedToProperty().set(assignedTo);

            programsDTO.priorityProperty().set(TaskPriority.MEDIUM);
            programsDTO.statusProperty().set(TaskStatus.NOT_STARTED);

            programsDTO.createdAtProperty().set(Date.from(Instant.now()));

            if (taskDeadline.getValue() != null) {
                programsDTO.dueDateProperty().set(Date.from(taskDeadline.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }

            boolean isSaved;
            try {
                isSaved = programsBO.insertProgram(programsDTO);
                if (isSaved) {
                    Date deadline = programsDTO.dueDateProperty().get();

                    String Email = userBO.getUserEmailById(programsDTO.assignedToProperty().get());
                    String Name = userBO.getUserFullNameById(programsDTO.assignedToProperty().get());
                    System.out.println("===========Create Task Notification===========");
                    System.out.println("Name: " + Name);



                    new Thread(() -> {
                        String receiverName;
                        try {
                            receiverName = userBO.getUserFullNameById(programsDTO.assignedToProperty().get());
                        } catch (SQLException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        String uName = SessionUser.getLoggedInUsername();
                        Long idByUsername;
                        try {
                            idByUsername = userBO.getUserIdByUsername(uName);
                        } catch (SQLException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        String taskCreatorName;
                        try {
                            taskCreatorName = userBO.getUserFullNameById(idByUsername);
                        } catch (SQLException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("Task Created By : " + taskCreatorName);
                        System.out.println("Task Assigned To : " + receiverName);
                        String receiverEmail = null;

                        try {
                            receiverEmail = userBO.getUserEmailById(programsDTO.assignedToProperty().get());
                        } catch (SQLException e) {
                            System.out.println("Error getting receiver email: " + e.getMessage());
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }

                        System.out.println("Receiver Email : " + receiverEmail);
                        if (receiverEmail == null) {
                            System.out.println("Email is " + null);
                            return;
                        }
                        try {
                            Thread.sleep(1000);
                            Platform.runLater(() -> {
                                System.out.println("Task created successfully!");
                                CustomAlert.showAlert("Task created", "Task created successfully!");
                                TherapistsViewController.bindNavigation(taskCreatePage, "/view/nav-buttons/program-view.fxml");
                                clearContent();
                            });
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }).start();
                } else {
                    System.out.println("Error saving task.");
                    CustomErrorAlert.showAlert("Error saving task", "Failed to save the task.");
                }
            } catch (Exception e) {
                System.out.println("Error saving task: " + e.getMessage());
                CustomErrorAlert.showAlert("Error saving task", "Error saving task: " + e.getMessage());
            }
        } else {
            System.out.println("Please fill all the required fields.");
            CustomErrorAlert.showAlert("Invalid Input", "Please fill all the required fields.");
        }
    }

    public void cancelOnClick() {
        if (areFieldsCleared()) {
            TherapistsViewController.bindNavigation(taskCreatePage, "/view/nav-buttons/program-view.fxml");
        } else {
            clearContent();
        }
    }

    private boolean areFieldsCleared() {
        return taskNameField.getText().isEmpty() &&
                descriptionIdField.getText().isEmpty() &&
                selectMemberNameComboBox.getSelectionModel().isEmpty() &&
                selectProjectNameComboBox.getSelectionModel().isEmpty() &&
                taskDeadline.getValue() == null;
    }

    private void clearContent() {
        taskNameField.clear();
        descriptionIdField.clear();
        selectMemberNameComboBox.getSelectionModel().clearSelection();
        selectProjectNameComboBox.getSelectionModel().clearSelection();
        taskDeadline.setValue(null);
    }

    private boolean validateInputFields() {
        return !taskNameField.getText().isEmpty() &&
                !descriptionIdField.getText().isEmpty() &&
                !selectMemberNameComboBox.getSelectionModel().isEmpty() &&
                !selectProjectNameComboBox.getSelectionModel().isEmpty() &&
                taskDeadline.getValue() != null;
    }
}
