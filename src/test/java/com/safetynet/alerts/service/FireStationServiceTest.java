package com.safetynet.alerts.service;

import com.safetynet.alerts.data.DataList;
import com.safetynet.alerts.dto.FireDTO;
import com.safetynet.alerts.dto.FloodDTO;
import com.safetynet.alerts.dto.StationCoverageDTO;
import com.safetynet.alerts.exception.AlreadyExistException;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Data;
import com.safetynet.alerts.model.FireStation;
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

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class FireStationServiceTest {

    private Data data;

    @Mock
    private JsonFileHandler jsonFileHandler;

    @InjectMocks
    private FireStationService fireStationService;

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
        @DisplayName("Test creating fire station")
        public void create_ShouldReturnCreatedResponseEntity() {
            // Given
            FireStation fireStation = new FireStation("125 Schrimp St", 4);

            when(jsonFileHandler.sortFireStationsByStationNumber(any(Data.class))).thenAnswer(invocation -> invocation.getArgument(0));
            doNothing().when(jsonFileHandler).writeData(any(Data.class));

            // When
            ResponseEntity<Void> result = fireStationService.create(fireStation);

            // Then
            verify(jsonFileHandler).sortFireStationsByStationNumber(any(Data.class));
            verify(jsonFileHandler).writeData(any(Data.class));
            assertThat(data.fireStations().contains(fireStation)).isTrue();
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }

        @Test
        @DisplayName("Test already exist exception")
        public void create_ShouldReturnAlreadyExistException_WhenFireStationAlreadyExist() {
            // Given
            FireStation fireStation = new FireStation("123 Main St", 1);

            // When / Then
            assertThatThrownBy(() -> fireStationService.create(fireStation))
                    .isInstanceOf(AlreadyExistException.class);

            verify(jsonFileHandler, never()).sortFireStationsByStationNumber(any(Data.class));
            verify(jsonFileHandler, never()).writeData(any(Data.class));
        }

        @Test
        @DisplayName("Test updating fire station")
        public void update_ShouldReturnOkResponseEntity() {
            // Given
            FireStation fireStation = new FireStation("123 Main St", 1);
            FireStation fireStationUpdate = new FireStation("123 Main St", 5);

            doNothing().when(jsonFileHandler).writeData(any(Data.class));

            // When
            ResponseEntity<Void> result = fireStationService.update(fireStationUpdate);

            // Then
            verify(jsonFileHandler).writeData(any(Data.class));
            assertThat(data.fireStations().contains(fireStation)).isFalse();
            assertThat(data.fireStations().contains(fireStationUpdate)).isTrue();
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("Test update not found exception")
        public void update_ShouldReturnNotFoundException_WhenNoFireStationAddressExisting() {
            // Given
            FireStation fireStation = new FireStation("130 Yolo St", 1);

            // When / Then
            assertThatThrownBy(() -> fireStationService.update(fireStation))
                    .isInstanceOf(NotFoundException.class);

            verify(jsonFileHandler, never()).writeData(any(Data.class));
        }

        @Test
        @DisplayName("Test deleting fire station")
        public void delete_ShouldReturnOkResponseEntity() {
            // Given
            FireStation fireStation = new FireStation("123 Main St", 1);

            doNothing().when(jsonFileHandler).writeData(any(Data.class));

            // When
            ResponseEntity<Void> result = fireStationService.delete(fireStation);

            // Then
            verify(jsonFileHandler).writeData(any(Data.class));
            assertThat(data.fireStations().contains(fireStation)).isFalse();
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("Test delete not found exception")
        public void delete_ShouldReturnNotFoundException_WhenNoFireStationExisting() {
            // Given
            FireStation fireStation = new FireStation("130 Yolo St", 1);

            // When / Then
            assertThatThrownBy(() -> fireStationService.delete(fireStation))
                    .isInstanceOf(NotFoundException.class);

            verify(jsonFileHandler, never()).writeData(any(Data.class));
        }
    }


    @Nested
    @DisplayName("Read Operations")
    class ReadOperationTest {

        @Test
        @DisplayName("Test getting homes by a list of station number")
        public void getHomesByStations_ShouldReturnFloodDTO() {
            // Given
            List<Integer> stationNumbers = Arrays.asList(1,2);
            List<String> expectedAddresses = Arrays.asList("123 Main St", "789 Oak St", "321 Pine St", "654 Elm St", "345 Cedar St");
            List<String> expectedFirstName = Arrays.asList("John", "Ember", "Alice", "Mark", "Bob", "Charlie", "Eve");
            // When
            List<FloodDTO> result = fireStationService.getHomesByStations(stationNumbers);
            // Then
            assertThat(result).extracting("fireStation").isEqualTo(stationNumbers);
            assertThat(result)
                    .flatExtracting(floodDTO -> floodDTO.personsByAddress().keySet())
                    .containsExactlyInAnyOrderElementsOf(expectedAddresses);
            assertThat(result)
                    .flatExtracting(floodDTO -> floodDTO.personsByAddress().values())
                    .flatExtracting(ArrayList::new)
                    .extracting("firstName")
                    .containsExactlyInAnyOrderElementsOf(expectedFirstName);
        }
        @Test
        @DisplayName("Test getting number of adults, children and persons by station number")
        public void getPersonsStationCoverage_ShouldReturnStationCoverageDTO() {
            // Given
            int stationNumber = 2;
            int expectedAdults = 3;
            int expectedChildren = 2;
            List<String> expectedFirstName = Arrays.asList("Ember","Alice","Mark", "Charlie", "Eve");
            // When
            StationCoverageDTO result = fireStationService.getPersonsStationCoverage(stationNumber);
            // Then
            assertThat(result.adults()).isEqualTo(expectedAdults);
            assertThat(result.children()).isEqualTo(expectedChildren);
            assertThat(result.persons())
                    .extracting("firstName")
                    .containsExactlyInAnyOrderElementsOf(expectedFirstName);
        }

        @Test
        @DisplayName("Test getting phones by station number")
        public void getPersonsPhonesByStation_ShouldReturnUniquePhonesOfCorrectSize() {
            // Given
            int stationNumber = 2;
            Set<String> expectedPhones = Set.of("123-456-7894", "123-456-7896", "123-456-7892");
            // When
            Set<String> phones = fireStationService.getPersonsPhonesByStation(stationNumber);
            // Then
            assertThat(phones).containsExactlyInAnyOrderElementsOf(expectedPhones);
        }

        @Test
        @DisplayName("Test getting persons and station")
        public void getPersonsAndStationByAddress_ShouldReturnFireDTO() {
            // Given
            String address = "789 Oak St";
            List<String> expectedFirstName = Arrays.asList("Ember","Alice","Mark");
            // When
            FireDTO result = fireStationService.getPersonsAndStationByAddress(address);
            // Then
            assertThat(result.fireStation()).isEqualTo(2);
            assertThat(result.persons())
                    .extracting("firstName")
                    .containsExactlyInAnyOrderElementsOf(expectedFirstName);
        }

        @Test
        @DisplayName("Test address exception")
        public void getPersonsAndStationByAddress_ShouldThrowNotFoundExceptionForNonExistentAddress() {
            // Given
            String nonExistentAddress = "123 Schrimp St";
            // When / Then
            assertThatThrownBy(() -> fireStationService.getPersonsAndStationByAddress(nonExistentAddress))
                    .isInstanceOf(NotFoundException.class);
        }

        @Test
        @DisplayName("Test getting addresses by station number")
        public void getAddressesByStation_ShouldReturnSetOfAdresses() {
            // Given
            int nonExistentStation = 1;
            Set<String> expectedAddresses = new HashSet<>(Arrays.asList("123 Main St", "321 Pine St"));
            // When
            Set<String> addresses = fireStationService.getAddressesByStation(nonExistentStation);
            // Then
            assertThat(addresses).containsExactlyInAnyOrderElementsOf(expectedAddresses);
        }

        @Test
        @DisplayName("Test station number exception")
        public void getAddressesByStation_ShouldThrowNotFoundExceptionForNonExistentStationNumber() {
            // Given
            int nonExistentStation = 10;
            // When / Then
            assertThatThrownBy(() -> fireStationService.getAddressesByStation(nonExistentStation))
                    .isInstanceOf(NotFoundException.class);
        }
    }
}
