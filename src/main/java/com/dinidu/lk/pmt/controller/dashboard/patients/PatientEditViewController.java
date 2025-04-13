package com.dinidu.lk.pmt.controller.dashboard.patients;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.PatientBO;
import com.dinidu.lk.pmt.bo.custom.UserBO;
import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.custom.impl.QueryDAOImpl;
import com.dinidu.lk.pmt.dto.PatientsDTO;
import com.dinidu.lk.pmt.utils.*;
import com.dinidu.lk.pmt.utils.customAlerts.CustomAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomDeleteAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.listeners.IssueDeletionHandler;
import com.dinidu.lk.pmt.utils.listeners.IssueUpdateListener;
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

public class PatientEditViewController implements Initializable {
    public Button saveIssueBtn;
    public Button cancelProjectBtn;
    public Button deleteIssueBtn;
    public TextField PatientEmail;
    public TextField PatientNumber;
    public TextField PatientAddress;
    public TextField PatientName;
    @Setter
    private IssueDeletionHandler deletionHandler;
    @Setter
    private IssueUpdateListener updateListener;
    @FXML
    private Label backToMain;
    @FXML
    private AnchorPane projectEdit;
    private static PatientsDTO currentIssue;

    UserBO userBO= (UserBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.USER);

    PatientBO issuesBO =
            (PatientBO) BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.PATIENTS);

    QueryDAO queryDAO = new QueryDAOImpl();


    public static void setIssue(PatientsDTO patientsDTO) {
        currentIssue = patientsDTO;
    }

    @FXML
    public void saveIssue() {
        System.out.println("Saving Issue Method in EditViewController");

        currentIssue.setFullName(PatientName.getText());
        currentIssue.setEmail(PatientEmail.getText());
        currentIssue.setPhoneNumber(PatientNumber.getText());
        currentIssue.setAddress(PatientAddress.getText());
        currentIssue.setId(currentIssue.getId());
        validateFields();

        try {
            boolean issueUpdated = issuesBO.updatePatient(currentIssue);
            if (issueUpdated) {
                System.out.println("Issue saved successfully.");

                if (updateListener != null) {
                    System.out.println("Updating listener...");
                    updateListener.onIssueUpdated(currentIssue);
                }

                System.out.println("Saving issue to database...");
                CustomAlert.showAlert("Success", "Issue saved successfully.");
                System.out.println("Returning to main...");
                backToMain();
            } else {
                System.out.println("Failed to save issue.");
                CustomErrorAlert.showAlert("Error", "Failed to save issue.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (currentIssue == null) {
            List<PatientsDTO> issues = null;

            try {
                issues = issuesBO.getAllPatients();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            if (issues != null && !issues.isEmpty()) {
                currentIssue = issues.get(0);
            } else {
                System.out.println("No issues available.");
                return;
            }
        }
        loadIssueData();
    }

    private void loadIssueData() {
        System.out.println("Loading Patient data...");

        if (currentIssue == null) {
            System.out.println("Patient is null");
            return;
        }

        System.out.println("Patient : " + currentIssue);
        PatientName.setText(currentIssue.getFullName());
        PatientAddress.setText(currentIssue.getAddress());
        PatientNumber.setText(currentIssue.getPhoneNumber());
        PatientEmail.setText(currentIssue.getEmail());
    }

    private void validateFields() {
        boolean isValid = PatientName.getText().trim().isEmpty() ||
                PatientEmail.getText().trim().isEmpty() ||
                PatientNumber.getText().trim().isEmpty() ||
                PatientAddress.getText().trim().isEmpty();
        saveIssueBtn.setDisable(isValid);
    }

    @FXML
    public void backToMain() {
        System.out.println("Returning to main...");
        ((Stage) backToMain.getScene().getWindow()).close();
    }

    @FXML
    public void cancelIssueBtnOnClick(ActionEvent actionEvent) {
        System.out.println("Cancelling project edit...");
        ((Stage) projectEdit.getScene().getWindow()).close();
    }

    @FXML
    public void deleteIssue() {
        String username = SessionUser.getLoggedInUsername();
        System.out.println("Deleting Issue for user: " + username);

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
            return;
        }

        System.out.println("User role: " + userRoleByUsername);

        if (userRoleByUsername != UserRole.ADMIN &&
                userRoleByUsername != UserRole.RECEPTIONIST) {
            System.out.println("Access denied: Only Admin, Project Manager, or Product Owner can delete Issue.");
            CustomErrorAlert.showAlert("Access Denied", "You do not have permission to delete Issue.");
            deleteIssueBtn.setVisible(false);
            return;
        }

        boolean confirmed = CustomDeleteAlert.showAlert(
                (Stage) projectEdit.getScene().getWindow(),
                "Confirm Deletion",
                "Are you sure you want to delete this issue? "
        );

        if (confirmed) {
            System.out.println("Deleting issue...");
            boolean isDeleted;
            try {
                isDeleted = issuesBO.deletePatient(currentIssue.getId());
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            if (isDeleted) {
                System.out.println("Issue deleted successfully.");
                CustomAlert.showAlert("Success", "Issue deleted successfully.");

                Stage currentStage = (Stage) projectEdit.getScene().getWindow();
                currentStage.close();

                if (deletionHandler != null) {
                    deletionHandler.onIssueDeleted();
                }
            } else {
                System.out.println("Issue deletion failed.");
                CustomErrorAlert.showAlert("Error", "Failed to delete issue.");
            }
        } else {
            System.out.println("Issue deletion canceled by user.");
        }
    }

    @FXML
    public void editIssueDesc(ActionEvent actionEvent) {
        System.out.println("Editing project description...");
    }

    public void nameTyping(ActionEvent actionEvent) {
        PatientName.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean isValid = newValue.trim().isEmpty();
            saveIssueBtn.setDisable(isValid);
        });
    }

    public void emailTyping(ActionEvent actionEvent) {
        PatientEmail.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean isValid = newValue.trim().isEmpty();
            saveIssueBtn.setDisable(isValid);
        });
    }

    public void mobileNumber(ActionEvent actionEvent) {
        PatientNumber.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean isValid = newValue.trim().isEmpty();
            saveIssueBtn.setDisable(isValid);
        });
    }
}
