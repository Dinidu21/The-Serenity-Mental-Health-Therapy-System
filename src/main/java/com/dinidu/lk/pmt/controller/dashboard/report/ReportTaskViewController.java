package com.dinidu.lk.pmt.controller.dashboard.report;

import com.dinidu.lk.pmt.controller.dashboard.TherapistsViewController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ReportTaskViewController implements Initializable {

    public Label currentDateTime;
    public ImageView csvIcon;
    public ImageView excelIcon;
    public ImageView refreshIcon;
    public ImageView printIcon;
    public AnchorPane assigneeView;
    @FXML
    private BarChart<Number, String> taskChart;

    @FXML
    private NumberAxis xAxis;

    @FXML
    private CategoryAxis yAxis;

//    ReportsBO reportsBO = (ReportsBO)
//            BOFactory.getInstance().getBO(BOFactory.BOTypes.REPORTS);


/*    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        String formattedDateTime = LocalDateTime.now().format(formatter);
        currentDateTime.setText("Calculated " + formattedDateTime);

        int maxTaskCount;
        try {
            maxTaskCount = reportsBO.getAllTaskReportData().values().stream()
                    .mapToInt(TaskReportData::getTaskCount)
                    .max()
                    .orElse(40);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(maxTaskCount + 10);
        xAxis.setTickUnit(Math.max(1, maxTaskCount / 10));

        XYChart.Series<Number, String> series = new XYChart.Series<>();
        try {
            for (TaskReportData data : reportsBO.getAllTaskReportData().values()) {
                int totalTaskCount = data.getTaskCount();
                String memberName = data.getAssigneeName();

                System.out.println("Member Name: " + memberName);
                System.out.println("Total Task Count: " + totalTaskCount);

                XYChart.Data<Number, String> chartData = new XYChart.Data<>(totalTaskCount, memberName);
                Label taskCountLabel = new Label(String.valueOf(totalTaskCount));
                taskCountLabel.getStyleClass().add("task-count-label");

                VBox nodeContainer = new VBox(taskCountLabel);
                nodeContainer.getStyleClass().add("node-container");
                nodeContainer.setAlignment(Pos.CENTER);

                chartData.setNode(nodeContainer);
                series.getData().add(chartData);
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        taskChart.setData(FXCollections.observableArrayList(series));
        taskChart.setBarGap(5);
        taskChart.setCategoryGap(10);

        animation(taskChart);
    }

    public void CSVonClick(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("TaskReport.csv");

        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            try (FileWriter writer = new FileWriter(file); CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Assignee", "Role", "Task ID", "Task Name", "Task Status", "Project Name", "Due Date", "Assigned Date", "Task Count"))) {

                for (TaskReportData data : reportsBO.getAllTaskReportData().values()) {
                    csvPrinter.printRecord(data.getAssigneeName(), data.getRole(), data.getTaskId(), data.getTaskName(), data.getTaskStatus(), data.getProjectName(), data.getDueDate(), data.getAssignedDate(), data.getTaskCount());
                }
                csvPrinter.flush();
                CustomAlert.showAlert("SUCCESS", "Report saved successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void XLSXonClick(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Excel File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("TaskReport.xlsx");

        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Tasks Per Assignee");

                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Assignee");
                headerRow.createCell(1).setCellValue("Role");
                headerRow.createCell(2).setCellValue("Task ID");
                headerRow.createCell(3).setCellValue("Task Name");
                headerRow.createCell(4).setCellValue("Task Status");
                headerRow.createCell(5).setCellValue("Project Name");
                headerRow.createCell(6).setCellValue("Due Date");
                headerRow.createCell(7).setCellValue("Assigned Date");
                headerRow.createCell(8).setCellValue("Task Count");

                int rowNum = 1;
                for (TaskReportData data : reportsBO.getAllTaskReportData().values()) {
                    Row row = sheet.createRow(rowNum++);

                    row.createCell(0).setCellValue(data.getAssigneeName());
                    row.createCell(1).setCellValue(data.getRole());
                    row.createCell(2).setCellValue(data.getTaskId());
                    row.createCell(3).setCellValue(data.getTaskName());
                    row.createCell(4).setCellValue(data.getTaskStatus());
                    row.createCell(5).setCellValue(data.getProjectName());
                    row.createCell(6).setCellValue(data.getDueDate());
                    row.createCell(7).setCellValue(data.getAssignedDate());
                    row.createCell(8).setCellValue(data.getTaskCount());
                }

                for (int i = 0; i < 9; i++) {
                    sheet.autoSizeColumn(i);
                }

                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }

                CustomAlert.showAlert("SUCCESS", "Report saved successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }*/

    public void PRINTonClick(MouseEvent mouseEvent) {

    }

    private void animation(BarChart<Number, String> taskChart) {
        for (Node node : taskChart.lookupAll(".node-container")) {
            node.setOnMouseEntered(event -> {
                node.setScaleX(1.03);
                node.setScaleY(1.03);
            });
            node.setOnMouseExited(event -> {
                node.setScaleX(1.0);
                node.setScaleY(1.0);
            });
        }
    }

    public void refreshClick(MouseEvent mouseEvent) {
        TherapistsViewController.bindNavigation(assigneeView, "/view/nav-buttons/report/report-task-view.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void XLSXonClick(MouseEvent mouseEvent) {
    }

    public void CSVonClick(MouseEvent mouseEvent) {

    }
}