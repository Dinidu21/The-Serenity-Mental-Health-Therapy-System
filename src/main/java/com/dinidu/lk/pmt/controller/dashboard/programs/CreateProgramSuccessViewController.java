package com.dinidu.lk.pmt.controller.dashboard.programs;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.*;
import com.dinidu.lk.pmt.controller.dashboard.TherapistsViewController;
import com.dinidu.lk.pmt.controller.dashboard.programs.checklist.ChecklistEditViewController;
import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.custom.impl.QueryDAOImpl;
import com.dinidu.lk.pmt.dto.TherapyProgramsDTO;
import com.dinidu.lk.pmt.dto.TherapySessionsDTO;
import com.dinidu.lk.pmt.entity.TherapySessions;
import com.dinidu.lk.pmt.utils.*;
import com.dinidu.lk.pmt.utils.checklistTypes.ChecklistPriority;
import com.dinidu.lk.pmt.utils.checklistTypes.ChecklistStatus;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.listeners.ChecklistDeletionHandler;
import com.dinidu.lk.pmt.utils.listeners.ChecklistUpdateListener;
import com.dinidu.lk.pmt.utils.listeners.TaskDeletionHandler;
import com.dinidu.lk.pmt.utils.listeners.TaskUpdateListener;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
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
import lombok.Setter;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CreateProgramSuccessViewController implements Initializable, TaskDeletionHandler, TaskUpdateListener , ChecklistUpdateListener, ChecklistDeletionHandler {
    public Button resetFilterBtn;
    public VBox checklistContainer;
    public ComboBox<ChecklistPriority> sortBy;
    public ComboBox<ChecklistStatus> filterStatus;
    public Button createCheckListBtn;
    public TextField checklistSearch;
    public Label noChecklistLabel;
    public Label ProgramFee;
    public Label ProgramDuration;
    public AnchorPane checklistViewPane;
    private double xOffset = 0;
    private double yOffset = 0;
    public ImageView moreIcon;
    public AnchorPane taskIcon;
    public AnchorPane sideBar;
    public AnchorPane mainBar;
    public AnchorPane taskCreatedSuccessPage;
    public Label projectOwnerName;
    public Label teamCount;
    public Label teamMember1;
    public Label teamMember2;
    public Label teamMember3;
    public Label teamMember4;
    public Label ProgramIdWith2Digits;
    public ImageView projectOwnerImg;
    public ImageView teamMember1Img;
    public ImageView teamMember2Img;
    public ImageView teamMember3Img;
    public ImageView teamMember4Img;
    @FXML
    private Label ProgramName;
    @FXML
    private Label ProgramId;
    private TherapyProgramsDTO tasksDTO;
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

    TherapySessionsBO sessionsBO = (TherapySessionsBO)
            BOFactory.getInstance().getBO(BOFactory.BOTypes.SESSIONS);

    PatientBO patientsBO = (PatientBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.PATIENTS);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        noChecklistLabel.setVisible(false);
        loadChecklists();
        TherapyProgramsDTO activeTask = CreateProgramSuccessViewController.current_Task;
        if (activeTask != null) {
            this.tasksDTO = activeTask;
            setTaskData(tasksDTO);
            System.out.println("Active task is set, taskDTO is: " + tasksDTO);
        } else {
            System.out.println("No active task is set, taskDTO remains null");
        }

        resetFilterBtn.setVisible(false);
        colorSetter();
        userAccessControl();

        sortBy.setValue(null);
        filterStatus.setValue(null);
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

        if (tasksDTO == null) {
            System.out.println("Error: taskDTO is null");
        }
    }

    private void updateResetButtonVisibility() {
        boolean hasFilters = sortBy.getValue() != null ||
                filterStatus.getValue() != null ||
                !checklistSearch.getText().isEmpty();
        resetFilterBtn.setVisible(hasFilters);
    }

    private void updateChecklistDisplay(List<TherapySessionsDTO> sessionsDTOS) {
        checklistContainer.getChildren().clear();
        if (sessionsDTOS.isEmpty()) {
            showNoChecklists();
            return;
        }

        noChecklistLabel.setVisible(false);
        for (TherapySessionsDTO therapySessionsDTO : sessionsDTOS) {
            AnchorPane checklistItem = createChecklistItemPane(therapySessionsDTO);
            checklistContainer.getChildren().add(checklistItem);
        }
    }

    private AnchorPane createChecklistItemPane(TherapySessionsDTO therapySessionsDTO) {
        AnchorPane itemPane = new AnchorPane();
        itemPane.setPrefHeight(80);
        itemPane.setPrefWidth(508);
        itemPane.getStyleClass().add("checklist-item");

        Label nameLabel = new Label();

        nameLabel.textProperty().bind(therapySessionsDTO.descriptionProperty());
        nameLabel.setLayoutX(15);
        nameLabel.setLayoutY(10);
        nameLabel.getStyleClass().add("checklist-name");
        nameLabel.setMaxWidth(350);

        Label descLabel = new Label();
        descLabel.textProperty().bind(therapySessionsDTO.sessionTimeProperty());
        descLabel.setLayoutX(15);
        descLabel.setLayoutY(35);
        descLabel.getStyleClass().add("checklist-description");

        Label statusLabel = new Label();
        statusLabel.textProperty().bind(therapySessionsDTO.statusProperty().asString());
        statusLabel.setLayoutX(400);
        statusLabel.setLayoutY(10);
        statusLabel.getStyleClass().add("checklist-status");

/*
        Label priorityLabel = new Label();
        priorityLabel.textProperty().bind(therapySessionsDTO.therapistIdProperty());
        priorityLabel.setLayoutX(400);
        priorityLabel.setLayoutY(35);
        priorityLabel.getStyleClass().add("checklist-priority");

        TherapySessions.SessionStatus currentStatus = therapySessionsDTO.statusProperty().get();
        if (currentStatus != null) {
            String statusStyle = "status-" + currentStatus.name().toLowerCase().replace("_", "-") + "-checklist";
            statusLabel.getStyleClass().add(statusStyle);
        }

        therapySessionsDTO.statusProperty().addListener((observable, oldValue, newValue) -> {
            statusLabel.getStyleClass().removeIf(style ->
                    style.startsWith("status-") && style.endsWith("-checklist"));
            if (newValue != null) {
                String newStyle = "status-" + newValue.name().toLowerCase().replace("_", "-") + "-checklist";
                statusLabel.getStyleClass().add(newStyle);
            }
        });
*/

        itemPane.getChildren().addAll(nameLabel, descLabel, statusLabel);
        itemPane.setOnMouseClicked(event -> openChecklistEditModal(therapySessionsDTO));

        return itemPane;
    }

    public void openChecklistEditModal( TherapySessionsDTO sessionsDTO) {
        try {
            String fxmlPath = "/view/nav-buttons/task/checklist/checklist-edit-view.fxml";

            Stage modalStage = new Stage();
            modalStage.initStyle(StageStyle.TRANSPARENT);
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner((Stage) taskCreatedSuccessPage.getScene().getWindow());

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            ChecklistEditViewController controller = loader.getController();

            controller.setChecklistData(sessionsDTO);
            System.out.println("Current Checklist in Success Page: " + sessionsDTO.toString());
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

    public void setTaskData(TherapyProgramsDTO therapyProgramsDTO) {
        if (therapyProgramsDTO == null) {
            System.out.println("Error: taskDTO is null");
            return;
        }

        System.out.println("Now in set Task data method : " + therapyProgramsDTO);

        if (therapyProgramsDTO.getProgramId() ==0L) {
            return;
        }

        this.tasksDTO = therapyProgramsDTO;
        current_Task = therapyProgramsDTO;
        System.out.println("Static current task: " + current_Task);

        ProgramName.textProperty().bind(therapyProgramsDTO.nameProperty());
        ProgramFee.textProperty().bind(therapyProgramsDTO.feeProperty());
        ProgramDuration.textProperty().bind(therapyProgramsDTO.durationProperty());
        ProgramId.textProperty().bind(therapyProgramsDTO.programIdProperty().asString());

        System.out.println("\nInitializing task data: " + therapyProgramsDTO);
        System.out.println("Program ID: " + therapyProgramsDTO.getProgramId());
        System.out.println("Program Fee: " + therapyProgramsDTO.getFee());
        System.out.println("Program Duration: " + therapyProgramsDTO.getDuration());
        System.out.println("Program Name: " + therapyProgramsDTO.getName() +"\n");


        long id = therapyProgramsDTO.getProgramId();
        System.out.println("Program ID: " + id);
        if (id == 0 || id == -1) {
            System.out.println("Program ID is null");
            return;
        }

        List<TherapyProgramsDTO> therapyProgramsDTOS;
        try {
            therapyProgramsDTOS = programsBO.getProgramById(id);
            System.out.println("\nFetched program by ID In Controller: " + therapyProgramsDTOS);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        String userFullNameById = "";

        if (!therapyProgramsDTOS.isEmpty()) {
            TherapyProgramsDTO projectDTO = therapyProgramsDTOS.get(0);
            System.out.println("Project DTO: " + projectDTO);
            ProgramId.textProperty().bind(therapyProgramsDTO.programIdProperty().asString());
            ProgramIdWith2Digits.textProperty().bind(therapyProgramsDTO.programIdProperty().asString());
        } else {
            System.out.println("No project found with the given ID.");
        }
        projectOwnerName.setText(" " + userFullNameById);
    }

    @Override
    public void onTaskUpdated(TherapyProgramsDTO updatedTask) {
        setTaskData(updatedTask);
    }

    @Override
    public void onChecklistUpdated(TherapySessionsDTO therapySessionsDTO) {
        setTaskData(tasksDTO);
        loadChecklists();
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

            ProgramEditViewController controller = loader.getController();

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
    
    private void colorSetter() {
        if (TherapistsViewController.backgroundColor == null) {
            System.out.println("Project background color is null");
        }
        taskIcon.setStyle("-fx-background-color: " + TherapistsViewController.backgroundColor + ";");
        ProgramIdWith2Digits.setStyle("-fx-text-fill: " + TherapistsViewController.backgroundColor + ";");
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

    public void updateTaskView(TherapyProgramsDTO therapyProgramsDTO) {
        System.out.println("Updating task view with new data: " + therapyProgramsDTO);
        if (therapyProgramsDTO == null) {
            System.out.println("Error: therapyProgramsDTO is null");
            return;
        }
        setTaskData(therapyProgramsDTO);
    }

    public void createCheckList() {
        TherapistsViewController.bindNavigation(taskCreatedSuccessPage, "/view/nav-buttons/task/checklist/checklist-create-view.fxml");
    }

    public void resetClick() {
        sortBy.setValue(null);
        filterStatus.setValue(null);
        checklistSearch.clear();
        loadChecklists();
    }

    private void loadChecklists() {
        System.out.println("\nLoading checklists...");
        ChecklistStatus statusFilter = filterStatus.getValue();
        ChecklistPriority priorityFilter = sortBy.getValue();
        String searchQuery = checklistSearch.getText().toLowerCase().trim();

        List<TherapySessionsDTO> therapyProgramsDTOS;
        try {
            therapyProgramsDTOS = sessionsBO.getAllSessions();
            System.out.println("Fetched checklists: " + therapyProgramsDTOS);
        } catch (SQLException e) {
            System.out.println("Error fetching checklists: " + e.getMessage());
            CustomErrorAlert.showAlert("Error", "Failed to fetch checklists.");
            return;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (therapyProgramsDTOS.isEmpty()) {
            showNoChecklists();
        }

        List<TherapySessionsDTO> filteredChecklists = therapyProgramsDTOS.stream()
                .filter(checklist -> statusFilter == null || checklist.statusProperty().get().equals(statusFilter))
                .filter(checklist -> searchQuery.isEmpty() ||
                        checklist.getDescription().toLowerCase().contains(searchQuery) ||
                        checklist.descriptionProperty().get().toLowerCase().contains(searchQuery))
                .collect(Collectors.toList());

        if (filteredChecklists.isEmpty()) {
            showNoChecklists();
        } else {
            updateChecklistDisplay(filteredChecklists);
        }
    }

    private void showNoChecklists() {
        checklistContainer.getChildren().clear();
        noChecklistLabel.setVisible(true);
        if (!checklistContainer.getChildren().contains(noChecklistLabel)) {
            checklistContainer.getChildren().add(noChecklistLabel);
        }
    }

    @Override
    public void onChecklistDeleted() {
        if(CreateProgramSuccessViewController.current_Task == null){
            System.out.println("Error: current_Task is null");
        }
        System.out.println("Program: " + CreateProgramSuccessViewController.current_Task);
        updateTaskView(CreateProgramSuccessViewController.current_Task);
    }
}
