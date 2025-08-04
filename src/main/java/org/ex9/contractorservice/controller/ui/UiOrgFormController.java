package org.ex9.contractorservice.controller.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.ex9.contractorservice.dto.ErrorResponse;
import org.ex9.contractorservice.dto.orgform.OrgFormRequestDto;
import org.ex9.contractorservice.dto.orgform.OrgFormResponseDto;
import org.ex9.contractorservice.service.OrgFormService;
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
@RequestMapping("ui/orgform")
public class UiOrgFormController {

    private final OrgFormService orgFormService;

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('USER', 'CREDIT_USER', 'OVERDRAFT_USER', 'DEAL_SUPERUSER', 'CONTRACTOR_RUS', 'CONTRACTOR_SUPERUSER', 'SUPERUSER')")
    @Operation(summary = "Get all active organizational forms (protected)",
            description = "Retrieves a list of all organizational forms with is_active = true. Requires USER role or higher.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved the list of active organizational forms",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrgFormResponseDto.class))),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<List<OrgFormResponseDto>> getAllActiveOrgForms() {
        return ResponseEntity.ok(orgFormService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER', 'CREDIT_USER', 'OVERDRAFT_USER', 'DEAL_SUPERUSER', 'CONTRACTOR_RUS', 'CONTRACTOR_SUPERUSER', 'SUPERUSER')")
    @Operation(summary = "Get organizational form by ID (protected)",
            description = "Retrieves an organizational form by its unique identifier. Requires USER role or higher.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the organizational form",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrgFormResponseDto.class))),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404",
                    description = "Organizational form not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<OrgFormResponseDto> getOrgFormById(@PathVariable @NotNull int id) {
        return ResponseEntity.ok(orgFormService.findById(id));
    }

    @PutMapping("/save")
    @PreAuthorize("hasAnyAuthority('CONTRACTOR_SUPERUSER', 'SUPERUSER')")
    @Operation(summary = "Create or update an organizational form (protected)",
            description = "Creates or updates an organizational form. Requires CONTRACTOR_SUPERUSER or SUPERUSER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Organizational form successfully created or updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrgFormResponseDto.class))),
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
    }
    )
    public ResponseEntity<OrgFormResponseDto> saveOrgForm(@RequestBody OrgFormRequestDto orgFormRequestDto) {
        return ResponseEntity.ok(orgFormService.save(orgFormRequestDto));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('CONTRACTOR_SUPERUSER', 'SUPERUSER')")
    @Operation(summary = "Logically delete an organizational form (protected)",
            description = "Logical deletion of an organizational form by setting is_active = false. Requires CONTRACTOR_SUPERUSER or SUPERUSER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Organizational form successfully deleted (marked as inactive)"),
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
                    description = "Organizational form not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    public ResponseEntity<Void> deleteOrgForm(@PathVariable @NotNull int id) {
        orgFormService.delete(id);
        return ResponseEntity.accepted().build();
    }

}
