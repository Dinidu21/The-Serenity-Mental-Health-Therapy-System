package com.dinidu.lk.pmt.controller.dashboard;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.PatientBO;
import com.dinidu.lk.pmt.controller.BaseController;
import com.dinidu.lk.pmt.controller.dashboard.issue.IssueEditViewController;
import com.dinidu.lk.pmt.controller.dashboard.issue.CreateIssueSuccessViewController;
import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.custom.impl.QueryDAOImpl;
import com.dinidu.lk.pmt.dto.PatientsDTO;
import com.dinidu.lk.pmt.utils.SessionUser;
import com.dinidu.lk.pmt.utils.issuesTypes.IssuePriority;
import com.dinidu.lk.pmt.utils.issuesTypes.IssueStatus;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class PatientsViewController extends BaseController implements Initializable {
    @FXML
    public Label createLabel;
    @FXML
    public ImageView searchImg;
    @FXML
    public TextField searchBox;
    public AnchorPane issuesPage;
    @FXML
    public VBox projectIssuesContainer;
    public Label noIssuesLabel;
    public ScrollPane scrollPane;
    public Button createIssuesBtn;
    public Label noIssuesFoundLabel;
    public Button resetBTN;
    public ComboBox<String> sortByProjectName;
    @FXML
    private ComboBox<IssueStatus> sortByStatus;
    @FXML
    private ComboBox<IssuePriority> priorityDropdown;
    @FXML
    private ListView<String> suggestionList;

    PatientBO patientBO =
            (PatientBO) BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.PATIENTS);

    QueryDAO queryDAO = new QueryDAOImpl();


    private void openIssue(PatientsDTO patientsDTO) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/nav-buttons/issue/issue-create-success-view.fxml"));
            Parent root = loader.load();

            CreateIssueSuccessViewController controller = loader.getController();
            controller.setIssuesData(patientsDTO);

            IssueEditViewController.setIssue(patientsDTO);

            FXMLLoader editLoader = new FXMLLoader(getClass().getResource("/view/nav-buttons/issue/issue-edit-view.fxml"));
            Parent editRoot = editLoader.load();

            IssueEditViewController editController = editLoader.getController();
            if (editController != null) {
                editController.setUpdateListener(controller);
            } else {
                System.out.println("Failed to load the IssueEditViewController.");
            }

            issuesPage.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load the FXML files: " + e.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        noIssuesLabel.setVisible(false);
        createLabel.setVisible(false);
        noIssuesFoundLabel.setVisible(false);
        updateIssuesView();
        searchBox.textProperty().addListener((observable, oldValue, newValue) -> {
            searchImg.setVisible(newValue.isEmpty());
            if (!newValue.isEmpty()) {
                showSearchSuggestions(newValue);
            } else {
                suggestionList.setVisible(false);
                updateIssuesView();
            }
        });

        searchBox.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String projectName = searchBox.getText().trim();
                if (!projectName.isEmpty()) {
                    try {
                        patientBO.searchPatientsByName(projectName);
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    event.consume();
                }
            }
        });
        suggestionList.setOnMouseClicked(event -> {
            String selectedIssueName = suggestionList.getSelectionModel().getSelectedItem();
            if (selectedIssueName != null) {
                searchBox.setText(selectedIssueName);
                suggestionList.setVisible(false);
                List<PatientsDTO> filteredIssues = null;
                try {
                    filteredIssues = patientBO.searchPatientsByName(selectedIssueName);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

                assert filteredIssues != null;

                if (filteredIssues.isEmpty()) {
                    noIssuesFoundLabel.setVisible(true);
                } else {
                    noIssuesFoundLabel.setVisible(false);
                }

                displayIssues(filteredIssues);
            }
        });

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
            System.out.println("Access denied: Only Admin, Project Manager, or Product Owner can create issues.");
            createIssuesBtn.setDisable(true);
        }
    }

    private void updateIssuesView() {
        List<PatientsDTO> patientsDTOS = null;
        try {
            patientsDTOS = patientBO.getAllPatients();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


        if (patientsDTOS == null) {
            System.out.println("Failed to fetch patients.");
            noIssuesLabel.setVisible(true);
            createLabel.setVisible(true);
            noIssuesFoundLabel.setVisible(true);
            return;
        }

        if (patientsDTOS.isEmpty()) {
            noIssuesLabel.setVisible(true);
            createLabel.setVisible(true);
            System.out.println("No Patients found.");
            projectIssuesContainer.setVisible(false);
            return;
        }
        displayIssues(patientsDTOS);
    }

    public void searchIssuesOnClick() {
        searchImg.setDisable(true);
    }

    private void displayIssues(List<PatientsDTO> patientsDTOS) {
        projectIssuesContainer.getChildren().clear();
        projectIssuesContainer.getStyleClass().add("project-card-container");

        for (PatientsDTO issue : patientsDTOS) {
            AnchorPane projectCard = new AnchorPane();
            projectCard.getStyleClass().add("project-card");
            projectCard.setPrefHeight(120.0);
            projectCard.setPrefWidth(1322.0);

            StackPane projectIcon = new StackPane();
            projectIcon.setPrefWidth(60.0);
            projectIcon.setPrefHeight(60.0);
            projectIcon.setLayoutX(20);
            projectIcon.setLayoutY(30);
            projectIcon.getStyleClass().add("project-icon");

            TherapistsViewController.backgroundColor = generateRandomColor();
            projectIcon.setStyle(String.format("-fx-background-color: %s;", TherapistsViewController.backgroundColor));

            long initials = issue.getId();
            Label initialLabel = new Label(""+initials);
            initialLabel.getStyleClass().add("icon-text");
            projectIcon.getChildren().add(initialLabel);

            VBox projectDetails = new VBox(5);
            projectDetails.setLayoutX(100);
            projectDetails.setLayoutY(35);

            Label nameLabel = new Label(issue.getFullName());
            nameLabel.getStyleClass().add("project-name");

            Label idLabel = new Label("#" + issue.getId());
            idLabel.getStyleClass().add("project-id");

            projectDetails.getChildren().addAll(nameLabel, idLabel);

            HBox actionButtons = new HBox(10);
            actionButtons.setLayoutX(800);
            actionButtons.setLayoutY(40);
            actionButtons.getStyleClass().add("action-buttons");

            addHoverEffect(projectCard);

            projectCard.setOnMouseClicked(e -> {
                playClickAnimation(projectCard);
                openIssue(issue);
            });

            projectCard.getChildren().addAll(
                    projectIcon,
                    projectDetails,
                    actionButtons
            );

            playEntranceAnimation(projectCard);

            projectIssuesContainer.getChildren().add(projectCard);
            projectIssuesContainer.setPrefWidth(1522.0);
        }
    }

    private void showSearchSuggestions(String query) {
        List<PatientsDTO> filteredIssues = null;
        try {
            filteredIssues = patientBO.searchPatientsByName(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (filteredIssues == null) {
            System.out.println("Failed to fetch patients.");
            return;
        }

        if (!filteredIssues.isEmpty()) {
            suggestionList.getItems().clear();
            for (PatientsDTO issue : filteredIssues) {
                suggestionList.getItems().add(issue.getFullName());
            }
            suggestionList.setVisible(true);
        } else {
            suggestionList.setVisible(false);
        }
    }

    public static void playEntranceAnimation(Node node) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), node);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), node);
        slideIn.setFromY(20);
        slideIn.setToY(0);

        ParallelTransition parallelTransition = new ParallelTransition(fadeIn, slideIn);
        parallelTransition.play();
    }

    public static void playClickAnimation(Node node) {
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(100), node);
        scaleDown.setToX(0.98);
        scaleDown.setToY(0.98);
        scaleDown.setOnFinished(e -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(100), node);
            scaleUp.setToX(1.0);
            scaleUp.setToY(1.0);
            scaleUp.play();
        });
        scaleDown.play();
    }

    public void resetBtnClick() {
        sortByStatus.getSelectionModel().clearSelection();
        priorityDropdown.getSelectionModel().clearSelection();
        sortByProjectName.getSelectionModel().clearSelection();
        TherapistsViewController.bindNavigation(issuesPage, "/view/nav-buttons/patients-view.fxml");
        searchBox.clear();
        updateIssuesView();
    }

    public static void addHoverEffect(Node node) {
        node.setOnMouseEntered(e -> {
            node.getStyleClass().add("project-card-hover");
            node.setCursor(Cursor.HAND);
        });
        node.setOnMouseExited(e -> {
            node.getStyleClass().remove("project-card-hover");
            node.setCursor(Cursor.DEFAULT);
        });
    }

    public static String generateRandomColor() {
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return String.format("rgb(%d, %d, %d)", r, g, b);
    }

    public void createIssuesOnClick() {
        bindNavigation(issuesPage, "/view/nav-buttons/issue/issue-create-view.fxml");
    }

    public void startCreateLabelOnClick() {
        bindNavigation(issuesPage, "/view/nav-buttons/issue/issue-create-view.fxml");
    }

    public static void bindNavigation(AnchorPane pane, String fxmlPath) {
        try {
            pane.getChildren().clear();

            URL fxmlUrl = TherapistsViewController.class.getResource(fxmlPath);
            if (fxmlUrl == null) {
                throw new IOException("FXML file not found: " + fxmlPath);
            }

            AnchorPane load = FXMLLoader.load(fxmlUrl);

            load.prefWidthProperty().bind(pane.widthProperty());
            load.prefHeightProperty().bind(pane.heightProperty());

            FadeTransition fadeIn = new FadeTransition(Duration.millis(750), load);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();

            pane.getChildren().add(load);
            fadeIn.setOnFinished(event -> System.out.println("Scene loaded successfully."));
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load page!").show();
        }
    }
}
