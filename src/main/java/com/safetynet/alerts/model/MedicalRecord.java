package com.safetynet.alerts.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

// ! Corriger le système de validation des dates de naissance
public record MedicalRecord(
        @NotBlank(message = "Firstname is mandatory") String firstName,
        @NotBlank(message = "Lastname is mandatory") String lastName,
        @NotNull(message = "Birthdate is mandatory") @PastOrPresent(message = "Birthdate can't be in the future") String birthdate,
        String[] medications,
        String[] allergies
) {
}
