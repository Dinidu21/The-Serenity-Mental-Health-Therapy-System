package com.dinidu.lk.pmt.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "patients")
public class Patients {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "medical_history")
    private String medicalHistory;

    @Column(name = "registration_date", nullable = false)
    private java.time.LocalDateTime registrationDate;
}