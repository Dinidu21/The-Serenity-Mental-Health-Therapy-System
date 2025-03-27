package com.dinidu.lk.pmt.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "therapy_programs")
public class TherapyPrograms {

    @Id
    @Column(name = "program_id", nullable = false)
    private String programId;

    @Column(name = "program_name", nullable = false)
    private String programName;

    @Column(name = "duration", nullable = false)
    private String duration;

    @Column(name = "fee", nullable = false)
    private String fee;
}