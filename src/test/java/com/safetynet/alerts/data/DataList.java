package com.safetynet.alerts.data;

import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DataList {

    private List<Person> persons;
    private List<FireStation> fireStations;
    private List<MedicalRecord> medicalRecords;

    public DataList() {
        this.persons = new ArrayList<>(List.of(
                new Person("John", "Doe", "123 Main St", "Culver", "97451", "123-456-7890", "john.doe@example.com"),
                new Person("Jane", "Doe", "456 Maple St", "Culver", "97451", "123-456-7891", "jane.doe@example.com"),
                new Person("Ember", "Smith", "789 Oak St", "Culver", "97451", "123-456-7892", "ember.smith@example.com"),
                new Person("Alice", "Smith", "789 Oak St", "Culver", "97451", "123-456-7892", "ember.smith@example.com"),
                new Person("Mark", "Smith", "789 Oak St", "Culver", "97451", "123-456-7892", "ember.smith@example.com"),
                new Person("Bob", "Johnson", "321 Pine St", "Culver", "97451", "123-456-7893", "bob.johnson@example.com"),
                new Person("Charlie", "Brown", "654 Elm St", "Culver", "97451", "123-456-7894", "charlie.brown@example.com"),
                new Person("Eve", "Jones", "345 Cedar St", "Culver", "97451", "123-456-7896", "eve.jones@example.com")
        ));

        this.fireStations = new ArrayList<>(List.of(
                new FireStation("123 Main St", 1),
                new FireStation("456 Maple St", 3),
                new FireStation("789 Oak St", 2),
                new FireStation("321 Pine St", 1),
                new FireStation("654 Elm St", 2),
                new FireStation("987 Birch St", 4),
                new FireStation("345 Cedar St", 2)
        ));

        this.medicalRecords = new ArrayList<>(List.of(
                new MedicalRecord("John", "Doe", LocalDate.of(2000, 1, 1), new String[]{"medication1", "medication2"}, new String[]{"allergy1", "allergy2"}),
                new MedicalRecord("Jane", "Doe", LocalDate.of(2000, 1, 1), new String[]{"medication3", "medication4"}, new String[]{"allergy3", "allergy4"}),
                new MedicalRecord("Alice", "Smith", LocalDate.of(2020, 4, 7), new String[]{"medication5", "medication6"}, new String[]{"allergy5", "allergy6"}),
                new MedicalRecord("Mark", "Smith", LocalDate.of(2024, 9, 13), new String[0], new String[0]),
                new MedicalRecord("Bob", "Johnson", LocalDate.of(1968, 11, 4), new String[]{"medication7", "medication8"}, new String[0]),
                new MedicalRecord("Charlie", "Brown", LocalDate.of(1990, 5, 25), new String[0], new String[0]),
                new MedicalRecord("David", "Williams", LocalDate.of(1982, 7, 16), new String[0], new String[0])
        ));
    }

    public List<Person> getPersons() {
        return persons;
    }

    public List<FireStation> getFireStations() {
        return fireStations;
    }

    public List<MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }
}
