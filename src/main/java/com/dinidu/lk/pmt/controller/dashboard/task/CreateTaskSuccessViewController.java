package com.dinidu.lk.pmt.controller.dashboard.task;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.*;
import com.dinidu.lk.pmt.controller.dashboard.TherapistsViewController;
import com.dinidu.lk.pmt.controller.dashboard.task.checklist.ChecklistCreateViewController;
import com.dinidu.lk.pmt.controller.dashboard.task.checklist.ChecklistEditViewController;
import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.custom.impl.QueryDAOImpl;
import com.dinidu.lk.pmt.dto.ChecklistDTO;
import com.dinidu.lk.pmt.dto.TherapistDTO;
import com.dinidu.lk.pmt.dto.ProgramsDTO;
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
import javafx.beans.binding.Bindings;
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
import java.text.SimpleDateFormat;
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
    private ProgramsDTO programsDTO;
    static ProgramsDTO current_Task;

    UserBO userBO= (UserBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.USER);

    QueryDAO queryDAO = new QueryDAOImpl();

    TherapistsBO therapistsBO = (TherapistsBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.TherapistsBO);

    ProgramsBO programsBO = (ProgramsBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.ProgramsBO);

/*
    ChecklistBO checklistBO = (ChecklistBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.CHECKLISTS);
*/

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        noChecklistLabel.setVisible(false);
        ProgramsDTO activeTask = CreateTaskSuccessViewController.current_Task;
        if (activeTask != null) {
            this.programsDTO = activeTask;
            setTaskData(programsDTO);
            System.out.println("Active task is set, taskDTO is: " + programsDTO);
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

        sortBy.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateResetButtonVisibility();
            loadChecklists();
        });

        filterStatus.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateResetButtonVisibility();
            loadChecklists();
        });

        checklistSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            updateResetButtonVisibility();
            loadChecklists();
        });

        if (programsDTO == null) {
            System.out.println("Error: taskDTO is null");
            return;
        }

        applyInitialStyles();
    }

    private void applyInitialStyles() {
/*        List<ChecklistDTO> checklists;
        try {
            checklists = checklistBO.getChecklistsByTaskId(tasksDTO.getId());
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        assert checklists != null;
        for (ChecklistDTO checklist : checklists) {
            AnchorPane checklistPane = createChecklistItemPane(checklist);
            checklistContainer.getChildren().add(checklistPane);
        }*/
    }

    private AnchorPane createChecklistItemPane(ChecklistDTO checklist) {
        AnchorPane itemPane = new AnchorPane();
        itemPane.setPrefHeight(80);
        itemPane.setPrefWidth(538);
        itemPane.getStyleClass().add("checklist-item");

        Label nameLabel = new Label();
        nameLabel.textProperty().bind(checklist.nameProperty());
        nameLabel.setLayoutX(15);
        nameLabel.setLayoutY(10);
        nameLabel.getStyleClass().add("checklist-name");
        nameLabel.setMaxWidth(350);

        Label descLabel = new Label();
        descLabel.textProperty().bind(checklist.descriptionProperty());
        descLabel.setLayoutX(15);
        descLabel.setLayoutY(35);
        descLabel.getStyleClass().add("checklist-description");

        Label statusLabel = new Label();
        statusLabel.textProperty().bind(checklist.statusProperty().asString());
        statusLabel.setLayoutX(400);
        statusLabel.setLayoutY(10);
        statusLabel.getStyleClass().add("checklist-status");

        Label priorityLabel = new Label();
        priorityLabel.textProperty().bind(checklist.priorityProperty().asString());
        priorityLabel.setLayoutX(400);
        priorityLabel.setLayoutY(35);
        priorityLabel.getStyleClass().add("checklist-priority");

        ChecklistStatus currentStatus = checklist.statusProperty().get();
        if (currentStatus != null) {
            String statusStyle = "status-" + currentStatus.name().toLowerCase().replace("_", "-") + "-checklist";
            statusLabel.getStyleClass().add(statusStyle);
        }

        ChecklistPriority currentPriority = checklist.priorityProperty().get();
        if (currentPriority != null) {
            String priorityStyle = "priority-" + currentPriority.name().toLowerCase().replace("_", "-") + "-checklist";
            priorityLabel.getStyleClass().add(priorityStyle);
        }

        checklist.statusProperty().addListener((observable, oldValue, newValue) -> {
            statusLabel.getStyleClass().removeIf(style ->
                    style.startsWith("status-") && style.endsWith("-checklist"));
            if (newValue != null) {
                String newStyle = "status-" + newValue.name().toLowerCase().replace("_", "-") + "-checklist";
                statusLabel.getStyleClass().add(newStyle);
            }
        });

        checklist.priorityProperty().addListener((observable, oldValue, newValue) -> {
            priorityLabel.getStyleClass().removeIf(style ->
                    style.startsWith("priority-") && style.endsWith("-checklist"));
            if (newValue != null) {
                String newStyle = "priority-" + newValue.name().toLowerCase().replace("_", "-") + "-checklist";
                priorityLabel.getStyleClass().add(newStyle);
            }
        });

        itemPane.getChildren().addAll(nameLabel, descLabel, statusLabel, priorityLabel);
        itemPane.setOnMouseClicked(event -> openChecklistEditModal(checklist));

        return itemPane;
    }

    public void openChecklistEditModal(ChecklistDTO checklist) {
        try {
            String fxmlPath = "/view/nav-buttons/task/checklist/checklist-edit-view.fxml";

            Stage modalStage = new Stage();
            modalStage.initStyle(StageStyle.TRANSPARENT);
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner((Stage) taskCreatedSuccessPage.getScene().getWindow());

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            ChecklistEditViewController controller = loader.getController();

            controller.setChecklistData(checklist);
            System.out.println("Current Checklist in Success Page: " + checklist.toString());
            controller.setDeletionHandler(this);
            controller.setUpdateListener(this);

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
            modalStage.setScene(scene);
            modalStage.centerOnScreen();
            modalStage.show();

        } catch (Exception e) {
            System.out.println("Error while loading checklist edit modal: " + e.getMessage());
            e.printStackTrace();
            CustomErrorAlert.showAlert("Error", "Error while loading checklist edit modal.");
        }
    }

    private void updateResetButtonVisibility() {
        boolean hasFilters = sortBy.getValue() != null ||
                filterStatus.getValue() != null ||
                !checklistSearch.getText().isEmpty();
        resetFilterBtn.setVisible(hasFilters);
    }

    private void loadChecklists() {
/*        ChecklistStatus statusFilter = filterStatus.getValue();
        ChecklistPriority priorityFilter = sortBy.getValue();
        String searchQuery = checklistSearch.getText().toLowerCase().trim();

        List<ChecklistDTO> checklists;
        try {
            checklists = checklistBO.getChecklistsByTaskId(tasksDTO.getId());
            System.out.println("Fetched checklists: " + checklists);
        } catch (SQLException e) {
            System.out.println("Error fetching checklists: " + e.getMessage());
            CustomErrorAlert.showAlert("Error", "Failed to fetch checklists.");
            return;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (checklists.isEmpty()) {
            showNoChecklists();
            return;
        }

        List<ChecklistDTO> filteredChecklists = checklists.stream()
                .filter(checklist -> statusFilter == null || checklist.statusProperty().get().equals(statusFilter))
                .filter(checklist -> priorityFilter == null || checklist.priorityProperty().get().equals(priorityFilter))
                .filter(checklist -> searchQuery.isEmpty() ||
                        checklist.nameProperty().get().toLowerCase().contains(searchQuery) ||
                        checklist.descriptionProperty().get().toLowerCase().contains(searchQuery))
                .collect(Collectors.toList());

        if (filteredChecklists.isEmpty()) {
            showNoChecklists();
        } else {
            updateChecklistDisplay(filteredChecklists);
        }*/
    }

    private void updateChecklistDisplay(List<ChecklistDTO> checklists) {
        checklistContainer.getChildren().clear();
        if (checklists.isEmpty()) {
            showNoChecklists();
            return;
        }

        noChecklistLabel.setVisible(false);
        for (ChecklistDTO checklist : checklists) {
            AnchorPane checklistItem = createChecklistItemPane(checklist);
            checklistContainer.getChildren().add(checklistItem);
        }
    }

    private void showNoChecklists() {
        checklistContainer.getChildren().clear();
        noChecklistLabel.setVisible(true);
        if (!checklistContainer.getChildren().contains(noChecklistLabel)) {
            checklistContainer.getChildren().add(noChecklistLabel);
        }
    }

    public void resetClick() {
        sortBy.setValue(null); 
        filterStatus.setValue(null);
        checklistSearch.clear();
        loadChecklists();
    }

    public void updateTaskView(ProgramsDTO programsDTO) {
        setTaskData(programsDTO);
        loadChecklists();
    }

    @Override
    public void onChecklistDeleted() {
        if(CreateTaskSuccessViewController.current_Task == null){
            System.out.println("Error: current_Task is null");
        }
        System.out.println("Task: " + CreateTaskSuccessViewController.current_Task);
        updateTaskView(CreateTaskSuccessViewController.current_Task);
    }

    @Override
    public void onChecklistUpdated(ChecklistDTO updatedChecklist) {
        setTaskData(programsDTO);
        loadChecklists();
    }

    public void createCheckList() {
        ChecklistCreateViewController.setTaskId(programsDTO.getId());
        TherapistsViewController.bindNavigation(taskCreatedSuccessPage, "/view/nav-buttons/task/checklist/checklist-create-view.fxml");

        ProgramsDTO updatedProgramsDTO = CreateTaskSuccessViewController.current_Task;
        updateTaskView(updatedProgramsDTO);
        if(updatedProgramsDTO == null){
            System.out.println("Error: updatedTaskDTO is null");
        }else {
            System.out.println("Task: " + updatedProgramsDTO);
        }
    }

    public void setTaskData(ProgramsDTO programsDTO) {
        if (programsDTO == null) {
            System.out.println("Error: taskDTO is null");
            return;
        }

        System.out.println("Now in set Task data method : " + programsDTO);
        System.out.println("Here is the start date: " + programsDTO.getCreatedAt().get());

        if (programsDTO.getId() == null) {
            return;
        }

        this.programsDTO = programsDTO;
        current_Task = programsDTO;
        System.out.println("Static current task: " + current_Task);

        taskName.textProperty().bind(programsDTO.nameProperty());
        taskDescription.textProperty().bind(programsDTO.descriptionProperty());

        taskStartDate.textProperty().bind(Bindings.createStringBinding(
                () -> {
                    if (programsDTO.getCreatedAt() != null && programsDTO.getCreatedAt().get() != null) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        return dateFormat.format(programsDTO.getCreatedAt().get());
                    } else {
                        return "Start date not set";
                    }
                },
                programsDTO.createdAtProperty()
        ));

        taskDeadline.textProperty().bind(Bindings.createStringBinding(
                () -> {
                    if (programsDTO.getDueDate() != null && programsDTO.getDueDate().get() != null) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        return dateFormat.format(programsDTO.getDueDate().get());
                    } else {
                        return "End date is not set";
                    }
                },
                programsDTO.dueDateProperty()
        ));

        taskStatus.textProperty().bind(Bindings.convert(programsDTO.statusProperty()));
        taskPriority.textProperty().bind(Bindings.convert(programsDTO.priorityProperty()));
        String projectId = programsDTO.getProjectId().get();
        List<TherapistDTO> project;
        try {
            project = therapistsBO.getTherapistById(projectId);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
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
        List<ProgramsDTO> tasks;
        try {
            tasks = programsBO.getProgramByTherapistId(programsDTO.projectIdProperty().get());

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
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
        setupStyleListeners();
        updateStyles(programsDTO);
    }

    private void setupStyleListeners() {
        programsDTO.statusProperty().addListener((observable, oldValue, newValue) -> updateStatusStyle(newValue));
        programsDTO.priorityProperty().addListener((observable, oldValue, newValue) -> updatePriorityStyle(newValue));
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

    private void updateStyles(ProgramsDTO programsDTO) {
        updateStatusStyle(programsDTO.getStatus());
        updatePriorityStyle(programsDTO.getPriority());
    }

    @Override
    public void onTaskUpdated(ProgramsDTO updatedTask) {
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
}
