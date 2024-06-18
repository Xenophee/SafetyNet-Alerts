package com.safetynet.alerts.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record FireStation(
        @NotBlank(message = "Address is mandatory") String address,
        @Positive(message = "Station must be a positive number") int station
) {
}
