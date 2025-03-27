package com.dinidu.lk.pmt.controller.dashboard.timesheet;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.IssuesBO;
import com.dinidu.lk.pmt.bo.custom.ProjectsBO;
import com.dinidu.lk.pmt.bo.custom.TimeSheetBO;
import com.dinidu.lk.pmt.bo.custom.UserBO;
import com.dinidu.lk.pmt.controller.dashboard.ProjectViewController;
import com.dinidu.lk.pmt.dto.TimesheetDTO;
import com.dinidu.lk.pmt.utils.customAlerts.CustomAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.SessionUser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class CreateTimesheetViewController implements Initializable {

    @FXML
    public AnchorPane timesheetCreatePg;
    @FXML
    public ComboBox<String> selectTaskNameComboBox;
    @FXML
    public ComboBox<String> selectProjectNameComboBox;
    @FXML
    public TextArea descriptionIdField;
    public DatePicker datePicker;

    UserBO userBO= (UserBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.USER);

    ProjectsBO projectBO =
            (ProjectsBO) BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.PROJECTS);
    TimeSheetBO timeSheetBO =
            (TimeSheetBO) BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.TIMESHEET);
    IssuesBO issuesBO =
            (IssuesBO) BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.ISSUES);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeProjectComboBox();
        initializeTaskComboBox();
        initializeHoursComboBox();
    }

    private void initializeHoursComboBox() {
        ObservableList<String> hours = FXCollections.observableArrayList();
        hours.add("1");
        hours.add("2");
        hours.add("3");
        hours.add("4");
        hours.add("5");
        hours.add("6");
        hours.add("7");
        hours.add("8");
        hours.add("9");
        hours.add("10");
    }

    private void initializeProjectComboBox() {
        ObservableList<String> projectNames = FXCollections.observableArrayList();
        try {
            List<String> projects = issuesBO.getActiveProjectNames();  // Now returns a List<String>
            projectNames.addAll(projects);  // Add all the projects to the ObservableList
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
                    List<String> tasks = issuesBO.getTasksByProject(selectedProject);
                    taskNames.addAll(tasks);
                    selectTaskNameComboBox.setItems(taskNames);
                } catch (SQLException | ClassNotFoundException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        });
    }


    public void cancelOnClick() {
        selectProjectNameComboBox.getSelectionModel().clearSelection();
        selectTaskNameComboBox.getSelectionModel().clearSelection();
        descriptionIdField.clear();
        datePicker.setValue(null);
        //hoursSpent.getSelectionModel().clearSelection();
    }

    public void createTimesheetOnClick() {
        String projectName = selectProjectNameComboBox.getValue();
        String taskName = selectTaskNameComboBox.getValue();
        String description = descriptionIdField.getText();
        LocalDate dueDate = datePicker.getValue();
        //String hrs = hoursSpent.getValue();

        System.out.println("Project name: " + projectName);
        System.out.println("Task name: " + taskName);
        System.out.println("Description: " + description);
        System.out.println("Date :"+dueDate);

        try {


            String username = SessionUser.getLoggedInUsername();

            if (username == null) {
                System.out.println("User not logged in. username: " + null);
                return;
            }

            String project_name = selectProjectNameComboBox.getValue();
            String id_project = projectBO.getProjectIdByName(project_name);

            String task_name = selectTaskNameComboBox.getValue();
            Long id_task = issuesBO.getTaskIdByName(task_name);

            String user_name = SessionUser.getLoggedInUsername();
            Long idByUsername = userBO.getUserIdByUsername(user_name);

            String date = datePicker.getValue().toString();
            //String hours = hoursSpent.getValue();
            TimesheetDTO timesheetDTO = new TimesheetDTO();

            timesheetDTO.setUserId(idByUsername);
            timesheetDTO.setProjectId(id_project);
            timesheetDTO.setTaskId(id_task);
            //timesheetDTO.setHours(new BigDecimal(hours));
            timesheetDTO.setWorkDate(Date.valueOf(date));
            timesheetDTO.setDescription(description);

            System.out.println("TimesheetDTO: " + timesheetDTO);

            boolean isCreated = timeSheetBO.createTimeSheet(timesheetDTO);
            if (isCreated) {
                System.out.println("Issue created successfully.");
                CustomAlert.showAlert("SUCCESS", "Issue created successfully.");
                ProjectViewController.bindNavigation(timesheetCreatePg, "/view/dashboard/timesheet/timesheet-view.fxml");
            } else {
                System.out.println("Failed to create issue.");
                CustomErrorAlert.showAlert("ERROR", "Failed to create issue.");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
