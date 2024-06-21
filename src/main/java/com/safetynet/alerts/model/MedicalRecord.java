package com.safetynet.alerts.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;


public record MedicalRecord(
        @NotBlank(message = "Firstname is mandatory") String firstName,
        @NotBlank(message = "Lastname is mandatory") String lastName,
        @NotNull(message = "Birthdate is mandatory") @PastOrPresent(message = "Birthdate can't be in the future") @JsonFormat(pattern = "MM/dd/yyyy") LocalDate birthdate,
        String[] medications,
        String[] allergies
) {
}
