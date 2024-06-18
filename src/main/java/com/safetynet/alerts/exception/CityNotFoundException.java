package com.safetynet.alerts.exception;

public class CityNotFoundException extends RuntimeException {
    public CityNotFoundException(String city) {
        super("City " + city + " could not be found.");
    }
}
