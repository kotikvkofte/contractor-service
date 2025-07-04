package org.ex9.contractorservice.dto.orgform;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class OrgFormRequestDto {

    private Integer id;

    @NotNull
    private String name;

}
