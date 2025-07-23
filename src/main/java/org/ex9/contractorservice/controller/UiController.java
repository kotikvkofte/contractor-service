package org.ex9.contractorservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.ex9.contractorservice.dto.ErrorResponse;
import org.ex9.contractorservice.dto.contractor.ContractorRequestDto;
import org.ex9.contractorservice.dto.contractor.ContractorResponseDto;
import org.ex9.contractorservice.dto.contractor.SearchContractorRequestDto;
import org.ex9.contractorservice.dto.country.CountryRequestDto;
import org.ex9.contractorservice.dto.country.CountryResponseDto;
import org.ex9.contractorservice.dto.industry.IndustryRequestDto;
import org.ex9.contractorservice.dto.industry.IndustryResponseDto;
import org.ex9.contractorservice.dto.orgform.OrgFormRequestDto;
import org.ex9.contractorservice.dto.orgform.OrgFormResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/ui")
public class UiController {

    private final CountryController countryController;
    private final IndustryController industryController;
    private final OrgFormController orgFormController;
    private final ContractorController contractorController;

    @GetMapping("/country/all")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all active countries (protected)", description = "Retrieves a list of all countries with is_active = true. Requires authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of active countries",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CountryResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<List<CountryResponseDto>> getAllActiveCountries() {
        log.info("Get all active countries (protected)");
        return countryController.getAllActive();
    }

    @GetMapping("/country/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get country by ID (protected)", description = "Returns only active countries (is_active = true). Requires authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the country",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CountryResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Country not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CountryResponseDto> getCountryById(@PathVariable @NotNull String id) {
        return countryController.getById(id);
    }

    @PutMapping("/country/save")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create or update a country (protected)", description = "Creates or updates a country. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Country successfully created or updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CountryResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    public ResponseEntity<CountryResponseDto> saveCountry(@RequestBody CountryRequestDto countryRequest) {
        return countryController.save(countryRequest);
    }

    @DeleteMapping("/country/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Logically delete a country (protected)", description = "Logical deletion of a country by setting is_active = false. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Country successfully deleted (marked as inactive)", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Country not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteCountry(@PathVariable @NotNull String id) {
        return countryController.delete(id);
    }

    @GetMapping("/industry/all")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all active industries (protected)", description = "Retrieves a list of all industries with is_active = true. Requires authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of active industries",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = IndustryResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<List<IndustryResponseDto>> getAllActiveIndustries() {
        return industryController.getAllActive();
    }

    @GetMapping("/industry/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get industry by ID (protected)", description = "Retrieves an industry by its unique identifier. Requires authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the industry",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = IndustryResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Industry not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<IndustryResponseDto> getIndustryById(@PathVariable @NotNull int id) {
        return industryController.getById(id);
    }

    @PutMapping("/industry/save")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create or update an industry (protected)", description = "Creates or updates an industry. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Industry successfully created or updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = IndustryResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
    public ResponseEntity<IndustryResponseDto> saveIndustry(@RequestBody IndustryRequestDto industryRequestDto) {
        return industryController.save(industryRequestDto);
    }

    @DeleteMapping("/industry/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Logically delete an industry (protected)", description = "Logical deletion of an industry by setting is_active = false. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Industry successfully deleted (marked as inactive)", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Industry not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    }
    )
    public ResponseEntity<Void> deleteIndustry(@PathVariable @NotNull int id) {
        return industryController.delete(id);
    }

    @GetMapping("/orgform/all")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all active organizational forms (protected)",
            description = "Retrieves a list of all organizational forms with is_active = true. Requires authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved the list of active organizational forms",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrgFormResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<List<OrgFormResponseDto>> getAllActiveOrgForms() {
        return orgFormController.getAllActive();
    }

    @GetMapping("/orgform/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get organizational form by ID (protected)",
            description = "Retrieves an organizational form by its unique identifier. Requires authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the organizational form",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrgFormResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Organizational form not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<OrgFormResponseDto> getOrgFormById(@PathVariable @NotNull int id) {
        return orgFormController.getById(id);
    }

    @PutMapping("/orgform/save")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create or update an organizational form (protected)",
            description = "Creates or updates an organizational form. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Organizational form successfully created or updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrgFormResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    }
    )
    public ResponseEntity<OrgFormResponseDto> saveOrgForm(@RequestBody OrgFormRequestDto orgFormRequestDto) {
        return orgFormController.save(orgFormRequestDto);
    }

    @DeleteMapping("/orgform/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Logically delete an organizational form (protected)",
            description = "Logical deletion of an organizational form by setting is_active = false. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Organizational form successfully deleted (marked as inactive)", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Organizational form not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteOrgForm(@PathVariable @NotNull int id) {
        return orgFormController.delete(id);
    }

    // Contractor UI Endpoints
    @GetMapping("/contractor/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get contractor by ID (protected)",
            description = "Returns only active contractor (is_active = true). Requires authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the contractor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContractorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Contractor not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ContractorResponseDto> getContractorById(@PathVariable @NotNull String id) {
        return contractorController.getById(id);
    }

    @DeleteMapping("/contractor/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Logically delete a contractor (protected)",
            description = "Logical deletion of a contractor by setting is_active = false. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Contractor successfully deleted (marked as inactive)", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Contractor not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteContractor(@PathVariable @NotNull String id) {
        return contractorController.delete(id);
    }

    @PutMapping("/contractor/save")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create or update a contractor (protected)",
            description = "Creates or updates a contractor. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contractor successfully created or updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContractorResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ContractorResponseDto> saveContractor(@RequestBody @NotNull ContractorRequestDto request) {
        return contractorController.save(request);
    }

    @PostMapping("/contractor/search")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Search contractors (protected)",
            description = "Returns a paginated list of active contractors with filtering. Requires authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of contractors",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContractorResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<List<ContractorResponseDto>> searchContractors(@Valid @RequestBody SearchContractorRequestDto request) {
        return contractorController.search(request);
    }

}
