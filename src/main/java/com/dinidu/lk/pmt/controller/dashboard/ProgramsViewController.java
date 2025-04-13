package com.dinidu.lk.pmt.controller.dashboard;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.ProgramsBO;
import com.dinidu.lk.pmt.bo.custom.TherapistsBO;
import com.dinidu.lk.pmt.controller.BaseController;
import com.dinidu.lk.pmt.controller.dashboard.programs.CreateProgramSuccessViewController;
import com.dinidu.lk.pmt.controller.dashboard.programs.ProgramEditViewController;
import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.custom.impl.QueryDAOImpl;
import com.dinidu.lk.pmt.dto.TherapyProgramsDTO;
import com.dinidu.lk.pmt.utils.SessionUser;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ProgramsViewController extends BaseController implements Initializable {
    public TextField searchBox;
    public ListView<String> suggestionList;
    public Button createProgramsBtn;
    public ImageView searchImg;
    public Label noTaskLabel;
    public Label createLabel;
    public ScrollPane scrollPane;
    public VBox taskCardContainer;
    public AnchorPane tasksPage;
    public Label noTasksFoundLabel;

    QueryDAO queryDAO = new QueryDAOImpl();

    TherapistsBO therapistsBO = (TherapistsBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.THERAPIST);

    ProgramsBO programsBO = (ProgramsBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.PROGRAM);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        noTasksFoundLabel.setVisible(false);
        updateTaskView();

        searchBox.textProperty().addListener((observable, oldValue, newValue) -> {
            searchImg.setVisible(newValue.isEmpty());
            if (!newValue.isEmpty()) {
                showSearchSuggestions(newValue);
            } else {
                suggestionList.setVisible(false);
                updateTaskView();
            }
        });

        searchBox.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String taskName = searchBox.getText().trim();
                if (!taskName.isEmpty()) {
                    try {
                        programsBO.searchProgramsByName(taskName);
                    } catch (SQLException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    event.consume();
                }
            }
        });

        suggestionList.setOnMouseClicked(event -> {
            String selectedProgramName = suggestionList.getSelectionModel().getSelectedItem();
            if (selectedProgramName != null) {
                searchBox.setText(selectedProgramName);
                suggestionList.setVisible(false);
                List<TherapyProgramsDTO> filteredProgram;
                try {
                    filteredProgram = programsBO.searchProgramsByName(selectedProgramName);
                } catch (SQLException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                noTasksFoundLabel.setVisible(filteredProgram.isEmpty());
                displayPrograms(filteredProgram);
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
        if (userRoleByUsername != UserRole.ADMIN && userRoleByUsername != UserRole.RECEPTIONIST) {
            System.out.println("Access denied: Only Admin, Project Manager, or Product Owner can create Tasks.");
            createProgramsBtn.setDisable(true);
            createLabel.setVisible(false);
        }
    }

    private void updateTaskView() {
        List<TherapyProgramsDTO> dtos;
        try {
            dtos = programsBO.getAllPrograms();
            System.out.println("Therapy Programs fetched: " + dtos);
            for (TherapyProgramsDTO dto : dtos) {
                System.out.println("Therapy Program: " + dto.getName());
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println("dto size:" + dtos.size());

        for (TherapyProgramsDTO task : dtos) {
            System.out.println(task);
        }

        if (dtos.isEmpty()) {
            createLabel.setVisible(true);
            scrollPane.setVisible(false);

            System.out.println("No dtos found.");
            taskCardContainer.setVisible(false);
            return;
        }

        noTaskLabel.setVisible(false);
        createLabel.setVisible(false);
        noTasksFoundLabel.setVisible(false);
        taskCardContainer.setVisible(true);
        displayPrograms(dtos);
    }

    private void showSearchSuggestions(String query) {
        List<TherapyProgramsDTO> filteredTasks;
        try {
            filteredTasks = programsBO.searchProgramsByName(query);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (!filteredTasks.isEmpty()) {
            suggestionList.getItems().clear();
            for (TherapyProgramsDTO task : filteredTasks) {
                System.out.println("Task name: " + task.getName());
                suggestionList.getItems().add(task.getName());
            }
            suggestionList.setVisible(true);
        } else {
            suggestionList.setVisible(false);
        }
    }

    private void displayPrograms(List<TherapyProgramsDTO> programsDTOS) {
        taskCardContainer.getChildren().clear();
        taskCardContainer.getStyleClass().add("task-card-container");

        for (TherapyProgramsDTO task : programsDTOS) {
            AnchorPane taskCard = new AnchorPane();
            taskCard.getStyleClass().add("task-card");
            taskCard.setPrefHeight(120.0);
            taskCard.setPrefWidth(1322.0);

            StackPane taskIcon = new StackPane();
            taskIcon.setPrefWidth(60.0);
            taskIcon.setPrefHeight(60.0);
            taskIcon.setLayoutX(20);
            taskIcon.setLayoutY(30);
            taskIcon.getStyleClass().add("task-icon");

            TherapistsViewController.backgroundColor = TherapistsViewController.generateRandomColor();
            taskIcon.setStyle(String.format("-fx-background-color: %s;", TherapistsViewController.backgroundColor));
            String initialsString = getString(task);

            System.out.println("Initials: " + initialsString);
            Label initialLabel = new Label(initialsString);
            initialLabel.getStyleClass().add("icon-text");
            taskIcon.getChildren().add(initialLabel);

            VBox taskDetails = new VBox(5);
            taskDetails.setLayoutX(100);
            taskDetails.setLayoutY(35);

            Label nameLabel = new Label(task.getName());
            nameLabel.getStyleClass().add("task-name");

            Label idLabel = new Label("Duration: " + task.getDuration()); // 👈 correct
            idLabel.getStyleClass().add("project-name");

            taskDetails.getChildren().addAll(nameLabel, idLabel);


            HBox actionButtons = new HBox(10);
            actionButtons.setLayoutX(800);
            actionButtons.setLayoutY(40);
            actionButtons.getStyleClass().add("action-buttons");

            TherapistsViewController.addHoverEffect(taskCard);

            taskCard.setOnMouseClicked(e -> {
                TherapistsViewController.playClickAnimation(taskCard);
                openTask(task);
                System.out.println("clicked task : " + task);
            });

            taskCard.getChildren().addAll(taskIcon, taskDetails, actionButtons);

            TherapistsViewController.playEntranceAnimation(taskCard);

            taskCardContainer.getChildren().add(taskCard);
            taskCardContainer.setPrefWidth(1522.0);
        }
    }

    private void openTask(TherapyProgramsDTO task) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/nav-buttons/task/task-create-success-view.fxml"));
            Parent root = loader.load();

            CreateProgramSuccessViewController controller = loader.getController();
            controller.updateTaskView(task);

            ProgramEditViewController.setTask(task);

            FXMLLoader editLoader = new FXMLLoader(getClass().getResource("/view/nav-buttons/task/task-edit-view.fxml"));
            Parent editRoot = editLoader.load();
            ProgramEditViewController editController = editLoader.getController();

            editController.setUpdateListener(controller);

            tasksPage.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getString(TherapyProgramsDTO task) {
        String taskName = task.getName();
        String[] nameParts = taskName.split(" ");
        StringBuilder initials = new StringBuilder();

        for (String part : nameParts) {
            if (!part.isEmpty()) {
                initials.append(part.charAt(0));
            }
        }

        String initialsString = initials.toString().toUpperCase();
        if (initialsString.length() > 3) {
            initialsString = initialsString.substring(0, 3);
        }
        return initialsString;
    }

    public void startCreateLabelOnClick(MouseEvent mouseEvent) {
        TherapistsViewController.bindNavigation(tasksPage, "/view/nav-buttons/task/task-create-view.fxml");
    }

    public void createProgramsOnClick(ActionEvent actionEvent) {
        TherapistsViewController.bindNavigation(tasksPage, "/view/nav-buttons/task/task-create-view.fxml");
    }

    public void searchTaskOnClick(ActionEvent actionEvent) {
        searchImg.setDisable(true);
    }
}
