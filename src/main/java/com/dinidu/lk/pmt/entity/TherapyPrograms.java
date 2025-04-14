package com.dinidu.lk.pmt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Setter
@Table(name = "therapy_programs")
public class TherapyPrograms {

    @Id
    @Column(name = "program_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long programId;

    @Setter
    @Column(name = "program_name")
    private String programName;

    @Setter
    @Column(name = "duration", nullable = false)
    private String duration;

    @Setter
    @Column(name = "fee", nullable = false)
    private String fee;

}