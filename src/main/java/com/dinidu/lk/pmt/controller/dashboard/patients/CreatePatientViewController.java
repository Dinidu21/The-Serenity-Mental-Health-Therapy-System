package com.dinidu.lk.pmt.controller.dashboard.patients;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.PatientBO;
import com.dinidu.lk.pmt.bo.custom.UserBO;
import com.dinidu.lk.pmt.controller.dashboard.TherapistsViewController;
import com.dinidu.lk.pmt.dto.PatientsDTO;
import com.dinidu.lk.pmt.utils.customAlerts.CustomAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.SessionUser;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class CreatePatientViewController implements Initializable {

    @FXML
    public AnchorPane issuesCreatePg;
    @FXML
    public TextArea PatientAddress;
    public TextField PatientName;
    public TextField PatientEmail;
    public TextField PatientMobileNumber;

    UserBO userBO= (UserBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.USER);
    PatientBO patientBO = (PatientBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.PATIENTS);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            try {
                String username = SessionUser.getLoggedInUsername();
                if (username == null) {
                    System.out.println("User not logged in. username: " + null);
                } else {
                    System.out.println("User logged in. username: " + username);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void cancelOnClick(ActionEvent actionEvent) {
        PatientName.clear();
        PatientEmail.clear();
        PatientAddress.clear();
        PatientMobileNumber.clear();
    }

    public void createIssueOnClick() {

        try {
            String username = SessionUser.getLoggedInUsername();

            if (username == null) {
                System.out.println("User not logged in. username: " + null);
            }

            PatientsDTO patientsDTO = new PatientsDTO();
            patientsDTO.setFullName(PatientName.getText());
            patientsDTO.setEmail(PatientEmail.getText());
            patientsDTO.setAddress(PatientAddress.getText());
            patientsDTO.setPhoneNumber(PatientMobileNumber.getText());
            patientsDTO.setMedicalHistory("No medical history");
            patientsDTO.setRegistrationDate(LocalDate.now());

            if (patientsDTO.getFullName().isEmpty() || patientsDTO.getEmail().isEmpty() || patientsDTO.getAddress().isEmpty() || patientsDTO.getPhoneNumber().isEmpty()) {
                CustomErrorAlert.showAlert("ERROR", "Please fill all the fields.");
                return;
            }

            boolean isCreated = patientBO.createPatient(patientsDTO);
            if (isCreated) {
                System.out.println("Patients created successfully.");
                CustomAlert.showAlert("SUCCESS", "Patients created successfully.");
                TherapistsViewController.bindNavigation(issuesCreatePg, "/view/nav-buttons/patients-view.fxml");
            } else {
                System.out.println("Failed to create Patients.");
                CustomErrorAlert.showAlert("ERROR", "Failed to create Patients.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
