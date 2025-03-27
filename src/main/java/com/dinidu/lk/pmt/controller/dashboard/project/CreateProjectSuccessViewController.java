package com.dinidu.lk.pmt.controller.dashboard.project;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.ProjectsBO;
import com.dinidu.lk.pmt.bo.custom.TasksBO;
import com.dinidu.lk.pmt.bo.custom.TeamAssignmentBO;
import com.dinidu.lk.pmt.bo.custom.UserBO;
import com.dinidu.lk.pmt.controller.DashboardViewController;
import com.dinidu.lk.pmt.controller.dashboard.ProjectViewController;
import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.custom.impl.QueryDAOImpl;
import com.dinidu.lk.pmt.dto.TasksDTO;
import com.dinidu.lk.pmt.dto.TeamAssignmentDTO;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.listeners.ProjectDeletionHandler;
import com.dinidu.lk.pmt.utils.listeners.ProjectUpdateListener;
import com.dinidu.lk.pmt.utils.SessionUser;
import com.dinidu.lk.pmt.utils.projectTypes.ProjectPriority;
import com.dinidu.lk.pmt.utils.projectTypes.ProjectStatus;
import com.dinidu.lk.pmt.utils.projectTypes.ProjectVisibility;
import com.dinidu.lk.pmt.utils.taskTypes.TaskStatus;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import com.dinidu.lk.pmt.dto.ProjectDTO;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class CreateProjectSuccessViewController implements Initializable, ProjectDeletionHandler, ProjectUpdateListener {
    public Label projectDescription;
    public Label projectStartDate;
    public Label projectDeadline;
    public Label projectStatus;
    public Label projectPriority;
    public Label visibilityLabel;
    public AnchorPane unresolvedTaskOnCurrentProject;
    public ImageView refreshButtonForTasks;
    public VBox tasksContainer;
    private double xOffset = 0;
    private double yOffset = 0;
    public ImageView moreIcon;
    public AnchorPane projectIcon;
    public AnchorPane sideBar;
    public AnchorPane mainBar;
    public Label newIssue;
    public AnchorPane projectSID;
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
    private Label projectName;
    String projectIdForTasks;

    UserBO userBO= (UserBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.USER);
    ProjectsBO projectsBO =
            (ProjectsBO) BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.PROJECTS);

    QueryDAO queryDAO = new QueryDAOImpl();

    TasksBO tasksBO = (TasksBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.TASKS);

    TeamAssignmentBO teamAssignmentBO = (TeamAssignmentBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.TEAM_ASSIGNMENTS);

    @FXML
    private Label projectId;
    private ProjectDTO project;

    public void setProjectData(ProjectDTO project) {
        if (project == null || project.getId() == null) {
            return;
        }
        if (project.getCreatedBy() == null) {
            System.out.println("User is null ");
            return;
        }

        this.project = project;

        projectName.textProperty().bind(project.nameProperty());
        projectDescription.textProperty().bind(project.descriptionProperty());
        projectStartDate.textProperty().bind(Bindings.createStringBinding(
                () -> {
                    if (project.getStartDate() != null) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        return dateFormat.format(project.getStartDate());
                    } else {
                        return "Start date not set";
                    }
                },
                project.startDateProperty()
        ));

        projectDeadline.textProperty().bind(Bindings.createStringBinding(
                () -> project.getEndDate() != null ? project.getEndDate().toString() : "End date is not set",
                project.endDateProperty()));
        projectStatus.textProperty().bind(Bindings.convert(project.statusProperty()));
        projectPriority.textProperty().bind(Bindings.convert(project.priorityProperty()));
        visibilityLabel.textProperty().bind(Bindings.convert(project.visibilityProperty()));

        projectId.setText("          " + project.getId());
        System.out.println("This is the project is : " + project.getId());
        projectIdForTasks = project.getId();
        System.out.println("This is the project id for tasks : " + projectIdForTasks);

        projectIdWith2Digits.setText(project.getId().contains("-") ? project.getId().split("-")[0] : project.getId());

        String ownerName;
        try {
            ownerName = project.getCreatedBy() != null ? userBO.getUserFullNameById(project.getCreatedBy()) : "Owner not specified";
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        projectOwnerName.setText(" " + ownerName);
        Image profilePic;
        try {
            profilePic = userBO.getUserProfilePicByUserId(project.getCreatedBy());
        } catch (SQLException | FileNotFoundException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        projectOwnerImg.setImage(profilePic);

        List<TeamAssignmentDTO> teamAssignments = getTeamAssignmentsForProject(project.getId());

        Label[] teamMemberLabels = {teamMember1, teamMember2, teamMember3, teamMember4};
        ImageView[] teamMemberImages = {teamMember1Img, teamMember2Img, teamMember3Img, teamMember4Img};
        int teamMemberCount = 0;

        for (TeamAssignmentDTO assignment : teamAssignments) {
            if (teamMemberCount >= 4) break;

            Long assignedUserId = assignment.getUserId();
            if (assignedUserId != null) {
                String memberName;
                try {
                    memberName = userBO.getUserFullNameById(assignedUserId);
                } catch (SQLException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                Image memberProfilePic;
                try {
                    memberProfilePic = userBO.getUserProfilePicByUserId(assignedUserId);
                    System.out.println("===================CreateProjectSuccessViewController: " + memberProfilePic);
                } catch (SQLException | ClassNotFoundException | FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

                teamMemberLabels[teamMemberCount].setText(memberName);
                teamMemberImages[teamMemberCount].setImage(memberProfilePic);
                teamMemberCount++;
            }
        }
        teamCount.setText("" + teamMemberCount);

        setupStyleListeners();
        updateStyles(project);
        loadUnresolvedTasks();
    }

    public List<TeamAssignmentDTO> getTeamAssignmentsForProject(String projectId) {
        List<TeamAssignmentDTO> assignments = new ArrayList<>();
        List<TasksDTO> tasks ;
        try {
            tasks = tasksBO.getTaskByProjectId(projectId);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        for (TasksDTO task : tasks) {
            List<TeamAssignmentDTO> taskAssignments;
            try {
                taskAssignments = teamAssignmentBO.getAssignmentsByTaskId(task.getId().get());
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            assignments.addAll(taskAssignments);
        }

        return assignments;
    }

    private void setupStyleListeners() {
        if (project != null) {
            project.statusProperty().addListener((observable, oldValue, newValue) ->
                    updateStatusStyle(newValue));
            project.priorityProperty().addListener((observable, oldValue, newValue) ->
                    updatePriorityStyle(newValue));
            project.visibilityProperty().addListener((observable, oldValue, newValue) ->
                    updateVisibilityStyle(newValue));
        }
    }

    private void updateStatusStyle(ProjectStatus status) {
        projectStatus.getStyleClass().clear();
        switch (status) {
            case PLANNED -> projectStatus.getStyleClass().add("status-planned");
            case IN_PROGRESS -> projectStatus.getStyleClass().add("status-in-progress");
            case COMPLETED -> projectStatus.getStyleClass().add("status-completed");
            case ON_HOLD -> projectStatus.getStyleClass().add("status-on-hold");
            case CANCELLED -> projectStatus.getStyleClass().add("status-cancelled");
        }
    }

    private void updatePriorityStyle(ProjectPriority priority) {
        projectPriority.getStyleClass().clear();
        switch (priority) {
            case HIGH -> projectPriority.getStyleClass().add("priority-high");
            case MEDIUM -> projectPriority.getStyleClass().add("priority-medium");
            case LOW -> projectPriority.getStyleClass().add("priority-low");
        }
    }

    private void updateVisibilityStyle(ProjectVisibility visibility) {
        visibilityLabel.getStyleClass().clear();
        switch (visibility) {
            case PUBLIC -> visibilityLabel.getStyleClass().add("visibility-public");
            case PRIVATE -> visibilityLabel.getStyleClass().add("visibility-private");
        }
    }

    private void updateStyles(ProjectDTO project) {
        updateStatusStyle(project.getStatus());
        updatePriorityStyle(project.getPriority());
        updateVisibilityStyle(project.getVisibility());
    }

    @Override
    public void onProjectUpdated(ProjectDTO updatedProject) {
        updateStyles(updatedProject);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupRefreshButtonForTasks();
        loadUnresolvedTasks();

        if (ProjectViewController.backgroundColor == null) {
            System.out.println("Project background color is null");
        }
        projectIcon.setStyle("-fx-background-color: " + ProjectViewController.backgroundColor + ";");
        projectIdWith2Digits.setStyle("-fx-text-fill: " + ProjectViewController.backgroundColor + ";");

        String username = SessionUser.getLoggedInUsername();


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
        }
    }

    private void setupRefreshButtonForTasks() {
        refreshButtonForTasks.setOnMouseClicked(e -> {
            System.out.println("Refresh button clicked for tasks");
            Timeline timeline = new Timeline(new javafx.animation.KeyFrame(Duration.millis(700), z -> DashboardViewController.fadeIn(unresolvedTaskOnCurrentProject)));
            timeline.play();
            loadUnresolvedTasks();
        });
    }

    private void loadUnresolvedTasks() {
        tasksContainer.getChildren().clear();
        System.out.println("Loading unresolved tasks for project: " + projectIdForTasks);
        List<TasksDTO> unresolvedTasks = null;
        try {
            unresolvedTasks = tasksBO.getTasksCurrentProjectByStatus(projectIdForTasks, TaskStatus.NOT_STARTED);
            System.out.println("Unresolved tasks: " + unresolvedTasks);
            if (unresolvedTasks.isEmpty()) {
                System.out.println("No unresolved tasks found for project: " + projectIdForTasks);
                unresolvedTaskOnCurrentProject.setVisible(true);
            }
        } catch (SQLException e) {
            System.out.println("Error loading unresolved tasks: " + e.getMessage());
            CustomErrorAlert.showAlert("Error loading unresolved tasks", e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        assert unresolvedTasks != null;
        for (TasksDTO task : unresolvedTasks) {
            HBox taskItem = createTaskItem(task);
            tasksContainer.getChildren().add(taskItem);
        }
    }

    private HBox createTaskItem(TasksDTO task) {
        HBox taskItem = new HBox();
        taskItem.getStyleClass().add("task-item");
        taskItem.setAlignment(Pos.CENTER_LEFT);
        taskItem.setSpacing(12);

        Region priorityIndicator = new Region();
        priorityIndicator.getStyleClass().add("priority-indicator");

        String priorityClass = "priority-task-" + task.getPriority().toString().toLowerCase();
        priorityIndicator.getStyleClass().add(priorityClass);

        System.out.println("Applied priority classes to Task: " + priorityIndicator.getStyleClass());

        VBox taskDetails = new VBox(4);
        Label taskName = new Label(task.nameProperty().get());
        taskName.getStyleClass().add("task-name");

        String PROJECT_ID = null;
        try {
            PROJECT_ID = projectsBO.getProjectIdByTaskId(task.idProperty().get());
        } catch (SQLException e) {
            System.out.println("Error fetching project ID: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Project ID: " + PROJECT_ID);
        Label taskId = new Label(PROJECT_ID);
        taskId.getStyleClass().add("task-id");

        taskDetails.getChildren().addAll(taskName, taskId);

        taskItem.getChildren().addAll(priorityIndicator, taskDetails);

        taskItem.setOnMouseClicked(e -> handleTaskClick(task));

        return taskItem;
    }

    private void handleTaskClick(TasksDTO task) {
        System.out.println("Task clicked: " + task.getName());
    }

    public void moreIconOnclick(MouseEvent mouseEvent) {
        try {
            String iconPath = "/asserts/icons/PN.png";
            String fxmlPath = "/view/nav-buttons/project/project-edit-view.fxml";

            Stage modalStage = new Stage();
            modalStage.initStyle(StageStyle.TRANSPARENT);
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner((Stage) projectSID.getScene().getWindow());

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            ProjectEditViewController controller = loader.getController();
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
    public void onProjectDeleted() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/nav-buttons/project-view.fxml"));
            AnchorPane newContent = loader.load();

            AnchorPane parentPane = (AnchorPane) projectSID.getParent();

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
            CustomErrorAlert.showAlert("Error", "Failed to load the project view.");
        }
    }

    public void newIssueClick(MouseEvent mouseEvent) {
        ProjectViewController.bindNavigation(projectSID, "/view/nav-buttons/issue/issue-create-view.fxml");
    }
}
