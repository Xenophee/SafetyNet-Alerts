package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.ChildInfoDTO;
import com.safetynet.alerts.dto.PersonInfoDTO;
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


    @Operation(summary = "Creates a person", description = "Creates a person with several information : lastName, firstName, address, city, zip, phone, email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Person has been created"),
            @ApiResponse(responseCode = "409", description = "Person with specified lastName and firstName already exists.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @PostMapping("/person")
    public String create(@RequestBody @Valid Person person) {
        return personService.create(person);
    }


    @Operation(summary = "Updates a person", description = "Updates a person's information by lastName and firstName")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Person has been updated"),
            @ApiResponse(responseCode = "404", description = "Person with specified lastName and firstName was not found.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @PutMapping("/person")
    public String update(@RequestBody @Valid Person person) {
        return personService.update(person);
    }


    @Operation(summary = "Deletes a person", description = "Deletes a person by lastName and firstName")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Person has been deleted"),
            @ApiResponse(responseCode = "404", description = "Person with specified lastName and firstName was not found.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @DeleteMapping("/person")
    public String delete(@RequestBody @Valid Person person) {
        return personService.delete(person);
    }


    @Operation(summary = "Get all information of a person by his lastName", description = "Get all information of a person by his lastName. The information contains the lastName, firstName, address, age, email. and his medical record. Can return multiple persons with the same lastName.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of information for the specified lastName.",
                    content = {@Content(schema = @Schema(implementation = PersonInfoDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Specified lastName was not found.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @GetMapping("/personinfo")
    public List<PersonInfoDTO> getPersonByLastname(@RequestParam("lastname") String lastname) {
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
    public Set<String> getEmailsByCity(@RequestParam("city") String city) {
        return personService.getEmailsByCity(city);
    }


    @Operation(summary = "Get the list of children by the address", description = "Get the list of children by the address. The list contains the children information (lastName, firstName, age) and the other members of the family. The list can be empty.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of children for the specified address. Can be empty.",
                    content = {@Content(schema = @Schema(implementation = ChildInfoDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Specified address was not found.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @GetMapping("/childalert")
    public List<ChildInfoDTO> getChildrenByAddress(@RequestParam("address") String address) {
        return personService.getChildrenByAddress(address);
    }

}
