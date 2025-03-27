package com.dinidu.lk.pmt.dto;

import lombok.*;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class IssueAttachmentDTO {
    private Long id;
    private Long issueId;
    private String fileName;
    private Timestamp uploadedAt;
    private Long uploadedBy;
    private String fileUrl;
    private byte[] fileData;
}
