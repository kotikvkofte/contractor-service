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
import org.ex9.contractorservice.dto.country.CountryRequestDto;
import org.ex9.contractorservice.dto.country.CountryResponseDto;
import org.ex9.contractorservice.exception.CountryNotFoundException;
import org.ex9.contractorservice.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
@RequestMapping("country")
@Tag(name = "Country API", description = "API for managing country reference data in the contractor service")
public class CountryController {

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
        log.debug("Getting all active countries");
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
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<?> getById(@PathVariable @NotNull String id) {
        log.debug("Getting country by ID: {}", id);
        return ResponseEntity.ok(countryService.findById(id));
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
        log.debug("Saving country: {}:{}", countryRequest.getId(), countryRequest.getName());
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
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Void> delete(@PathVariable @NotNull String id) {
        countryService.delete(id);
        log.debug("Country deleted: {}", id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(CountryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    @ApiResponse(
            responseCode = "404",
            description = "Country not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    public ErrorResponse handleCountryNotFoundException(CountryNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

}
