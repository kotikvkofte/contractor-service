package org.ex9.contractorservice.dto.country;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CountryRequestDto {
    @NotNull
    String id;
    @NotNull
    String name;
}
