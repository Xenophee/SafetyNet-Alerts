package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.*;
import com.safetynet.alerts.exception.AlreadyExistException;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Data;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.JsonFileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.safetynet.alerts.util.DatesUtil.*;


@Service
public class FireStationService {

    private final JsonFileReader jsonFileReader;

    @Autowired
    public FireStationService(JsonFileReader jsonFileReader) {
        this.jsonFileReader = jsonFileReader;
    }



    public ResponseEntity<Void> create(FireStation fireStation) {
        Data data = jsonFileReader.getData();

        if (data.fireStations().contains(fireStation)) {
            throw new AlreadyExistException("FireStation " + fireStation.station() + " with address " + fireStation.address() + " already exists");
        }

        data.fireStations().add(fireStation);
        jsonFileReader.writeData(data);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    public ResponseEntity<Void> update(FireStation fireStation) {
        Data data = jsonFileReader.getData();

        List<FireStation> fireStations = data.fireStations();
        int index = IntStream.range(0, fireStations.size())
                .filter(i -> fireStations.get(i).address().equals(fireStation.address()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("FireStation with address " + fireStation.address() + " not found"));

        fireStations.set(index, fireStation);
        jsonFileReader.writeData(data);

        return new ResponseEntity<>(HttpStatus.OK);
    }


    public ResponseEntity<Void> delete(FireStation fireStation) {
        Data data = jsonFileReader.getData();

        if (!data.fireStations().contains(fireStation)) {
            throw new NotFoundException("FireStation " + fireStation.station() + " with address " + fireStation.address() + " not found");
        }

        data.fireStations().remove(fireStation);
        jsonFileReader.writeData(data);

        return new ResponseEntity<>(HttpStatus.OK);
    }


    public StationCoverageDTO getPersonsStationCoverage(int stationNumber) {
        Data data = jsonFileReader.getData();

        Set<String> stationAddresses = getAddressesByStation(stationNumber);
        Map<Person, MedicalRecord> personMedicalRecordMap = PersonService.mapPersonsToMedicalRecords(data);

        List<StationCoveragePersonInfoDTO> persons = data.persons().stream()
                .filter(person -> stationAddresses.contains(person.address()))
                .map(person -> new StationCoveragePersonInfoDTO(person.firstName(), person.lastName(), person.address(), person.phone()))
                .toList();

        int children = (int) persons.stream()
                .filter(person -> {
                    Optional<MedicalRecord> medicalRecord = personMedicalRecordMap.entrySet().stream()
                            .filter(entry -> entry.getKey().firstName().equals(person.firstName()) && entry.getKey().lastName().equals(person.lastName()))
                            .map(Map.Entry::getValue)
                            .findFirst();
                    return isChild(medicalRecord.get().birthdate());
                })
                .count();

        int adults = persons.size() - children;

        return new StationCoverageDTO(adults, children, persons);
    }


    public List<FloodDTO> getHomesByStations(List<Integer> stations) {
        Data data = jsonFileReader.getData();
        Map<Person, MedicalRecord> personMedicalRecordMap = PersonService.mapPersonsToMedicalRecords(data);

        return stations.stream()
                .map(stationNumber -> {
                    Set<String> stationAddresses = getAddressesByStation(stationNumber);

                    Map<String, List<FireFloodPersonInfoDTO>> personsByAddress = data.persons().stream()
                            .filter(person -> stationAddresses.contains(person.address()))
                            .map(person -> {
                                MedicalRecord medicalRecord = personMedicalRecordMap.get(person);
                                return new AbstractMap.SimpleEntry<>(person.address(), new FireFloodPersonInfoDTO(person.firstName(), person.lastName(), person.phone(), getAge(medicalRecord.birthdate()), medicalRecord.medications(), medicalRecord.allergies()));
                            })
                            .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

                    return new FloodDTO(stationNumber, personsByAddress);
                })
                .toList();
    }


    public Set<String> getPersonsPhonesByStation(int stationNumber) {
        Data data = jsonFileReader.getData();

        Set<String> stationAddresses = getAddressesByStation(stationNumber);

        return data.persons().stream()
                .filter(person -> stationAddresses.contains(person.address()))
                .map(Person::phone)
                .collect(Collectors.toSet());
    }


    public FireDTO getPersonsAndStationByAddress(String address) {
        Data data = jsonFileReader.getData();
        Map<Person, MedicalRecord> personMedicalRecordMap = PersonService.mapPersonsToMedicalRecords(data);

        FireStation fireStation = data.fireStations().stream()
                .filter(station -> address.equals(station.address()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("No station found for address: " + address));

        List<FireFloodPersonInfoDTO> persons = data.persons().stream()
                .filter(person -> address.equals(person.address()))
                .map(person -> {
                    MedicalRecord medicalRecord = personMedicalRecordMap.get(person);
                    return new FireFloodPersonInfoDTO(person.firstName(), person.lastName(), person.phone(), getAge(medicalRecord.birthdate()), medicalRecord.medications(), medicalRecord.allergies());
                })
                .toList();

        return new FireDTO(fireStation.station(), persons);
    }


    public Set<String> getAddressesByStation(int stationNumber) {
        Data data = jsonFileReader.getData();

        Set<String> stationAddresses = data.fireStations().stream()
                .filter(fireStation -> stationNumber == fireStation.station())
                .map(FireStation::address)
                .collect(Collectors.toSet());

        if (stationAddresses.isEmpty()) {
            throw new NotFoundException("Station number " + stationNumber + " not found");
        }

        return stationAddresses;
    }
}
