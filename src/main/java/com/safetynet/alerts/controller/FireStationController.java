package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.FireStationService;
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


@Tag(name = "Fire station API")
@RestController
public class FireStationController {

    private final FireStationService fireStationService;

    @Autowired
    public FireStationController(FireStationService fireStationService) {
        this.fireStationService = fireStationService;
    }



    @Operation(summary = "Creates a fire station", description = "Creates a fire station with his number and the address.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fire station has been created"),
            @ApiResponse(responseCode = "400", description = "Fire station with specified number and address already exists.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @PostMapping("/firestation")
    public String createFireStation(@RequestBody @Valid FireStation fireStation) {
        return fireStationService.createFireStation(fireStation);
    }


    @Operation(summary = "Updates a fire station", description = "Updates a fire station by the address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fire station has been updated"),
            @ApiResponse(responseCode = "404", description = "Fire station with specified lastname and firstname was not found.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @PutMapping("/firestation")
    public String updateFireStation(@RequestBody @Valid FireStation fireStation) {
        return fireStationService.updateFireStation(fireStation);
    }


    @Operation(summary = "Deletes a fire station", description = "Deletes a fire station by the number or the address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fire station has been deleted"),
            @ApiResponse(responseCode = "404", description = "Fire station with specified number or address was not found.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @DeleteMapping("/firestation")
    public String deleteFireStation(@RequestBody @Valid FireStation fireStation) {
        return fireStationService.deleteFireStation(fireStation);
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
        return fireStationService.getPersonsStationCoverage(stationNumber);
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
        return fireStationService.getHomesByStations(stations);
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
        return fireStationService.getPersonsPhonesByStation(stationNumber);
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
        return fireStationService.getPersonsAndStationByAddress(address);
    }
}
