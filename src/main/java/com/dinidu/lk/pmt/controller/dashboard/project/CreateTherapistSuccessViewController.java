package com.dinidu.lk.pmt.controller.dashboard.project;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.ProgramsBO;
import com.dinidu.lk.pmt.bo.custom.TherapistsBO;
import com.dinidu.lk.pmt.bo.custom.UserBO;
import com.dinidu.lk.pmt.controller.DashboardViewController;
import com.dinidu.lk.pmt.controller.dashboard.TherapistsViewController;
import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.custom.impl.QueryDAOImpl;
import com.dinidu.lk.pmt.dto.ProgramsDTO;
import com.dinidu.lk.pmt.dto.TeamAssignmentDTO;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.listeners.ProjectDeletionHandler;
import com.dinidu.lk.pmt.utils.listeners.ProjectUpdateListener;
import com.dinidu.lk.pmt.utils.SessionUser;
import com.dinidu.lk.pmt.utils.projectTypes.TherapistStatus;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
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
import com.dinidu.lk.pmt.dto.TherapistDTO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class CreateTherapistSuccessViewController implements Initializable, ProjectDeletionHandler, ProjectUpdateListener {
    public Label therapistLocation;
    public Label projectStartDate;
    public Label therapistStatus;
    public AnchorPane unresolvedTaskOnCurrentProject;
    public ImageView refreshButtonForTasks;
    public VBox tasksContainer;
    public Label therapistEmail;
    public Label phoneNumber;
    public Label projectStartDate1;
    public Label therapistStartDate;
    public Label therapistName;
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
    String projectIdForTasks;
    @FXML
    private Label projectId;
    private TherapistDTO therapistDTO;

    UserBO userBO= (UserBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.USER);
    TherapistsBO projectsBO =
            (TherapistsBO) BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.TherapistsBO);

    QueryDAO queryDAO = new QueryDAOImpl();

    ProgramsBO tasksBO = (ProgramsBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.ProgramsBO);

    public void setProjectData(TherapistDTO therapistDto) {
        if (therapistDto == null || therapistDto.getId() == null) {
            return;
        }

        if (therapistDto.getCreatedBy() == null) {
            System.out.println("User is null ");
            return;
        }

        this.therapistDTO = therapistDto;
        therapistName.textProperty().bind(therapistDto.fullNameProperty());
        therapistLocation.textProperty().bind(therapistDto.addressProperty());
        therapistStatus.textProperty().bind(Bindings.convert(therapistDto.statusProperty()));
        therapistEmail.textProperty().bind(therapistDto.emailProperty());
        phoneNumber.textProperty().bind(therapistDto.phoneNumberProperty());
        projectStartDate.setText(""+therapistDto.getCreatedAt());
        projectIdForTasks = therapistDto.getId();

        System.out.println("This is the therapistDto id for tasks : " + projectIdForTasks);
        String ownerName;
        try {
            ownerName = therapistDto.getCreatedBy() != null ? userBO.getUserFullNameById(therapistDto.getCreatedBy()) : "Owner not specified";
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        projectOwnerName.setText(" " + ownerName);
        Image profilePic;
        try {
            profilePic = userBO.getUserProfilePicByUserId(therapistDto.getCreatedBy());
        } catch (SQLException | FileNotFoundException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        projectOwnerImg.setImage(profilePic);

        List<TeamAssignmentDTO> teamAssignments = getTeamAssignmentsForProject(therapistDto.getId());

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
        updateStyles(therapistDto);
        loadUnresolvedTasks();
    }

    public List<TeamAssignmentDTO> getTeamAssignmentsForProject(String projectId) {
        List<TeamAssignmentDTO> assignments = new ArrayList<>();
        List<ProgramsDTO> tasks ;
        try {
            tasks = tasksBO.getProgramByTherapistId(projectId);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

/*        for (ProgramsDTO task : tasks) {
            List<TeamAssignmentDTO> taskAssignments;
            try {
                taskAssignments = teamAssignmentBO.getAssignmentsByTaskId(task.getId().get());
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            assignments.addAll(taskAssignments);
        }*/

        return assignments;
    }

    private void setupStyleListeners() {
        if (therapistDTO != null) {
            therapistDTO.statusProperty().addListener((observable, oldValue, newValue) ->
                    updateStatusStyle(newValue));}
    }

    private void updateStatusStyle(TherapistStatus status) {
        therapistStatus.getStyleClass().clear();
        switch (status) {
            case AVAILABLE -> therapistStatus.getStyleClass().add("status-completed");
            case NOT_AVAILABLE -> therapistStatus.getStyleClass().add("status-cancelled");
        }
    }

    private void updateStyles(TherapistDTO project) {
        updateStatusStyle(project.getStatus());
    }

    @Override
    public void onProjectUpdated(TherapistDTO updatedProject) {
        updateStyles(updatedProject);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupRefreshButtonForTasks();
        loadUnresolvedTasks();

        if (TherapistsViewController.backgroundColor == null) {
            System.out.println("Project background color is null");
        }
        projectIcon.setStyle("-fx-background-color: " + TherapistsViewController.backgroundColor + ";");
        projectIdWith2Digits.setStyle("-fx-text-fill: " + TherapistsViewController.backgroundColor + ";");

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
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(700), z -> DashboardViewController.fadeIn(unresolvedTaskOnCurrentProject)));
            timeline.play();
            loadUnresolvedTasks();
        });
    }

    private void loadUnresolvedTasks() {
        tasksContainer.getChildren().clear();
        System.out.println("Loading unresolved tasks for therapistDto: " + projectIdForTasks);
        List<ProgramsDTO> unresolvedTasks = null;
/*        try {
            unresolvedTasks = tasksBO.getProgramsCurrentTherapistByStatus(projectIdForTasks, TaskStatus.NOT_STARTED);
            System.out.println("Unresolved tasks: " + unresolvedTasks);
            if (unresolvedTasks.isEmpty()) {
                System.out.println("No unresolved tasks found for therapistDto: " + projectIdForTasks);
                unresolvedTaskOnCurrentProject.setVisible(true);
            }
        } catch (SQLException e) {
            System.out.println("Error loading unresolved tasks: " + e.getMessage());
            CustomErrorAlert.showAlert("Error loading unresolved tasks", e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        assert unresolvedTasks != null;
        for (ProgramsDTO task : unresolvedTasks) {
            HBox taskItem = createTaskItem(task);
            tasksContainer.getChildren().add(taskItem);
        }*/
    }

    private HBox createTaskItem(ProgramsDTO task) {
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
            PROJECT_ID = projectsBO.getTherapistIdByTaskId(task.idProperty().get());
        } catch (SQLException e) {
            System.out.println("Error fetching therapistDto ID: " + e.getMessage());
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

    private void handleTaskClick(ProgramsDTO task) {
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

            TherapistEditViewController controller = loader.getController();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/nav-buttons/therapist-view.fxml"));
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
            CustomErrorAlert.showAlert("Error", "Failed to load the therapistDto view.");
        }
    }

    public void newIssueClick(MouseEvent mouseEvent) {
        TherapistsViewController.bindNavigation(projectSID, "/view/nav-buttons/issue/issue-create-view.fxml");
    }
}
