package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.*;
import com.safetynet.alerts.exception.AlreadyExistException;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Data;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.JsonFileHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.tinylog.Logger;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.safetynet.alerts.util.BirthdateUtil.*;


/**
 * Service class for managing fire stations.
 * This class provides methods for creating, updating, deleting fire stations, and retrieving information related to fire stations.
 *
 * @author Perrine Dassonville
 * @version 1.0
 *
 * @see StationCoverageDTO
 * @see FloodDTO
 * @see FireDTO
 * @see AlreadyExistException
 * @see NotFoundException
 * @see Data
 * @see FireStation
 * @see JsonFileHandler
 */
@Service
public class FireStationService {

    private final JsonFileHandler jsonFileHandler;

    @Autowired
    public FireStationService(JsonFileHandler jsonFileHandler) {
        this.jsonFileHandler = jsonFileHandler;
    }


    /**
     * Creates a new fire station.
     *
     * @param fireStation The fire station to create.
     * @return A ResponseEntity with the HTTP status.
     * @throws AlreadyExistException if a fire station with the same address and station number already exists.
     */
    public ResponseEntity<Void> create(FireStation fireStation) {
        Logger.info("Creating fire station : {}", fireStation);
        Data data = jsonFileHandler.getData();

        boolean fireStationExists = data.fireStations().stream()
                .anyMatch(fs -> fs.address().equalsIgnoreCase(fireStation.address()) && fs.station() == fireStation.station());

        if (fireStationExists) {
            Logger.error("FireStation " + fireStation.station() + " with address " + fireStation.address() + " already exists");
            throw new AlreadyExistException("FireStation " + fireStation.station() + " with address " + fireStation.address() + " already exists");
        }

        data.fireStations().add(fireStation);
        jsonFileHandler.writeData(jsonFileHandler.sortFireStationsByStationNumber(data));

        Logger.info("Fire station created : {}", fireStation);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    /**
     * Updates an existing fire station.
     *
     * @param fireStation The fire station to update.
     * @return A ResponseEntity with the HTTP status.
     * @throws NotFoundException if no fire station with the same address is found.
     */
    public ResponseEntity<Void> update(FireStation fireStation) {
        Logger.info("Updating fire station : {}", fireStation);
        Data data = jsonFileHandler.getData();

        List<FireStation> fireStations = data.fireStations();
        int index = IntStream.range(0, fireStations.size())
                .filter(i -> fireStations.get(i).address().equalsIgnoreCase(fireStation.address()))
                .findFirst()
                .orElseThrow(() -> {
                    Logger.error("FireStation " + fireStation.station() + " with address " + fireStation.address() + " already exists");
                    return new NotFoundException("FireStation with address " + fireStation.address() + " not found");
                });

        fireStations.set(index, fireStation);
        jsonFileHandler.writeData(data);

        Logger.info("Fire station updated : {}", fireStation);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    // ! A vérifier avec le souci sur le doublon d'adresse dans le json
    /**
     * Deletes a fire station.
     *
     * @param fireStation The fire station to delete.
     * @return A ResponseEntity with the HTTP status.
     * @throws NotFoundException if no fire station with the same address and station number is found.
     */
    public ResponseEntity<Void> delete(FireStation fireStation) {
        Logger.info("Deleting fire station: {}", fireStation);
        Data data = jsonFileHandler.getData();

        FireStation fireStationToDelete = data.fireStations().stream()
                .filter(fs -> fs.address().equalsIgnoreCase(fireStation.address())
                        && fs.station() == fireStation.station())
                .findFirst()
                .orElseThrow(() -> {
                    Logger.error("FireStation " + fireStation.station() + " with address " + fireStation.address() + " not found");
                    return new NotFoundException("FireStation " + fireStation.station() + " with address " + fireStation.address() + " not found");
                });

        data.fireStations().remove(fireStationToDelete);
        jsonFileHandler.writeData(data);

        Logger.info("Fire station deleted: {}", fireStationToDelete);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * Retrieves persons station coverage by station number.
     *
     * @param stationNumber The station number to search for.
     * @return A StationCoverageDTO containing the number of adults, children, and a list of persons covered by the station.
     */
    public StationCoverageDTO getPersonsStationCoverage(int stationNumber) {
        Logger.info("Getting persons station coverage for station number: {}", stationNumber);
        Data data = jsonFileHandler.getData();

        Set<String> stationAddresses = getAddressesByStation(stationNumber);
        Map<Person, MedicalRecord> personMedicalRecordMap = PersonService.mapPersonsToMedicalRecords(data);

        List<StationCoveragePersonInfoDTO> persons = data.persons().stream()
                .filter(person -> stationAddresses.stream()
                        .anyMatch(stationAddress -> stationAddress.equalsIgnoreCase(person.address())))
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

        Logger.info("Successfully got persons station coverage for station number: {}", stationNumber);
        return new StationCoverageDTO(adults, children, persons);
    }


    /**
     * Retrieves homes by station numbers.
     *
     * @param stations The list of station numbers to search for.
     * @return A list of FloodDTOs containing the station number and a map of addresses to a list of persons living at each address.
     */
    public List<FloodDTO> getHomesByStations(List<Integer> stations) {
        Logger.info("Getting homes by stations: {}", stations);

        Data data = jsonFileHandler.getData();
        Map<Person, MedicalRecord> personMedicalRecordMap = PersonService.mapPersonsToMedicalRecords(data);

        List<FloodDTO> floodDTOList = stations.stream()
                .map(stationNumber -> {
                    Set<String> stationAddresses = getAddressesByStation(stationNumber);

                    Map<String, List<FireFloodPersonInfoDTO>> personsByAddress = data.persons().stream()
                            .filter(person -> stationAddresses.stream()
                                    .anyMatch(stationAddress -> stationAddress.equalsIgnoreCase(person.address())))
                            .map(person -> {
                                MedicalRecord medicalRecord = personMedicalRecordMap.get(person);
                                return new AbstractMap.SimpleEntry<>(person.address(), new FireFloodPersonInfoDTO(person.firstName(), person.lastName(), person.phone(), getAge(medicalRecord.birthdate()), medicalRecord.medications(), medicalRecord.allergies()));
                            })
                            .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

                    return new FloodDTO(stationNumber, personsByAddress);
                })
                .toList();

        Logger.info("Successfully got homes by stations: {}", stations);
        return floodDTOList;
    }


    /**
     * Retrieves persons phones by station number.
     *
     * @param stationNumber The station number to search for.
     * @return A set of phone numbers of persons covered by the station.
     * @throws NotFoundException if no station with the given number is found.
     */
    public Set<String> getPersonsPhonesByStation(int stationNumber) {
        Logger.info("Getting persons phones by station number: {}", stationNumber);
        Data data = jsonFileHandler.getData();

        Set<String> stationAddresses = getAddressesByStation(stationNumber);

        Set<String> phones = data.persons().stream()
                .filter(person -> stationAddresses.stream()
                        .anyMatch(stationAddress -> stationAddress.equalsIgnoreCase(person.address())))
                .map(Person::phone)
                .collect(Collectors.toSet());

        if (phones.isEmpty()) {
            Logger.error("Station number " + stationNumber + " not found");
            throw new NotFoundException("Station number " + stationNumber + " not found");
        }

        Logger.info("Successfully got persons phones by station number: {}", stationNumber);
        return phones;
    }


    /**
     * Retrieves persons and station by address.
     *
     * @param address The address to search for.
     * @return A FireDTO containing the station number and a list of persons living at the address.
     * @throws NotFoundException if no station covering the given address is found.
     */
    public FireDTO getPersonsAndStationByAddress(String address) {
        Logger.info("Getting persons and station by address: {}", address);
        Data data = jsonFileHandler.getData();
        Map<Person, MedicalRecord> personMedicalRecordMap = PersonService.mapPersonsToMedicalRecords(data);

        FireStation fireStation = data.fireStations().stream()
                .filter(station -> address.equalsIgnoreCase(station.address()))
                .findFirst()
                .orElseThrow(() -> {
                    Logger.error("No station found for address: " + address);
                    return new NotFoundException("No station found for address: " + address);
                });

        List<FireFloodPersonInfoDTO> persons = data.persons().stream()
                .filter(person -> address.equalsIgnoreCase(person.address()))
                .map(person -> {
                    MedicalRecord medicalRecord = personMedicalRecordMap.get(person);
                    return new FireFloodPersonInfoDTO(person.firstName(), person.lastName(), person.phone(), getAge(medicalRecord.birthdate()), medicalRecord.medications(), medicalRecord.allergies());
                })
                .toList();

        Logger.info("Successfully got persons and station by address: {}", address);
        return new FireDTO(fireStation.station(), persons);
    }


    /**
     * Retrieves addresses by station number.
     *
     * @param stationNumber The station number to search for.
     * @return A set of addresses covered by the station.
     * @throws NotFoundException if no station with the given number is found.
     */
    public Set<String> getAddressesByStation(int stationNumber) {
        Logger.info("Getting addresses by station number: {}", stationNumber);
        Data data = jsonFileHandler.getData();

        Set<String> stationAddresses = data.fireStations().stream()
                .filter(fireStation -> stationNumber == fireStation.station())
                .map(FireStation::address)
                .collect(Collectors.toSet());

        if (stationAddresses.isEmpty()) {
            Logger.error("Station number " + stationNumber + " not found");
            throw new NotFoundException("Station number " + stationNumber + " not found");
        }

        Logger.info("Successfully got addresses by station number: {}", stationNumber);
        return stationAddresses;
    }
}
