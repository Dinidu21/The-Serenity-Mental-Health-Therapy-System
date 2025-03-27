package com.dinidu.lk.pmt.dto;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@NoArgsConstructor
@ToString
@Getter
@Setter
@AllArgsConstructor
public class TimesheetDTO {
    private Long id;
    private Long userId;
    private String projectId;
    private Long taskId;
    private BigDecimal hours;
    private Date workDate;
    private String description;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
