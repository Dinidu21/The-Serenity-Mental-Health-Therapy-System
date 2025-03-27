package com.dinidu.lk.pmt.dto;

import lombok.*;

@ToString
@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class TaskReportData {
    private String assigneeName;
    private String role;
    private long taskId;
    private String taskName;
    private String taskStatus;
    private String projectName;
    private String dueDate;
    private String assignedDate;
    private int taskCount;
}
