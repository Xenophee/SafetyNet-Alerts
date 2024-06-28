package com.safetynet.alerts.service;

import com.safetynet.alerts.data.DataList;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Data;
import com.safetynet.alerts.repository.JsonFileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    private DataList dataList = new DataList();

    @Mock
    private JsonFileHandler jsonFileHandler;

    @InjectMocks
    private PersonService personService;

    @BeforeEach
    public void setUp() {
        Data data = new Data(dataList.getPersons(), dataList.getFireStations(), dataList.getMedicalRecords());
        when(jsonFileHandler.getData()).thenReturn(data);
    }

    @Test
    public void getEmailsByCity_ShouldReturnUniqueEmailsOfCorrectSize() {
        // Given
        String city = "Culver";
        Set<String> expectedEmails = Set.of("john.doe@example.com", "jane.doe@example.com", "ember.smith@example.com", "bob.johnson@example.com", "charlie.brown@example.com", "eve.jones@example.com");
        // When
        Set<String> emails = personService.getEmailsByCity(city);
        // Then
        assertThat(emails).hasSize(expectedEmails.size()).doesNotHaveDuplicates();
        assertThat(emails).containsExactlyInAnyOrderElementsOf(expectedEmails);
    }


    @Test
    public void getEmailsByCity_ShouldThrowNotFoundExceptionForNonExistentCity() {
        // Given
        String nonExistentCity = "New York";
        String expectedErrorMessage = "City : " + nonExistentCity + " not found";
        // When
        Exception exception = assertThrows(NotFoundException.class, () -> personService.getEmailsByCity(nonExistentCity));
        // Then
        assertThat(exception.getMessage()).isEqualTo(expectedErrorMessage);
    }
}
