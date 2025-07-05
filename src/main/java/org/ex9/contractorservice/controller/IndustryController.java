package org.ex9.contractorservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.ex9.contractorservice.dto.industry.IndustryRequestDto;
import org.ex9.contractorservice.dto.industry.IndustryResponseDto;
import org.ex9.contractorservice.exception.IndustryNotFoundException;
import org.ex9.contractorservice.service.IndustryService;
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
@RequestMapping("industry")
@Tag(name = "Industry API", description = "API for managing industry reference data in the contractor service")
public class IndustryController {

    private final IndustryService industryService;

    @Autowired
    public IndustryController(IndustryService industryService) {
        this.industryService = industryService;
    }

    @GetMapping("/all")
    @Operation(
            summary = "Get all active industries",
            description = "Retrieves a list of all industries with is_active = true."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the list of active industries",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = IndustryResponseDto.class)
                    )
            )
    })
    public ResponseEntity<List<IndustryResponseDto>> getAllActive() {
        return ResponseEntity.ok(industryService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get industry by ID",
            description = "Retrieves an industry by its unique identifier. Returns only active industries (is_active = true)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the industry",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = IndustryResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Industry not found",
                    content = @Content
            )
    })
    public ResponseEntity<IndustryResponseDto> getById(@PathVariable @NotNull int id) {
        try {
            return ResponseEntity.ok(industryService.findById(id));
        } catch (IndustryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/save")
    @Operation(
            summary = "Create or update an industry",
            description = "Creates a new industry or updates an existing one. " +
                    "If the ID is specified, updates the existing industry; else, creates a new one."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Industry successfully created or updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = IndustryResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content
            )
    })
    public ResponseEntity<IndustryResponseDto> save(@RequestBody IndustryRequestDto industryRequestDto) {
        try {
            return ResponseEntity.ok(industryService.save(industryRequestDto));
        } catch (IndustryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(
            summary = "Logically delete an industry",
            description = "Logical deletion of an industry by setting is_active = false."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Industry successfully deleted (marked as inactive)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Industry not found",
                    content = @Content
            )
    })
    public ResponseEntity<Void> delete(@PathVariable @NotNull int id) {
        try {
            industryService.delete(id);
            return ResponseEntity.accepted().build();
        } catch (IndustryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
