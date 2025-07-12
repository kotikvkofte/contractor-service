package org.ex9.contractorservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.ex9.contractorservice.dto.ErrorResponse;
import org.ex9.contractorservice.dto.contractor.ContractorRequestDto;
import org.ex9.contractorservice.dto.contractor.ContractorResponseDto;
import org.ex9.contractorservice.dto.contractor.SearchContractorRequestDto;
import org.ex9.contractorservice.service.ContractorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("contractor")
@Tag(name = "Contractor API", description = "API for managing contractor data in the contractor service")
public class ContractorController {

    private ContractorService contractorService;

    @Autowired
    public ContractorController(ContractorService contractorService) {
        this.contractorService = contractorService;
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get contractor by ID",
            description = "Returns only active contractor (is_active = true)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the contractor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ContractorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "contractor not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<ContractorResponseDto> getById(@PathVariable @NotNull String id) {
        log.debug("Getting contractor by ID: {}", id);
        return ResponseEntity.ok(contractorService.findById(id));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(
            summary = "Logically delete a contractor",
            description = "Logical deletion of a contractor by setting is_active = false."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Contractor successfully deleted (marked as inactive)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Contractor not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Void> delete(@PathVariable @NotNull String id) {
        contractorService.delete(id);
        log.debug("Contractor deleted: {}", id);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/save")
    @Operation(
            summary = "Create or update a contractor",
            description = "Creates a new contractor or updates an existing one. " +
                    "If the ID is exist, updates the existing contractor; else, creates a new one."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Contractor successfully created or updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ContractorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<ContractorResponseDto> save(@RequestBody @NotNull ContractorRequestDto request) {
        var resp = contractorService.save(request);
        log.debug("Contractor saved: {}, {}", resp.getId(), resp.getName());
        return ResponseEntity.ok(resp);
    }

    @Operation(
            summary = "Search contractors",
            description = "Returns a paginated list of active contractors with filtering")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of contractors",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ContractorResponseDto.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            )
    })
    @PostMapping("/search")
    public ResponseEntity<List<ContractorResponseDto>> search(@Valid @RequestBody SearchContractorRequestDto request) {
        return ResponseEntity.ok(contractorService.search(request));
    }

}
