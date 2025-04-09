package com.dinidu.lk.pmt.controller.dashboard.therapist;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.TherapistsBO;
import com.dinidu.lk.pmt.bo.custom.UserBO;
import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.custom.impl.QueryDAOImpl;
import com.dinidu.lk.pmt.dto.TherapistDTO;
import com.dinidu.lk.pmt.utils.*;
import com.dinidu.lk.pmt.utils.customAlerts.CustomAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomDeleteAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.listeners.ProjectDeletionHandler;
import com.dinidu.lk.pmt.utils.listeners.ProjectUpdateListener;
import com.dinidu.lk.pmt.utils.projectTypes.TherapistStatus;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.Setter;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class TherapistEditViewController implements Initializable {
    public Button saveProjectBtn;
    public Button deleteTherapistBtn;
    public TextField therapistEmail;
    public TextField therapistPhoneNumber;
    public Button saveTherapistBtn;
    public Button cancelProjectBtn;
    public TextField therapistNameField;
    public TextField therapistAddress;
    @Setter
    private ProjectDeletionHandler deletionHandler;
    @Setter
    private ProjectUpdateListener updateListener;
    @FXML
    private Label backToMain;
    @FXML
    private AnchorPane projectEdit;
    @FXML
    private ComboBox<TherapistStatus> therapistStatusCombo;
    @FXML
    private static TherapistDTO currentTherapist;

    UserBO userBO= (UserBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.USER);

    TherapistsBO therapistsBO = (TherapistsBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.THERAPIST);

    QueryDAO queryDAO = new QueryDAOImpl();


    public static void setProject(TherapistDTO project) {
        currentTherapist = project;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        therapistStatusCombo.getItems().setAll(TherapistStatus.values());

        if (currentTherapist == null) {
            List<TherapistDTO> projects;
            try {
                projects = therapistsBO.getAllTherapists();
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            if (projects != null && !projects.isEmpty()) {
                currentTherapist = projects.get(0);
            } else {
                System.out.println("No Therapist available.");
                return;
            }
        }

        loadProjectData();
        therapistNameField.setText(currentTherapist.getFullName() != null ? currentTherapist.getFullName() : "");
        therapistAddress.setText(currentTherapist.getAddress() != null ? currentTherapist.getAddress() : "");
        therapistPhoneNumber.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        therapistEmail.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
    }

    private void loadProjectData() {
        System.out.println("Loading Therapist data...");

        if (currentTherapist == null) {
            System.out.println("currentProject is null");
            return;
        }

        therapistNameField.setText(currentTherapist.getFullName());
        therapistAddress.setText(currentTherapist.getAddress());
        therapistPhoneNumber.setText(currentTherapist.getPhoneNumber());
        therapistEmail.setText(currentTherapist.getEmail());
        therapistStatusCombo.setValue(currentTherapist.getStatus());
    }

    @FXML
    public void saveProject() {
        System.out.println("Saving project...");

        if (therapistNameField.getText().isEmpty() || therapistAddress.getText().isEmpty()) {
            CustomErrorAlert.showAlert("Error", "Therapist name and description cannot be empty.");
            return;
        }

        currentTherapist.setEmail(therapistEmail.getText());
        currentTherapist.setPhoneNumber(therapistPhoneNumber.getText());
        currentTherapist.setFullName(therapistNameField.getText());
        currentTherapist.setAddress(therapistAddress.getText());
        currentTherapist.setStatus(therapistStatusCombo.getValue());

        try {
            therapistsBO.updateTherapist(currentTherapist);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Therapist saved successfully.");

        if (updateListener != null) {
            updateListener.onProjectUpdated(currentTherapist);
        }

        CustomAlert.showAlert("Success", "Therapist saved successfully.");
        backToMain();
    }

    private void validateFields() {
        boolean isValid = !therapistNameField.getText().isEmpty() && !therapistAddress.getText().isEmpty();
        saveProjectBtn.setDisable(!isValid);
    }

    @FXML
    public void backToMain() {
        System.out.println("Returning to main...");
        ((Stage) backToMain.getScene().getWindow()).close();
    }

    @FXML
    public void cancelProjectBtnOnClick(ActionEvent actionEvent) {
        System.out.println("Cancelling project edit...");
        ((Stage) projectEdit.getScene().getWindow()).close();
    }

    @FXML
    public void editName(ActionEvent actionEvent) {
        System.out.println("Editing project name...");
    }

    @FXML
    public void deleteProject(ActionEvent actionEvent) {
        String username = SessionUser.getLoggedInUsername();
        System.out.println("Deleting project for user: " + username);

        if (username == null) {
            System.out.println("User not logged in. username: " + username);
        }

        UserRole userRoleByUsername;
        try {
            userRoleByUsername = queryDAO.getUserRoleByUsername(username);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (userRoleByUsername == null) {
            System.out.println("User not logged in. userRoleByUsername: " + userRoleByUsername);
            return;
        }

        System.out.println("User role: " + userRoleByUsername);

        if (userRoleByUsername != UserRole.ADMIN &&
                userRoleByUsername != UserRole.RECEPTIONIST) {
            System.out.println("Access denied: Only Admin, Project Manager, or Product Owner can delete projects.");
            CustomErrorAlert.showAlert("Access Denied", "You do not have permission to delete projects.");
            return;
        }

        boolean confirmed = CustomDeleteAlert.showAlert(
                (Stage) projectEdit.getScene().getWindow(),
                "Confirm Deletion",
                "Are you sure you want to delete this project? "
        );

        if (confirmed) {
            System.out.println("Deleting project...");
            try {
                boolean b = therapistsBO.deleteTherapists(currentTherapist.getId());
                if(!b){
                    System.out.println("Deletion failed.");
                    CustomErrorAlert.showAlert("Failed","Project deletion failed.");
                }
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Project deleted successfully.");
            CustomAlert.showAlert("Success", "Project deleted successfully.");

            Stage currentStage = (Stage) projectEdit.getScene().getWindow();
            currentStage.close();

            if (deletionHandler != null) {
                deletionHandler.onProjectDeleted();
            }
        } else {
            System.out.println("Project deletion canceled by user.");
        }
    }

    @FXML
    public void editStatus(ActionEvent actionEvent) {
        System.out.println("Editing project status...");
    }
    @FXML
    public void editProjectDesc(ActionEvent actionEvent) {
        System.out.println("Editing project description...");
    }
}
