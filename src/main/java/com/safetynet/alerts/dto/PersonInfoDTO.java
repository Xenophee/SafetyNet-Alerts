package com.safetynet.alerts.dto;


public record PersonInfoDTO(
        String firstName,
        String lastName,
        String address,
        String email,
        int age,
        String[] medications,
        String[] allergies
) {
}
