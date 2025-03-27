package com.dinidu.lk.pmt.controller.dashboard;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

public class DashboardExpandViewController implements Initializable {
    public ImageView playIcon;
    public ImageView pauseIcon;
    public VBox notesContainer;
    public VBox activityContainer;
    public Circle userAvatar2;
    public Circle userAvatar1;
    public AnchorPane myDashboard;
    @FXML private MediaView mediaView;
    @FXML private ComboBox<String> dashboardComboBox;
    @FXML private Label daysLabel;
    @FXML private Label hoursLabel;
    @FXML private Label minutesLabel;
    @FXML private Label secondsLabel;
    @FXML private ImageView petImage;

    private MediaPlayer mediaPlayer;
    private Timeline timeline;
    private LocalDateTime conferenceDateTime;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        videoView();
        setupDashboardComboBox();
        setupTimer();
        setupImages();
        setupVideoControls();
    }

    private void videoView() {
        URL resource = getClass().getResource("/asserts/videos/Outlook _ a Microsoft Design video.mp4");
        if (resource == null) {
            System.out.println("Video path is null, please check the file path.");
        } else {
            String videoPath = resource.toExternalForm();
            Media media = new Media(videoPath);
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
        }
    }

    @FXML
    private void setupVideoControls() {
        playIcon.setOnMouseClicked(event -> {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                playIcon.setVisible(true);
                pauseIcon.setVisible(false);
            } else {
                mediaPlayer.play();
                playIcon.setVisible(false);
                pauseIcon.setVisible(true);
            }
        });

        pauseIcon.setOnMouseClicked(event -> {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                playIcon.setVisible(true);
                pauseIcon.setVisible(false);
            }
        });

        pauseIcon.setVisible(false);

        mediaView.setOnMouseEntered(event -> {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                pauseIcon.setVisible(true);
            }
        });

        mediaView.setOnMouseExited(event -> {
            pauseIcon.setVisible(false);
        });

        pauseIcon.setOnMouseEntered(event -> {
            pauseIcon.setVisible(true);
        });

        pauseIcon.setOnMouseExited(event -> {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                pauseIcon.setVisible(true);
            } else {
                pauseIcon.setVisible(false);
            }
        });

        updatePlayPauseIcons();
    }

    private void updatePlayPauseIcons() {
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            playIcon.setVisible(false);
            pauseIcon.setVisible(true);
        } else {
            playIcon.setVisible(true);
            pauseIcon.setVisible(false);
        }
    }

    private void setupDashboardComboBox() {
        dashboardComboBox.getItems().addAll("My Dashboard", "Business Overview");
        dashboardComboBox.setValue("My Dashboard");

        dashboardComboBox.setOnAction(event -> {
            String selectedDashboard = dashboardComboBox.getValue();
            System.out.println("Selected dashboard: " + selectedDashboard);

            if ("Business Overview".equals(selectedDashboard)) {
                ProjectViewController.bindNavigation(
                        myDashboard,
                        "/view/nav-buttons/dashboard-btn-view.fxml"
                );
            } else if ("My Dashboard".equals(selectedDashboard)) {
                ProjectViewController.bindNavigation(
                        myDashboard,
                        "/view/nav-buttons/dashboard-expand-view.fxml"
                );
            }
        });
    }

    private void setupTimer() {
        conferenceDateTime = LocalDateTime.now().plusDays(34).plusHours(23).plusMinutes(46).plusSeconds(56);

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateTimer()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void updateTimer() {
        LocalDateTime now = LocalDateTime.now();

        long days = ChronoUnit.DAYS.between(now, conferenceDateTime);
        long hours = ChronoUnit.HOURS.between(now, conferenceDateTime) % 24;
        long minutes = ChronoUnit.MINUTES.between(now, conferenceDateTime) % 60;
        long seconds = ChronoUnit.SECONDS.between(now, conferenceDateTime) % 60;

        daysLabel.setText(String.format("%02d", days));
        hoursLabel.setText(String.format("%02d", hours));
        minutesLabel.setText(String.format("%02d", minutes));
        secondsLabel.setText(String.format("%02d", seconds));
    }

    private void setupImages() {
        try {
            petImage.setImage(new Image(getClass().getResourceAsStream("/asserts/gifs/pets/kitty3.gif")));
        } catch (Exception e) {
            System.err.println("Could not load pet image: " + e.getMessage());
        }
    }

    public void timerMoreOnClick(MouseEvent mouseEvent) {
    }

    public void petMoreOnClick(MouseEvent mouseEvent) {
    }
}