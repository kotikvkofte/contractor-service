package org.ex9.contractorservice.dto.country;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Data Transfer Object representing a country in the contractor service")
public class CountryResponseDto {

    @Schema(description = "Unique country identifier", example = "RU")
    private String id;

    @Schema(description = "Name of country", example = "Россия")
    private String name;

}
