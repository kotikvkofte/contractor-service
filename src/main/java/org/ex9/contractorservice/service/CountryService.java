package org.ex9.contractorservice.service;

import org.ex9.contractorservice.dto.country.CountryRequestDto;
import org.ex9.contractorservice.dto.country.CountryResponseDto;
import org.ex9.contractorservice.exception.CountryNotFoundException;
import org.ex9.contractorservice.mapper.CountryMapper;
import org.ex9.contractorservice.model.Country;
import org.ex9.contractorservice.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервис для управления справочником стран.
 * Предоставляет методы для получения, создания, обновления и логического удаления стран.
 * @author Краковцев Артём
 */
@Service
public class CountryService {

    private final CountryRepository countryRepository;

    /**
     * Конструктор сервиса с внедрением зависимости репозитория.
     *
     * @param countryRepository репозиторий для работы с сущностью {@link Country}
     */
    @Autowired
    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    /**
     * Получает список всех активных стран.
     * @return список DTO {@link CountryResponseDto} с данными активных стран
     */
    public List<CountryResponseDto> findAll() {

        var countryList = countryRepository.findAllByIsActiveTrue();
        return countryList.stream().map(CountryMapper::toDto).toList();

    }

    /**
     * Создаёт новую страну или обновляет существующую.
     * Если страна с указанным ID уже существует, выполняется обновление;
     * иначе создаётся новая запись.
     *
     * @param request DTO {@link CountryRequestDto} с данными для создания или обновления
     * @return DTO {@link CountryResponseDto} с данными сохранённой или обновлённой страны
     * @throws CountryNotFoundException если страна не найдена после сохранения
     */
    @Transactional
    public CountryResponseDto save(CountryRequestDto request) {
        var c = CountryMapper.toCountry(request);
        if (countryRepository.existsById(c.getId())) {
            countryRepository.save(c);
        } else {
            countryRepository.insert(c);
        }
        var country = countryRepository.findById(c.getId()).orElseThrow(() -> new CountryNotFoundException("Country not found with id " + c.getId()));
        return CountryMapper.toDto(country);
    }

    /**
     * Выполняет логическое удаление страны по её идентификатору.
     * Устанавливает {@code is_active = false} для указанной страны.
     *
     * @param id уникальный идентификатор страны
     * @throws CountryNotFoundException если страны с указанным ID не существует
     */
    @Transactional
    public void delete(String id) {
        if (countryRepository.existsById(id)) {
            countryRepository.deleteById(id);
        } else {
            throw new CountryNotFoundException("Country not found with id " + id);
        }
    }

    /**
     * Получает страну по её идентификатору.
     *
     * @param id уникальный идентификатор страны
     * @return DTO {@link CountryResponseDto} с данными страны
     * @throws CountryNotFoundException если страны с указанным ID не существует
     */
    public CountryResponseDto findById(String id) {
        var country = countryRepository.findById(id).orElseThrow(() -> new CountryNotFoundException("Country not found with id " + id));
        return CountryMapper.toDto(country);
    }

}
