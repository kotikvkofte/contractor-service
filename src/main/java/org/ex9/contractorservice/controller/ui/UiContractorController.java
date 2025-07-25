package org.ex9.contractorservice.controller.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.ex9.contractorservice.dto.ErrorResponse;
import org.ex9.contractorservice.dto.contractor.ContractorRequestDto;
import org.ex9.contractorservice.dto.contractor.ContractorResponseDto;
import org.ex9.contractorservice.dto.contractor.SearchContractorRequestDto;
import org.ex9.contractorservice.service.ContractorService;
import org.ex9.contractorservice.utils.AuthInfo;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("ui/contractor")
public class UiContractorController {

    private final ContractorService contractorService;

    @GetMapping("/contractor/{id}")
    @PreAuthorize("hasAnyAuthority('CONTRACTOR_SUPERUSER', 'SUPERUSER')")
    @Operation(summary = "Get contractor by ID (protected)",
            description = "Returns only active contractor (is_active = true). Requires CONTRACTOR_SUPERUSER or SUPERUSER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved the contractor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ContractorResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404",
                    description = "Contractor not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<ContractorResponseDto> getContractorById(@PathVariable @NotNull String id) {
        return ResponseEntity.ok(contractorService.findById(id));
    }

    @DeleteMapping("/contractor/delete/{id}")
    @PreAuthorize("hasAnyAuthority('CONTRACTOR_SUPERUSER', 'SUPERUSER')")
    @Operation(summary = "Logically delete a contractor (protected)",
            description = "Logical deletion of a contractor by setting is_active = false. Requires CONTRACTOR_SUPERUSER or SUPERUSER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Contractor successfully deleted (marked as inactive)"),
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
            ),
            @ApiResponse(responseCode = "404",
                    description = "Contractor not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Void> deleteContractor(@PathVariable @NotNull String id) {
        contractorService.delete(id);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/contractor/save")
    @PreAuthorize("hasAnyAuthority('CONTRACTOR_SUPERUSER', 'SUPERUSER')")
    @Operation(summary = "Create or update a contractor (protected)",
            description = "Creates or updates a contractor. Requires CONTRACTOR_SUPERUSER or SUPERUSER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Contractor successfully created or updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ContractorResponseDto.class)
                    )
            ),
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
            ),
            @ApiResponse(responseCode = "404",
                    description = "Not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<ContractorResponseDto> saveContractor(@RequestBody @NotNull ContractorRequestDto request) {
        String userId = AuthInfo.getUsername();
        return ResponseEntity.ok(contractorService.save(request, userId));
    }

    @PostMapping("/contractor/search")
    @PreAuthorize("hasAnyAuthority('CONTRACTOR_RUS', 'CONTRACTOR_SUPERUSER', 'SUPERUSER')")
    @Operation(summary = "Search contractors (protected)",
            description = "Returns a paginated list of active contractors with filtering. " +
                    "Requires CONTRACTOR_SUPERUSER or SUPERUSER, and CONTRACTOR_RUS role only with country - RUS filter.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of contractors",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContractorResponseDto.class))),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<List<ContractorResponseDto>> searchContractors(@Valid @RequestBody SearchContractorRequestDto request) {
        var roles = AuthInfo.getRoles();

        var result = contractorService.search(request);

        boolean isRus = result.stream().allMatch(response ->
                        response.getCountry().equalsIgnoreCase("Российская Федерация"));

        boolean isContractorRusRole = roles.size() == 1 && roles.getFirst().equals("CONTRACTOR_RUS");

        if (isContractorRusRole && !isRus) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return ResponseEntity.ok(contractorService.search(request));
    }

}
