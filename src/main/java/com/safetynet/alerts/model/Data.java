package com.safetynet.alerts.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Data(
        @JsonProperty("persons") List<Person> persons,
        @JsonProperty("firestations") List<FireStation> fireStations,
        @JsonProperty("medicalrecords") List<MedicalRecord> medicalRecords
) {
}
