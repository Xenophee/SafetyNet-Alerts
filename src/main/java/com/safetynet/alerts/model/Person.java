package com.safetynet.alerts.model;

public record Person(
        String firstName,
        String lastName,
        String address,
        String city,
        String zip,
        String phone,
        String email
) {
}
