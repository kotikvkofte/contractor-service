package org.ex9.contractorservice.mapper;

import org.ex9.contractorservice.dto.orgform.OrgFormRequestDto;
import org.ex9.contractorservice.dto.orgform.OrgFormResponseDto;
import org.ex9.contractorservice.model.OrgForm;

public final class OrgFormMapper {

    private OrgFormMapper() {}

    public static OrgFormResponseDto toDto(OrgForm orgForm) {
        OrgFormResponseDto orgFormResponseDto = new OrgFormResponseDto();
        orgFormResponseDto.setId(orgForm.getId());
        orgFormResponseDto.setName(orgForm.getName());
        return orgFormResponseDto;
    }

    public static OrgForm toOrgForm(OrgFormRequestDto orgFormRequestDto) {
        return OrgForm.builder().id(orgFormRequestDto.getId()).name(orgFormRequestDto.getName()).build();
    }

}
