package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.PersonQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@Tag(name = "Person query API")
@RestController
public class PersonQueryController {

    private final PersonQueryService personQueryService;

    @Autowired
    public PersonQueryController(PersonQueryService personQueryService) {
        this.personQueryService = personQueryService;
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
        return personQueryService.getAPersonByLastname(lastname);
    }


    @Operation(summary = "Get the list of all person's email by the city", description = "Get the list of all person's email by the city.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all emails for the specified city.",
                    content = {@Content(schema = @Schema(implementation = Person.class))}),
            @ApiResponse(responseCode = "404", description = "Specified city was not found.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @GetMapping("/communityemail")
    public Object getEmails(@RequestParam("city") String city) {
        return personQueryService.getEmails(city);
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
        return personQueryService.getChildrenByAddress(address);
    }
}
