package com.dinidu.lk.pmt.utils.permissionTypes;

import java.util.EnumSet;
import java.util.Set;

public enum PermissionLevel {
    READ_ONLY("Read Only") {
        @Override
        public Set<Permission> getPermissions() {
            return EnumSet.of(
                    Permission.READ_THERAPIST,
                    Permission.READ_PROGRAM,
                    Permission.READ_PATIENT,
                    Permission.READ_PATIENT_HISTORY,
                    Permission.READ_APPOINTMENT,
                    Permission.READ_INVOICE,
                    Permission.VIEW_THERAPIST_SCHEDULE,
                    Permission.VIEW_THERAPIST_REPORTS,
                    Permission.VIEW_FINANCIAL_REPORTS,
                    Permission.VIEW_PATIENT_THERAPY_HISTORY
            );
        }
    },
    EDITOR("Editor") {
        @Override
        public Set<Permission> getPermissions() {
            return EnumSet.of(
                    Permission.CREATE_THERAPIST,
                    Permission.READ_THERAPIST,
                    Permission.UPDATE_THERAPIST,
                    Permission.CREATE_PROGRAM,
                    Permission.READ_PROGRAM,
                    Permission.UPDATE_PROGRAM,
                    Permission.CREATE_PATIENT,
                    Permission.READ_PATIENT,
                    Permission.UPDATE_PATIENT,
                    Permission.READ_PATIENT_HISTORY,
                    Permission.CREATE_APPOINTMENT,
                    Permission.READ_APPOINTMENT,
                    Permission.UPDATE_APPOINTMENT,
                    Permission.PROCESS_PAYMENT,
                    Permission.CREATE_INVOICE,
                    Permission.READ_INVOICE,
                    Permission.TRACK_PAYMENT_STATUS,
                    Permission.VIEW_THERAPIST_SCHEDULE,
                    Permission.VIEW_THERAPIST_REPORTS,
                    Permission.VIEW_FINANCIAL_REPORTS,
                    Permission.VIEW_PATIENT_THERAPY_HISTORY
            );
        }
    },
    ALL("All") {
        @Override
        public Set<Permission> getPermissions() {
            return EnumSet.allOf(Permission.class);
        }
    };

    private final String displayName;

    PermissionLevel(String displayName) {
        this.displayName = displayName;
    }

    // Abstract method to be implemented by each enum constant
    public abstract Set<Permission> getPermissions();

    public static PermissionLevel getLevelFromPermissions(Set<Permission> permissions) {
        for (PermissionLevel level : PermissionLevel.values()) {
            if (level.getPermissions().equals(permissions)) {
                return level;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return displayName;
    }

    // Get permissions by selected level
    public static Set<Permission> getPermissionsByLevel(PermissionLevel level) {
        return level != null ? level.getPermissions() : EnumSet.noneOf(Permission.class);
    }
}