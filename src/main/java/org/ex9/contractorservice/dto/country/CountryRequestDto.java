package org.ex9.contractorservice.dto.country;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Data Transfer Object for creating or updating a country in the contractor service")
public class CountryRequestDto {

    @NotNull
    @Schema(description = "Unique country identifier", example = "RU", required = true)
    private String id;

    @NotNull
    @Schema(description = "Name of country", example = "Россия", required = true)
    private String name;

}
