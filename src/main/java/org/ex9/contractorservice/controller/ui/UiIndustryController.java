package org.ex9.contractorservice.controller.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.ex9.contractorservice.dto.ErrorResponse;
import org.ex9.contractorservice.dto.industry.IndustryRequestDto;
import org.ex9.contractorservice.dto.industry.IndustryResponseDto;
import org.ex9.contractorservice.service.IndustryService;
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
@RequestMapping("ui/industry")
public class UiIndustryController {

    private final IndustryService industryService;

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('USER', 'CREDIT_USER', 'OVERDRAFT_USER', 'DEAL_SUPERUSER', 'CONTRACTOR_RUS', 'CONTRACTOR_SUPERUSER', 'SUPERUSER')")
    @Operation(summary = "Get all active industries (protected)", description = "Retrieves a list of all industries with is_active = true. Requires USER role or higher.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of active industries",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = IndustryResponseDto.class))),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<List<IndustryResponseDto>> getAllActiveIndustries() {
        return ResponseEntity.ok(industryService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'CREDIT_USER', 'OVERDRAFT_USER', 'DEAL_SUPERUSER', 'CONTRACTOR_RUS', 'CONTRACTOR_SUPERUSER', 'SUPERUSER')")
    @Operation(summary = "Get industry by ID (protected)", description = "Retrieves an industry by its unique identifier. Requires USER role or higher.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the industry",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = IndustryResponseDto.class))),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404",
                    description = "Industry not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<IndustryResponseDto> getIndustryById(@PathVariable @NotNull int id) {
        return ResponseEntity.ok(industryService.findById(id));
    }

    @PutMapping("/save")
    @PreAuthorize("hasAnyAuthority('CONTRACTOR_SUPERUSER', 'SUPERUSER')")
    @Operation(summary = "Create or update an industry (protected)", description = "Creates or updates an industry. Requires CONTRACTOR_SUPERUSER and SUPERUSER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Industry successfully created or updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = IndustryResponseDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
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
            )
    })
    public ResponseEntity<IndustryResponseDto> saveIndustry(@RequestBody IndustryRequestDto industryRequestDto) {
        var savedIndustry = industryService.save(industryRequestDto);
        return ResponseEntity.ok(savedIndustry);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('CONTRACTOR_SUPERUSER', 'SUPERUSER')")
    @Operation(summary = "Logically delete an industry (protected)", description = "Logical deletion of an industry by setting is_active = false. " +
            "Requires CONTRACTOR_SUPERUSER and SUPERUSER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Industry successfully deleted (marked as inactive)", content = @Content),
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
                    description = "Industry not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    }
    )
    public ResponseEntity<Void> deleteIndustry(@PathVariable @NotNull int id) {
        industryService.delete(id);
        return ResponseEntity.accepted().build();
    }

}
