package org.ex9.contractorservice.mapper;

import org.ex9.contractorservice.dto.country.CountryRequestDto;
import org.ex9.contractorservice.dto.country.CountryResponseDto;
import org.ex9.contractorservice.model.Country;

/**
 * Утилитарный класс для преобразования объектов между сущностью {@link Country} и DTO
 * ({@link CountryRequestDto}, {@link CountryResponseDto}).
 * Используется для маппинга данных при работе с API и базой данных.
 * @author Крковцев Артём
 */
public final class CountryMapper {

    /**
     * Приватный конструктор для предотвращения создания экземпляров утилитного класса.
     */
    private CountryMapper() {}

    /**
     * Преобразует сущность {@link Country} в DTO {@link CountryResponseDto} для ответа API.
     *
     * @param country сущность страны из базы данных
     * @return {@link CountryResponseDto} с данными о стране
     */
    public static CountryResponseDto toDto(Country country) {
        CountryResponseDto countryResponseDto = new CountryResponseDto(country.getId(), country.getName());
        return countryResponseDto;
    }

    /**
     * Преобразует DTO {@link CountryRequestDto} в сущность {@link Country}.
     *
     * @param countryRequestDto DTO с данными запроса для создания или обновления страны
     * @return {@link Country} сущность, готовая для дальнейшей работы
     */
    public static Country toCountry(CountryRequestDto countryRequestDto) {
        return Country.builder().id(countryRequestDto.getId()).name(countryRequestDto.getName()).build();
    }

}
