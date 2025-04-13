package com.dinidu.lk.pmt.controller.dashboard.programs;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.ProgramsBO;
import com.dinidu.lk.pmt.bo.custom.TherapistsBO;
import com.dinidu.lk.pmt.bo.custom.UserBO;
import com.dinidu.lk.pmt.controller.dashboard.TherapistsViewController;
import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.custom.impl.QueryDAOImpl;
import com.dinidu.lk.pmt.dto.TherapyProgramsDTO;
import com.dinidu.lk.pmt.utils.*;
import com.dinidu.lk.pmt.utils.customAlerts.CustomAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.sql.SQLException;

public class CreateProgramsViewController {

    public TextArea feeField;
    public ComboBox<String> selectProgramDurationComboBox;
    public TextField programIDField;
    @FXML
    private AnchorPane taskCreatePage;
    @FXML
    private TextField programNameField;

    UserBO userBO = (UserBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.USER);
    TherapistsBO therapistsBO = (TherapistsBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.THERAPIST);
    ProgramsBO programsBO = (ProgramsBO) BOFactory.getInstance().getBO(BOFactory.BOTypes.PROGRAM);
    QueryDAO queryDAO = new QueryDAOImpl();

    public void initialize() {
        setProgramIDForUI();
        selectProgramDurationComboBox.setItems(FXCollections.observableArrayList("1 weeks", "2 weeks", "3 weeks", "1 Month", "2 Months", "3 Months", "4 Months", "5 Months", "6 Months"));
    }

    private void setProgramIDForUI() {
        long lastProgramID = getLastProgramIDFromDB();
        int lastGeneratedID;
        if (lastProgramID == 0) {
            lastGeneratedID = 1000;
        } else {
            String numberPart = String.valueOf(lastProgramID);
            lastGeneratedID = Integer.parseInt(numberPart);
        }

        lastGeneratedID++;
        String programsID = "MT" + String.format("%04d", lastGeneratedID);
        programIDField.setText(programsID);
    }

    private long getLastProgramIDFromDB() {
        try {
            return programsBO.getLastProgramID();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void createTaskClick() {
        Auth.userAccessLevelCheck();
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
            return;
        }

        System.out.println("\nProgram Details:");
        System.out.println("Program ID: " + programIDField.getText());
        System.out.println("Program Name: " + programNameField.getText());
        System.out.println("Program Fee: " + feeField.getText());
        System.out.println("Program Duration: " + selectProgramDurationComboBox.getSelectionModel().getSelectedItem());

        if (validateInputFields()) {
            TherapyProgramsDTO therapyProgramsDTO = new TherapyProgramsDTO();
            therapyProgramsDTO.setName(programNameField.getText());
            therapyProgramsDTO.setFee(feeField.getText());
            therapyProgramsDTO.setDuration(selectProgramDurationComboBox.getSelectionModel().getSelectedItem());

            System.out.println("Creating program: " + therapyProgramsDTO);

            try {
                if (therapyProgramsDTO.getName() == null) {
                    therapyProgramsDTO.setName(therapyProgramsDTO.getName());
                    System.out.println("Program name is null");
                    System.out.println("Program name: " + therapyProgramsDTO.getName());
                }else {
                    System.out.println("Program name in DTO: " + therapyProgramsDTO.getName());
                }

                boolean isInserted = programsBO.insertProgram(therapyProgramsDTO);
                if (isInserted) {
                    CustomAlert.showAlert("Success", "Program created successfully.");
                    TherapistsViewController.bindNavigation(taskCreatePage, "/view/nav-buttons/program-view.fxml");
                } else {
                    CustomErrorAlert.showAlert("Error", "Failed to create program.");
                }
            } catch (SQLException | ClassNotFoundException e) {
                CustomErrorAlert.showAlert("Error", "Failed to create program: " + e.getMessage());
            }
        }
    }

    public void cancelOnClick() {
        if (areFieldsCleared()) {
            TherapistsViewController.bindNavigation(taskCreatePage, "/view/nav-buttons/program-view.fxml");
        } else {
            clearContent();
        }
    }

    private boolean areFieldsCleared() {
        return programNameField.getText().isEmpty() &&
                feeField.getText().isEmpty() &&
                selectProgramDurationComboBox.getSelectionModel().isEmpty();
    }

    private void clearContent() {
        programNameField.clear();
        feeField.clear();
        selectProgramDurationComboBox.getSelectionModel().clearSelection();
    }

    private boolean validateInputFields() {
        return !programNameField.getText().isEmpty() &&
                !feeField.getText().isEmpty() &&
                !selectProgramDurationComboBox.getSelectionModel().isEmpty();
    }
}
