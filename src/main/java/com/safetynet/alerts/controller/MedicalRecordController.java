package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.PersonIdentifierDTO;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.service.MedicalRecordService;
import io.swagger.v3.oas.annotations.Operation;
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

import java.util.Map;


@Tag(name = "Medical record API")
@RestController
@RequestMapping("/medicalrecord")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @Autowired
    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }


    @Operation(summary = "Creates a medical record", description = "Creates a medical record with a last name, first name, birthdate, and medications.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Medical record has been created"),
            @ApiResponse(responseCode = "400", description = "Medical record with specified data are not valid.",
                    content = {@Content(schema = @Schema(implementation = Map.class))}),
            @ApiResponse(responseCode = "406", description = "Birthdate format is not valid to parse.",
                    content = {@Content(schema = @Schema(implementation = Error.class))}),
            @ApiResponse(responseCode = "409", description = "Medical record with specified last name and first name already exists.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid MedicalRecord medicalRecord) {
        Logger.info("Request to create a new medical record : {}", medicalRecord);
        return medicalRecordService.create(medicalRecord);
    }


    @Operation(summary = "Updates a medical record", description = "Updates a medical record by the last name and the first name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medical record has been updated"),
            @ApiResponse(responseCode = "400", description = "Medical record with specified data are not valid.",
                    content = {@Content(schema = @Schema(implementation = Map.class))}),
            @ApiResponse(responseCode = "406", description = "Birthdate format is not valid to parse.",
                    content = {@Content(schema = @Schema(implementation = Error.class))}),
            @ApiResponse(responseCode = "404", description = "Medical record with specified last name and first name was not found.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @PutMapping
    public ResponseEntity<Void> update(@RequestBody @Valid MedicalRecord medicalRecord) {
        Logger.info("Request to update a medical record : {}", medicalRecord);
        return medicalRecordService.update(medicalRecord);
    }


    @Operation(summary = "Deletes a medical record", description = "Deletes a medical record by the last name and the first name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medical record has been deleted"),
            @ApiResponse(responseCode = "400", description = "First name and / or last name must be specified.",
                    content = {@Content(schema = @Schema(implementation = Map.class))}),
            @ApiResponse(responseCode = "404", description = "Medical record with specified last name and first name was not found.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestBody @Valid PersonIdentifierDTO personIdentifier) {
        Logger.info("Request to delete a medical record : {}", personIdentifier);
        return medicalRecordService.delete(personIdentifier);
    }
}
