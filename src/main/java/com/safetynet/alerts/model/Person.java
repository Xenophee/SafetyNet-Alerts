package com.safetynet.alerts.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record Person(
        @NotBlank(message = "Firstname is mandatory") String firstName,
        @NotBlank(message = "Lastname is mandatory") String lastName,
        @NotBlank(message = "Address is mandatory") String address,
        @NotBlank(message = "City is mandatory") String city,
        @NotBlank(message = "Zipcode is mandatory") String zip,
        @NotBlank(message = "Phone number is mandatory") String phone,
        @NotBlank(message = "Email is mandatory") @Email(message = "Email format is not respected") String email
) {
}
