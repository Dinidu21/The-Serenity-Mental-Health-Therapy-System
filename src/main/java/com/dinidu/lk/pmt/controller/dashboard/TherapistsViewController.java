package com.dinidu.lk.pmt.controller.dashboard;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.TherapistsBO;
import com.dinidu.lk.pmt.controller.BaseController;
import com.dinidu.lk.pmt.controller.dashboard.therapist.CreateTherapistSuccessViewController;
import com.dinidu.lk.pmt.controller.dashboard.therapist.TherapistEditViewController;
import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.custom.impl.QueryDAOImpl;
import com.dinidu.lk.pmt.dto.TherapistDTO;
import com.dinidu.lk.pmt.utils.SessionUser;
import com.dinidu.lk.pmt.utils.projectTypes.TherapistStatus;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
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
import javafx.scene.input.MouseEvent;
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
import java.util.stream.Collectors;

public class TherapistsViewController extends BaseController implements Initializable {
    @FXML
    public Label createLabel;
    @FXML
    public ImageView searchImg;
    @FXML
    public TextField searchBox;
    public AnchorPane projectPage;
    @FXML
    public VBox projectCardContainer;
    public Label noProjectLabel;
    public ScrollPane scrollPane;
    public Button createProjBtn;
    public Label noProjectsFoundLabel;
    public Button resetBTN;
    @FXML
    private ComboBox<TherapistStatus> sortByStatus;
    @FXML
    private ListView<String> suggestionList;

    public static String backgroundColor = null;

    static TherapistsBO therapistsBO =
            (TherapistsBO) BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.TherapistsBO);

    QueryDAO queryDAO = new QueryDAOImpl();


    private void openProject(TherapistDTO project) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/nav-buttons/project/project-create-success-view.fxml"));
            Parent root = loader.load();

            CreateTherapistSuccessViewController controller = loader.getController();
            controller.setProjectData(project);

            TherapistEditViewController.setProject(project);

            FXMLLoader editLoader = new FXMLLoader(getClass().getResource("/view/nav-buttons/project/project-edit-view.fxml"));
            Parent editRoot = editLoader.load();
            TherapistEditViewController editController = editLoader.getController();

            editController.setUpdateListener(controller);

            projectPage.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resetBTN.setVisible(false);

        sortByStatus.getItems().addAll(TherapistStatus.values());

        sortByStatus.setOnAction(event -> {
            updateProjectView();
            resetBTN.setVisible(true);
        });
        updateProjectView();

        searchBox.textProperty().addListener((observable, oldValue, newValue) -> {
            searchImg.setVisible(newValue.isEmpty());
            if (!newValue.isEmpty()) {
                showSearchSuggestions(newValue);
            } else {
                suggestionList.setVisible(false);
                updateProjectView();
            }
        });

        searchBox.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String projectName = searchBox.getText().trim();
                if (!projectName.isEmpty()) {
                    try {
                        therapistsBO.searchTherapistByName(projectName);
                    } catch (SQLException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    event.consume();
                }
            }
        });

        suggestionList.setOnMouseClicked(event -> {
            String selectedProjectName = suggestionList.getSelectionModel().getSelectedItem();
            if (selectedProjectName != null) {
                searchBox.setText(selectedProjectName);
                suggestionList.setVisible(false);
                List<TherapistDTO> filteredProjects;
                try {
                    filteredProjects = therapistsBO.searchTherapistByName(selectedProjectName);
                } catch (SQLException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                if (filteredProjects.isEmpty()) {
                    noProjectsFoundLabel.setVisible(true);
                } else {
                    noProjectsFoundLabel.setVisible(false);
                }
                displayProjects(filteredProjects);
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
            System.out.println("Access denied: Only Admin, Project Manager, or Product Owner can create projects.");
            createProjBtn.setDisable(true);
        }
    }

    public void resetBtnClick(ActionEvent actionEvent) {
        sortByStatus.getSelectionModel().clearSelection();
        TherapistsViewController.bindNavigation(projectPage, "/view/nav-buttons/therapist-view.fxml");
        searchBox.clear();
        updateProjectView();
    }

    private void updateProjectView() {
        List<TherapistDTO> therapistDTOS;
        try {
            therapistDTOS = therapistsBO.getAllTherapists();
            System.out.println("Retrieved therapists: " + therapistDTOS);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        TherapistStatus selectedStatus = sortByStatus.getValue();
        noProjectLabel.setVisible(false);
        createLabel.setVisible(false);
        noProjectsFoundLabel.setVisible(false);
        projectCardContainer.setVisible(true);

        if (therapistDTOS == null || therapistDTOS.isEmpty()) {
            noProjectLabel.setVisible(true);
            createLabel.setVisible(true);
            System.out.println("No Therapist found.");
            projectCardContainer.setVisible(false);
            return;
        }

        List<TherapistDTO> filteredProjects = therapistDTOS.stream()
                .filter(project -> (selectedStatus == null || project.getStatus() == selectedStatus))
                .collect(Collectors.toList());

        if (filteredProjects.isEmpty()) {
            noProjectsFoundLabel.setVisible(true);
            System.out.println("No therapistDTOS found after filtering.");
        } else {
            noProjectsFoundLabel.setVisible(false);
        }

        displayProjects(filteredProjects);
    }

    public void searchTherapistsOnClick(ActionEvent actionEvent) {
        searchImg.setDisable(true);
    }

    private void displayProjects(List<TherapistDTO> projects) {
        projectCardContainer.getChildren().clear();
        projectCardContainer.getStyleClass().add("project-card-container");

        for (TherapistDTO therapistDTO : projects) {
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

            String initials = therapistDTO.getFullName().toUpperCase().substring(0, 3);
            Label initialLabel = new Label(initials);
            initialLabel.getStyleClass().add("icon-text");
            projectIcon.getChildren().add(initialLabel);

            VBox projectDetails = new VBox(5);
            projectDetails.setLayoutX(100);
            projectDetails.setLayoutY(35);

            Label nameLabel = new Label(therapistDTO.getFullName());
            nameLabel.getStyleClass().add("project-name");

            Label idLabel = new Label("" + therapistDTO.getStatus());
            idLabel.getStyleClass().add("project-id");

            projectDetails.getChildren().addAll(nameLabel, idLabel);

            HBox actionButtons = new HBox(10);
            actionButtons.setLayoutX(800);
            actionButtons.setLayoutY(40);
            actionButtons.getStyleClass().add("action-buttons");

            addHoverEffect(projectCard);

            projectCard.setOnMouseClicked(e -> {
                playClickAnimation(projectCard);
                openProject(therapistDTO);
            });

            projectCard.getChildren().addAll(
                    projectIcon,
                    projectDetails,
                    actionButtons
            );

            playEntranceAnimation(projectCard);

            projectCardContainer.getChildren().add(projectCard);
            projectCardContainer.setPrefWidth(1522.0);
        }
    }

    private void showSearchSuggestions(String query) {
        List<TherapistDTO> filteredProjects;
        try {
            filteredProjects = therapistsBO.searchTherapistByName(query);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (!filteredProjects.isEmpty()) {
            suggestionList.getItems().clear();
            for (TherapistDTO project : filteredProjects) {
                suggestionList.getItems().add(project.getFullName());
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

    public void createTherapistsOnClick() {
        bindNavigation(projectPage, "/view/nav-buttons/project/project-create-view.fxml");
    }

    public void startCreateLabelOnClick(MouseEvent mouseEvent) {
        bindNavigation(projectPage, "/view/nav-buttons/project/project-create-view.fxml");
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
