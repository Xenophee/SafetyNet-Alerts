package com.safetynet.alerts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;


public record PersonIdentifierDTO(
        @Schema(description = "First name of the person", example = "John")
        @NotBlank(message = "First name is mandatory")
        String firstName,

        @Schema(description = "Last name of the person", example = "Doe")
        @NotBlank(message = "Last name is mandatory")
        String lastName
) {
}
