package org.ex9.contractorservice.dto.industry;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class IndustryRequestDto {

    private Integer id;

    @NotNull
    private String name;

}
