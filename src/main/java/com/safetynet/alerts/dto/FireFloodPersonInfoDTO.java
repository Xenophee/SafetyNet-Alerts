package com.safetynet.alerts.dto;


public record FireFloodPersonInfoDTO(
        String firstName,
        String lastName,
        String phone,
        int age,
        String[] medications,
        String[] allergies
) {
}
