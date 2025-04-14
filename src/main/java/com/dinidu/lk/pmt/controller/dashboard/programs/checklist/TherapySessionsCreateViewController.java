package com.dinidu.lk.pmt.controller.dashboard.programs.checklist;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.*;
import com.dinidu.lk.pmt.controller.dashboard.TherapistsViewController;
import com.dinidu.lk.pmt.controller.dashboard.programs.CreateProgramSuccessViewController;
import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.custom.impl.QueryDAOImpl;
import com.dinidu.lk.pmt.dto.PatientsDTO;
import com.dinidu.lk.pmt.dto.TherapistDTO;
import com.dinidu.lk.pmt.dto.TherapyProgramsDTO;
import com.dinidu.lk.pmt.dto.TherapySessionsDTO;
import com.dinidu.lk.pmt.entity.TherapySessions;
import com.dinidu.lk.pmt.utils.customAlerts.CustomAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.SessionUser;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class TherapySessionsCreateViewController {
    public ComboBox<String> SelectTherapist;
    public ComboBox<String> SelectPatient;
    public DatePicker sessionMakeDay;
    public ComboBox<String> SelectProgram;
    public ComboBox<String> timeComboBox;
    @FXML
    private AnchorPane taskChecklistCreatePage;
    @FXML
    private TextArea descriptionIdField;

    UserBO userBO= (UserBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.USER);

    QueryDAO queryDAO= new QueryDAOImpl();

    TherapySessionsBO sessionsBO = (TherapySessionsBO)
            BOFactory.getInstance().getBO(BOFactory.BOTypes.SESSIONS);

    TherapistsBO therapistsBO = (TherapistsBO)
            BOFactory.getInstance().getBO(BOFactory.BOTypes.THERAPIST);

    PatientBO patientBO = (PatientBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.PATIENTS);

    ProgramsBO programsBO = (ProgramsBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.PROGRAM);



    public void initialize() {
        sessionMakeDay.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date != null && (date.isBefore(LocalDate.now()) || date.isEqual(LocalDate.now()))) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });

        comboboxInitialization();
    }

    private void comboboxInitialization() {
        try {
            // Convert patients to names
            List<String> patientNames = patientBO.getAllPatients().stream()
                    .map(PatientsDTO::getFullName)
                    .collect(Collectors.toList());
            SelectPatient.getItems().addAll(patientNames);

            // Convert therapists to names
            List<String> therapistNames = therapistsBO.getAllTherapists().stream()
                    .map(TherapistDTO::getFullName)
                    .collect(Collectors.toList());
            SelectTherapist.getItems().addAll(therapistNames);

            // Convert programs to program names
            List<String> programNames = programsBO.getAllPrograms().stream()
                    .map(TherapyProgramsDTO::getName)
                    .collect(Collectors.toList());
            SelectProgram.getItems().addAll(programNames);

            // Populate timeComboBox with time slots
            for (int i = 8; i <= 17; i++) {
                timeComboBox.getItems().add(String.format("%02d:00", i));
                timeComboBox.getItems().add(String.format("%02d:30", i));
            }


        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public void createChecklistClick(ActionEvent actionEvent) {
        userAuthorityCheck();

        if (!validateInputFields()) {
            CustomErrorAlert.showAlert("Invalid Input", "Please fill all required fields, including session date, time, patient, therapist, and program.");
            return;
        }

        try {
            TherapySessionsDTO therapySessionsDTO = new TherapySessionsDTO();
            therapySessionsDTO.setId(0);
            therapySessionsDTO.setDescription(descriptionIdField.getText());
            therapySessionsDTO.setSessionDate(sessionMakeDay.getValue());
            therapySessionsDTO.setSessionMadeDate(LocalDate.now());
            therapySessionsDTO.setStatus(TherapySessions.SessionStatus.SCHEDULED);

            String patientValue = SelectPatient.getValue();
            String therapistValue = SelectTherapist.getValue();
            String programValue = SelectProgram.getValue();
            String timeValue = timeComboBox.getValue();
            therapySessionsDTO.setSessionTime(timeValue);

            TherapistDTO therapist = therapistsBO.getTherapistByName(therapistValue);
            PatientsDTO patient = patientBO.getPatientByName(patientValue);
            TherapyProgramsDTO program = programsBO.getProgramByName(programValue);

            if (therapist == null || patient == null || program == null) {
                String errorMsg = "Invalid selection: " +
                        (therapist == null ? "Therapist not found, " : "") +
                        (patient == null ? "Patient not found, " : "") +
                        (program == null ? "Program not found" : "");
                CustomErrorAlert.showAlert("Invalid Selection", errorMsg);
                return;
            }

            therapySessionsDTO.setTherapistId(therapist.getId());
            therapySessionsDTO.setPatientId(patient.getId());
            // Parse StringProperty programId to Long
            String programIdStr = String.valueOf(program.getProgramId());
            if (programIdStr.trim().isEmpty()) {
                CustomErrorAlert.showAlert("Invalid Program", "Program ID is missing.");
                return;
            }
            try {
                therapySessionsDTO.setTherapyProgramId(Long.parseLong(programIdStr));
            } catch (NumberFormatException e) {
                CustomErrorAlert.showAlert("Invalid Program ID", "Program ID is not a valid number: " + programIdStr);
                return;
            }

            boolean isSaved = sessionsBO.insert(therapySessionsDTO);
            if (isSaved) {
                CustomAlert.showAlert("Checklist Created", "Checklist created successfully!");
                TherapistsViewController.bindNavigation(taskChecklistCreatePage, "/view/nav-buttons/task/task-create-success-view.fxml");
                clearContent();
            } else {
                CustomErrorAlert.showAlert("Save Failed", "Failed to save the checklist.");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            CustomErrorAlert.showAlert("Database Error", "Failed to save checklist due to a database issue.");
        } catch (ClassNotFoundException e) {
            System.err.println("Configuration error: " + e.getMessage());
            CustomErrorAlert.showAlert("Configuration Error", "Failed to save checklist due to a system configuration issue.");
        }
    }

    private void userAuthorityCheck() {
        String username = SessionUser.getLoggedInUsername();
        System.out.println("Logged in username Inside Create Checklist: " + username);

        UserRole userRole;
        try {
            userRole = queryDAO.getUserRoleByUsername(username);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if ((userRole != UserRole.ADMIN &&
                userRole != UserRole.RECEPTIONIST )) {
            CustomErrorAlert.showAlert("Access Denied", "You do not have permission to create projects.");

            return;
        }

        String loggedInUsername = SessionUser.getLoggedInUsername();
        Long userIdByUsername;
        try {
            userIdByUsername = userBO.getUserIdByUsername(loggedInUsername);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (userIdByUsername == null) {
            System.out.println("User ID is null for username: " + loggedInUsername);
            CustomErrorAlert.showAlert("User not found", "User not found. Please login again.");
        }
    }

    public void cancelOnClick(ActionEvent actionEvent) {
        if (areFieldsCleared()) {
            TherapistsViewController.bindNavigation(taskChecklistCreatePage, "/view/nav-buttons/program-view.fxml");
        } else {
            clearContent();
        }
    }

    private boolean areFieldsCleared() {
        return !SelectPatient.getSelectionModel().isEmpty() &&
                !descriptionIdField.getText().isEmpty() &&
                !SelectProgram.getSelectionModel().isEmpty() &&
                !SelectTherapist.getSelectionModel().isEmpty() &&
                sessionMakeDay.getValue() != null;
    }

    private void clearContent() {
        SelectProgram.getSelectionModel().clearSelection();
        SelectPatient.getSelectionModel().clearSelection();
        SelectTherapist.getSelectionModel().clearSelection();
        descriptionIdField.clear();
        sessionMakeDay.setValue(null);
    }

    private boolean validateInputFields() {
        return !SelectPatient.getSelectionModel().isEmpty() &&
                !descriptionIdField.getText().isEmpty() &&
                !SelectProgram.getSelectionModel().isEmpty() &&
                !SelectTherapist.getSelectionModel().isEmpty() &&
                sessionMakeDay.getValue() != null;
    }
}

