package com.safetynet.alerts.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.safetynet.alerts.exception.JsonFileException;
import com.safetynet.alerts.model.Data;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import org.springframework.stereotype.Component;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;


/**
 * Handler class for managing JSON files.
 * This class provides methods for reading and writing data to a JSON file.
 * It also provides methods for sorting persons by last name and first name, sorting medical records by last name and first name, and sorting fire stations by station number.
 *
 * @author Perrine Dassonville
 * @version 1.0
 *
 * @see JsonFileException
 * @see Data
 * @see FireStation
 * @see MedicalRecord
 * @see Person
 */
@Component
public class JsonFileHandler {

    private static final String FILE_PATH = "src/main/java/com/safetynet/alerts/repository/data.json";
    private final Data data;


    /**
     * Constructor for JsonFileHandler.
     * Reads data from a JSON file and stores it in a Data object.
     *
     * @throws JsonFileException if an error occurs while reading the JSON file.
     */
    public JsonFileHandler() throws JsonFileException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            data = mapper.readValue(new File(FILE_PATH), Data.class);
        } catch (IOException ex) {
            Logger.error(ex, "Failed to access to data.");
            throw new JsonFileException("Failed to access to data.");
        }
    }

    /**
     * Retrieves the data read from the JSON file.
     *
     * @return The data read from the JSON file.
     */
    public Data getData() {
        return data;
    }


    /**
     * Writes data to a JSON file.
     *
     * @param data The data to write to the JSON file.
     * @throws JsonFileException if an error occurs while writing to the JSON file.
     */
    public void writeData(Data data) throws JsonFileException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), data);
        } catch (IOException ex) {
            Logger.error(ex, "Failed to write data to JSON file");
            throw new JsonFileException("Failed to write data");
        }
    }


    /**
     * Sorts persons by last name and first name.
     *
     * @param data The data containing the persons to sort.
     * @return The data with the persons sorted by last name and first name.
     */
    public Data sortPersonsByLastNameAndFirstName(Data data) {
        data.persons().sort(Comparator.comparing(Person::lastName)
                .thenComparing(Person::firstName));
        return data;
    }

    /**
     * Sorts medical records by last name and first name.
     *
     * @param data The data containing the medical records to sort.
     * @return The data with the medical records sorted by last name and first name.
     */
    public Data sortMedicalRecordsByLastNameAndFirstName(Data data) {
        data.medicalRecords().sort(Comparator.comparing(MedicalRecord::lastName)
                .thenComparing(MedicalRecord::firstName));
        return data;
    }

    /**
     * Sorts fire stations by station number.
     *
     * @param data The data containing the fire stations to sort.
     * @return The data with the fire stations sorted by station number.
     */

    public Data sortFireStationsByStationNumber(Data data) {
        data.fireStations().sort(Comparator.comparingInt(FireStation::station));
        return data;
    }

}
