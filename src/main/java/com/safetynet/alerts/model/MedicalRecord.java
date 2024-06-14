package com.safetynet.alerts.model;

public record MedicalRecord(
        String firstName,
        String lastName,
        String birthdate,
        String[] medications,
        String[] allergies
) {
}
