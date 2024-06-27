package com.safetynet.alerts.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record FireStation(

        @Schema(description = "Address of the fire station", example = "123 Main St")
        @NotBlank(message = "Address is mandatory")
        @Size(min = 5, max = 100, message = "Address must be between 5 and 100 characters")
        @Pattern(regexp = "^\\d+ [A-Za-z\\d '-]+$", message = "Address must start with a number followed by a space and then letters, numbers, spaces, hyphens or apostrophes")
        String address,

        @Schema(description = "Number of the station", example = "1")
        @Positive(message = "Station must be a positive number")
        int station
) {
}
