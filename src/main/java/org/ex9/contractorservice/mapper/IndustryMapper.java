package org.ex9.contractorservice.mapper;

import org.ex9.contractorservice.dto.industry.IndustryRequestDto;
import org.ex9.contractorservice.dto.industry.IndustryResponseDto;
import org.ex9.contractorservice.model.Industry;

public final class IndustryMapper {

    private IndustryMapper() {}

    public static IndustryResponseDto toDto(Industry industry) {
        IndustryResponseDto industryResponseDto = new IndustryResponseDto(industry.getId(), industry.getName());
        return industryResponseDto;
    }

    public static Industry toIndustry(IndustryRequestDto industryRequestDto) {
        return Industry.builder().id(industryRequestDto.getId()).name(industryRequestDto.getName()).build();
    }

}
