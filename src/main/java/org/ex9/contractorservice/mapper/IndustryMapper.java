package org.ex9.contractorservice.mapper;

import org.ex9.contractorservice.dto.industry.IndustryRequestDto;
import org.ex9.contractorservice.dto.industry.IndustryResponseDto;
import org.ex9.contractorservice.model.Industry;

/**
 * Утилитарный класс для преобразования объектов между сущностью {@link Industry} и DTO
 * ({@link IndustryRequestDto}, {@link IndustryResponseDto}).
 * Используется для маппинга данных при работе с API и базой данных.
 * @author Крковцев Артём
 */
public final class IndustryMapper {

    /**
     * Приватный конструктор для предотвращения создания экземпляров утилитного класса.
     */
    private IndustryMapper() {}

    /**
     * Преобразует сущность {@link Industry} в DTO {@link IndustryResponseDto} для ответа API.
     *
         * @param industry сущность промышленности из базы данных
     * @return {@link IndustryResponseDto} с данными о промышленности
     */
    public static IndustryResponseDto toDto(Industry industry) {
        IndustryResponseDto industryResponseDto = new IndustryResponseDto(industry.getId(), industry.getName());
        return industryResponseDto;
    }

    /**
     * Преобразует DTO {@link IndustryRequestDto} в сущность {@link Industry}.
     *
     * @param industryRequestDto DTO с данными запроса для создания или обновления промышленности
     * @return {@link Industry} сущность, готовая для дальнейшей работы
     */
    public static Industry toIndustry(IndustryRequestDto industryRequestDto) {
        return Industry.builder().id(industryRequestDto.getId()).name(industryRequestDto.getName()).build();
    }

}
