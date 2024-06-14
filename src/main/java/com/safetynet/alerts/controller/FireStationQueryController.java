package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.FireStationQueryService;
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

import java.util.List;


@Tag(name = "Fire station query API")
@RestController
public class FireStationQueryController {

    private final FireStationQueryService fireStationQueryService;

    @Autowired
    public FireStationQueryController(FireStationQueryService fireStationQueryService) {
        this.fireStationQueryService = fireStationQueryService;
    }



    @Operation(summary = "Get the list of persons coverage by a station", description = "Get the list of persons coverage by a station number. The list contains the persons information (lastname, firstname, address, phone) and the number of children and adults.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of persons for the specified station number.",
                    content = {@Content(schema = @Schema(implementation = Person.class))}),
            @ApiResponse(responseCode = "404", description = "Station with specified station number was not found.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @GetMapping("/firestation")
    public Object getPersonsStationCoverage(@RequestParam("stationnumber") int stationNumber) {
        return fireStationQueryService.getPersonsStationCoverage(stationNumber);
    }


    @Operation(summary = "Get all homes for each stations", description = "Get all homes for each stations order by station number and group by address. The information of each resident contains the lastname, the phone number, the age and the medical record.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of information for specified stations numbers.",
                    content = {@Content(schema = @Schema(implementation = Person.class))}),
            @ApiResponse(responseCode = "404", description = "Specified stations numbers were not found.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @GetMapping("/flood/stations")
    public Object getHomesByStations(@RequestParam("stations") List<Integer> stations) {
        return fireStationQueryService.getHomesByStations(stations);
    }


    @Operation(summary = "Get all resident phone numbers for station coverage", description = "Get all resident phone numbers for station coverage.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of resident phone numbers for station coverage.",
                    content = {@Content(schema = @Schema(implementation = Person.class))}),
            @ApiResponse(responseCode = "404", description = "Specified station number was not found.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @GetMapping("/phonealert")
    public Object getPersonsPhonesByStation(@RequestParam("firestation") int stationNumber) {
        return fireStationQueryService.getPersonsPhonesByStation(stationNumber);
    }


    @Operation(summary = "Get all resident and the fire station by a specified address", description = "Get all resident and the fire station by a specified address. The list contains the persons information (lastname, firstname, phone, age, the medical record), and the fire station number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of resident and the fire station by a specified address.",
                    content = {@Content(schema = @Schema(implementation = Person.class))}),
            @ApiResponse(responseCode = "404", description = "Specified address was not found.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @GetMapping("/fire")
    public Object getPersonsAndStationByAddress(@RequestParam("address") String address) {
        return fireStationQueryService.getPersonsAndStationByAddress(address);
    }
}
