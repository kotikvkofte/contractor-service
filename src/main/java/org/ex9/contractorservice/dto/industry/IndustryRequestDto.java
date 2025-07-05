package org.ex9.contractorservice.dto.industry;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Data Transfer Object for creating or updating a industry in the contractor service")
public class IndustryRequestDto {

    @Schema(description = "Unique industry identifier", example = "25", required = false)
    private Integer id;

    @NotNull
    @Schema(description = "Name of industry", example = "Горнодобывающая промышленность", required = true)
    private String name;

}
