package com.dinidu.lk.pmt.controller;

import com.dinidu.lk.pmt.controller.dashboard.NotifyViewController;
import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.custom.impl.QueryDAOImpl;

import com.dinidu.lk.pmt.utils.ProfileImageManager;
import com.dinidu.lk.pmt.utils.SessionUser;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DashboardViewController extends BaseController {

    public ImageView settingId;
    public ImageView mainIcon;
    public AnchorPane projectsDisplayCard;
    public AnchorPane pieChartDisplayCard;
    public AnchorPane taskDisplayCard;
    public PieChart assigneesPieChart;
    public VBox legendContainer;
    public ImageView refreshButton;
    public ImageView refreshButtonForTasks;
    public VBox tasksContainer;
    public ScrollPane scrollPane;
    public Label totalProjectsLabel;
    public VBox ongoingProjectsContainer;
    public ImageView refreshButtonForProjects;
    public Button therapistsButton;
    public Button programButton;
    public Button patientButton;
    public Button dashboardButton;
    @FXML
    private ImageView profileImageIcon;
    public AnchorPane dashboardPage;
    public AnchorPane contentPane;
    private final List<Button> navButtons = new ArrayList<>();
    private Button activeButton;
    private AnchorPane currentNotification;
    private boolean isNotificationVisible = false;


    QueryDAO queryDAO = new QueryDAOImpl();

    public void mainIconOnClick() {
        contentPane.getChildren().clear();
        contentPane.getChildren().addAll(projectsDisplayCard,taskDisplayCard,pieChartDisplayCard);
        showCardContainers();
        setupRefreshButtonForTasks();
        loadUnresolvedTasks();
        loadOngoingProjects();
    }

    private void showCardContainers() {
        Timeline timeline = new Timeline(
                new javafx.animation.KeyFrame(Duration.ZERO, e -> fadeIn(projectsDisplayCard)),
                new javafx.animation.KeyFrame(Duration.millis(700), e -> fadeIn(taskDisplayCard)),
                new javafx.animation.KeyFrame(Duration.millis(1400), e -> fadeIn(pieChartDisplayCard))
        );
        timeline.play();
    }

    public static void fadeIn(Node node) {
        node.setVisible(true);
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(700), node);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
    }

    private void hideCardContainers() {
        projectsDisplayCard.setVisible(false);
        pieChartDisplayCard.setVisible(false);
        taskDisplayCard.setVisible(false);
    }

    public void initialize() {
        setupScrollPane();
        loadOngoingProjects();
        setupTasksView();
        setupRefreshButtonForTasks();
        loadUnresolvedTasks();
        mainIconOnClick();
        navButtonStyle();
        setupRefreshButtonForPieChart();
        setupRefreshButtonForProjects();
        
        ProfileImageManager.addListener(newImage -> {
            profileImageIcon.setImage(newImage);
            Circle clip = new Circle(36.5, 36.5, 36.5);
            profileImageIcon.setClip(clip);
        });

        userAccessController();
    }

    private void loadOngoingProjects() {
        System.out.println("Loading ongoing projects...");
    }

    private void setupScrollPane() {
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("custom-scroll-pane");
    }

    private void setupTasksView() {
        tasksContainer.setSpacing(8);
        tasksContainer.setPadding(new Insets(8));
    }

    private void loadUnresolvedTasks() {
        System.out.println("Loading unresolved tasks...");
    }

    private void setupRefreshButtonForPieChart() {
        refreshButton.setOnMouseClicked(event -> System.out.println("Refresh button clicked for pie chart"));
    }

    private void setupRefreshButtonForProjects(){
        refreshButtonForProjects.setOnMouseClicked(event -> {
            System.out.println("Refresh button clicked for Projects");
            Timeline timeline = new Timeline(new javafx.animation.KeyFrame(Duration.millis(700), z -> fadeIn(projectsDisplayCard)));
            timeline.play();
            loadUnresolvedTasks();
        });
    }

    private void setupRefreshButtonForTasks() {
        refreshButtonForTasks.setOnMouseClicked(e -> {
            System.out.println("Refresh button clicked for tasks");
            Timeline timeline = new Timeline(new javafx.animation.KeyFrame(Duration.millis(700), z -> fadeIn(taskDisplayCard)));
            timeline.play();
            loadUnresolvedTasks();
        });
    }

    private void navButtonStyle() {
        navButtons.add((Button) dashboardPage.lookup("#projectsButton"));
        navButtons.add((Button) dashboardPage.lookup("#issuesButton"));
        navButtons.add((Button) dashboardPage.lookup("#timesheetsButton"));
        navButtons.add((Button) dashboardPage.lookup("#taskButton"));
        navButtons.add((Button) dashboardPage.lookup("#reportsButton"));
        navButtons.add((Button) dashboardPage.lookup("#dashboardButton"));
    }

    private void handleButtonClick(Button clickedButton) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("active");
        }

        clickedButton.getStyleClass().add("active");
        activeButton = clickedButton;
    }

    private void userAccessController() {
/*        String username = "dinidu"; //dev purpose
        System.out.println("Username is logged in Now in Dashboard: " + username);
        SessionUser.initializeSession(username);*/

        String username = SessionUser.getLoggedInUsername();
        System.out.println("Username is logged in Now in Dashboard: " + username);
        SessionUser.initializeSession(username);

        String u = SessionUser.getLoggedInUsername();
        if (username == null) {
            System.out.println("User not logged in. username: " + u);
        }

        UserRole userRoleByUsername;
        try {
            userRoleByUsername = queryDAO.getUserRoleByUsername(u);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (userRoleByUsername == null) {
            System.out.println("User not logged in. userRoleByUsername: " + null);
        }

        if (userRoleByUsername != UserRole.ADMIN) {
            System.out.println("Access denied: Only Admin");
            settingId.setDisable(true);
        }
    }

    public void notifyClick() {
        if (NotifyViewController.isCurrentlyAnimating()) {
            return;
        }

        try {
            if (isNotificationVisible && currentNotification != null) {
                dismissCurrentNotification();
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/nav-buttons/notify-view.fxml"));
            currentNotification = loader.load();

            if (contentPane != null) {
                contentPane.getChildren().add(currentNotification);
                isNotificationVisible = true;
                setupAutoDismiss();
            }
        } catch (IOException e) {
            System.out.println("Error loading notification view: " + e.getMessage());
        }
    }

    private void dismissCurrentNotification() {
        if (currentNotification != null) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), currentNotification);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> {
                contentPane.getChildren().remove(currentNotification);
                currentNotification = null;
                isNotificationVisible = false;
            });
            fadeOut.play();
        }
    }

    private void setupAutoDismiss() {
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(Duration.seconds(30), event -> {
                    if (isNotificationVisible && currentNotification != null) {
                        dismissCurrentNotification();
                    }
                })
        );
        timeline.play();
    }

    public void myProfile() {
        hideCardContainers();
        navigateTo("/view/nav-buttons/profile-view.fxml");
    }

    public void clickOnReports(ActionEvent actionEvent) {
        handleButtonClick((Button) actionEvent.getSource());
        hideCardContainers();
        navigateTo("/view/nav-buttons/report-view.fxml");
    }

    public void clickOnProgram(ActionEvent actionEvent) {
        handleButtonClick((Button) actionEvent.getSource());
        hideCardContainers();
        navigateTo("/view/nav-buttons/program-view.fxml");
    }

    public void clickOnDashboard(ActionEvent actionEvent) {
        handleButtonClick((Button) actionEvent.getSource());
        hideCardContainers();
        navigateTo("/view/nav-buttons/dashboard-expand-view.fxml");
    }

    public void clickOnTherapists(ActionEvent actionEvent) {
        handleButtonClick((Button) actionEvent.getSource());
        hideCardContainers();
        navigateTo("/view/nav-buttons/therapist-view.fxml");
    }

    public void clickOnTimesheets(ActionEvent actionEvent) {
        handleButtonClick((Button) actionEvent.getSource());
        hideCardContainers();
        navigateTo("/view/nav-buttons/timesheet-view.fxml");
    }

    public void settingsClick() {
        hideCardContainers();
        navigateTo("/view/nav-buttons/settings-view.fxml");
    }

    public void navigateTo(String fxmlPath) {
        try {
            contentPane.getChildren().clear();
            AnchorPane load = FXMLLoader.load(getClass().getResource(fxmlPath));

            load.prefWidthProperty().bind(contentPane.widthProperty());
            load.prefHeightProperty().bind(contentPane.heightProperty());

            FadeTransition fadeIn = new FadeTransition(Duration.millis(750), load);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();

            contentPane.getChildren().add(load);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load page: " + e.getMessage());
            new Alert(Alert.AlertType.ERROR, "Failed to load page!").show();
        }
    }

    public void clickOnPatients() {
        handleButtonClick((Button) dashboardPage.lookup("#issuesButton"));
        hideCardContainers();
        navigateTo("/view/nav-buttons/patients-view.fxml");
    }
}
