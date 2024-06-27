package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.ChildInfoDTO;
import com.safetynet.alerts.dto.PersonIdentifierDTO;
import com.safetynet.alerts.dto.PersonInfoDTO;
import com.safetynet.alerts.exception.AlreadyExistException;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.repository.JsonFileHandler;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Data;
import com.safetynet.alerts.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.tinylog.Logger;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.safetynet.alerts.util.BirthdateUtil.*;


/**
 * Service class for managing persons.
 * This class provides methods for creating, updating, deleting, and retrieving persons.
 * It also provides methods for getting persons by lastname, emails by city, and children by address.
 *
 * @author Perrine Dassonville
 * @version 1.0
 *
 * @see ChildInfoDTO
 * @see PersonIdentifierDTO
 * @see PersonInfoDTO
 * @see AlreadyExistException
 * @see NotFoundException
 * @see MedicalRecord
 * @see Person
 * @see JsonFileHandler
 */
@Service
public class PersonService {

    private final JsonFileHandler jsonFileHandler;

    @Autowired
    public PersonService(JsonFileHandler jsonFileHandler) {
        this.jsonFileHandler = jsonFileHandler;
    }


    /**
     * Creates a new person.
     *
     * @param person The person to create.
     * @return A ResponseEntity with the HTTP status.
     * @throws AlreadyExistException if a person with the same first and last name already exists.
     */
    public ResponseEntity<Void> create(Person person) {
        Logger.info("Creating person : {}", person);
        Data data = jsonFileHandler.getData();

        boolean personExists = data.persons().stream()
                .anyMatch(p -> p.firstName().equals(person.firstName()) && p.lastName().equals(person.lastName()));

        if (personExists) {
            Logger.error(person.firstName() + " " + person.lastName() + " already exists");
            throw new AlreadyExistException(person.firstName() + " " + person.lastName() + " already exists");
        }

        data.persons().add(person);
        jsonFileHandler.writeData(jsonFileHandler.sortPersonsByLastNameAndFirstName(data));

        Logger.info("Person created : {}", person);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    /**
     * Updates an existing person.
     *
     * @param person The person to update.
     * @return A ResponseEntity with the HTTP status.
     * @throws NotFoundException if no person with the same first and last name is found.
     */
    public ResponseEntity<Void> update(Person person) {
        Logger.info("Updating person : {}", person);
        Data data = jsonFileHandler.getData();

        List<Person> persons = data.persons();
        int index = IntStream.range(0, persons.size())
                .filter(i -> persons.get(i).firstName().equals(person.firstName()) && persons.get(i).lastName().equals(person.lastName()))
                .findFirst()
                .orElseThrow(() -> {
                    Logger.error(person.firstName() + " " + person.lastName() + " not found");
                    return new NotFoundException(person.firstName() + " " + person.lastName() + " not found");
                });

        persons.set(index, person);
        jsonFileHandler.writeData(data);

        Logger.info("Person updated : {}", person);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * Deletes a person.
     *
     * @param personIdentifier The identifier of the person to delete.
     * @return A ResponseEntity with the HTTP status.
     * @throws NotFoundException if no person with the same first and last name is found.
     */
    public ResponseEntity<Void> delete(PersonIdentifierDTO personIdentifier) {
        Logger.info("Deleting person : {}", personIdentifier);
        Data data = jsonFileHandler.getData();

        Person personToDelete = data.persons().stream()
                .filter(person -> person.firstName().equalsIgnoreCase(personIdentifier.firstName())
                        && person.lastName().equalsIgnoreCase(personIdentifier.lastName()))
                .findFirst()
                .orElseThrow(() -> {
                    Logger.error(personIdentifier.firstName() + " " + personIdentifier.lastName() + " not found");
                    return new NotFoundException(personIdentifier.firstName() + " " + personIdentifier.lastName() + " not found");
                });

        data.persons().remove(personToDelete);
        jsonFileHandler.writeData(data);

        Logger.info("Person deleted : {}", personToDelete);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * Retrieves persons by their last name.
     *
     * @param lastname The last name to search for.
     * @return A list of PersonInfoDTOs of persons with the given last name.
     * @throws NotFoundException if no persons with the given last name are found.
     */
    public List<PersonInfoDTO> getPersonByLastname(String lastname) {
        Logger.info("Getting persons by lastname : {}", lastname);

        Data data = jsonFileHandler.getData();
        Map<Person, MedicalRecord> personMedicalRecordMap = mapPersonsToMedicalRecords(data);

        List<PersonInfoDTO> personsInfo = data.persons().stream()
                .filter(person -> person.lastName().equalsIgnoreCase(lastname))
                .map(person -> {
                    MedicalRecord medicalRecord = personMedicalRecordMap.get(person);

                    int age = getAge(medicalRecord.birthdate());
                    String[] medications = medicalRecord.medications();
                    String[] allergies = medicalRecord.allergies();

                    return new PersonInfoDTO(person.firstName(), person.lastName(), person.address(), person.email(), age, medications, allergies);
                })
                .toList();

        if (personsInfo.isEmpty()) {
            Logger.error("Lastname: " + lastname + " not found");
            throw new NotFoundException("Lastname: " + lastname + " not found");
        }

        Logger.info("Successfully got persons by lastname : {}", lastname);
        return personsInfo;
    }


    /**
     * Retrieves emails of persons living in a given city.
     *
     * @param city The city to search for.
     * @return A set of emails of persons living in the given city.
     * @throws NotFoundException if no persons living in the given city are found.
     */
    public Set<String> getEmailsByCity(String city) {
        Logger.info("Getting emails by city : {}", city);
        Data data = jsonFileHandler.getData();

        Set<String> emails = data.persons().stream()
                .filter(person -> person.city().equalsIgnoreCase(city))
                .map(Person::email)
                .collect(Collectors.toSet());

        if (emails.isEmpty()) {
            Logger.error("City: " + city + " not found");
            throw new NotFoundException("City: " + city + " not found");
        }

        Logger.info("Successfully got emails by city : {}", city);
        return emails;
    }


    /**
     * Retrieves children living at a given address.
     *
     * @param address The address to search for.
     * @return A list of ChildInfoDTOs of children living at the given address.
     */
    public List<ChildInfoDTO> getChildrenByAddress(String address) {
        Logger.info("Getting children by address : {}", address);

        Data data = jsonFileHandler.getData();
        Map<Person, MedicalRecord> personMedicalRecordMap = mapPersonsToMedicalRecords(data);

        List<ChildInfoDTO> children = data.persons().stream()
                .filter(person -> person.address().equalsIgnoreCase(address))
                .map(person -> {
                    MedicalRecord medicalRecord = personMedicalRecordMap.get(person);

                    if (!isChild(medicalRecord.birthdate())) return null;

                    List<String> familyMembers = data.persons().stream()
                            .filter(familyMember -> familyMember.lastName().equals(person.lastName()) && !familyMember.firstName().equals(person.firstName()))
                            .map(familyMember -> familyMember.firstName() + " " + familyMember.lastName())
                            .toList();

                    int age = getAge(medicalRecord.birthdate());

                    return new ChildInfoDTO(person.firstName(), person.lastName(), age, familyMembers);
                })
                .filter(Objects::nonNull)
                .toList();

        Logger.info("Successfully got children by address : {}", address);
        return children;
    }


    /**
     * Maps persons to their medical records.
     *
     * @param data The data containing the persons and medical records.
     * @return A map of persons to their medical records.
     */
    public static Map<Person, MedicalRecord> mapPersonsToMedicalRecords(Data data) {
        return data.persons().stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        person -> data.medicalRecords().stream()
                                .filter(record -> record.lastName().equals(person.lastName()) && record.firstName().equals(person.firstName()))
                                .findFirst()
                                .orElseGet(() -> new MedicalRecord(
                                        person.firstName(),
                                        person.lastName(),
                                        LocalDate.now().minusYears(999),
                                        new String[0],
                                        new String[0])
                                )
                ));
    }
}
