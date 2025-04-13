package com.dinidu.lk.pmt.controller.dashboard.patients;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.*;
import com.dinidu.lk.pmt.controller.dashboard.TherapistsViewController;
import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.custom.impl.QueryDAOImpl;
import com.dinidu.lk.pmt.dto.*;
import com.dinidu.lk.pmt.utils.*;
import com.dinidu.lk.pmt.utils.customAlerts.CustomAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomDeleteAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.listeners.IssueDeletionHandler;
import com.dinidu.lk.pmt.utils.listeners.IssueUpdateListener;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;
import javafx.animation.FadeTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CreatePatientSuccessViewController implements Initializable, IssueUpdateListener, IssueDeletionHandler {

    public Label taskName;
    public VBox dragDropArea;
    public Button uploadButton;
    public VBox attachmentContainer;
    public Label noAttachments;
    public Label PatientName;
    public Label PatientFName;
    public Label PatientAddress;
    public Label PatientMobile;
    public Label PatientRegDate;
    public Label PatientEmail;
    private double xOffset = 0;
    private double yOffset = 0;
    public ImageView moreIcon;
    public AnchorPane sideBar;
    public AnchorPane mainBar;
    public AnchorPane issueSuccessPage;
    public Label projectOwnerName;
    public Label teamCount;
    public Label teamMember1;
    public Label teamMember2;
    public Label teamMember3;
    public Label teamMember4;
    public ImageView projectOwnerImg;
    public ImageView teamMember1Img;
    public ImageView teamMember2Img;
    public ImageView teamMember3Img;
    public ImageView teamMember4Img;
    private PatientsDTO patientsDTO;
    private static Long currentIssueId;
    private static Long currentUserId;
    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB in bytes

    UserBO userBO= (UserBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.USER);

    QueryDAO queryDAO = new QueryDAOImpl();

    TherapistsBO therapistsBO = (TherapistsBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.THERAPIST);

    ProgramsBO programsBO = (ProgramsBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.PROGRAM);
    PatientBO patientBO = (PatientBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.PATIENTS);



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        noAttachments.setVisible(false);
        userAccessControl();
        setupDragAndDrop();
        setupUploadButton();
    }

    public void setIssuesData(PatientsDTO patientsDTO) {
        this.patientsDTO = patientsDTO;
        currentIssueId = patientsDTO.getId();
        System.out.println("current Patient Id: " + currentIssueId);

        PatientName.setText(patientsDTO.getFullName());
        PatientFName.setText(patientsDTO.getFullName());
        PatientAddress.setText(patientsDTO.getAddress());
        PatientMobile.setText(patientsDTO.getPhoneNumber());
        PatientRegDate.setText(patientsDTO.getRegistrationDate() != null ? patientsDTO.getRegistrationDate().toString() : "");
        PatientEmail.setText(patientsDTO.getEmail());

        setupStyleListeners();
        updateStyles(patientsDTO);
    }

    @Override
    public void onIssueUpdated(PatientsDTO updatedIssue) {
        setIssuesData(updatedIssue);
    }

    private void userAccessControl() {
        if (TherapistsViewController.backgroundColor == null) {
            System.out.println("Project background color is null");
        }

        String username = SessionUser.getLoggedInUsername();
        try {
            currentUserId = userBO.getUserIdByUsername(username);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

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
        }
        System.out.println("User role: " + userRoleByUsername);
        if (userRoleByUsername != UserRole.ADMIN &&
                userRoleByUsername != UserRole.RECEPTIONIST) {
            System.out.println("Access denied: Only Admin, Project Manager, or Product Owner can edit projects.");
            moreIcon.setVisible(false);
        }
    }

    public void moreIconOnclick(MouseEvent mouseEvent) {
        try {
            String iconPath = "/asserts/icons/PN.png";
            String fxmlPath = "/view/nav-buttons/issue/issue-edit-view.fxml";

            Stage modalStage = new Stage();
            modalStage.initStyle(StageStyle.TRANSPARENT);
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner((Stage) issueSuccessPage.getScene().getWindow());

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            PatientEditViewController controller = loader.getController();
            controller.setDeletionHandler(this);
            controller.setUpdateListener(this);

            root.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });

            root.setOnMouseDragged(event -> {
                modalStage.setX(event.getScreenX() - xOffset);
                modalStage.setY(event.getScreenY() - yOffset);
            });

            Scene scene = new Scene(root);
            scene.setFill(null);
            scene.setFill(null);

            modalStage.setScene(scene);
            modalStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath))));
            modalStage.centerOnScreen();
            modalStage.show();

        } catch (Exception e) {
            System.out.println("Error while loading modal: " + e.getMessage());
            e.printStackTrace();
            CustomErrorAlert.showAlert("Error", "Error while loading modal.");
        }
    }

    @Override
    public void onIssueDeleted() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/nav-buttons/patients-view.fxml"));
            AnchorPane newContent = loader.load();

            AnchorPane parentPane = (AnchorPane) issueSuccessPage.getParent();

            parentPane.getChildren().clear();

            newContent.prefWidthProperty().bind(parentPane.widthProperty());
            newContent.prefHeightProperty().bind(parentPane.heightProperty());

            FadeTransition fadeIn = new FadeTransition(Duration.millis(750), newContent);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();

            parentPane.getChildren().add(newContent);

        } catch (IOException e) {
            e.printStackTrace();
            CustomErrorAlert.showAlert("Error", "Failed to load the issue view.");
        }
    }


    /*========================*/
    private void setupDragAndDrop() {
        dragDropArea.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        dragDropArea.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                List<File> validFiles = db.getFiles().stream()
                        .filter(this::isFileSizeValid)
                        .collect(Collectors.toList());
                if (validFiles.isEmpty()) {
                    CustomErrorAlert.showAlert("Invalid Files", "All selected files exceed the 100MB size limit.");
                } else {
                    uploadFiles(validFiles);
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void setupUploadButton() {
        uploadButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Files to Upload");
            List<File> files = fileChooser.showOpenMultipleDialog(uploadButton.getScene().getWindow());
            if (files != null) {
                List<File> validFiles = files.stream()
                        .filter(this::isFileSizeValid)
                        .collect(Collectors.toList());
                if (validFiles.isEmpty()) {
                    CustomErrorAlert.showAlert("Invalid Files", "All selected files exceed the 100MB size limit.");
                } else {
                    uploadFiles(validFiles);
                }
            }
        });
    }

    private void uploadFiles(List<File> files) {
        for (File file : files) {
            if (isFileSizeValid(file)) {
                uploadFile(file);
            } else {
                CustomErrorAlert.showAlert("File Size Exceeded",
                        "The file \"" + file.getName() + "\" exceeds the maximum size limit of 100MB and will not be uploaded.");
            }
        }
    }

    private void uploadFile(File file) {
        Task<IssueAttachmentDTO> uploadTask = new Task<>() {
            @Override
            protected IssueAttachmentDTO call() throws Exception {
                updateProgress(0, 100);
                noAttachments.setVisible(false);

                IssueAttachmentDTO attachment = new IssueAttachmentDTO();
                attachment.setIssueId(currentIssueId);
                attachment.setFileName(file.getName());
                attachment.setUploadedAt(new Timestamp(System.currentTimeMillis()));
                attachment.setUploadedBy(currentUserId);

                byte[] fileData = new byte[(int) file.length()];
                try (FileInputStream fis = new FileInputStream(file)) {
                    fis.read(fileData);
                }
                attachment.setFileData(fileData);

                for (int i = 0; i <= 100; i++) {
                    updateProgress(i, 100);
                    Thread.sleep(20);
                }

//                issueAttachmentBO.saveAttachment(attachment);

                System.out.println("=================CREATE ISSUE SUCCESS VIEW CONTROLLER=================");
                System.out.println("Here is the Uploaded id "+ attachment.getId());
                return attachment;
            }
        };

        HBox progressBox = new HBox(10);
        progressBox.setAlignment(Pos.CENTER_LEFT);
        Label fileNameLabel = new Label(file.getName());
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(200);
        progressBox.getChildren().addAll(fileNameLabel, progressBar);
        attachmentContainer.getChildren().add(progressBox);

        progressBar.progressProperty().bind(uploadTask.progressProperty());

        uploadTask.setOnSucceeded(e -> {
            IssueAttachmentDTO attachment = uploadTask.getValue();
            attachmentContainer.getChildren().remove(progressBox);
            addAttachmentRow(attachment);
        });

        new Thread(uploadTask).start();
    }

    private void addAttachmentRow(IssueAttachmentDTO attachment) {
        HBox attachmentRow = new HBox(10);
        attachmentRow.getStyleClass().add("attachment-row");
        attachmentRow.setAlignment(Pos.CENTER_LEFT);


        ImageView fileIcon = new ImageView(new Image(getClass().getResourceAsStream("/asserts/icons/file.png")));
        fileIcon.setFitHeight(24);
        fileIcon.setFitWidth(24);

        VBox fileInfo = new VBox(2);
        Label fileName = new Label(attachment.getFileName());
        Label fileSize = new Label(formatFileSize(attachment.getFileData().length));
        fileInfo.getChildren().addAll(fileName, fileSize);

        Button downloadBtn = new Button("Download");
        downloadBtn.getStyleClass().add("download-button");
        downloadBtn.setOnAction(e -> downloadFile(attachment));

        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("delete-button");
        deleteBtn.setOnAction(e -> deleteAttachment(attachment, attachmentRow));

        attachmentRow.getChildren().addAll(fileIcon, fileInfo, downloadBtn, deleteBtn);
        attachmentContainer.getChildren().add(attachmentRow);
    }

    private void downloadFile(IssueAttachmentDTO attachment) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(attachment.getFileName());
        File file = fileChooser.showSaveDialog(attachmentContainer.getScene().getWindow());

        if (file != null) {
            Task<Void> downloadTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        fos.write(attachment.getFileData());
                    }
                    return null;
                }
            };

            downloadTask.setOnSucceeded(e -> {
                CustomAlert.showAlert("Success", "Attachment downloaded successfully!");
            });

            new Thread(downloadTask).start();
        }
    }

    private void deleteAttachment(IssueAttachmentDTO attachment, HBox attachmentRow) {
        long currentIssueAttachmentId = 0;
        if (attachment == null) {
            System.out.println("❌ ERROR: Attachment object is null!");
            return;
        }


/*
        if (attachment.getId() == null) {
            System.out.println("❌ ERROR: Attachment ID is null! Cannot delete.");
            System.out.println("Fetching Last Added Attachment ID...");
            try {
                currentIssueAttachmentId = issueAttachmentBO.getLastAddedAttachmentId(currentIssueId);
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
*/

        boolean confirmed = CustomDeleteAlert.showAlert(
                (Stage) attachmentContainer.getScene().getWindow(),
                "Confirm Delete",
                "Are you sure you want to delete this attachment? This action cannot be undone."
        );

        if (!confirmed) {
            return;
        }

        long finalCurrentIssueAttachmentId = currentIssueAttachmentId;
        Task<Boolean> deleteTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                System.out.println("🔍 Calling deleteAttachment() with ID: " + attachment.getId());
                if(attachment.getId() == null) {
                    System.out.println("dto id is null...");
                    System.out.println("Fetching Last Added Attachment ID...: "+ finalCurrentIssueAttachmentId);

                }
                return null;
//                return issueAttachmentBO.deleteAttachment(attachment.getId() == null ? finalCurrentIssueAttachmentId : attachment.getId());
            }
        };

        deleteTask.setOnSucceeded(e -> {
            if (deleteTask.getValue()) {
                attachmentContainer.getChildren().remove(attachmentRow);
                CustomAlert.showAlert("Success", "Attachment deleted successfully!");
            } else {
                CustomErrorAlert.showAlert("Error", "Failed to delete the attachment.");
            }
        });

        deleteTask.setOnFailed(e -> {
            Throwable ex = deleteTask.getException();
            ex.printStackTrace();
            CustomErrorAlert.showAlert("Error", "An unexpected error occurred: " + ex.getMessage());
        });

        new Thread(deleteTask).start();
    }

    private String formatFileSize(long bytes) {
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int unitIndex = 0;
        double size = bytes;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return String.format("%.2f %s", size, units[unitIndex]);
    }

    private boolean isFileSizeValid(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        return file.length() <= MAX_FILE_SIZE;
    }

    private void setupStyleListeners() {

    }

    private void updateStyles(PatientsDTO patientsDTO) {

    }
}
