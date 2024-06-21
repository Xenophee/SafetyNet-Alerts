package com.safetynet.alerts.exception;

public class JsonFileReadException extends Exception {
    public JsonFileReadException() {
        super("Failed to access to data.");
    }
}
