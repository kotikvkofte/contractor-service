package org.ex9.contractorservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.apache.logging.log4j.LogManager;
import org.ex9.contractorservice.dto.orgform.OrgFormRequestDto;
import org.ex9.contractorservice.dto.orgform.OrgFormResponseDto;
import org.ex9.contractorservice.exception.OrgFormNotFoundException;
import org.ex9.contractorservice.service.OrgFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("orgform")
@SuppressWarnings("checkstyle:FileTabCharacter")
@Tag(name = "OrgForm API", description = "API for managing organizational form reference data in the contractor service")
public class OrgFormController {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(OrgFormController.class);
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
    	LOG.info("Getting all active orgForms");
    	List<OrgFormResponseDto> orgForms = orgFormService.findAll();
    	LOG.info("Found {} active orgForms", orgForms.size());
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
    				content = @Content
    		)
    })
    public ResponseEntity<OrgFormResponseDto> getById(@PathVariable @NotNull int id) {
    	LOG.info("Getting orgForm by ID: {}", id);
    	try {
    		return ResponseEntity.ok(orgFormService.findById(id));
    	} catch (OrgFormNotFoundException e) {
    		LOG.warn("orgForm not found with ID: {}", id);
    		return ResponseEntity.notFound().build();
    	}
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
    	LOG.info("Saving orgForm: {}:{}", orgFormRequestDto.getId(), orgFormRequestDto.getName());
    	try {
    		var savedorgForm = orgFormService.save(orgFormRequestDto);
    		LOG.info("Successfully saved orgForm: {}:{}", savedorgForm.getId(), savedorgForm.getName());
    		return ResponseEntity.ok(savedorgForm);
    	} catch (OrgFormNotFoundException e) {
    		LOG.warn("Failed to save orgForm: {}", e.getMessage());
    		return ResponseEntity.notFound().build();
    	}
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
    				content = @Content
    		)
    })
    public ResponseEntity<Void> delete(@PathVariable @NotNull int id) {
    	LOG.info("Deleting orgForm by ID: {}", id);
    	try {
    		orgFormService.delete(id);
    		LOG.info("orgForm deleted: {}", id);
    		return ResponseEntity.accepted().build();
    	} catch (OrgFormNotFoundException e) {
    		LOG.warn("Delete failed - orgForm {} not found", id);
    		return ResponseEntity.notFound().build();
    	}
    }

}
