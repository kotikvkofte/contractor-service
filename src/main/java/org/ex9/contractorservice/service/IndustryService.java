package org.ex9.contractorservice.service;

import org.ex9.contractorservice.dto.industry.IndustryRequestDto;
import org.ex9.contractorservice.dto.industry.IndustryResponseDto;
import org.ex9.contractorservice.exception.IndustryNotFoundException;
import org.ex9.contractorservice.mapper.IndustryMapper;
import org.ex9.contractorservice.model.Industry;
import org.ex9.contractorservice.repository.IndustryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервис для управления справочником производств.
 * Предоставляет методы для получения, создания, обновления и логического удаления производств.
 * @author Краковцев Артём
 */
@Service
public class IndustryService {

    private final IndustryRepository repository;

    /**
     * Конструктор сервиса с внедрением зависимости репозитория.
     *
     * @param industryRepository репозиторий для работы с сущностью {@link Industry}
     */
    @Autowired
    public IndustryService(IndustryRepository industryRepository) {
        this.repository = industryRepository;
    }

    /**
     * Получает список всех активных производств.
     *
     * @return список DTO {@link IndustryResponseDto} с данными активных производств
     */
    public List<IndustryResponseDto> findAll() {
        var industryList = repository.findAllByIsActiveTrue();
        return industryList.stream().map(IndustryMapper::toDto).toList();
    }

    /**
     * Получает производство по её идентификатору.
     *
     * @param id уникальный идентификатор производства
     * @return DTO {@link IndustryResponseDto} с данными производства
     * @throws IndustryNotFoundException если производство с указанным идентификатором не существует
     */
    public IndustryResponseDto findById(int id) {
        var industry = repository.findById(id).orElseThrow(() -> new IndustryNotFoundException("Industry with id " + id + " not found"));
        return IndustryMapper.toDto(industry);
    }

    /**
     * Создаёт новое производство или обновляет существующее.
     * Если указан ID и производство с таким ID не существует, выбрасывается исключение.
     *
     * @param request DTO {@link IndustryRequestDto} с данными для создания или обновления
     * @return DTO {@link IndustryResponseDto} с данными сохранённого или обновлённого производства
     * @throws IndustryNotFoundException если указан ID, но производство не найдено
     */
    @Transactional
    public IndustryResponseDto save(IndustryRequestDto request) {
        Industry industry = IndustryMapper.toIndustry(request);
        if (industry.getId() != null && !repository.existsById(industry.getId())) {
            throw new IndustryNotFoundException("Industry with id " + industry.getId() + " not found");
        }
        var newOrUpdatedIndustry = repository.save(industry);
        return IndustryMapper.toDto(newOrUpdatedIndustry);
    }

    /**
     * Выполняет логическое удаление производства по его идентификатору.
     * Устанавливает {@code is_active = false} для указанного производства.
     *
     * @param id уникальный идентификатор производства
     * @throws IndustryNotFoundException если производство с указанным ID не существует
     */
    @Transactional
    public void delete(int id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new IndustryNotFoundException("Industry with id " + id + " not found");
        }
    }

}
