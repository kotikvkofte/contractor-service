package org.ex9.contractorservice.controller.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.ex9.contractorservice.dto.ErrorResponse;
import org.ex9.contractorservice.dto.country.CountryRequestDto;
import org.ex9.contractorservice.dto.country.CountryResponseDto;
import org.ex9.contractorservice.service.CountryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("ui/country")
public class UiCountryController {

    private final CountryService countryService;

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('USER', 'CREDIT_USER', 'OVERDRAFT_USER', 'DEAL_SUPERUSER', 'CONTRACTOR_RUS', 'CONTRACTOR_SUPERUSER', 'SUPERUSER')")
    @Operation(summary = "Get all active countries (protected)", description = "Retrieves a list of all countries with is_active = true. Requires USER role or higher.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of active countries",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CountryResponseDto.class))),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<List<CountryResponseDto>> getAllActiveCountries() {
        return ResponseEntity.ok(countryService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'CREDIT_USER', 'OVERDRAFT_USER', 'DEAL_SUPERUSER', 'CONTRACTOR_RUS', 'CONTRACTOR_SUPERUSER', 'SUPERUSER')")
    @Operation(summary = "Get country by ID (protected)", description = "Returns only active countries (is_active = true). Requires USER role or higher.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the country",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CountryResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Country not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<CountryResponseDto> getCountryById(@PathVariable @NotNull String id) {
        return ResponseEntity.ok(countryService.findById(id));
    }

    @PutMapping("/save")
    @PreAuthorize("hasAnyAuthority('CONTRACTOR_SUPERUSER', 'SUPERUSER')")
    @Operation(summary = "Create or update a country (protected)", description = "Creates or updates a country. Requires CONTRACTOR_SUPERUSER or SUPERUSER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Country successfully created or updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CountryResponseDto.class))),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<CountryResponseDto> saveCountry(@RequestBody CountryRequestDto countryRequest) {
        return ResponseEntity.ok(countryService.save(countryRequest));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('CONTRACTOR_SUPERUSER', 'SUPERUSER')")
        @Operation(summary = "Logically delete a country (protected)", description = "Logical deletion of a country by setting is_active = false. " +
                "Requires CONTRACTOR_SUPERUSER or SUPERUSER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Country successfully deleted (marked as inactive)", content = @Content),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404",
                    description = "Country not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Void> deleteCountry(@PathVariable @NotNull String id) {
        countryService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
