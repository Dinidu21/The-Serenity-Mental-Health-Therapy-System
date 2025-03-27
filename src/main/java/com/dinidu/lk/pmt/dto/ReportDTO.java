package com.dinidu.lk.pmt.dto;

import com.dinidu.lk.pmt.utils.reportTypes.ReportType;
import lombok.*;

import java.sql.Timestamp;

@NoArgsConstructor
@ToString
@Getter
@Setter
@AllArgsConstructor
public class ReportDTO {
    private Long id;
    private String projectId;
    private Long userId;
    private ReportType reportType;
    private String content;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
