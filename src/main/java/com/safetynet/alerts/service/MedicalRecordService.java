package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.PersonIdentifierDTO;
import com.safetynet.alerts.exception.AlreadyExistException;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Data;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.repository.JsonFileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class MedicalRecordService {

    private final JsonFileReader jsonFileReader;

    @Autowired
    public MedicalRecordService(JsonFileReader jsonFileReader) {
        this.jsonFileReader = jsonFileReader;
    }


    public ResponseEntity<Void> create(MedicalRecord medicalRecord) {
        Data data = jsonFileReader.getData();

        boolean medicalRecordExists = data.medicalRecords().stream()
                .anyMatch(p -> p.firstName().equals(medicalRecord.firstName()) && p.lastName().equals(medicalRecord.lastName()));

        if (medicalRecordExists) {
            throw new AlreadyExistException(medicalRecord.firstName() + " " + medicalRecord.lastName() + " already exists");
        }

        data.medicalRecords().add(medicalRecord);
        jsonFileReader.writeData(data);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    public ResponseEntity<Void> update(MedicalRecord medicalRecord) {
        Data data = jsonFileReader.getData();

        List<MedicalRecord> medicalRecords = data.medicalRecords();
        int index = IntStream.range(0, medicalRecords.size())
                .filter(i -> medicalRecords.get(i).firstName().equals(medicalRecord.firstName()) && medicalRecords.get(i).lastName().equals(medicalRecord.lastName()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("No medical record found for : " + medicalRecord.firstName() + " " + medicalRecord.lastName()));

        if (!medicalRecords.get(index).equals(medicalRecord)) {
            medicalRecords.set(index, medicalRecord);
            jsonFileReader.writeData(data);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }


    public ResponseEntity<Void> delete(PersonIdentifierDTO personIdentifier) {
        Data data = jsonFileReader.getData();

        MedicalRecord medicalRecordToDelete = data.medicalRecords().stream()
                .filter(medicalRecord -> medicalRecord.firstName().equalsIgnoreCase(personIdentifier.firstName())
                        && medicalRecord.lastName().equalsIgnoreCase(personIdentifier.lastName()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(personIdentifier.firstName() + " " + personIdentifier.lastName() + " not found"));

        data.medicalRecords().remove(medicalRecordToDelete);
        jsonFileReader.writeData(data);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
