package com.hr.newwork.util.enums;

public enum AbsenceType {
    VACATION, SICK, PERSONAL;

    public static AbsenceType fromString(String value) {
        for (AbsenceType type : AbsenceType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + value);
    }
}
