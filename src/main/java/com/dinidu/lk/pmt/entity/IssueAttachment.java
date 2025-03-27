package com.dinidu.lk.pmt.entity;

import lombok.*;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class IssueAttachment {
    private Long id;
    private Long issueId;
    private String fileName;
    private Timestamp uploadedAt;
    private Long uploadedBy;
    private String fileUrl;
    private byte[] fileData;
}
