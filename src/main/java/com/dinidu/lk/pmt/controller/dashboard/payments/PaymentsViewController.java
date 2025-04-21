package com.dinidu.lk.pmt.controller.dashboard.payments;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.PatientBO;
import com.dinidu.lk.pmt.bo.custom.PaymentsBO;
import com.dinidu.lk.pmt.bo.custom.TherapySessionsBO;
import com.dinidu.lk.pmt.dto.PatientsDTO;
import com.dinidu.lk.pmt.dto.PaymentDTO;
import com.dinidu.lk.pmt.dto.TherapySessionsDTO;
import com.dinidu.lk.pmt.entity.Patients;
import com.dinidu.lk.pmt.entity.Payments;
import com.dinidu.lk.pmt.entity.TherapySessions;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PaymentsViewController implements Initializable {

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

    @FXML
    private Button processPaymentBtn;

    @FXML
    private Button clearFormBtn;

    @FXML
    private TextArea invoiceDetailsArea;

    @FXML
    private Button generateInvoiceBtn;

    @FXML
    private Button printInvoiceBtn;

    @FXML
    private Button emailInvoiceBtn;

    @FXML
    private ComboBox<String> filterComboBox;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Payments> transactionsTableView;

    @FXML
    private TableColumn<Payments, Long> idColumn;

    @FXML
    private TableColumn<Payments, String> patientColumn;

    @FXML
    private TableColumn<Payments, String> sessionColumn;

    @FXML
    private TableColumn<Payments, Double> amountColumn;

    @FXML
    private TableColumn<Payments, LocalDateTime> dateColumn;

    @FXML
    private TableColumn<Payments, Payments.PaymentMethod> methodColumn;

    @FXML
    private TableColumn<Payments, Payments.PaymentStatus> statusColumn;

    @FXML
    private TableColumn<Payments, Void> actionsColumn;

    @FXML
    private Button refreshBtn;

    @FXML
    private Button exportBtn;

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
        setupComboBoxes();
        setupTableView();
        loadInitialData();
    }

    private void loadInitialData() {
        try {
            List<PaymentDTO> paymentDTOs = paymentsBO.getAllPayments();
            System.out.println("Fetched PaymentDTOs: " + paymentDTOs.size());
            paymentDTOs.forEach(dto -> System.out.println("PaymentDTO: " + dto));

            List<Payments> payments = paymentDTOs.stream()
                    .map(this::convertToPaymentsEntity)
                    .collect(Collectors.toList());
            System.out.println("Converted Payments: " + payments);
            payments.forEach(p -> System.out.println("Payment: ID=" + p.getId() +
                    ", Patient=" + (p.getPatient() != null ? p.getPatient().getFullName() : "null") +
                    ", Session=" + (p.getTherapySession() != null ? p.getTherapySession().getDescription() : "null") +
                    ", Amount=" + p.getAmount() +
                    ", Date=" + p.getPaymentDate() +
                    ", Method=" + p.getPaymentMethod() +
                    ", Status=" + p.getStatus()));

            ObservableList<Payments> paymentList = FXCollections.observableArrayList(payments);
            transactionsTableView.setItems(paymentList);
            transactionsTableView.refresh();
            System.out.println("TableView items set: " + transactionsTableView.getItems().size());
        } catch (SQLException | ClassNotFoundException e) {
            CustomErrorAlert.showAlert("Data Load Error", "Failed to load payment data: " + e.getMessage());
        }
    }

    private Payments convertToPaymentsEntity(PaymentDTO dto) {
        Payments payment = new Payments();
        payment.setId(dto.getId());
        payment.setAmount(dto.getAmount());
        payment.setPaymentDate(dto.getPaymentDate());
        payment.setStatus(dto.getStatus());
        payment.setPaymentMethod(dto.getPaymentMethod());

        try {
            // Fetch patient
            PatientsDTO patientDTO = patientBO.getPatientById(dto.getPatientId());
            Patients patient = new Patients();
            patient.setId(patientDTO.getId());
            patient.setFullName(patientDTO.getFullName()); // Adjust based on actual fields
            payment.setPatient(patient);

            // Fetch therapy session
            TherapySessionsDTO sessionDTO = sessionsBO.getSessionById(dto.getSessionId());
            TherapySessions session = new TherapySessions();
            session.setId(sessionDTO.getId());
            session.setDescription(sessionDTO.getDescription()); // Adjust based on actual fields
            payment.setTherapySession(session);
        } catch (SQLException | ClassNotFoundException e) {
            CustomErrorAlert.showAlert("Data Conversion Error", "Failed to convert DTO to entity: " + e.getMessage());
        }

        return payment;
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadInitialData();
    }

    private void setupComboBoxes() {
        comboboxInitialization();
        statusComboBox.setItems(FXCollections.observableArrayList(Payments.PaymentStatus.values()));
        filterComboBox.setItems(FXCollections.observableArrayList("All Transactions", "Pending", "Completed"));
        filterComboBox.getSelectionModel().selectFirst();
    }

    private void comboboxInitialization() {
        try {

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

    private void setupTableView() {
        System.out.println("\nSetting up table view...\n");

        idColumn.setCellValueFactory(cellData -> {
            Payments payment = cellData.getValue();
            System.out.println("ID for payment: " + payment.getId());
            return new SimpleLongProperty(payment.getId()).asObject();
        });

        patientColumn.setCellValueFactory(cellData -> {
            Payments payment = cellData.getValue();
            String patientName = payment.getPatient() != null && payment.getPatient().getFullName() != null
                    ? payment.getPatient().getFullName()
                    : "Unknown Patient";
            System.out.println("Patient for payment " + payment.getId() + ": " + patientName);
            return new SimpleStringProperty(patientName);
        });

        sessionColumn.setCellValueFactory(cellData -> {
            Payments payment = cellData.getValue();
            String sessionDesc = payment.getTherapySession() != null && payment.getTherapySession().getDescription() != null
                    ? payment.getTherapySession().getDescription()
                    : "Unknown Session";
            System.out.println("Session for payment " + payment.getId() + ": " + sessionDesc);
            return new SimpleStringProperty(sessionDesc);
        });

        amountColumn.setCellValueFactory(cellData -> {
            Payments payment = cellData.getValue();
            System.out.println("Amount for payment " + payment.getId() + ": " + payment.getAmount());
            return new SimpleDoubleProperty(payment.getAmount() != null ? payment.getAmount() : 0.0).asObject();
        });

        dateColumn.setCellValueFactory(cellData -> {
            Payments payment = cellData.getValue();
            System.out.println("Date for payment " + payment.getId() + ": " + payment.getPaymentDate());
            return new SimpleObjectProperty<>(payment.getPaymentDate());
        });

        methodColumn.setCellValueFactory(cellData -> {
            Payments payment = cellData.getValue();
            System.out.println("Method for payment " + payment.getId() + ": " + payment.getPaymentMethod());
            return new SimpleObjectProperty<>(payment.getPaymentMethod());
        });

        statusColumn.setCellValueFactory(cellData -> {
            Payments payment = cellData.getValue();
            System.out.println("Status for payment " + payment.getId() + ": " + payment.getStatus());
            return new SimpleObjectProperty<>(payment.getStatus());
        });

        // Setup actions column
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("View");
            private final Button deleteBtn = new Button("Delete");
            private final HBox pane = new HBox(5, viewBtn, deleteBtn);

            {
                viewBtn.setOnAction(event -> {
                    Payments payment = getTableView().getItems().get(getIndex());
                    viewPaymentDetails(payment);
                });
                deleteBtn.setOnAction(event -> {
                    Payments payment = getTableView().getItems().get(getIndex());
                    deletePayment(payment);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        // Set placeholder
        transactionsTableView.setPlaceholder(new Label("No payments available"));
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
        invoiceDetailsArea.clear();
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

    @FXML
    private void handleGenerateInvoice(ActionEvent event) {
        invoiceDetailsArea.setText("Generated invoice details...");
    }

    @FXML
    private void handlePrintInvoice(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Print", "Printing invoice...");
    }

    @FXML
    private void handleEmailInvoice(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Email", "Sending invoice via email...");
    }

    @FXML
    private void handleExport(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Export", "Exporting data...");
    }

    private void viewPaymentDetails(Payments payment) {
        invoiceDetailsArea.setText("Payment ID: " + payment.getId() +
                "\nPatient: " + (payment.getPatient() != null ? payment.getPatient().toString() : "") +
                "\nAmount: $" + payment.getAmount() +
                "\nDate: " + payment.getPaymentDate() +
                "\nMethod: " + payment.getPaymentMethod() +
                "\nStatus: " + payment.getStatus());
    }

    private void deletePayment(Payments payment) {
        try {
            boolean isDeleted = paymentsBO.delete(payment.getId());
            if (isDeleted) {
                transactionsTableView.getItems().remove(payment);
                showAlert(Alert.AlertType.INFORMATION, "Deleted", "Payment deleted successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Delete Failed", "Unable to delete payment.");
            }
        } catch (Exception e) {
            CustomErrorAlert.showAlert("Delete Error", "Failed to delete payment: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}