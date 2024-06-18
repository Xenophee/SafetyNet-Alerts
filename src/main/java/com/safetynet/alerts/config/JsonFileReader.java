package com.safetynet.alerts.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.exception.JsonFileReadException;
import com.safetynet.alerts.model.Data;

import java.io.File;
import java.io.IOException;

public class JsonFileReader {

    private static final String FILE_PATH = "resources/data.json";
    private final Data data;

    public JsonFileReader() throws JsonFileReadException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            data = mapper.readValue(new File(FILE_PATH), Data.class);
        } catch (IOException e) {
            throw new JsonFileReadException(FILE_PATH);
        }
    }

    public Data getData() {
        return data;
    }

}
