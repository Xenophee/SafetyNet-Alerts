package com.safetynet.alerts.service;

import com.safetynet.alerts.data.DataList;
import com.safetynet.alerts.dto.ChildInfoDTO;
import com.safetynet.alerts.dto.PersonIdentifierDTO;
import com.safetynet.alerts.dto.PersonInfoDTO;
import com.safetynet.alerts.exception.AlreadyExistException;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Data;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.JsonFileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    private Data data;

    @Mock
    private JsonFileHandler jsonFileHandler;

    @InjectMocks
    private PersonService personService;

    @BeforeEach
    public void setUp() {
        DataList dataList = new DataList();
        this.data = new Data(dataList.getPersons(), dataList.getFireStations(), dataList.getMedicalRecords());
        when(jsonFileHandler.getData()).thenReturn(this.data);
    }


    @Nested
    @DisplayName("Mutable Operations")
    class MutableOperationTest {

        @Test
        @DisplayName("Test creating person")
        public void create_ShouldReturnCreatedResponseEntity() {
            // Given
            Person person = new Person("Johnny", "Donut", "123 Main St", "Culver", "97451", "123-456-7898", "johnny.donut@example.com");

            when(jsonFileHandler.sortPersonsByLastNameAndFirstName(any(Data.class))).thenAnswer(invocation -> invocation.getArgument(0));
            doNothing().when(jsonFileHandler).writeData(any(Data.class));

            // When
            ResponseEntity<Void> result = personService.create(person);

            // Then
            verify(jsonFileHandler).sortPersonsByLastNameAndFirstName(any(Data.class));
            verify(jsonFileHandler).writeData(any(Data.class));
            assertThat(data.persons().contains(person)).isTrue();
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }

        @Test
        @DisplayName("Test already exist exception")
        public void create_ShouldReturnAlreadyExistException_WhenPersonAlreadyExist() {
            // Given
            Person person = new Person("John", "Doe", "123 Main St", "Culver", "97451", "123-456-7890", "john.doe@example.com");

            // When / Then
            assertThatThrownBy(() -> personService.create(person))
                    .isInstanceOf(AlreadyExistException.class);

            verify(jsonFileHandler, never()).sortPersonsByLastNameAndFirstName(any(Data.class));
            verify(jsonFileHandler, never()).writeData(any(Data.class));
        }

        @Test
        @DisplayName("Test updating person")
        public void update_ShouldReturnOkResponseEntity() {
            // Given
            Person person = new Person("John", "Doe", "123 Main St", "Culver", "97451", "123-456-7890", "john.doe@example.com");
            Person personUpdate = new Person("John", "Doe", "123 Main St", "Culver", "97451", "333-333-3333", "john.douille@example.com");

            doNothing().when(jsonFileHandler).writeData(any(Data.class));

            // When
            ResponseEntity<Void> result = personService.update(personUpdate);

            // Then
            verify(jsonFileHandler).writeData(any(Data.class));
            assertThat(data.persons().contains(person)).isFalse();
            assertThat(data.persons().contains(personUpdate)).isTrue();
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("Test update not found exception")
        public void update_ShouldReturnNotFoundException_WhenNoPersonExisting() {
            // Given
            Person person = new Person("Johnny", "Donut", "123 Main St", "Culver", "97451", "123-456-7898", "johnny.donut@example.com");

            // When / Then
            assertThatThrownBy(() -> personService.update(person))
                    .isInstanceOf(NotFoundException.class);

            verify(jsonFileHandler, never()).writeData(any(Data.class));
        }

        @Test
        @DisplayName("Test deleting person")
        public void delete_ShouldReturnOkResponseEntity() {
            // Given
            PersonIdentifierDTO personIdentifier = new PersonIdentifierDTO("John", "Doe");

            doNothing().when(jsonFileHandler).writeData(any(Data.class));

            // When
            ResponseEntity<Void> result = personService.delete(personIdentifier);
            boolean isDeleted = data.persons().stream()
                    .noneMatch(person -> person.firstName().equals(personIdentifier.firstName()) &&
                            person.lastName().equals(personIdentifier.lastName()));

            // Then
            verify(jsonFileHandler).writeData(any(Data.class));
            assertThat(isDeleted).isTrue();
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("Test delete not found exception")
        public void delete_ShouldReturnNotFoundException_WhenNoPersonExisting() {
            // Given
            PersonIdentifierDTO personIdentifier = new PersonIdentifierDTO("Johnny", "Donut");

            // When / Then
            assertThatThrownBy(() -> personService.delete(personIdentifier))
                    .isInstanceOf(NotFoundException.class);

            verify(jsonFileHandler, never()).writeData(any(Data.class));
        }
    }



    @Nested
    @DisplayName("Read Operations")
    class ReadOperationTest {

        @Test
        @DisplayName("Test getting emails by city")
        public void getEmailsByCity_ShouldReturnUniqueEmailsOfCorrectSize() {
            // Given
            String city = "Culver";
            Set<String> expectedEmails = Set.of("john.doe@example.com", "jane.doe@example.com", "ember.smith@example.com", "bob.johnson@example.com", "charlie.brown@example.com", "eve.jones@example.com");
            // When
            Set<String> emails = personService.getEmailsByCity(city);
            // Then
            assertThat(emails).containsExactlyInAnyOrderElementsOf(expectedEmails);
        }

        @Test
        @DisplayName("Test emails exception")
        public void getEmailsByCity_ShouldThrowNotFoundExceptionForNonExistentCity() {
            // Given
            String nonExistentCity = "New York";
            // When / Then
            assertThatThrownBy(() -> personService.getEmailsByCity(nonExistentCity))
                    .isInstanceOf(NotFoundException.class);
        }

        @Test
        @DisplayName("Test getting persons by last name")
        public void getPersonByLastname_ShouldReturnAListOfPersonInfoDTO() {
            // Given
            String lastName = "Doe";
            List<String> expectedFirstName = Arrays.asList("John","Jane");
            // When
            List<PersonInfoDTO> personInfoDTOs = personService.getPersonByLastname(lastName);
            // Then
            assertThat(personInfoDTOs)
                    .extracting("firstName")
                    .containsExactlyInAnyOrderElementsOf(expectedFirstName);
        }

        @Test
        @DisplayName("Test last name exception")
        public void getPersonByLastname_ShouldThrowNotFoundExceptionForNonExistentLastname() {
            // Given
            String nonExistentLastName = "Alf";
            // When / Then
            assertThatThrownBy(() -> personService.getEmailsByCity(nonExistentLastName))
                    .isInstanceOf(NotFoundException.class);
        }

        @Test
        @DisplayName("Test getting children by address")
        public void getChildrenByAddress_ShouldReturnAListOfChildInfoDTO() {
            // Given
            String address = "789 Oak St";
            // When
            List<ChildInfoDTO> children = personService.getChildrenByAddress(address);
            // Then
            assertThat(children).hasSize(2);
            assertThat(children).extracting("firstName").containsExactlyInAnyOrder("Alice", "Mark");
        }

        @Test
        @DisplayName("Test no children at address")
        public void getChildrenByAddress_ShouldReturnEmptyList_WhenNoChildrenAtAddress() {
            // Given
            String addressWithNoChildren = "123 Main St";
            // When
            List<ChildInfoDTO> children = personService.getChildrenByAddress(addressWithNoChildren);
            // Then
            assertThat(children).isEmpty();
        }

        @Test
        @DisplayName("Test address exception")
        public void getChildrenByAddress_ShouldThrowNotFoundExceptionForNonExistentAddress() {
            // Given
            String nonExistentAddress = "123 Schrimp St";
            // When / Then
            assertThatThrownBy(() -> personService.getChildrenByAddress(nonExistentAddress))
                    .isInstanceOf(NotFoundException.class);
        }
    }
}
