package com.safetynet.alerts.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.safetynet.alerts.dto.PersonIdentifierDTO;
import com.safetynet.alerts.exception.AlreadyExistException;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.service.MedicalRecordService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;




@WebMvcTest(MedicalRecordController.class)
public class MedicalRecordControllerTest {

    private static ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicalRecordService medicalRecordService;

    @BeforeAll
    public static void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }



    @Nested
    @DisplayName("Mutable Operations")
    class MutableOperationTest {

        @Test
        @DisplayName("201 when medical record is successfully created")
        void create_success() throws Exception {
            MedicalRecord newMedicalRecord = new MedicalRecord("John", "Doe", LocalDate.of(1990, 1, 1), new String[]{"med1", "med2"}, new String[]{"allergy1"});
            when(medicalRecordService.create(any(MedicalRecord.class))).thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());

            mockMvc.perform(post("/medicalrecord")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newMedicalRecord)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("400 when medical record data is invalid")
        void create_invalidData() throws Exception {
            MedicalRecord invalidMedicalRecord = new MedicalRecord("", "", LocalDate.of(1990, 1, 1), new String[0], new String[0]);

            mockMvc.perform(post("/medicalrecord")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidMedicalRecord)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("409 when medical record to create already exist")
        void create_conflict() throws Exception {
            MedicalRecord existingMedicalRecord = new MedicalRecord("Jexiste", "Deja", LocalDate.of(1990, 1, 1), new String[]{"med1"}, new String[]{"allergy1"});
            when(medicalRecordService.create(any(MedicalRecord.class))).thenThrow(new AlreadyExistException("Medical record already exist"));

            mockMvc.perform(post("/medicalrecord")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(existingMedicalRecord)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("200 when medical record is successfully updated")
        void update_success() throws Exception {
            MedicalRecord updatedMedicalRecord = new MedicalRecord("John", "Doe", LocalDate.of(1990, 1, 1), new String[]{"med1"}, new String[]{"allergy1"});
            when(medicalRecordService.update(any(MedicalRecord.class))).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(put("/medicalrecord")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedMedicalRecord)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("400 when medical record data is invalid")
        void update_invalidData() throws Exception {
            MedicalRecord invalidMedicalRecord = new MedicalRecord("", "", LocalDate.of(1990, 1, 1), new String[0], new String[0]);

            mockMvc.perform(put("/medicalrecord")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidMedicalRecord)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("404 when medical record to update is not found")
        void update_notFound() throws Exception {
            MedicalRecord nonExistentMedicalRecord = new MedicalRecord("Personne", "Nobody", LocalDate.of(1990, 1, 1), new String[]{"med1"}, new String[]{"allergy1"});
            when(medicalRecordService.update(any(MedicalRecord.class))).thenThrow(new NotFoundException("Medical record not found"));

            mockMvc.perform(put("/medicalrecord")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(nonExistentMedicalRecord)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("200 when medical record is successfully deleted")
        void delete_success() throws Exception {
            PersonIdentifierDTO personIdentifier = new PersonIdentifierDTO("John", "Doe");
            when(medicalRecordService.delete(any(PersonIdentifierDTO.class))).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(delete("/medicalrecord")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(personIdentifier)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("400 when person identifier data is invalid")
        void delete_invalidData() throws Exception {
            PersonIdentifierDTO invalidPerson = new PersonIdentifierDTO("Jean", "");

            mockMvc.perform(delete("/medicalrecord")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidPerson)))
                    .andExpect(status().isBadRequest());
        }


        @Test
        @DisplayName("404 when medical record to delete is not found")
        void delete_notFound() throws Exception {
            PersonIdentifierDTO nonExistentPerson = new PersonIdentifierDTO("Personne", "Nobody");
            when(medicalRecordService.delete(any(PersonIdentifierDTO.class))).thenThrow(new NotFoundException("Medical record not found"));

            mockMvc.perform(delete("/medicalrecord")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(nonExistentPerson)))
                    .andExpect(status().isNotFound());
        }
    }

}
