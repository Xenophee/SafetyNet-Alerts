package com.safetynet.alerts.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.safetynet.alerts.dto.ChildInfoDTO;
import com.safetynet.alerts.dto.PersonIdentifierDTO;
import com.safetynet.alerts.dto.PersonInfoDTO;
import com.safetynet.alerts.exception.AlreadyExistException;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.PersonService;
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
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    private static ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonService personService;

    @BeforeAll
    public static void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Nested
    @DisplayName("Mutable Operations")
    class MutableOperationTest {

        @Test
        @DisplayName("201 when person is successfully created")
        void create_success() throws Exception {
            Person newPerson = new Person("Johnny", "Donut", "123 Main St", "Culver", "97451", "123-456-7890", "johnny.donut@example.com");
            when(personService.create(any(Person.class))).thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());

            mockMvc.perform(post("/person")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newPerson)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("400 when person data is invalid")
        void create_invalidData() throws Exception {
            Person invalidPerson = new Person("", "", "123 Main St", "Culver", "97451", "123-456-7890", "johnny.donut@example.com");

            mockMvc.perform(post("/person")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidPerson)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("409 when person to create already exist")
        void create_conflict() throws Exception {
            Person existingPerson = new Person("Johnny", "Donut", "123 Main St", "Culver", "97451", "123-456-7890", "johnny.donut@example.com");
            when(personService.create(any(Person.class))).thenThrow(new AlreadyExistException("Person already exist"));

            mockMvc.perform(post("/person")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(existingPerson)))
                    .andExpect(status().isConflict());
        }


        @Test
        @DisplayName("200 when person is successfully updated")
        void update_success() throws Exception {
            Person updatedPerson = new Person("Johnny", "Donut", "123 Main St", "Culver", "97451", "123-456-7890", "johnny.donut@example.com");
            when(personService.update(any(Person.class))).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(put("/person")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedPerson)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("400 when person data is invalid")
        void update_invalidData() throws Exception {
            Person invalidPerson = new Person("Johnny", "Donut", "", "", "97451", "123-456-7890", "johnny.donut@example.com");

            mockMvc.perform(put("/person")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidPerson)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("404 when person to update is not found")
        void update_notFound() throws Exception {
            Person nonExistentPerson = new Person("Johnny", "Donut", "123 Main St", "Culver", "97451", "123-456-7890", "johnny.donut@example.com");
            when(personService.update(any(Person.class))).thenThrow(new NotFoundException("Person not found"));

            mockMvc.perform(put("/person")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(nonExistentPerson)))
                    .andExpect(status().isNotFound());
        }


        @Test
        @DisplayName("200 when person is successfully deleted")
        void delete_success() throws Exception {
            PersonIdentifierDTO personIdentifier = new PersonIdentifierDTO("John", "Doe");
            when(personService.delete(any(PersonIdentifierDTO.class))).thenReturn(ResponseEntity.ok().build());

            mockMvc.perform(delete("/person")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(personIdentifier)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("400 when person identifier data is invalid")
        void delete_invalidData() throws Exception {
            PersonIdentifierDTO invalidPerson = new PersonIdentifierDTO("", "Peutplus");

            mockMvc.perform(delete("/person")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidPerson)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("404 when person to delete is not found")
        void delete_notFound() throws Exception {
            PersonIdentifierDTO nonExistentPerson = new PersonIdentifierDTO("Personne", "Nobody");
            when(personService.delete(any(PersonIdentifierDTO.class))).thenThrow(new NotFoundException("Person not found"));

            mockMvc.perform(delete("/person")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(nonExistentPerson)))
                    .andExpect(status().isNotFound());
        }
    }


    @Nested
    @DisplayName("Read Operations")
    class ReadOperationTest {

        @Test
        @DisplayName("personinfo - Success")
        void getPersonByLastname_success() throws Exception {
            List<PersonInfoDTO> personInfoDTOs = List.of(new PersonInfoDTO("John", "Doe", "1509 Culver St", "johndoe@example.com", 35, new String[0], new String[0]));
            when(personService.getPersonByLastname("Doe")).thenReturn(personInfoDTOs);

            mockMvc.perform(get("/personinfo?lastname=Doe"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(personInfoDTOs)));
        }

        @Test
        @DisplayName("personinfo - Not Found")
        void getPersonByLastname_notFound() throws Exception {
            when(personService.getPersonByLastname("Unknown Lastname")).thenThrow(new NotFoundException("Lastname not found"));

            mockMvc.perform(get("/personinfo?lastname=Unknown Lastname"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("communityemail - Success")
        void getEmailsByCity_success() throws Exception {
            Set<String> emails = Set.of("johndoe@example.com", "janedoe@example.com");
            when(personService.getEmailsByCity("Culver")).thenReturn(emails);

            mockMvc.perform(get("/communityemail?city=Culver"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(emails)));
        }

        @Test
        @DisplayName("communityemail - Not Found")
        void getEmailsByCity_notFound() throws Exception {
            when(personService.getEmailsByCity("Unknown City")).thenThrow(new NotFoundException("City not found"));

            mockMvc.perform(get("/communityemail?city=Unknown City"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("childalert - Success")
        void getChildrenByAddress_success() throws Exception {
            List<ChildInfoDTO> childInfoDTOs = List.of(new ChildInfoDTO("Little", "John", 5, List.of("John", "Jane")));
            when(personService.getChildrenByAddress("1509 Culver St")).thenReturn(childInfoDTOs);

            mockMvc.perform(get("/childalert?address=1509 Culver St"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(childInfoDTOs)));
        }

        @Test
        @DisplayName("childalert - Not Found")
        void getChildrenByAddress_notFound() throws Exception {
            when(personService.getChildrenByAddress("Unknown Address")).thenThrow(new NotFoundException("Address not found"));

            mockMvc.perform(get("/childalert?address=Unknown Address"))
                    .andExpect(status().isNotFound());
        }
    }
}
