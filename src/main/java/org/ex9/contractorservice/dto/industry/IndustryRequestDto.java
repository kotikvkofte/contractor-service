package org.ex9.contractorservice.dto.industry;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IndustryRequestDto {

    private Integer id;

    @NotNull
    private String name;

}
