package com.dinidu.lk.pmt.controller.dashboard.payments;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.PatientBO;
import com.dinidu.lk.pmt.bo.custom.PaymentsBO;
import com.dinidu.lk.pmt.bo.custom.TherapySessionsBO;
import com.dinidu.lk.pmt.dto.PatientsDTO;
import com.dinidu.lk.pmt.dto.PaymentDTO;
import com.dinidu.lk.pmt.dto.TherapySessionsDTO;
import com.dinidu.lk.pmt.entity.Payments;
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
        setupSearchAndFilter();
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
        idColumn.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getId()).asObject());
        patientColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getPatient() != null ? cellData.getValue().getPatient().toString() : ""));
        sessionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getTherapySession() != null ? cellData.getValue().getTherapySession().toString() : ""));
        amountColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getAmount()).asObject());
        dateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPaymentDate()));
        methodColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPaymentMethod()));
        statusColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStatus()));

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

//        transactionsTableView.setItems();
    }

    private void setupSearchAndFilter() {
        searchField.textProperty().addListener((obs, oldValue, newValue) -> filterTransactions());
        filterComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> filterTransactions());
    }

    private void loadInitialData() {

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
    private void handleRefresh(ActionEvent event) {

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
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete payment ID " + payment.getId() + "?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    paymentsBO.delete(payment);
                } catch (SQLException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void filterTransactions() {
        String searchText = searchField.getText().toLowerCase();
        String filter = filterComboBox.getValue();

        ObservableList<Payments> filteredList = FXCollections.observableArrayList();

//        for (Payments payment : paymentsList) {
//            boolean matchesSearch = searchText.isEmpty() ||
//                    String.valueOf(payment.getId()).contains(searchText) ||
//                    (payment.getPatient() != null && payment.getPatient().toString().toLowerCase().contains(searchText));
//
//            boolean matchesFilter = filter == null || filter.equals("All Transactions") ||
//                    (filter.equals("Pending") && payment.getStatus() == Payments.PaymentStatus.PENDING) ||
//                    (filter.equals("Completed") && payment.getStatus() == Payments.PaymentStatus.COMPLETED);
//
//            if (matchesSearch && matchesFilter) {
//                filteredList.add(payment);
//            }
//        }

        transactionsTableView.setItems(filteredList);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}