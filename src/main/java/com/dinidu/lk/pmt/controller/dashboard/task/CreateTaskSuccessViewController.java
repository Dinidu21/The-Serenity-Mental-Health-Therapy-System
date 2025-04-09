package com.dinidu.lk.pmt.controller.dashboard.task;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.*;
import com.dinidu.lk.pmt.controller.dashboard.TherapistsViewController;
import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.custom.impl.QueryDAOImpl;
import com.dinidu.lk.pmt.dto.ChecklistDTO;
import com.dinidu.lk.pmt.dto.TherapistDTO;
import com.dinidu.lk.pmt.dto.TherapyProgramsDTO;
import com.dinidu.lk.pmt.utils.*;
import com.dinidu.lk.pmt.utils.checklistTypes.ChecklistPriority;
import com.dinidu.lk.pmt.utils.checklistTypes.ChecklistStatus;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.listeners.ChecklistDeletionHandler;
import com.dinidu.lk.pmt.utils.listeners.ChecklistUpdateListener;
import com.dinidu.lk.pmt.utils.listeners.TaskDeletionHandler;
import com.dinidu.lk.pmt.utils.listeners.TaskUpdateListener;
import com.dinidu.lk.pmt.utils.taskTypes.TaskPriority;
import com.dinidu.lk.pmt.utils.taskTypes.TaskStatus;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class CreateTaskSuccessViewController implements Initializable, TaskDeletionHandler, TaskUpdateListener, ChecklistDeletionHandler, ChecklistUpdateListener {
    public Label taskDescription;
    public Label taskStartDate;
    public Label taskDeadline;
    public Label taskStatus;
    public Label taskPriority;
    public Button resetFilterBtn;
    public VBox checklistContainer;
    public ComboBox<ChecklistPriority> sortBy;
    public ComboBox<ChecklistStatus> filterStatus;
    public Button createCheckListBtn;
    public TextField checklistSearch;
    public Label noChecklistLabel;
    private double xOffset = 0;
    private double yOffset = 0;
    public ImageView moreIcon;
    public AnchorPane taskIcon;
    public AnchorPane sideBar;
    public AnchorPane mainBar;
    public Label newIssue;
    public AnchorPane taskCreatedSuccessPage;
    public Label projectOwnerName;
    public Label teamCount;
    public Label teamMember1;
    public Label teamMember2;
    public Label teamMember3;
    public Label teamMember4;
    public Label projectIdWith2Digits;
    public ImageView projectOwnerImg;
    public ImageView teamMember1Img;
    public ImageView teamMember2Img;
    public ImageView teamMember3Img;
    public ImageView teamMember4Img;
    @FXML
    private Label taskName;
    @FXML
    private Label taskId;
    private TherapyProgramsDTO therapyProgramsDTO;
    static TherapyProgramsDTO current_Task;

    UserBO userBO= (UserBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.USER);

    QueryDAO queryDAO = new QueryDAOImpl();

    TherapistsBO therapistsBO = (TherapistsBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.THERAPIST);

    ProgramsBO programsBO = (ProgramsBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.PROGRAM);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        noChecklistLabel.setVisible(false);
        TherapyProgramsDTO activeTask = CreateTaskSuccessViewController.current_Task;
        if (activeTask != null) {
            this.therapyProgramsDTO = activeTask;
            setTaskData(therapyProgramsDTO);
            System.out.println("Active task is set, taskDTO is: " + therapyProgramsDTO);
        } else {
            System.out.println("No active task is set, taskDTO remains null");
        }

        resetFilterBtn.setVisible(false);
        colorSetter();
        userAccessControl();
        sortBy.getItems().setAll(ChecklistPriority.values());
        filterStatus.getItems().setAll(ChecklistStatus.values());

        sortBy.setValue(null);
        filterStatus.setValue(null);



        if (therapyProgramsDTO == null) {
            System.out.println("Error: taskDTO is null");
        }

    }

    public void setTaskData(TherapyProgramsDTO therapyProgramsDTO) {
        if (therapyProgramsDTO == null) {
            System.out.println("Error: taskDTO is null");
            return;
        }

/*        System.out.println("Now in set Task data method : " + programsDTO);
        System.out.println("Here is the start date: " + programsDTO.getCreatedAt().get());

        if (programsDTO.getId() == null) {
            return;
        }

        this.programsDTO = programsDTO;
        current_Task = programsDTO;
        System.out.println("Static current task: " + current_Task);

        taskName.textProperty().bind(programsDTO.nameProperty());
        taskDescription.textProperty().bind(programsDTO.descriptionProperty());


        taskStatus.textProperty().bind(Bindings.convert(programsDTO.statusProperty()));
        taskPriority.textProperty().bind(Bindings.convert(programsDTO.priorityProperty()));
        String projectId = programsDTO.getProjectId().get();*/
        
        List<TherapistDTO> project = List.of();
/*        try {
            project = therapistsBO.getTherapistById(projectId);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }*/
        String userFullNameById = "";

        if (!project.isEmpty()) {
            TherapistDTO therapistDTO = project.get(0);
            System.out.println("Created By: " + therapistDTO.getCreatedBy());
            taskId.setText("Project Name : " + therapistDTO.getFullName());
            System.out.println("Project name: " + therapistDTO.getFullName());
            String projectIdWith2DigitsString = project.get(0).getId().split("-")[0];
            projectIdWith2Digits.setText(projectIdWith2DigitsString);
            System.out.println("Project ID: " + projectIdWith2DigitsString);
            Image profilePic;
            try {
                profilePic = userBO.getUserProfilePicByUserId(therapistDTO.getCreatedBy());
            } catch (SQLException | FileNotFoundException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            projectOwnerImg.setImage(profilePic);
            try {
                userFullNameById = userBO.getUserFullNameById(therapistDTO.getCreatedBy());
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            System.out.println(userFullNameById);
        } else {
            System.out.println("No project found with the given ID.");
        }

        projectOwnerName.setText(" " + userFullNameById);
        List<TherapyProgramsDTO> tasks;
/*        try {
            tasks = programsBO.getProgramByTherapistId(programsDTO).getId().get());

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }*/
        Label[] teamMemberLabels = {teamMember1, teamMember2, teamMember3, teamMember4};
        ImageView[] teamMemberImages = {teamMember1Img, teamMember2Img, teamMember3Img, teamMember4Img};
        int teamMemberCount = 0;

 /*       for (ProgramsDTO task : tasks) {
            if (teamMemberCount >= 4) break;
            List<TeamAssignmentDTO> assignments;
            try {
                assignments = teamAssignmentBO.getAssignmentsByTaskId(task.getId().get());
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            for (TeamAssignmentDTO assignment : assignments) {
                if (teamMemberCount >= 4) break;
                Long assignedTo = assignment.getUserId();

                if (assignedTo != null) {
                    String memberName;
                    try {
                        memberName = userBO.getUserFullNameById(assignedTo);
                    } catch (SQLException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    Image memberProfilePic;
                    try {
                        memberProfilePic = userBO.getUserProfilePicByUserId(assignedTo);
                    } catch (SQLException | FileNotFoundException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                    teamMemberLabels[teamMemberCount].setText(memberName);
                    teamMemberImages[teamMemberCount].setImage(memberProfilePic);
                    teamMemberCount++;
                }
            }
        }*/

        teamCount.setText("" + teamMemberCount);
    }


    private void updateStatusStyle(TaskStatus status) {
        taskStatus.getStyleClass().clear();
        switch (status) {
            case NOT_STARTED -> taskStatus.getStyleClass().add("status-planned");
            case IN_PROGRESS -> taskStatus.getStyleClass().add("status-in-progress");
            case COMPLETED -> taskStatus.getStyleClass().add("status-completed");
        }
    }

    private void updatePriorityStyle(TaskPriority priority) {
        taskPriority.getStyleClass().clear();
        switch (priority) {
            case HIGH -> taskPriority.getStyleClass().add("priority-high");
            case MEDIUM -> taskPriority.getStyleClass().add("priority-medium");
            case LOW -> taskPriority.getStyleClass().add("priority-low");
        }
    }

    @Override
    public void onTaskUpdated(TherapyProgramsDTO updatedTask) {
        setTaskData(updatedTask);
    }

    public void moreIconOnclick() {
        try {
            String iconPath = "/asserts/icons/PN.png";
            String fxmlPath = "/view/nav-buttons/task/task-edit-view.fxml";

            Stage modalStage = new Stage();
            modalStage.initStyle(StageStyle.TRANSPARENT);
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner((Stage) taskCreatedSuccessPage.getScene().getWindow());

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            TaskEditViewController controller = loader.getController();

            controller.setDeletionHandler(this);


            root.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });

            root.setOnMouseDragged(event -> {
                modalStage.setX(event.getScreenX() - xOffset);
                modalStage.setY(event.getScreenY() - yOffset);
            });

            Scene scene = new Scene(root);
            scene.setFill(null);
            scene.setFill(null);

            modalStage.setScene(scene);
            modalStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath))));
            modalStage.centerOnScreen();
            modalStage.show();

        } catch (Exception e) {
            System.out.println("Error while loading modal: " + e.getMessage());
            e.printStackTrace();
            CustomErrorAlert.showAlert("Error", "Error while loading modal.");
        }
    }

    @Override
    public void onTaskDeleted() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/nav-buttons/program-view.fxml"));
            AnchorPane newContent = loader.load();
            AnchorPane parentPane = (AnchorPane) taskCreatedSuccessPage.getParent();
            parentPane.getChildren().clear();
            newContent.prefWidthProperty().bind(parentPane.widthProperty());
            newContent.prefHeightProperty().bind(parentPane.heightProperty());
            FadeTransition fadeIn = new FadeTransition(Duration.millis(750), newContent);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
            parentPane.getChildren().add(newContent);

        } catch (IOException e) {
            e.printStackTrace();
            CustomErrorAlert.showAlert("Error", "Failed to load the task view.");
        }
    }

    public void newIssueClick() {
        TherapistsViewController.bindNavigation(taskCreatedSuccessPage, "/view/nav-buttons/issue/issue-create-view.fxml");
    }

    private void colorSetter() {
        if (TherapistsViewController.backgroundColor == null) {
            System.out.println("Project background color is null");
        }
        taskIcon.setStyle("-fx-background-color: " + TherapistsViewController.backgroundColor + ";");
        projectIdWith2Digits.setStyle("-fx-text-fill: " + TherapistsViewController.backgroundColor + ";");
    }

    private void userAccessControl() {
        String username = SessionUser.getLoggedInUsername();

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
        }
        System.out.println("User role: " + userRoleByUsername);
        if (userRoleByUsername != UserRole.ADMIN &&
                userRoleByUsername != UserRole.RECEPTIONIST) {
            System.out.println("Access denied: Only Admin, Project Manager, or Product Owner can edit projects.");
            moreIcon.setVisible(false);
            createCheckListBtn.setVisible(false);
        }
    }

    @Override
    public void onChecklistDeleted() {

    }

    @Override
    public void onChecklistUpdated(ChecklistDTO updatedChecklist) {

    }

    public void updateTaskView(TherapyProgramsDTO task) {

    }
}
