package com.safetynet.alerts.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record Person(

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

        @Schema(description = "Address of the person", example = "123 Main St")
        @NotBlank(message = "Address is mandatory")
        @Size(min = 5, max = 100, message = "Address must be between 5 and 100 characters")
        @Pattern(regexp = "^\\d+ [A-Za-z\\d '-]+$", message = "Address must start with a number followed by a space and then letters, numbers, spaces, hyphens or apostrophes")
        String address,

        @Schema(description = "City of the person", example = "Culver")
        @NotBlank(message = "City is mandatory")
        @Size(min = 2, max = 50, message = "City name must be between 2 and 50 characters")
        @Pattern(regexp = "^[A-Z][a-z]*(?:[ '-][A-Z][a-z-' ]*)*$", message = "City name must start with an uppercase letter and can only contain letters, hyphens, spaces and apostrophes")
        String city,

        @Schema(description = "Zip code of the person", example = "97451")
        @NotBlank(message = "Zipcode is mandatory")
        @Pattern(regexp = "^\\d{5}$", message = "Zip code must be in the format 12345")
        String zip,

        @Schema(description = "Phone number of the person", example = "123-456-7890")
        @NotBlank(message = "Phone number is mandatory")
        @Pattern(regexp = "^\\d{3}-\\d{3}-\\d{4}$", message = "Phone number must be in the format 123-456-7890")
        String phone,

        @Schema(description = "Email of the person", example = "john.doe@example.com")
        @NotBlank(message = "Email is mandatory")
        @Email(message = "Email format is not respected")
        String email
) {
}
