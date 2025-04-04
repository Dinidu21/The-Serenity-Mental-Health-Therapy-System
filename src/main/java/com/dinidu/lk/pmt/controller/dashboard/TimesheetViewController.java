package com.dinidu.lk.pmt.controller.dashboard;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TimesheetViewController implements Initializable {

    public AnchorPane timeSheetPage;
    @FXML private GridPane calendarGrid;
    @FXML private FlowPane issuesPane;
    @FXML private Label dateRangeLabel;
    @FXML private Label spentTimeLabel;
    @FXML private Button prevWeekButton;
    @FXML private Button nextWeekButton;
    @FXML private Button todayButton;
    @FXML private Button addTimeButton;

    private LocalDate currentWeekStart;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM d");
    private final List<String> departments = Arrays.asList(
            "Design Dept", "Event Management Dept",
            "Marketing Dept", "Web Development"
    );

    private static class TimeEntry {
        String code;
        String type;
        int hours;

        TimeEntry(String code, String type, int hours) {
            this.code = code;
            this.type = type;
            this.hours = hours;
        }
    }

    private final Map<LocalDate, List<TimeEntry>> timeEntries = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentWeekStart = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
        setupUI();
        loadData();
        updateCalendar();
    }

    private void setupUI() {
        departments.forEach(dept -> {
            Label label = new Label(dept);
            label.getStyleClass().add("department-tag");
            issuesPane.getChildren().add(label);
        });

        calendarGrid.getColumnConstraints().clear();
        for (int i = 0; i < 7; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / 7);
            calendarGrid.getColumnConstraints().add(col);
        }

        setupNavigationHandlers();
    }

    private void setupNavigationHandlers() {
        if (prevWeekButton != null) {
            prevWeekButton.setOnAction(e -> navigateWeek(-1));
        }

        if (nextWeekButton != null) {
            nextWeekButton.setOnAction(e -> navigateWeek(1));
        }

        if (todayButton != null) {
            todayButton.setOnAction(e -> goToCurrentWeek());
        }

        if (addTimeButton != null) {
            addTimeButton.setOnAction(e -> handleAddSpentTime());
        }
    }

    private void navigateWeek(int weeks) {
        currentWeekStart = currentWeekStart.plusWeeks(weeks);
        updateCalendar();
    }

    private void goToCurrentWeek() {
        currentWeekStart = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
        updateCalendar();
    }

    private void loadData() {
        LocalDate today = LocalDate.now();

        LocalDate monday = today.minusDays(today.getDayOfWeek().getValue() - 1);
        addTimeEntry(monday, new TimeEntry("MKT-45", "Design", 8));
        addTimeEntry(monday, new TimeEntry("EVENT-27", "Documentation", 2));

        LocalDate tuesday = monday.plusDays(1);
        addTimeEntry(tuesday, new TimeEntry("MKT-56", "Development", 5));
        addTimeEntry(tuesday, new TimeEntry("DESIGN-22", "Design", 1));
        addTimeEntry(tuesday, new TimeEntry("EVENT-27", "Documentation", 6));

        LocalDate wednesday = monday.plusDays(2);
        addTimeEntry(wednesday, new TimeEntry("MKT-56", "Development", 3));
        addTimeEntry(wednesday, new TimeEntry("DT-22", "Testing", 2));

        LocalDate thursday = monday.plusDays(3);
        addTimeEntry(thursday, new TimeEntry("DT-22", "Testing", 2));
        addTimeEntry(thursday, new TimeEntry("PT-22", "Planning", 3));
        addTimeEntry(thursday, new TimeEntry("MKT-56", "Development", 2));

        prevWeeksData(monday);

        prevMonthData(today);
    }

    private void prevWeeksData(LocalDate monday) {
        LocalDate firstDayOfMonth = monday.withDayOfMonth(1);
        LocalDate firstMondayOfMonth = firstDayOfMonth.plusDays(
                (8 - firstDayOfMonth.getDayOfWeek().getValue()) % 7
        );

        LocalDate currentMonday = monday.minusWeeks(1);

        while (!currentMonday.isBefore(firstMondayOfMonth)) {
            generateRandomEntriesForWeek(currentMonday);
            currentMonday = currentMonday.minusWeeks(1);
        }
    }

    private void generateRandomEntriesForWeek(LocalDate monday) {
        LocalDate tuesday = monday.plusDays(1);
        LocalDate wednesday = monday.plusDays(2);
        LocalDate thursday = monday.plusDays(3);
        LocalDate friday = monday.plusDays(4);

        addTimeEntry(monday, new TimeEntry("WEEK-MKT-45", "Design", randomHours()));
        addTimeEntry(monday, new TimeEntry("WEEK-EVENT-10", "Documentation", randomHours()));

        addTimeEntry(tuesday, new TimeEntry("WEEK-DEV-12", "Development", randomHours()));
        addTimeEntry(tuesday, new TimeEntry("WEEK-TEST-5", "Testing", randomHours()));

        addTimeEntry(wednesday, new TimeEntry("WEEK-DESIGN-22", "Design", randomHours()));
        addTimeEntry(wednesday, new TimeEntry("WEEK-PLANNING-8", "Planning", randomHours()));

        addTimeEntry(thursday, new TimeEntry("WEEK-RESEARCH-9", "Research", randomHours()));
        addTimeEntry(thursday, new TimeEntry("WEEK-DEV-56", "Development", randomHours()));

        addTimeEntry(friday, new TimeEntry("WEEK-REVIEW-15", "Review", randomHours()));
        addTimeEntry(friday, new TimeEntry("WEEK-MKT-20", "Design", randomHours()));
    }

    private int randomHours() {
        return (int) (Math.random() * 8) + 1;
    }


    private void prevMonthData(LocalDate today) {
        LocalDate startOfPreviousMonth = today.minusMonths(1).withDayOfMonth(1);
        LocalDate endOfPreviousMonth = today.minusMonths(1).withDayOfMonth(today.minusMonths(1).lengthOfMonth());

        LocalDate currentDay = startOfPreviousMonth;
        while (!currentDay.isAfter(endOfPreviousMonth)) {
            if (currentDay.getDayOfWeek().getValue() <= 5) {
                addTimeEntry(currentDay, new TimeEntry("PREV-EVENT-27-" + currentDay.getDayOfMonth(), "Documentation", 7));
                addTimeEntry(currentDay, new TimeEntry("PREV-DT-" + currentDay.getDayOfMonth(), "Development", 3));
                addTimeEntry(currentDay, new TimeEntry("PREV-DESIGN-" + currentDay.getDayOfMonth(), "Design", 2));
            }
            currentDay = currentDay.plusDays(1);
        }
    }

    private void addTimeEntry(LocalDate date, TimeEntry entry) {
        timeEntries.computeIfAbsent(date, k -> new ArrayList<>()).add(entry);
    }

    private void updateCalendar() {
        calendarGrid.getChildren().clear();

        LocalDate weekEnd = currentWeekStart.plusDays(6);
        dateRangeLabel.setText(String.format("%s - %s",
                currentWeekStart.format(dateFormatter),
                weekEnd.format(dateFormatter)));

        for (int i = 0; i < 7; i++) {
            LocalDate date = currentWeekStart.plusDays(i);
            createDayColumn(i, date);
        }

        updateSpentTime();
    }

    private void createDayColumn(int col, LocalDate date) {
        VBox dayColumn = new VBox(10);
        dayColumn.getStyleClass().add("day-column");

        HBox header = createDayHeader(date);
        dayColumn.getChildren().add(header);

        List<TimeEntry> entries = timeEntries.getOrDefault(date, Collections.emptyList());
        for (TimeEntry entry : entries) {
            VBox entryBox = createTimeEntryBox(entry);
            dayColumn.getChildren().add(entryBox);
        }

        calendarGrid.add(dayColumn, col, 0);
    }

    private HBox createDayHeader(LocalDate date) {
        HBox header = new HBox(10);
        header.getStyleClass().add("day-header");
        header.setAlignment(Pos.CENTER_LEFT);

        Label dayLabel = new Label(date.getDayOfWeek().toString().substring(0, 3) + " " +
                date.getDayOfMonth());

        Label hoursLabel = new Label(getFormattedHours(date));
        hoursLabel.getStyleClass().add("hours-label");

        header.getChildren().addAll(dayLabel, hoursLabel);
        return header;
    }

    private VBox createTimeEntryBox(TimeEntry entry) {
        VBox entryBox = new VBox(5);
        entryBox.getStyleClass().add("time-entry");

        Label codeLabel = new Label(entry.code);
        codeLabel.getStyleClass().add("entry-code");

        Label hoursLabel = new Label(entry.hours + "h");
        hoursLabel.getStyleClass().add("entry-hours");

        Label typeLabel = new Label(entry.type);
        typeLabel.getStyleClass().addAll("entry-type", "entry-type-" + entry.type.toLowerCase());

        entryBox.getChildren().addAll(codeLabel, hoursLabel, typeLabel);
        return entryBox;
    }

    private String getFormattedHours(LocalDate date) {
        List<TimeEntry> entries = timeEntries.getOrDefault(date, Collections.emptyList());
        int totalHours = entries.stream().mapToInt(e -> e.hours).sum();
        return String.format("%dh of 8h", totalHours);
    }

    private void updateSpentTime() {
        int totalSpentHours = timeEntries.values().stream()
                .flatMap(List::stream)
                .mapToInt(e -> e.hours)
                .sum();

        spentTimeLabel.setText(String.format("Spent time %dd of 5d",
                totalSpentHours / 8));
    }

    @FXML
    private void handleAddSpentTime() {
        System.out.println("Add spent time clicked");
        TherapistsViewController.bindNavigation(timeSheetPage, "/view/nav-buttons/timesheet/timesheet-create-view.fxml");
    }
}