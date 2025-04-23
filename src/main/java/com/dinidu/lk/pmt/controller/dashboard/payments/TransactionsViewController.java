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
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TransactionsViewController implements Initializable {

    public AnchorPane paymentsPage;
    public AnchorPane transactionsPg;
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

    @FXML
    private Button processPaymentBtn;

    @FXML
    private Button clearFormBtn;

    @FXML
    private TextArea invoiceDetailsArea;

    @FXML
    private ComboBox<String> filterComboBox;

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
        setupComboBoxes();
    }

    private void setupComboBoxes() {
        statusComboBox.setItems(FXCollections.observableArrayList(Payments.PaymentStatus.values()));
        filterComboBox.setItems(FXCollections.observableArrayList("All Transactions", "Pending", "Completed"));
        filterComboBox.getSelectionModel().selectFirst();
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
            if (payment == null) {
                showAlert(Alert.AlertType.ERROR, "Delete Failed", "No payment selected.");
                return;
            }
            if (payment.getId() == null) {
                showAlert(Alert.AlertType.ERROR, "Delete Failed", "Payment ID is null.");
                return;
            }
            // Confirm deletion
            boolean confirmed = CustomDeleteAlert.showAlert(
                    (Stage) transactionsPg.getScene().getWindow(),
                    "Confirm Deletion",
                    "Are you sure you want to delete this Payment ? "
            );

            if (confirmed) {
                boolean isDeleted;
                try {
                    isDeleted = paymentsBO.delete(payment.getId());
                } catch (SQLException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                if (isDeleted) {
                    transactionsTableView.getItems().remove(payment);
                    showAlert(Alert.AlertType.INFORMATION, "Deleted", "Payment deleted successfully!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Delete Failed", "Unable to delete payment.");
                }
            }

        } catch (RuntimeException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleCreatePayments(ActionEvent actionEvent) {
        TherapistsViewController.bindNavigation(transactionsPg,"/view/nav-buttons/payments-view.fxml");
    }
}