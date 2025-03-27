package com.dinidu.lk.pmt.controller.dashboard;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.UserBO;
import com.dinidu.lk.pmt.controller.BaseController;
import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.custom.impl.QueryDAOImpl;
import com.dinidu.lk.pmt.dto.UserDTO;
import com.dinidu.lk.pmt.utils.customAlerts.CustomDeleteAlert;
import com.dinidu.lk.pmt.utils.permissionTypes.Permission;
import com.dinidu.lk.pmt.utils.permissionTypes.PermissionLevel;
import com.dinidu.lk.pmt.utils.customAlerts.CustomAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.SessionUser;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class SettingsViewController extends BaseController {

    public AnchorPane settingPage;
    @FXML
    private VBox usersContainer;

    UserBO userBO= (UserBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.USER);

    QueryDAO queryDAO= new QueryDAOImpl();


    @FXML
    public void initialize() {
        loadActiveUsers();
    }

    private void loadActiveUsers() {
        usersContainer.getChildren().clear();
        List<UserDTO> users;

        try {
            users = queryDAO.getAllUsersWithRolesAndPermissions();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        for (UserDTO user : users) {
            addUserToView(user);
        }
    }

    private void addUserToView(UserDTO user) {
        AnchorPane userPane = new AnchorPane();
        userPane.setPrefHeight(50.0);
        userPane.setPrefWidth(1392.0);
        userPane.getStyleClass().add("card-container");

        Label nameLabel = new Label(user.getFullName());
        nameLabel.setLayoutX(8.0);
        nameLabel.setLayoutY(35.0);
        nameLabel.getStyleClass().add("main-label");

        HBox controlsBox = new HBox(10);
        controlsBox.setLayoutX(850.0);
        controlsBox.setLayoutY(35.0);

        ComboBox<UserRole> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll(UserRole.values());

        if (user.getRole() != null) {
            roleComboBox.getSelectionModel().select(user.getRole());
        } else {
            roleComboBox.setPromptText("Not set");
        }

        ComboBox<PermissionLevel> permissionComboBox = new ComboBox<>();
        permissionComboBox.getItems().addAll(PermissionLevel.values());

        PermissionLevel currentPermissionLevel = PermissionLevel.getLevelFromPermissions(user.getPermissions());
        if (currentPermissionLevel != null) {
            permissionComboBox.getSelectionModel().select(currentPermissionLevel);
        } else {
            permissionComboBox.setPromptText("None");
        }

        Button assignButton = new Button("Assign");
        assignButton.setOnAction(e -> assignRoleAndPermission(user, roleComboBox.getValue(), permissionComboBox.getValue()));

        Button deactivateButton = new Button("Deactivate");
        deactivateButton.getStyleClass().add("red-button");
        deactivateButton.setOnAction(e -> confirmDeactivation(user));

        UserRole currentUserRole;
        try {
            currentUserRole = queryDAO.getUserRoleByUsername(SessionUser.getLoggedInUsername());
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        boolean hasPermission = currentUserRole == UserRole.ADMIN || currentUserRole == UserRole.RECEPTIONIST;
        roleComboBox.setDisable(!hasPermission);
        permissionComboBox.setDisable(!hasPermission);
        assignButton.setDisable(!hasPermission);

        controlsBox.getChildren().addAll(roleComboBox, permissionComboBox, assignButton, deactivateButton);

        userPane.getChildren().addAll(nameLabel, controlsBox);
        usersContainer.getChildren().add(userPane);
    }

    private void deactivateUser(UserDTO user) {
        boolean updated;
        try {
            updated = userBO.updateUserStatus(user.getUsername(), "INACTIVE");
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (updated) {
            System.out.println("User successfully deactivated.");
            CustomAlert.showAlert("SUCCESS", "User successfully deactivated.");
            loadActiveUsers();
            usersContainer.requestLayout();
        } else {
            System.out.println("Failed to deactivate user.");
        }
    }

    private void confirmDeactivation(UserDTO user) {
        String username = SessionUser.getLoggedInUsername();
        System.out.println("Deleting project for user: " + username);

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
            System.out.println("Access denied: Only Admin, Project Manager, or Product Owner can delete projects.");
            CustomErrorAlert.showAlert("Access Denied", "You do not have permission to delete projects.");
            return;
        }

        Stage parentStage = (Stage) usersContainer.getScene().getWindow();
        boolean confirm = CustomDeleteAlert.showAlert(parentStage, "Confirm Deactivation",
                "Are you sure you want to deactivate this user?");

        if (confirm) {
            deactivateUser(user);
        }
    }

    private void assignRoleAndPermission(UserDTO user, UserRole selectedRole, PermissionLevel selectedPermissionLevel) {
        UserRole currentUserRole = SessionUser.getLoggedInUserRole();
        String loggedInUsername = SessionUser.getLoggedInUsername();
        System.out.println("Before get db Current user role: " + currentUserRole);
        System.out.println("Current user: " + loggedInUsername);
        UserRole userRoleByUsername;
        try {
            userRoleByUsername = queryDAO.getUserRoleByUsername(loggedInUsername);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


        System.out.println("Current user role after fetching db : " + userRoleByUsername);

        if (userRoleByUsername == null) {
            System.out.println("Current user role is null");
        }

        if (userRoleByUsername != UserRole.ADMIN && userRoleByUsername != UserRole.RECEPTIONIST) {
            System.out.println("Access denied: Only Admin, Project Manager, or Product Owner can assign roles and permissions.");
            CustomErrorAlert.showAlert("Access Denied", "You do not have permission.");
            return;
        }

        System.out.println("Assigning role and permissions for user: " + user.getUsername());

        user.setRole(selectedRole);

        Set<Permission> permissions = PermissionLevel.getPermissionsByLevel(selectedPermissionLevel);
        user.setPermissions(permissions);

        System.out.println("UserDTO details: " + user);
        if (user.getUsername() == null) {
            System.out.println("User name is null");
            return;
        }

        boolean updated = false;
        try {
            updated = userBO.updateUserRoleAndPermissions(user.getUsername(), user);
        } catch (SQLException e) {
            System.out.println("Error updating user role and permissions: " + e.getMessage());
            CustomErrorAlert.showAlert("ERROR", "Error updating user role and permissions.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (updated) {
            System.out.println("User role and permissions updated successfully.");
            displayUserPermissions(user);
            CustomAlert.showAlert("Success", "User role and permissions updated successfully.");
        } else {
            System.out.println("Failed to update user role and permissions.");
            CustomErrorAlert.showAlert("ERROR", "Failed to update user role and permissions.");
        }
    }

    private void displayUserPermissions(UserDTO user) {
        Set<Permission> userPermissions = user.getPermissions();
        System.out.println("Permissions for user " + user.getFullName() + ":");

        if (userPermissions.isEmpty()) {
            System.out.println("No permissions assigned.");
        } else {
            for (Permission permission : userPermissions) {
                System.out.println("- " + permission);
            }
        }
    }

    public void logoutOnClick(ActionEvent actionEvent) {
        transitionToScene(settingPage, "/view/login-view.fxml");
        SessionUser.clearSession();
    }
}
