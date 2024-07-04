package com.safetynet.alerts.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.safetynet.alerts.exception.AlreadyExistException;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.service.FireStationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@WebMvcTest(FireStationController.class)
public class FireStationControllerTest {

    private static ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FireStationService fireStationService;

    @BeforeAll
    public static void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Nested
    @DisplayName("Mutable Operations")
    class MutableOperationTest {

        @Test
        @DisplayName("201 when fire station is successfully created")
        void create_success() throws Exception {
            FireStation newFireStation = new FireStation("123 Main St", 1);
            when(fireStationService.create(any(FireStation.class))).thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());

            mockMvc.perform(post("/firestation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newFireStation)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("400 when fire station data is invalid")
        void create_invalidData() throws Exception {
            FireStation invalidFireStation = new FireStation("", 1);

            mockMvc.perform(post("/firestation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidFireStation)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("409 when fire station to create already exist")
        void create_conflict() throws Exception {
            FireStation existingFireStation = new FireStation("123 Main St", 1);
            when(fireStationService.create(any(FireStation.class))).thenThrow(new AlreadyExistException("Fire station already exist"));

            mockMvc.perform(post("/firestation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(existingFireStation)))
                    .andExpect(status().isConflict());
        }


        @Test
        @DisplayName("200 when fire station is successfully updated")
        void update_success() throws Exception {
            FireStation updatedFireStation = new FireStation("123 Main St", 1);
            when(fireStationService.update(any(FireStation.class))).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(put("/firestation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedFireStation)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("400 when fire station data is invalid")
        void update_invalidData() throws Exception {
            FireStation invalidFireStation = new FireStation("", 1);

            mockMvc.perform(put("/firestation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidFireStation)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("404 when fire station to update is not found")
        void update_notFound() throws Exception {
            FireStation nonExistentFireStation = new FireStation("123 Main St", 1);
            when(fireStationService.update(any(FireStation.class))).thenThrow(new NotFoundException("Fire station not found"));

            mockMvc.perform(put("/firestation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(nonExistentFireStation)))
                    .andExpect(status().isNotFound());
        }


        @Test
        @DisplayName("200 when fire station is successfully deleted")
        void delete_success() throws Exception {
            FireStation deletedFireStation = new FireStation("123 Main St", 1);
            when(fireStationService.delete(any(FireStation.class))).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(delete("/firestation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(deletedFireStation)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("400 when fire station data is invalid")
        void delete_invalidData() throws Exception {
            FireStation invalidFireStation = new FireStation("", 1);

            mockMvc.perform(delete("/firestation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidFireStation)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("404 when fire station to delete is not found")
        void delete_notFound() throws Exception {
            FireStation nonExistentFireStation = new FireStation("123 Main St", 1);
            when(fireStationService.delete(any(FireStation.class))).thenThrow(new NotFoundException("Fire station not found"));

            mockMvc.perform(delete("/firestation")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(nonExistentFireStation)))
                    .andExpect(status().isNotFound());
        }
    }
}
