package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@Tag(name = "Person API")
@RestController
@RequestMapping("/person")
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
    @PostMapping
    public String createPerson(@RequestBody Person person) {
        return personService.createPerson(person);
    }


    @Operation(summary = "Updates a person", description = "Updates a person's information by lastname and firstname")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Person has been updated"),
            @ApiResponse(responseCode = "404", description = "Person with specified lastname and firstname was not found.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @PutMapping
    public String updatePerson(@RequestBody Person person) {
        return personService.updatePerson(person);
    }


    @Operation(summary = "Deletes a person", description = "Deletes a person by lastname and firstname")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Person has been deleted"),
            @ApiResponse(responseCode = "404", description = "Person with specified lastname and firstname was not found.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @DeleteMapping
    public String deletePerson(@RequestBody Person person) {
        return personService.deletePerson(person);
    }

}
