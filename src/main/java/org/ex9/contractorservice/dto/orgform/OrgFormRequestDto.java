package org.ex9.contractorservice.dto.orgform;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class OrgFormRequestDto {
    Integer id;
    @NotNull
    String name;
}
