package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.PersonIdentifierDTO;
import com.safetynet.alerts.exception.AlreadyExistException;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Data;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.repository.JsonFileHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.tinylog.Logger;

import java.util.List;
import java.util.stream.IntStream;


/**
 * Service class for managing medical records.
 * This class provides methods for creating, updating, and deleting medical records.
 *
 * @author Perrine Dassonville
 * @version 1.0
 * @see PersonIdentifierDTO
 * @see AlreadyExistException
 * @see NotFoundException
 * @see Data
 * @see MedicalRecord
 * @see JsonFileHandler
 */
@Service
public class MedicalRecordService {

    private final JsonFileHandler jsonFileHandler;

    @Autowired
    public MedicalRecordService(JsonFileHandler jsonFileHandler) {
        this.jsonFileHandler = jsonFileHandler;
    }


    /**
     * Creates a new medical record.
     *
     * @param medicalRecord The medical record to create.
     * @return A ResponseEntity with the HTTP status.
     * @throws AlreadyExistException if a medical record with the same first and last name already exists.
     */
    public ResponseEntity<Void> create(MedicalRecord medicalRecord) {
        Logger.info("Creating medical record : {}", medicalRecord);
        Data data = jsonFileHandler.getData();

        boolean medicalRecordExists = data.medicalRecords().stream()
                .anyMatch(p -> p.firstName().equals(medicalRecord.firstName()) && p.lastName().equals(medicalRecord.lastName()));

        if (medicalRecordExists) {
            Logger.error(medicalRecord.firstName() + " " + medicalRecord.lastName() + " already exists");
            throw new AlreadyExistException(medicalRecord.firstName() + " " + medicalRecord.lastName() + " already exists");
        }

        data.medicalRecords().add(medicalRecord);
        jsonFileHandler.writeData(jsonFileHandler.sortMedicalRecordsByLastNameAndFirstName(data));

        Logger.info("Medical record created : {}", medicalRecord);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    /**
     * Updates an existing medical record.
     *
     * @param medicalRecord The medical record to update.
     * @return A ResponseEntity with the HTTP status.
     * @throws NotFoundException if no medical record with the same first and last name is found.
     */
    public ResponseEntity<Void> update(MedicalRecord medicalRecord) {
        Logger.info("Updating medical record : {}", medicalRecord);
        Data data = jsonFileHandler.getData();

        List<MedicalRecord> medicalRecords = data.medicalRecords();
        int index = IntStream.range(0, medicalRecords.size())
                .filter(i -> medicalRecords.get(i).firstName().equals(medicalRecord.firstName()) && medicalRecords.get(i).lastName().equals(medicalRecord.lastName()))
                .findFirst()
                .orElseThrow(() -> {
                    Logger.error("No medical record found for : " + medicalRecord.firstName() + " " + medicalRecord.lastName());
                    return new NotFoundException("No medical record found for : " + medicalRecord.firstName() + " " + medicalRecord.lastName());
                });

        medicalRecords.set(index, medicalRecord);
        jsonFileHandler.writeData(data);

        Logger.info("Medical record updated : {}", medicalRecord);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * Deletes a medical record.
     *
     * @param personIdentifier The identifier of the person whose medical record to delete.
     * @return A ResponseEntity with the HTTP status.
     * @throws NotFoundException if no medical record with the same first and last name is found.
     */
    public ResponseEntity<Void> delete(PersonIdentifierDTO personIdentifier) {
        Logger.info("Deleting medical record of : {}", personIdentifier);
        Data data = jsonFileHandler.getData();

        MedicalRecord medicalRecordToDelete = data.medicalRecords().stream()
                .filter(medicalRecord -> medicalRecord.firstName().equalsIgnoreCase(personIdentifier.firstName())
                        && medicalRecord.lastName().equalsIgnoreCase(personIdentifier.lastName()))
                .findFirst()
                .orElseThrow(() -> {
                    Logger.error(personIdentifier.firstName() + " " + personIdentifier.lastName() + " not found");
                    return new NotFoundException(personIdentifier.firstName() + " " + personIdentifier.lastName() + " not found");
                });

        data.medicalRecords().remove(medicalRecordToDelete);
        jsonFileHandler.writeData(data);

        Logger.info("Person deleted : {}", medicalRecordToDelete);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
