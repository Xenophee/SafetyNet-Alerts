package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.ChildInfoDTO;
import com.safetynet.alerts.dto.PersonIdentifierDTO;
import com.safetynet.alerts.dto.PersonInfoDTO;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tinylog.Logger;

import java.util.List;
import java.util.Set;


@Tag(name = "Person API")
@RestController
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }


    @Operation(summary = "Creates a person", description = "Creates a person with several information : last name, first name, address, city, zip, phone, email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Person has been created"),
            @ApiResponse(responseCode = "400", description = "Person with specified data are not valid.",
                    content = {@Content(schema = @Schema(implementation = Error.class))}),
            @ApiResponse(responseCode = "409", description = "Person with specified last name and first name already exists.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @PostMapping("/person")
    public ResponseEntity<Void> create(@RequestBody @Valid Person person) {
        Logger.info("Request to create a new person : {}", person);
        return personService.create(person);
    }


    @Operation(summary = "Updates a person", description = "Updates a person's information by last name and first name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Person has been updated"),
            @ApiResponse(responseCode = "400", description = "Person with specified data are not valid.",
                    content = {@Content(schema = @Schema(implementation = Error.class))}),
            @ApiResponse(responseCode = "404", description = "Person with specified last name and first name was not found.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @PutMapping("/person")
    public ResponseEntity<Void> update(@RequestBody @Valid Person person) {
        Logger.info("Request to update a person : {}", person);
        return personService.update(person);
    }


    @Operation(summary = "Deletes a person", description = "Deletes a person by last name and first name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Person has been deleted"),
            @ApiResponse(responseCode = "400", description = "First name and / or last name must be specified.",
                    content = {@Content(schema = @Schema(implementation = Error.class))}),
            @ApiResponse(responseCode = "404", description = "Person with specified last name and first name was not found.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @DeleteMapping("/person")
    public ResponseEntity<Void> delete(@RequestBody @Valid PersonIdentifierDTO personIdentifier) {
        Logger.info("Request to delete a person : {}", personIdentifier);
        return personService.delete(personIdentifier);
    }


    @Operation(summary = "Get all information of a person by his last name", description = "Get all information of a person by his last name. The information contains the last name, first name, address, age, email. and his medical record. Can return multiple persons with the same last name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of information for the specified last name.",
                    content = {@Content(schema = @Schema(implementation = PersonInfoDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Specified last name was not found.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @GetMapping("/personinfo")
    public List<PersonInfoDTO> getPersonByLastname(
            @RequestParam("lastname")
            @Parameter(description = "The last name of the person to be searched", example = "Boyd")
            String lastname) {
        Logger.info("Request to get one or several person by the last name : {}", lastname);
        return personService.getPersonByLastname(lastname);
    }


    @Operation(summary = "Get the set of all person's email by the city", description = "Get the set of all person's email by the city.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Set of all emails for the specified city.",
                    content = {@Content(schema = @Schema(implementation = Set.class))}),
            @ApiResponse(responseCode = "404", description = "Specified city was not found.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @GetMapping("/communityemail")
    public Set<String> getEmailsByCity(
            @RequestParam("city")
            @Parameter(description = "The city where we want all the residents' emails", example = "Culver")
            String city) {
        Logger.info("Request to get emails by the city : {}", city);
        return personService.getEmailsByCity(city);
    }


    @Operation(summary = "Get the list of children by the address", description = "Get the list of children by the address. The list contains the children information (last name, first name, age) and the other members of the family. The list can be empty.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of children for the specified address. Can be empty.",
                    content = {@Content(schema = @Schema(implementation = ChildInfoDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Specified address was not found.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @GetMapping("/childalert")
    public List<ChildInfoDTO> getChildrenByAddress(
            @RequestParam("address")
            @Parameter(description = "The address where we want to retrieve the information of all the children", example = "\"1509 Culver St\"")
            String address) {
        Logger.info("Request to get children by the address : {}", address);
        return personService.getChildrenByAddress(address);
    }

}
