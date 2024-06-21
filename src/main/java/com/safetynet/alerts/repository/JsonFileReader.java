package com.safetynet.alerts.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.safetynet.alerts.exception.JsonFileReadException;
import com.safetynet.alerts.model.Data;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;


@Component
public class JsonFileReader {

    private static final String FILE_PATH = "src/main/java/com/safetynet/alerts/repository/data.json";
    private final Data data;

    public JsonFileReader() throws JsonFileReadException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            data = mapper.readValue(new File(FILE_PATH), Data.class);
        } catch (IOException e) {
            throw new JsonFileReadException();
        }
    }

    public Data getData() {
        return data;
    }

}
