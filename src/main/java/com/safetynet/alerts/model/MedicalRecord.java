package com.safetynet.alerts.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;


public record MedicalRecord(

        @Schema(description = "First name of the person", example = "John")
        @NotBlank(message = "First name is mandatory")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        @Pattern(regexp = "^[A-Z][a-z]*(?:[ '-][A-Z][a-z-' ]*)*$", message = "First name must start with an uppercase letter and can only contain letters, hyphens, spaces and apostrophes")
        String firstName,

        @Schema(description = "Last name of the person", example = "Doe")
        @NotBlank(message = "Last name is mandatory")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        @Pattern(regexp = "^[A-Z][a-z]*(?:[ '-][A-Z][a-z-' ]*)*$", message = "Last name must start with an uppercase letter and can only contain letters, hyphens, spaces and apostrophes")
        String lastName,

        @Schema(description = "Birthdate of the person", type = "string", example = "12/29/2000")
        @NotNull(message = "Birthdate is mandatory")
        @PastOrPresent(message = "Birthdate can't be in the future")
        @JsonFormat(pattern = "MM/dd/yyyy")
        LocalDate birthdate,

        @Schema(description = "Array of medications. Each item should be a string", example = "[\"medication1\", \"medication2\"]")
        String[] medications,

        @Schema(description = "Array of allergies. Each item should be a string", example = "[\"allergy1\", \"allergy2\"]")
        String[] allergies
) {
}
