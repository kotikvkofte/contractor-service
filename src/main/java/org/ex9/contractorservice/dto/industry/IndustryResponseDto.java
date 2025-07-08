package org.ex9.contractorservice.dto.industry;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Data Transfer Object representing a industry in the contractor service")
public class IndustryResponseDto {

    @Schema(description = "Unique industry identifier", example = "25")
    private Integer id;

    @Schema(description = "Name of industry", example = "Горнодобывающая промышленность")
    private String name;

}
