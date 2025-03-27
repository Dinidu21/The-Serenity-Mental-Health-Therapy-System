package com.dinidu.lk.pmt.dto;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TeamAssignmentDTO {
    private Long id;
    private Long taskId;
    private Long userId;
    private Timestamp assignedAt;
}