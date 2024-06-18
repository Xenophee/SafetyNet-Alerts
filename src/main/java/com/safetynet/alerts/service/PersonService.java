package com.safetynet.alerts.service;

import com.safetynet.alerts.config.JsonFileReader;
import com.safetynet.alerts.exception.CityNotFoundException;
import com.safetynet.alerts.exception.JsonFileReadException;
import com.safetynet.alerts.model.Data;
import com.safetynet.alerts.model.Person;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PersonService {

    public String createPerson(Person person) {
        return null;
    }

    public String updatePerson(Person person) {
        return null;
    }

    public String deletePerson(Person person) {
        return null;
    }

    public List<Person> getAPersonByLastname(String lastname) {
        return null;
    }

    public Set<String> getEmailsByCity(String city) throws JsonFileReadException {
        Data data = new JsonFileReader().getData();

        Set<String> emails = data.persons().stream()
                .filter(person -> person.city().equalsIgnoreCase(city))
                .map(Person::email)
                .collect(Collectors.toSet());

        if (emails.isEmpty()) throw new CityNotFoundException(city);

        return emails;
    }

    public List<Person> getChildrenByAddress(String address) {
        return null;
    }
}
