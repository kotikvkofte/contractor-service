package org.ex9.contractorservice.dto.orgform;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data Transfer Object for creating or updating a organizational form  in the contractor service")
public class OrgFormRequestDto {

    @Schema(description = "Unique organizational form identifier", example = "29", required = false)
    private Integer id;

    @NotNull
    @Schema(description = "Name of organizational form", example = "Бюджетное учреждение", required = true)
    private String name;

}
