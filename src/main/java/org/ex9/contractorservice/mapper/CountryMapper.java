package org.ex9.contractorservice.mapper;

import org.ex9.contractorservice.dto.country.CountryRequestDto;
import org.ex9.contractorservice.dto.country.CountryResponseDto;
import org.ex9.contractorservice.model.Country;

public final class CountryMapper {

    private CountryMapper() {}

    public static CountryResponseDto toDto(Country country) {
        CountryResponseDto countryResponseDto = new CountryResponseDto(country.getId(), country.getName());
        return countryResponseDto;
    }

    public static Country toCountry(CountryRequestDto countryRequestDto) {
        return Country.builder().id(countryRequestDto.getId()).name(countryRequestDto.getName()).build();
    }

}
