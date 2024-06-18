package com.safetynet.alerts.controller;

import com.safetynet.alerts.exception.JsonFileReadException;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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


    @Operation(summary = "Creates a person", description = "Creates a person with several information : lastname, firstname, address, city, zip, phone, email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Person has been created"),
            @ApiResponse(responseCode = "400", description = "Person with specified lastname and firstname already exists.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @PostMapping("/person")
    public String createPerson(@RequestBody @Valid Person person) {
        return personService.createPerson(person);
    }


    @Operation(summary = "Updates a person", description = "Updates a person's information by lastname and firstname")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Person has been updated"),
            @ApiResponse(responseCode = "404", description = "Person with specified lastname and firstname was not found.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @PutMapping("/person")
    public String updatePerson(@RequestBody @Valid Person person) {
        return personService.updatePerson(person);
    }


    @Operation(summary = "Deletes a person", description = "Deletes a person by lastname and firstname")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Person has been deleted"),
            @ApiResponse(responseCode = "404", description = "Person with specified lastname and firstname was not found.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @DeleteMapping("/person")
    public String deletePerson(@RequestBody @Valid Person person) {
        return personService.deletePerson(person);
    }


    @Operation(summary = "Get all information of a person by his lastname", description = "Get all information of a person by his lastname. The information contains the lastname, firstname, address, age, email. and his medical record. Can return multiple persons with the same lastname.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of information for the specified lastname.",
                    content = {@Content(schema = @Schema(implementation = Person.class))}),
            @ApiResponse(responseCode = "404", description = "Specified lastname was not found.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @GetMapping("/personinfo")
    public Object getAPersonByLastname(@RequestParam("lastname") String lastname) {
        return personService.getAPersonByLastname(lastname);
    }


    @Operation(summary = "Get the set of all person's email by the city", description = "Get the set of all person's email by the city.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Set of all emails for the specified city.",
                    content = {@Content(schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "404", description = "Specified city was not found.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @GetMapping("/communityemail")
    public Set<String> getEmails(@RequestParam("city") String city) throws JsonFileReadException {
        return personService.getEmailsByCity(city);
    }


    @Operation(summary = "Get the list of children by the address", description = "Get the list of children by the address. The list contains the children information (lastname, firstname, age) and the other members of the family. The list can be empty.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of children for the specified address. Can be empty.",
                    content = {@Content(schema = @Schema(implementation = Person.class))}),
            @ApiResponse(responseCode = "404", description = "Specified address was not found.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @GetMapping("/childalert")
    public Object getChildrenByAddress(@RequestParam("address") String address) {
        return personService.getChildrenByAddress(address);
    }

}
