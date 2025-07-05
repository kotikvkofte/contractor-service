package org.ex9.contractorservice.dto.orgform;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Data Transfer Object representing a organizational form  in the contractor service")
public class OrgFormResponseDto {

    @Schema(description = "Unique organizational form identifier", example = "29")
    private Integer id;

    @Schema(description = "Name of organizational form", example = "Бюджетное учреждение")
    private String name;

}
