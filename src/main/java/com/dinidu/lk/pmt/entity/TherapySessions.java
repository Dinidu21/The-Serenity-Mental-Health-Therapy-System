package com.dinidu.lk.pmt.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "therapy_sessions")
public class TherapySessions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patients patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "therapist_id", nullable = false)
    private Therapists therapist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private TherapyPrograms therapyProgram;

    @Column(name = "session_date", nullable = false)
    private java.time.LocalDateTime sessionDate;

    @Column(name = "status", nullable = false)
    private String status; // e.g., "SCHEDULED", "COMPLETED", "CANCELLED"
}