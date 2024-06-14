package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.service.FireStationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Fire station API")
@RestController
@RequestMapping("/firestation")
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
    @PostMapping
    public String createFireStation(@RequestBody FireStation fireStation) {
        return fireStationService.createFireStation(fireStation);
    }


    @Operation(summary = "Updates a fire station", description = "Updates a fire station by the address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fire station has been updated"),
            @ApiResponse(responseCode = "404", description = "Fire station with specified lastname and firstname was not found.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @PutMapping
    public String updateFireStation(@RequestBody FireStation fireStation) {
        return fireStationService.updateFireStation(fireStation);
    }


    @Operation(summary = "Deletes a fire station", description = "Deletes a fire station by the number or the address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fire station has been deleted"),
            @ApiResponse(responseCode = "404", description = "Fire station with specified number or address was not found.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @DeleteMapping
    public String deleteFireStation(@RequestBody FireStation fireStation) {
        return fireStationService.deleteFireStation(fireStation);
    }
}
