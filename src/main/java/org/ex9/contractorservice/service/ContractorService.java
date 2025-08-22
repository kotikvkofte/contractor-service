package org.ex9.contractorservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.ex9.contractorservice.dao.ContractorJdbcDao;
import org.ex9.contractorservice.dto.contractor.ContractorResponseDto;
import org.ex9.contractorservice.dto.contractor.ContractorRequestDto;
import org.ex9.contractorservice.dto.country.CountryResponseDto;
import org.ex9.contractorservice.dto.contractor.SearchContractorRequestDto;
import org.ex9.contractorservice.dto.rabbit.ContractorDto;
import org.ex9.contractorservice.exception.ContractorNotFoundException;
import org.ex9.contractorservice.exception.CountryNotFoundException;
import org.ex9.contractorservice.exception.IndustryNotFoundException;
import org.ex9.contractorservice.exception.OrgFormNotFoundException;
import org.ex9.contractorservice.mapper.ContractorMapper;
import org.ex9.contractorservice.model.OutboxEvent;
import org.ex9.contractorservice.repository.ContractorRepository;
import org.ex9.contractorservice.repository.CountryRepository;
import org.ex9.contractorservice.repository.IndustryRepository;
import org.ex9.contractorservice.repository.OrgFormRepository;
import org.ex9.contractorservice.service.outbox.OutboxPublisher;
import org.ex9.contractorservice.service.outbox.OutboxService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractorService {

    private final ContractorJdbcDao contractorJdbcDao;
    private final ContractorRepository contractorRepository;
    private final CountryRepository countryRepository;
    private final IndustryRepository industryRepository;
    private final OrgFormRepository orgFormRepository;

    private final OutboxService outboxService;
    private final OutboxPublisher outboxPublisher;
    private final ObjectMapper objectMapper;

    /**
     * Получает контрагента по её идентификатору.
     *
     * @param id уникальный идентификатор контрагента
     * @return DTO {@link CountryResponseDto} с данными контрагента
     * @throws ContractorNotFoundException если контрагента с указанным ID не существует
     */
    public ContractorResponseDto findById(String id) {
        var contractor = contractorJdbcDao.findById(id).orElseThrow(() -> new ContractorNotFoundException("Contractor not found with id " + id));
        return ContractorMapper.toDto(contractor);
    }

    /**
     * Создаёт нового контрагента или обновляет существующего.
     * Если контрагент с указанным ID уже существует, выполняется обновление;
     * иначе создаётся новая запись.
     *
     * @param request DTO {@link ContractorRequestDto} с данными для создания или обновления
     * @return DTO {@link ContractorResponseDto} с данными сохранённой или обновлённой страны
     * @throws ContractorNotFoundException если контрагент не найден после сохранения
     * @throws CountryNotFoundException    если страна не найдена после сохранения
     * @throws IndustryNotFoundException   если производство с указанным идентификатором не существует
     * @throws OrgFormNotFoundException    если организационная форма с указанным ID не существует
     */
    @Transactional
    public ContractorResponseDto save(ContractorRequestDto request) {
        if (request.getParentId() != null) {
            contractorJdbcDao.findById(request.getParentId())
                    .orElseThrow(() -> new ContractorNotFoundException("Parent contractor with ID " + request.getParentId() + " not found"));
        }

        countryRepository.findById(request.getCountryId())
                .orElseThrow(() -> new CountryNotFoundException("Country with ID " + request.getCountryId() + " not found"));

        industryRepository.findById(request.getIndustryId())
                .orElseThrow(() -> new IndustryNotFoundException("Industry with ID " + request.getIndustryId() + " not found"));

        orgFormRepository.findById(request.getOrgFormId())
                .orElseThrow(() -> new OrgFormNotFoundException("OrgForm with ID " + request.getOrgFormId() + " not found"));

        var c = ContractorMapper.toContractor(request);
        if (contractorRepository.existsById(c.getId())) {
            contractorJdbcDao.update(c);
        } else {
            contractorJdbcDao.insert(c);
        }

        var contractor = contractorJdbcDao.findById(c.getId())
                .orElseThrow(() -> new ContractorNotFoundException("Contractor not found with id " + c.getId()));

        OutboxEvent event = outboxService.saveEvent(contractor);
        try {
            ContractorDto dto = objectMapper.readValue(event.getPayload(), ContractorDto.class);
            outboxPublisher.publish(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        outboxService.markAsPublished(event);

        return ContractorMapper.toDto(contractor);
    }

    /**
     * Создаёт нового контрагента или обновляет существующего.
     * Если контрагент с указанным ID уже существует, выполняется обновление;
     * иначе создаётся новая запись.
     *
     * @param request DTO {@link ContractorRequestDto} с данными для создания или обновления
     * @param userId идентификатор авторизированного пользователя
     * @return DTO {@link ContractorResponseDto} с данными сохранённой или обновлённой страны
     * @throws ContractorNotFoundException если контрагент не найден после сохранения
     * @throws CountryNotFoundException    если страна не найдена после сохранения
     * @throws IndustryNotFoundException   если производство с указанным идентификатором не существует
     * @throws OrgFormNotFoundException    если организационная форма с указанным ID не существует
     */
    @Transactional
    public ContractorResponseDto save(ContractorRequestDto request, String userId) {
        if (request.getParentId() != null) {
            contractorJdbcDao.findById(request.getParentId())
                    .orElseThrow(() -> new ContractorNotFoundException("Parent contractor with ID " + request.getParentId() + " not found"));
        }

        countryRepository.findById(request.getCountryId())
                .orElseThrow(() -> new CountryNotFoundException("Country with ID " + request.getCountryId() + " not found"));

        industryRepository.findById(request.getIndustryId())
                .orElseThrow(() -> new IndustryNotFoundException("Industry with ID " + request.getIndustryId() + " not found"));

        orgFormRepository.findById(request.getOrgFormId())
                .orElseThrow(() -> new OrgFormNotFoundException("OrgForm with ID " + request.getOrgFormId() + " not found"));

        var c = ContractorMapper.toContractor(request);
        if (contractorRepository.existsById(c.getId())) {
            c.setModifyUserId(userId);
            contractorJdbcDao.update(c);
        } else {
            c.setCreateUserId(userId);
            contractorJdbcDao.insert(c);
        }

        var contractor = contractorJdbcDao.findById(c.getId()).
                orElseThrow(() -> new ContractorNotFoundException("Contractor not found with id " + c.getId()));

        OutboxEvent event = outboxService.saveEvent(contractor);
        try {
            ContractorDto dto = objectMapper.readValue(event.getPayload(), ContractorDto.class);
            outboxPublisher.publish(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        outboxService.markAsPublished(event);

        return ContractorMapper.toDto(contractor);
    }

    /**
     * Выполняет поиск активных контрагентов с фильтрацией и пагинацией.
     *
     * @param request DTO {@link SearchContractorRequestDto} с параметрами фильтрации и пагинации
     * @return {@link Page} с DTO {@link ContractorResponseDto} для найденных контрагентов
     */
    @Transactional(readOnly = true)
    public List<ContractorResponseDto> search(SearchContractorRequestDto request) {
        var result = contractorJdbcDao.search(request);
        return result.stream().map(ContractorMapper::toDto).toList();
    }

    /**
     * Выполняет логическое удаление контрагента по его идентификатору.
     * Устанавливает {@code is_active = false} для указанного контрагента.
     *
     * @param id уникальный идентификатор контрагента
     * @throws ContractorNotFoundException если контрагент с указанным ID не существует
     */
    @Transactional
    public void delete(String id) {
        if (contractorRepository.existsById(id)) {
            contractorRepository.deleteById(id);
        } else {
            throw new ContractorNotFoundException("Contractor not found with id " + id);
        }
    }

}
