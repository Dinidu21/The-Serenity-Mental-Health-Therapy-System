package com.dinidu.lk.pmt.entity;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TeamAssignment {
    private Long id;
    private Long taskId;
    private Long userId;
    private Timestamp assignedAt;
}