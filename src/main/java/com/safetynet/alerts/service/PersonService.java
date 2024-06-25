package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.ChildInfoDTO;
import com.safetynet.alerts.dto.PersonIdentifierDTO;
import com.safetynet.alerts.dto.PersonInfoDTO;
import com.safetynet.alerts.exception.AlreadyExistException;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.repository.JsonFileReader;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Data;
import com.safetynet.alerts.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.safetynet.alerts.util.DatesUtil.getAge;
import static com.safetynet.alerts.util.DatesUtil.isChild;

@Service
public class PersonService {

    private final JsonFileReader jsonFileReader;

    @Autowired
    public PersonService(JsonFileReader jsonFileReader) {
        this.jsonFileReader = jsonFileReader;
    }


    public ResponseEntity<Void> create(Person person) {
        Data data = jsonFileReader.getData();

        boolean personExists = data.persons().stream()
                .anyMatch(p -> p.firstName().equals(person.firstName()) && p.lastName().equals(person.lastName()));

        if (personExists) {
            throw new AlreadyExistException(person.firstName() + " " + person.lastName() + " already exists");
        }

        data.persons().add(person);
        jsonFileReader.writeData(data);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    public ResponseEntity<Void> update(Person person) {
        Data data = jsonFileReader.getData();

        List<Person> persons = data.persons();
        int index = IntStream.range(0, persons.size())
                .filter(i -> persons.get(i).firstName().equals(person.firstName()) && persons.get(i).lastName().equals(person.lastName()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(person.firstName() + " " + person.lastName() + " not found"));

        if (!persons.get(index).equals(person)) {
            persons.set(index, person);
            jsonFileReader.writeData(data);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }


    public ResponseEntity<Void> delete(PersonIdentifierDTO personIdentifier) {
        Data data = jsonFileReader.getData();

        Person personToDelete = data.persons().stream()
                .filter(person -> person.firstName().equalsIgnoreCase(personIdentifier.firstName())
                        && person.lastName().equalsIgnoreCase(personIdentifier.lastName()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(personIdentifier.firstName() + " " + personIdentifier.lastName() + " not found"));

        data.persons().remove(personToDelete);
        jsonFileReader.writeData(data);

        return new ResponseEntity<>(HttpStatus.OK);
    }


    public List<PersonInfoDTO> getPersonByLastname(String lastname) {
        Data data = jsonFileReader.getData();
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
            throw new NotFoundException("Lastname: " + lastname + " not found");
        }

        return personsInfo;
    }


    public Set<String> getEmailsByCity(String city) {
        Data data = jsonFileReader.getData();

        Set<String> emails = data.persons().stream()
                .filter(person -> person.city().equalsIgnoreCase(city))
                .map(Person::email)
                .collect(Collectors.toSet());

        if (emails.isEmpty()) {
            throw new NotFoundException("City: " + city + " not found");
        }

        return emails;
    }


    public List<ChildInfoDTO> getChildrenByAddress(String address) {
        Data data = jsonFileReader.getData();
        Map<Person, MedicalRecord> personMedicalRecordMap = mapPersonsToMedicalRecords(data);

        List<ChildInfoDTO> childrenInfo = data.persons().stream()
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

        return childrenInfo;
    }


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
