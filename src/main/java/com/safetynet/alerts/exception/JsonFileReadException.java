package com.safetynet.alerts.exception;

public class JsonFileReadException extends Exception {
    public JsonFileReadException(String filePath) {
        super("Failed to read JSON file at " + filePath + ".");
    }
}
