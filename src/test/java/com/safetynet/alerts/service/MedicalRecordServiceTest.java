package com.safetynet.alerts.service;

import com.safetynet.alerts.data.DataList;
import com.safetynet.alerts.dto.PersonIdentifierDTO;
import com.safetynet.alerts.exception.AlreadyExistException;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Data;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
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

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class MedicalRecordServiceTest {

    private Data data;

    @Mock
    private JsonFileHandler jsonFileHandler;

    @InjectMocks
    private MedicalRecordService medicalRecordService;

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
        @DisplayName("Test creating medical record")
        public void create_ShouldReturnCreatedResponseEntity() {
            // Given
            MedicalRecord medicalRecord = new MedicalRecord("Charlotte", "Brownie", LocalDate.of(1990, 5, 25), new String[0], new String[0]);

            when(jsonFileHandler.sortMedicalRecordsByLastNameAndFirstName(any(Data.class))).thenAnswer(invocation -> invocation.getArgument(0));
            doNothing().when(jsonFileHandler).writeData(any(Data.class));

            // When
            ResponseEntity<Void> result = medicalRecordService.create(medicalRecord);

            // Then
            verify(jsonFileHandler).sortMedicalRecordsByLastNameAndFirstName(any(Data.class));
            verify(jsonFileHandler).writeData(any(Data.class));
            assertThat(data.medicalRecords().contains(medicalRecord)).isTrue();
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }

        @Test
        @DisplayName("Test already exist exception")
        public void create_ShouldReturnAlreadyExistException_WhenMedicalRecordAlreadyExist() {
            // Given
            MedicalRecord medicalRecord = new MedicalRecord("John", "Doe", LocalDate.of(2000, 1, 1), new String[]{"medication1", "medication2"}, new String[]{"allergy1", "allergy2"});

            // When / Then
            assertThatThrownBy(() -> medicalRecordService.create(medicalRecord))
                    .isInstanceOf(AlreadyExistException.class);

            verify(jsonFileHandler, never()).sortMedicalRecordsByLastNameAndFirstName(any(Data.class));
            verify(jsonFileHandler, never()).writeData(any(Data.class));
        }

        @Test
        @DisplayName("Test updating medical record")
        public void update_ShouldReturnOkResponseEntity() {
            // Given
            MedicalRecord medicalRecord = new MedicalRecord("John", "Doe", LocalDate.of(2000, 1, 1), new String[]{"medication1", "medication2"}, new String[]{"allergy1", "allergy2"});
            MedicalRecord medicalRecordUpdate = new MedicalRecord("John", "Doe", LocalDate.of(2000, 1, 1), new String[0], new String[0]);

            doNothing().when(jsonFileHandler).writeData(any(Data.class));

            // When
            ResponseEntity<Void> result = medicalRecordService.update(medicalRecordUpdate);

            // Then
            verify(jsonFileHandler).writeData(any(Data.class));
            assertThat(data.medicalRecords().contains(medicalRecord)).isFalse();
            assertThat(data.medicalRecords().contains(medicalRecordUpdate)).isTrue();
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("Test update not found exception")
        public void update_ShouldReturnNotFoundException_WhenNoMedicalRecordExisting() {
            // Given
            MedicalRecord medicalRecord = new MedicalRecord("Dédé", "Arsouilleur", LocalDate.of(2000, 1, 1), new String[0], new String[0]);

            // When / Then
            assertThatThrownBy(() -> medicalRecordService.update(medicalRecord))
                    .isInstanceOf(NotFoundException.class);

            verify(jsonFileHandler, never()).writeData(any(Data.class));
        }

        @Test
        @DisplayName("Test deleting medical record")
        public void delete_ShouldReturnOkResponseEntity() {
            // Given
            PersonIdentifierDTO personIdentifier = new PersonIdentifierDTO("John", "Doe");

            doNothing().when(jsonFileHandler).writeData(any(Data.class));

            // When
            ResponseEntity<Void> result = medicalRecordService.delete(personIdentifier);
            boolean isDeleted = data.medicalRecords().stream()
                    .noneMatch(medicalRecord -> medicalRecord.firstName().equals(personIdentifier.firstName()) &&
                            medicalRecord.lastName().equals(personIdentifier.lastName()));

            // Then
            verify(jsonFileHandler).writeData(any(Data.class));
            assertThat(isDeleted).isTrue();
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("Test delete not found exception")
        public void delete_ShouldReturnNotFoundException_WhenNoMedicalRecordExisting() {
            // Given
            PersonIdentifierDTO personIdentifier = new PersonIdentifierDTO("Johnny", "Donut");

            // When / Then
            assertThatThrownBy(() -> medicalRecordService.delete(personIdentifier))
                    .isInstanceOf(NotFoundException.class);

            verify(jsonFileHandler, never()).writeData(any(Data.class));
        }
    }
}
