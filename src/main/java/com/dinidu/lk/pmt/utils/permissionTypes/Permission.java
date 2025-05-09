package com.dinidu.lk.pmt.utils.permissionTypes;

public enum Permission {
    MANAGE_ROLES(1),
    MANAGE_AUTHENTICATION(2),
    CREATE_THERAPIST(3),
    READ_THERAPIST(4),
    UPDATE_THERAPIST(5),
    DELETE_THERAPIST(6),
    ASSIGN_THERAPIST_PROGRAM(7),
    VIEW_THERAPIST_SCHEDULE(8),
    CREATE_PROGRAM(9),
    READ_PROGRAM(10),
    UPDATE_PROGRAM(11),
    DELETE_PROGRAM(12),
    CREATE_PATIENT(13),
    READ_PATIENT(14),
    UPDATE_PATIENT(15),
    DELETE_PATIENT(16),
    READ_PATIENT_HISTORY(17),
    CREATE_APPOINTMENT(18),
    READ_APPOINTMENT(19),
    UPDATE_APPOINTMENT(20),
    DELETE_APPOINTMENT(21),
    PROCESS_PAYMENT(22),
    CREATE_INVOICE(23),
    READ_INVOICE(24),
    TRACK_PAYMENT_STATUS(25),
    VIEW_THERAPIST_REPORTS(26),
    VIEW_FINANCIAL_REPORTS(27),
    VIEW_PATIENT_THERAPY_HISTORY(28);

    private final int id;

    Permission(int id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}