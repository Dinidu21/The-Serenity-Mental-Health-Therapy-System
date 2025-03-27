package com.dinidu.lk.pmt.utils.userTypes;

public enum UserRole {
    ADMIN,
    RECEPTIONIST;

    public int getId() {
        return ordinal() + 1;
    }
}