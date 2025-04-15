package com.dinidu.lk.pmt.controller.dashboard.programs.checklist;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.*;
import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.custom.impl.QueryDAOImpl;
import com.dinidu.lk.pmt.dto.PatientsDTO;
import com.dinidu.lk.pmt.dto.TherapistDTO;
import com.dinidu.lk.pmt.dto.TherapyProgramsDTO;
import com.dinidu.lk.pmt.dto.TherapySessionsDTO;
import com.dinidu.lk.pmt.entity.TherapySessions;
import com.dinidu.lk.pmt.utils.*;
import com.dinidu.lk.pmt.utils.customAlerts.CustomAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomDeleteAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.listeners.ChecklistDeletionHandler;
import com.dinidu.lk.pmt.utils.listeners.ChecklistUpdateListener;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.Setter;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ChecklistEditViewController implements Initializable {
    @FXML
    public Button saveTaskBtn;
    @FXML
    public Button cancelTaskBtn;
    @FXML
    public Button deleteTaskBtn;
    @FXML
    public ComboBox<String> newMembersComboBox;
    @FXML
    public TextField SessionDescription;
    @FXML
    public ComboBox<String> TherapistNamesCombo;
    @FXML
    public ComboBox<String> SessionTime;
    @FXML
    public ComboBox<String> PatientsNamesCombo;
    @FXML
    public ComboBox<String> programNamesCombo;
    public ComboBox<String> sessionStatus;
    @Setter
    private ChecklistDeletionHandler deletionHandler;
    @Setter
    private ChecklistUpdateListener updateListener;
    @FXML
    private Label backToMain;
    @FXML
    private AnchorPane TaskEdit;
    @FXML
    private DatePicker SessionDatePicker;

    public TherapySessionsDTO currentChecklist;

    private final UserBO userBO = (UserBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.USER);
    private final TherapySessionsBO sessionsBO = (TherapySessionsBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.SESSIONS);
    private final TherapistsBO therapistsBO = (TherapistsBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.THERAPIST);
    private final PatientBO patientBO = (PatientBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.PATIENTS);
    private final ProgramsBO programsBO = (ProgramsBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.PROGRAM);
    private final QueryDAO queryDAO = new QueryDAOImpl();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        comboboxInitialization();
        if (currentChecklist == null) {
            try {
                List<TherapySessionsDTO> checklistDTOList = sessionsBO.getAllSessions();
                if (!checklistDTOList.isEmpty()) {
                    currentChecklist = checklistDTOList.get(0);
                } else {
                    CustomErrorAlert.showAlert("No Data", "No checklists available.");
                    return;
                }
            } catch (SQLException | ClassNotFoundException e) {
                CustomErrorAlert.showAlert("Error", "Failed to fetch checklists: " + e.getMessage());
                return;
            }
        }
        loadChecklistData();

        SessionDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date != null && date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });
    }

    public void setChecklistData(TherapySessionsDTO checklist) {
        currentChecklist = checklist;
        loadChecklistData();
    }

    private void comboboxInitialization() {
        try {
            // Populate PatientsNamesCombo
            List<String> patientNames = patientBO.getAllPatients().stream()
                    .map(PatientsDTO::getFullName)
                    .collect(Collectors.toList());
            PatientsNamesCombo.getItems().setAll(patientNames);

            // Populate TherapistNamesCombo
            List<String> therapistNames = therapistsBO.getAllTherapists().stream()
                    .map(TherapistDTO::getFullName)
                    .collect(Collectors.toList());
            TherapistNamesCombo.getItems().setAll(therapistNames);

            // Populate programNamesCombo
            List<String> programNames = programsBO.getAllPrograms().stream()
                    .map(TherapyProgramsDTO::getName)
                    .collect(Collectors.toList());
            programNamesCombo.getItems().setAll(programNames);

            // Populate SessionTime
            for (int i = 8; i <= 17; i++) {
                SessionTime.getItems().add(String.format("%02d:00", i));
                SessionTime.getItems().add(String.format("%02d:30", i));
            }

            // Populate sessionStatus
            sessionStatus.getItems().addAll("SCHEDULED", "COMPLETED", "CANCELLED", "IN_PROGRESS");

        } catch (SQLException | ClassNotFoundException e) {
            CustomErrorAlert.showAlert("Initialization Error", "Failed to populate dropdowns: " + e.getMessage());
        }
    }

    private boolean updateModifiedChecklistFields() throws SQLException, ClassNotFoundException {
        boolean isModified = false;

        String description = SessionDescription.getText().trim();
        if (!description.equals(currentChecklist.getDescription() == null ? "" : currentChecklist.getDescription())) {
            currentChecklist.setDescription(description);
            isModified = true;
        }

        LocalDate sessionDate = SessionDatePicker.getValue();
        if (sessionDate != null && !sessionDate.equals(currentChecklist.getSessionDate())) {
            currentChecklist.setSessionDate(sessionDate);
            isModified = true;
        }

        String sessionTime = SessionTime.getValue();
        if (sessionTime != null && !sessionTime.equals(currentChecklist.getSessionTime())) {
            currentChecklist.setSessionTime(sessionTime);
            isModified = true;
        }

        String therapistName = TherapistNamesCombo.getValue();
        if (therapistName != null) {
            TherapistDTO therapist = therapistsBO.getTherapistByName(therapistName);
            if (therapist != null && !therapist.getId().equals(currentChecklist.getTherapistId())) {
                currentChecklist.setTherapistId(therapist.getId());
                isModified = true;
            }
        }

        String patientName = PatientsNamesCombo.getValue();
        if (patientName != null) {
            PatientsDTO patient = patientBO.getPatientByName(patientName);
            if (patient != null && !patient.getId().equals(currentChecklist.getPatientId())) {
                currentChecklist.setPatientId(patient.getId());
                isModified = true;
            }
        }

        String programName = programNamesCombo.getValue();
        if (programName != null) {
            TherapyProgramsDTO program = programsBO.getProgramByName(programName);
            if (program != null && program.getProgramId() != currentChecklist.getTherapyProgramId()) {
                currentChecklist.setTherapyProgramId(program.getProgramId());
                isModified = true;
            }
        }

        String status = sessionStatus.getValue();
        if (status != null && !status.equals(currentChecklist.getStatus().toString())) {
            currentChecklist.setStatus(TherapySessions.SessionStatus.valueOf(status));
            isModified = true;
        }

        return isModified;
    }

    @FXML
    public void saveChecklist() {
        try {
            boolean isModified = updateModifiedChecklistFields();
            if (!isModified) {
                CustomAlert.showAlert("No Changes", "No changes detected.");
                return;
            }

            if (!validateFilledFields()) {
                return;
            }

            System.out.println("Saving checklist: " + currentChecklist);

            System.out.println("Therapist ID: " + currentChecklist.getTherapistId());
            System.out.println("Patient ID: " + currentChecklist.getPatientId());
            System.out.println("Program ID: " + currentChecklist.getTherapyProgramId());
            System.out.println("Session Date: " + currentChecklist.getSessionDate());
            System.out.println("Session Time: " + currentChecklist.getSessionTime());
            System.out.println("Session Description: " + currentChecklist.getDescription());
            System.out.println("Session ID: " + currentChecklist.getId());
            System.out.println("Session Made Date: " + currentChecklist.getSessionMadeDate());
            System.out.println("Session Status: " + currentChecklist.getStatus());

            boolean isChecklistUpdated = sessionsBO.updateChecklist(currentChecklist);
            if (isChecklistUpdated) {
                CustomAlert.showAlert("Success", "Checklist updated successfully.");
                if (updateListener != null) {
                    updateListener.onChecklistUpdated(currentChecklist);
                }
                backToMain();
            } else {
                CustomErrorAlert.showAlert("Error", "Failed to update checklist.");
            }
        } catch (SQLException | ClassNotFoundException e) {
            CustomErrorAlert.showAlert("Error", "Failed to save checklist: " + e.getMessage());
        }
    }

    private void loadChecklistData() {
        if (currentChecklist == null) {
            CustomErrorAlert.showAlert("No Data", "No checklist selected.");
            return;
        }
        SessionDescription.setText(currentChecklist.getDescription());
        SessionDatePicker.setValue(currentChecklist.getSessionDate());
        SessionTime.setValue(currentChecklist.getSessionTime());
        sessionStatus.setValue(currentChecklist.getStatus().toString());

        try {
            TherapistNamesCombo.setValue(therapistsBO.getTherapistNameById(currentChecklist.getTherapistId()));
            System.out.println("Here is the therapist name: " + therapistsBO.getTherapistNameById(currentChecklist.getTherapistId()));
            PatientsNamesCombo.setValue(patientBO.getPatientNameById(currentChecklist.getPatientId()));
            System.out.println("Here is the patient name: " + patientBO.getPatientNameById(currentChecklist.getPatientId()));
            programNamesCombo.setValue(programsBO.getProgramNameById(currentChecklist.getTherapyProgramId()));
            System.out.println("Here is the program name: " + programsBO.getProgramNameById(currentChecklist.getTherapyProgramId()));

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean validateFilledFields() {
        StringBuilder errorMessage = new StringBuilder();
        boolean hasErrors = false;

        if (SessionDescription.getText().isEmpty()) {
            errorMessage.append("Session description cannot be empty.\n");
            hasErrors = true;
        }
        if (SessionDatePicker.getValue() == null) {
            errorMessage.append("Session date cannot be empty.\n");
            hasErrors = true;
        }
        if (SessionTime.getValue() == null) {
            errorMessage.append("Session time cannot be empty.\n");
            hasErrors = true;
        }
        if (TherapistNamesCombo.getValue() == null) {
            errorMessage.append("Therapist name cannot be empty.\n");
            hasErrors = true;
        }
        if (PatientsNamesCombo.getValue() == null) {
            errorMessage.append("Patient name cannot be empty.\n");
            hasErrors = true;
        }
        if (programNamesCombo.getValue() == null) {
            errorMessage.append("Program name cannot be empty.\n");
            hasErrors = true;
        }

        if (hasErrors) {
            CustomErrorAlert.showAlert("Validation Error", errorMessage.toString());
            return false;
        }
        return true;
    }

    @FXML
    public void deleteChecklist() {
        String username = SessionUser.getLoggedInUsername();
        if (username == null) {
            CustomErrorAlert.showAlert("Error", "User not logged in.");
            return;
        }

        UserRole userRole;
        try {
            userRole = queryDAO.getUserRoleByUsername(username);
        } catch (SQLException | ClassNotFoundException e) {
            CustomErrorAlert.showAlert("Error", "Failed to verify user role: " + e.getMessage());
            return;
        }

        if (userRole != UserRole.ADMIN && userRole != UserRole.RECEPTIONIST) {
            CustomErrorAlert.showAlert("Access Denied", "Only Admin or Receptionist can delete checklists.");
            deleteTaskBtn.setVisible(false);
            return;
        }

        boolean confirmed = CustomDeleteAlert.showAlert(
                (Stage) TaskEdit.getScene().getWindow(),
                "Confirm Deletion",
                "Are you sure you want to delete this checklist?"
        );

        if (confirmed) {
            try {
                if (sessionsBO.deleteChecklist(currentChecklist.getId())) {
                    CustomAlert.showAlert("Success", "Checklist deleted successfully.");
                    Stage currentStage = (Stage) TaskEdit.getScene().getWindow();
                    currentStage.close();
                    if (deletionHandler != null) {
                        deletionHandler.onChecklistDeleted();
                    }
                } else {
                    CustomErrorAlert.showAlert("Error", "Failed to delete checklist.");
                }
            } catch (SQLException | ClassNotFoundException e) {
                CustomErrorAlert.showAlert("Error", "Failed to delete checklist: " + e.getMessage());
            }
        }
    }

    @FXML
    public void backToMain() {
        ((Stage) backToMain.getScene().getWindow()).close();
    }

    @FXML
    public void cancelTaskBtnOnClick() {
        ((Stage) TaskEdit.getScene().getWindow()).close();
    }

    @FXML
    public void editStatus() {}
    @FXML
    public void editPriority() {}
    @FXML
    public void editName() {}
    @FXML
    public void setEndDate() {}
    @FXML
    public void editTaskDesc() {}
    @FXML
    public void assignNewUserForCurrentTask() {}
}