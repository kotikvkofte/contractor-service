package org.ex9.contractorservice.mapper;

import org.ex9.contractorservice.dto.industry.IndustryRequestDto;
import org.ex9.contractorservice.dto.orgform.OrgFormRequestDto;
import org.ex9.contractorservice.dto.orgform.OrgFormResponseDto;
import org.ex9.contractorservice.model.Industry;
import org.ex9.contractorservice.model.OrgForm;

/**
 * Утилитарный класс для преобразования объектов между сущностью {@link OrgForm} и DTO
 * ({@link OrgFormRequestDto}, {@link OrgFormResponseDto}).
 * Используется для маппинга данных при работе с API и базой данных.
 * @author Крковцев Артём
 */
public final class OrgFormMapper {

    /**
     * Приватный конструктор для предотвращения создания экземпляров утилитного класса.
     */
    private OrgFormMapper() {}

    /**
     * Преобразует сущность {@link OrgForm} в DTO {@link OrgFormResponseDto} для ответа API.
     *
     * @param orgForm сущность организационной формы из базы данных
     * @return {@link OrgFormResponseDto} с данными об организационной форме
     */
    public static OrgFormResponseDto toDto(OrgForm orgForm) {
        OrgFormResponseDto orgFormResponseDto = new OrgFormResponseDto();
        orgFormResponseDto.setId(orgForm.getId());
        orgFormResponseDto.setName(orgForm.getName());
        return orgFormResponseDto;
    }

    /**
     * Преобразует DTO {@link OrgFormRequestDto} в сущность {@link OrgForm}.
     *
     * @param orgFormRequestDto DTO с данными запроса для создания или обновления организационной формы
     * @return {@link OrgForm} сущность, готовая для дальнейшей работы
     */
    public static OrgForm toOrgForm(OrgFormRequestDto orgFormRequestDto) {
        return OrgForm.builder().id(orgFormRequestDto.getId()).name(orgFormRequestDto.getName()).build();
    }

}
