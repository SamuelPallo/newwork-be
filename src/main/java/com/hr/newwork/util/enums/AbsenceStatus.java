package com.hr.newwork.util.enums;

public enum AbsenceStatus {
    PENDING, APPROVED, REJECTED;

    public static AbsenceStatus fromString(String value) {
        for (AbsenceStatus status : AbsenceStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + value);
    }
}
