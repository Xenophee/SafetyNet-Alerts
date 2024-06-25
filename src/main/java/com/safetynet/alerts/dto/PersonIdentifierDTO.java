package com.safetynet.alerts.dto;

import jakarta.validation.constraints.NotBlank;

public record PersonIdentifierDTO(
        @NotBlank(message = "Firstname is mandatory") String firstName,
        @NotBlank(message = "Lastname is mandatory") String lastName
) {
}
