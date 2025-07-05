package org.ex9.contractorservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.apache.logging.log4j.LogManager;
import org.ex9.contractorservice.dto.country.CountryRequestDto;
import org.ex9.contractorservice.dto.country.CountryResponseDto;
import org.ex9.contractorservice.exception.CountryNotFoundException;
import org.ex9.contractorservice.service.CountryService;
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
@RequestMapping("country")
@Tag(name = "Country API", description = "API for managing country reference data in the contractor service")
public class CountryController {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(CountryController.class);
    private final CountryService countryService;

    @Autowired
    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping("/all")
    @Operation(
            summary = "Get all active countries",
            description = "Retrieves a list of all countries with is_active = true."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the list of active countries",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CountryResponseDto.class)
                    )
            )
    })
    public ResponseEntity<List<CountryResponseDto>> getAllActive() {

        LOG.info("Get all country's list");

        return ResponseEntity.ok(countryService.findAll());

    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get country by ID",
            description = "Returns only active countries (is_active = true)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the country",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CountryResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Country not found",
                    content = @Content
            )
    })
    public ResponseEntity<CountryResponseDto> getById(@PathVariable @NotNull String id) {
        try {
            return ResponseEntity.ok(countryService.findById(id));
        } catch (CountryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/save")
    @Operation(
            summary = "Create or update a country",
            description = "Creates a new country or updates an existing one. " +
                    "If the ID is exist, updates the existing country; else, creates a new one."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Country successfully created or updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CountryResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content
            )
    })
    public ResponseEntity<CountryResponseDto> save(@RequestBody CountryRequestDto countryRequest) {
        return ResponseEntity.ok(countryService.save(countryRequest));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(
            summary = "Logically delete a country",
            description = "Logical deletion of a country by setting is_active = false."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Country successfully deleted (marked as inactive)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Country not found",
                    content = @Content
            )
    })
    public ResponseEntity<Void> delete(@PathVariable @NotNull String id) {
        try {
            countryService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (CountryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
