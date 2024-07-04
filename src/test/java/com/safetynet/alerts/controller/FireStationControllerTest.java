package com.safetynet.alerts.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.safetynet.alerts.dto.*;
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

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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


    @Nested
    @DisplayName("Read Operations")
    class ReadOperationTest {

        @Test
        @DisplayName("firestation - Success")
        void getPersonsStationCoverage_success() throws Exception {
            StationCoverageDTO stationCoverage = new StationCoverageDTO(
                    2, 3,
                    List.of(
                            new StationCoveragePersonInfoDTO("John", "Doe", "1509 Culver St", "841-874-6512"),
                            new StationCoveragePersonInfoDTO("Jane", "Doe", "1509 Culver St", "841-874-6513")
                    )
            );
            when(fireStationService.getPersonsStationCoverage(1)).thenReturn(stationCoverage);

            mockMvc.perform(get("/firestation?stationnumber=1"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(stationCoverage)));
        }

        @Test
        @DisplayName("firestation - Not Found")
        void getPersonsStationCoverage_notFound() throws Exception {
            when(fireStationService.getPersonsStationCoverage(999)).thenThrow(new NotFoundException("Station number not found"));

            mockMvc.perform(get("/firestation?stationnumber=999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("flood/stations - Success")
        void getHomesByStations_success() throws Exception {
            List<FloodDTO> flood = List.of(
                    new FloodDTO(1,
                    Map.of(
                            "1509 Culver St", List.of(
                                    new FireFloodPersonInfoDTO("John", "Doe", "841-874-6512", 35, new String[0], new String[0]),
                                    new FireFloodPersonInfoDTO("Jane", "Doe", "841-874-6513", 34, new String[0], new String[0])
                            )
                    )),
                    new FloodDTO(2,
                            Map.of(
                                    "1555 Culver St", List.of(
                                            new FireFloodPersonInfoDTO("John", "Doe", "841-874-6512", 35, new String[0], new String[0]),
                                            new FireFloodPersonInfoDTO("Jane", "Doe", "841-874-6513", 34, new String[0], new String[0])
                                    )
                            ))
            );
            when(fireStationService.getHomesByStations(List.of(1, 2))).thenReturn(flood);

            mockMvc.perform(get("/flood/stations?stations=1,2"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(flood)));
        }

        @Test
        @DisplayName("flood/stations - Not Found")
        void getHomesByStations_notFound() throws Exception {
            when(fireStationService.getHomesByStations(List.of(999))).thenThrow(new NotFoundException("Stations numbers not found"));

            mockMvc.perform(get("/flood/stations?stations=999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("phonealert - Success")
        void getPersonsPhonesByStation_success() throws Exception {
            Set<String> phones = Set.of("123-456-7890", "098-765-4321");
            when(fireStationService.getPersonsPhonesByStation(1)).thenReturn(phones);

            mockMvc.perform(get("/phonealert?firestation=1"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(phones)));
        }

        @Test
        @DisplayName("phonealert - Not Found")
        void getPersonsPhonesByStation_notFound() throws Exception {
            when(fireStationService.getPersonsPhonesByStation(999)).thenThrow(new NotFoundException("Station number not found"));

            mockMvc.perform(get("/phonealert?firestation=999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("fire - Success")
        void getPersonsAndStationByAddress_success() throws Exception {
            FireDTO fire = new FireDTO(
                    1,
                    List.of(
                            new FireFloodPersonInfoDTO("John", "Doe", "841-874-6512", 35, new String[0], new String[0]),
                            new FireFloodPersonInfoDTO("Jane", "Doe", "841-874-6513", 34, new String[0], new String[0])
                    )
            );
            when(fireStationService.getPersonsAndStationByAddress("123 Main St")).thenReturn(fire);

            mockMvc.perform(get("/fire?address=123 Main St"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(fire)));
        }


        @Test
        @DisplayName("fire - Not Found")
        void getPersonsAndStationByAddress_notFound() throws Exception {
            when(fireStationService.getPersonsAndStationByAddress("Unknown Address")).thenThrow(new NotFoundException("Address not found"));

            mockMvc.perform(get("/fire?address=Unknown Address"))
                    .andExpect(status().isNotFound());
        }
    }
}
