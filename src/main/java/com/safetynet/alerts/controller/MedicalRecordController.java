package com.safetynet.alerts.controller;

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
import org.springframework.web.bind.annotation.*;


@Tag(name = "Medical record API")
@RestController
@RequestMapping("/medicalrecord")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @Autowired
    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @Operation(summary = "Creates a medical record", description = "Creates a medical record with a lastname, firstname, birthdate, and medications.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medical record has been created"),
            @ApiResponse(responseCode = "400", description = "Medical record with specified lastname and firstname already exists.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @PostMapping
    public String createMedicalRecord(@RequestBody @Valid MedicalRecord medicalRecord) {
        return medicalRecordService.createMedicalRecord(medicalRecord);
    }


    @Operation(summary = "Updates a medical record", description = "Updates a medical record by the lastname and the firstname.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medical record has been updated"),
            @ApiResponse(responseCode = "404", description = "Medical record with specified lastname and firstname was not found.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @PutMapping
    public String updateMedicalRecord(@RequestBody @Valid MedicalRecord medicalRecord) {
        return medicalRecordService.updateMedicalRecord(medicalRecord);
    }


    @Operation(summary = "Deletes a medical record", description = "Deletes a medical record by the lastname and the firstname.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medical record has been deleted"),
            @ApiResponse(responseCode = "404", description = "Medical record with specified lastname and firstname was not found.",
                    content = {@Content(schema = @Schema(implementation = Error.class))})
    })
    @DeleteMapping
    public String deleteMedicalRecord(@RequestBody @Valid MedicalRecord medicalRecord) {
        return medicalRecordService.deleteMedicalRecord(medicalRecord);
    }
}
