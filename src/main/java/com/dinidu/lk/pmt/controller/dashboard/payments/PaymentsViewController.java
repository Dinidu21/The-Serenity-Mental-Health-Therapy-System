package com.dinidu.lk.pmt.controller.dashboard.payments;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.PatientBO;
import com.dinidu.lk.pmt.bo.custom.PaymentsBO;
import com.dinidu.lk.pmt.bo.custom.TherapySessionsBO;
import com.dinidu.lk.pmt.controller.dashboard.TherapistsViewController;
import com.dinidu.lk.pmt.dto.PatientsDTO;
import com.dinidu.lk.pmt.dto.PaymentDTO;
import com.dinidu.lk.pmt.dto.TherapySessionsDTO;
import com.dinidu.lk.pmt.entity.Patients;
import com.dinidu.lk.pmt.entity.Payments;
import com.dinidu.lk.pmt.entity.TherapySessions;
import com.dinidu.lk.pmt.utils.customAlerts.CustomDeleteAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PaymentsViewController implements Initializable {

    public AnchorPane paymentsPage;
    public Button viewTransactionsBtn;
    @FXML
    private ComboBox<String> patientComboBox;

    @FXML
    private ComboBox<String> sessionComboBox;

    @FXML
    private TextField amountField;

    @FXML
    private DatePicker paymentDatePicker;

    @FXML
    private RadioButton cashRadio;

    @FXML
    private RadioButton cardRadio;

    @FXML
    private RadioButton insuranceRadio;

    @FXML
    private ToggleGroup paymentMethodToggle;

    @FXML
    private ComboBox<Payments.PaymentStatus> statusComboBox;

    PaymentsBO paymentsBO =
            (PaymentsBO) BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.PAYMENTS);
    PatientBO patientBO =
            (PatientBO) BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.PATIENTS);
    TherapySessionsBO sessionsBO =
            (TherapySessionsBO) BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.SESSIONS);


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        comboboxInitialization();
    }
    private void comboboxInitialization() {
        try {
            statusComboBox.setItems(FXCollections.observableArrayList(Payments.PaymentStatus.values()));

            List<String> patientNames = patientBO.getAllPatients().stream()
                    .map(PatientsDTO::getFullName)
                    .collect(Collectors.toList());
            patientComboBox.getItems().setAll(patientNames);

            List<String> sessionNames = sessionsBO.getAllSessions().stream()
                    .map(TherapySessionsDTO::getDescription)
                    .collect(Collectors.toList());
            sessionComboBox.getItems().setAll(sessionNames);

        } catch (SQLException | ClassNotFoundException e) {
            CustomErrorAlert.showAlert("Initialization Error", "Failed to populate dropdowns: " + e.getMessage());
        }
    }

    @FXML
    private void handleProcessPayment(ActionEvent event) {
        try {
            String selectedPatient = patientComboBox.getSelectionModel().getSelectedItem();
            String selectedSession = sessionComboBox.getSelectionModel().getSelectedItem();
            double amount = Double.parseDouble(amountField.getText());
            LocalDate paymentDate = paymentDatePicker.getValue();
            Payments.PaymentStatus status = statusComboBox.getValue();

            handleValidation();

            PaymentDTO paymentDTO = new PaymentDTO();

            PatientsDTO patientByName = patientBO.getPatientByName(selectedPatient);

            paymentDTO.setPatientId(patientByName.getId());

            TherapySessionsDTO sessionsDTO = sessionsBO.getSessionIdByDesc(selectedSession);

            paymentDTO.setSessionId(sessionsDTO.getId());
            paymentDTO.setAmount(amount);
            paymentDTO.setPaymentDate(paymentDate.atStartOfDay());
            paymentDTO.setStatus(status);


            if (cashRadio.isSelected()) {
                paymentDTO.setPaymentMethod(Payments.PaymentMethod.CASH);
            } else if (cardRadio.isSelected()) {
                paymentDTO.setPaymentMethod(Payments.PaymentMethod.CARD);
            } else if (insuranceRadio.isSelected()) {
                paymentDTO.setPaymentMethod(Payments.PaymentMethod.INSURANCE);
            }

            boolean isSave = paymentsBO.save(paymentDTO);

            if (!isSave) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to process payment");
                return;
            }

            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Payment processed successfully");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid amount format");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to process payment: " + e.getMessage());
        }
    }

    private void handleValidation() {
        if (amountField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Amount field cannot be empty");
            return;
        }
        if (patientComboBox.getSelectionModel().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a patient");
            return;
        }
        if (sessionComboBox.getSelectionModel().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a session");
            return;
        }
        if (paymentDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a payment date");
            return;
        }
        if (statusComboBox.getSelectionModel().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a payment status");
            return;
        }
        if (paymentMethodToggle.getSelectedToggle() == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a payment method");
            return;
        }
        if (Double.parseDouble(amountField.getText()) <= 0) {
            showAlert(Alert.AlertType.ERROR, "Error", "Amount must be greater than zero");
            return;
        }
    }

    private void clearForm() {
        patientComboBox.getSelectionModel().clearSelection();
        sessionComboBox.getSelectionModel().clearSelection();
        amountField.clear();
        paymentDatePicker.setValue(LocalDate.now());
        statusComboBox.getSelectionModel().clearSelection();
        paymentMethodToggle.selectToggle(cashRadio);
    }

    @FXML
    private void handleClearForm(ActionEvent event) {
        patientComboBox.getSelectionModel().clearSelection();
        sessionComboBox.getSelectionModel().clearSelection();
        amountField.clear();
        paymentDatePicker.setValue(LocalDate.now());
        statusComboBox.getSelectionModel().clearSelection();
        cashRadio.setSelected(true);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleViewTransactions(ActionEvent actionEvent) {
        TherapistsViewController.bindNavigation(paymentsPage,"/view/nav-buttons/transactions-view.fxml");
    }
}