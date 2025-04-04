package com.dinidu.lk.pmt.controller.dashboard.issue;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.PatientBO;
import com.dinidu.lk.pmt.bo.custom.UserBO;
import com.dinidu.lk.pmt.controller.dashboard.TherapistsViewController;
import com.dinidu.lk.pmt.dto.PatientsDTO;
import com.dinidu.lk.pmt.utils.customAlerts.CustomAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.SessionUser;
import com.dinidu.lk.pmt.utils.issuesTypes.IssuePriority;
import com.dinidu.lk.pmt.utils.issuesTypes.IssueStatus;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class CreateIssueViewController implements Initializable {

    @FXML
    public AnchorPane issuesCreatePg;
    @FXML
    public ComboBox<IssuePriority> selectPriorityComboBox;
    @FXML
    public ComboBox<String> selectTaskNameComboBox;
    @FXML
    public ComboBox<String> selectProjectNameComboBox;
    @FXML
    public ComboBox<String> selectMemberNameComboBox;
    @FXML
    public TextArea descriptionIdField;
    public DatePicker dueDatePicker;

    UserBO userBO= (UserBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.USER);
    PatientBO patientBO = (PatientBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.PatientBO);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeProjectComboBox();
        initializeTaskComboBox();
        initializeMemberComboBox();
        initializePriorityComboBox();
    }

    private void initializeProjectComboBox() {
        ObservableList<String> projectNames = FXCollections.observableArrayList();
        try {
            List<String> projects = patientBO.getActiveTherapistsNames();  // Now returns a List<String>
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
                    List<String> tasks = patientBO.getProgramByTherapist(selectedProject);
                    taskNames.addAll(tasks);
                    selectTaskNameComboBox.setItems(taskNames);
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });



        dueDatePicker.setDayCellFactory(picker -> new DateCell() {
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

    private void initializeMemberComboBox() {
        ObservableList<String> memberNames = FXCollections.observableArrayList();
        try {
            List<String> members = patientBO.getActivePatients();  // Now returns a List<String>
            memberNames.addAll(members);  // Add all member names to the ObservableList
            selectMemberNameComboBox.setItems(memberNames);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }



    private void initializePriorityComboBox() {
        selectPriorityComboBox.getItems().setAll(IssuePriority.values());
    }

    public void cancelOnClick(ActionEvent actionEvent) {
        selectProjectNameComboBox.getSelectionModel().clearSelection();
        selectTaskNameComboBox.getSelectionModel().clearSelection();
        selectMemberNameComboBox.getSelectionModel().clearSelection();
        selectPriorityComboBox.getSelectionModel().clearSelection();
        descriptionIdField.clear();
    }

    public void createIssueOnClick() {
        String projectName = selectProjectNameComboBox.getValue();
        String taskName = selectTaskNameComboBox.getValue();
        String memberName = selectMemberNameComboBox.getValue();
        IssuePriority priority = selectPriorityComboBox.getValue();
        String description = descriptionIdField.getText();
        LocalDate dueDate = dueDatePicker.getValue();

        System.out.println("Project name: " + projectName);
        System.out.println("Task name: " + taskName);
        System.out.println("Member name: " + memberName);
        System.out.println("Priority: " + priority);
        System.out.println("Description: " + description);
        System.out.println("Due Date :"+dueDate);

        if (projectName == null || memberName == null || description.isEmpty() ||taskName == null ||priority == null ||dueDate == null) {
            System.out.println("Please fill in all fields.");
            CustomErrorAlert.showAlert("ERROR", "Please fill in all fields.");
            return;
        }

        try {
            String projectId = patientBO.getTherapistsIdByName(projectName);
            Long taskId = patientBO.getProgramIdByName(taskName);
            Long memberId = patientBO.getUserIdByName(memberName);
            String username = SessionUser.getLoggedInUsername();

            if (username == null) {
                System.out.println("User not logged in. username: " + null);
            }

            Long idByUsername;
            try {
                idByUsername = userBO.getUserIdByUsername(null);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            IssueStatus status = IssueStatus.OPEN;

            PatientsDTO patientsDTO = new PatientsDTO();
            patientsDTO.setProjectId(projectId);
            patientsDTO.setTaskId(taskId != null ? taskId : 0L);
            patientsDTO.setDescription(description);
            patientsDTO.setReportedBy(idByUsername);
            patientsDTO.setAssignedTo(memberId);
            patientsDTO.setStatus(status);
            patientsDTO.setPriority(priority);
            patientsDTO.setDueDate(Date.valueOf(dueDate));

            boolean isCreated = patientBO.createPatient(patientsDTO);
            if (isCreated) {
                new Thread(() -> {
                    String recipientName ;
                    try {
                        recipientName = userBO.getUserFullNameById(patientsDTO.assignedToProperty().get());
                    } catch (SQLException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    String uName = SessionUser.getLoggedInUsername();
                    Long issueCreatorId ;
                    try {
                        issueCreatorId = userBO.getUserIdByUsername(uName);
                    } catch (SQLException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    String issueCreatorName ;
                    try {
                        issueCreatorName = userBO.getUserFullNameById(issueCreatorId);
                    } catch (SQLException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("ISSUE Created By : "+ issueCreatorName);
                    System.out.println("ISSUE Assigned To : "+ recipientName);
                    String receiverEmail = null;

                    try {
                        receiverEmail = userBO.getUserEmailById(patientsDTO.assignedToProperty().get());
                        System.out.println("=======Create Issue Email====="+receiverEmail);
                    } catch (SQLException e) {
                        System.out.println("Error getting receiver email: " + e.getMessage());
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                    System.out.println("Receiver Email : "+ receiverEmail);
                    if(receiverEmail == null){
                        System.out.println("Email is "+ null);
                        return;
                    }
                    try {
                        Thread.sleep(1000);
                        Platform.runLater(() -> {
                            System.out.println("Issue created successfully.");
                            CustomAlert.showAlert("SUCCESS", "Issue created successfully.");
                            TherapistsViewController.bindNavigation(issuesCreatePg, "/view/nav-buttons/patients-view.fxml");
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();

            } else {
                System.out.println("Failed to create issue.");
                CustomErrorAlert.showAlert("ERROR", "Failed to create issue.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
