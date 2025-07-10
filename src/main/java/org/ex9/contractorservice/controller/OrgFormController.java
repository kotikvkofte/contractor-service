package org.ex9.contractorservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.ex9.contractorservice.dto.ErrorResponse;
import org.ex9.contractorservice.dto.orgform.OrgFormRequestDto;
import org.ex9.contractorservice.dto.orgform.OrgFormResponseDto;
import org.ex9.contractorservice.exception.OrgFormNotFoundException;
import org.ex9.contractorservice.service.OrgFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("orgform")
@Log4j2
@Tag(name = "OrgForm API", description = "API for managing organizational form reference data in the contractor service")
public class OrgFormController {

    private final OrgFormService orgFormService;

    @Autowired
    public OrgFormController(OrgFormService orgFormService) {
        this.orgFormService = orgFormService;
    }

    @GetMapping("/all")
    @Operation(
            summary = "Get all active organizational forms",
            description = "Retrieves a list of all organizational forms with is_active = true."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the list of active organizational forms",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrgFormResponseDto.class)
                    )
            )
    })
    public ResponseEntity<List<OrgFormResponseDto>> getAllActive() {
        List<OrgFormResponseDto> orgForms = orgFormService.findAll();
        log.debug("Found {} active orgForms", orgForms.size());
        return ResponseEntity.ok(orgForms);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get organizational form by ID",
            description = "Retrieves an organizational form by its unique identifier. Returns only active organizational forms (is_active = true)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the organizational form",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrgFormResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Organizational form not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<OrgFormResponseDto> getById(@PathVariable @NotNull int id) {
        log.debug("Getting orgForm by ID: {}", id);
        return ResponseEntity.ok(orgFormService.findById(id));
    }

    @PutMapping("/save")
    @Operation(
            summary = "Create or update an organizational form",
            description = "Creates a new organizational form or updates an existing one. " +
                    "If the ID is specified, updates the existing organizational form; else, creates a new one."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Organizational form successfully created or updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrgFormResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content
            )
    })
    public ResponseEntity<OrgFormResponseDto> save(@RequestBody OrgFormRequestDto orgFormRequestDto) {
        var savedorgForm = orgFormService.save(orgFormRequestDto);
        log.debug("Successfully saved orgForm: {}:{}", savedorgForm.getId(), savedorgForm.getName());
        return ResponseEntity.ok(savedorgForm);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(
            summary = "Logically delete an organizational form",
            description = "Performs a logical deletion of an organizational form by setting is_active = false for the specified ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Organizational form successfully deleted (marked as inactive)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Organizational form not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Void> delete(@PathVariable @NotNull int id) {
        orgFormService.delete(id);
        log.debug("orgForm deleted: {}", id);
        return ResponseEntity.accepted().build();
    }

    @ExceptionHandler(OrgFormNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    @ApiResponse(
            responseCode = "404",
            description = "Organizational form not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    public ErrorResponse handleOrgFormNotFoundException(OrgFormNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

}
