package org.ex9.contractorservice.service;

import org.apache.logging.log4j.LogManager;
import org.ex9.contractorservice.dto.orgform.OrgFormRequestDto;
import org.ex9.contractorservice.dto.orgform.OrgFormResponseDto;
import org.ex9.contractorservice.exception.OrgFormNotFoundException;
import org.ex9.contractorservice.mapper.OrgFormMapper;
import org.ex9.contractorservice.model.OrgForm;
import org.ex9.contractorservice.repository.OrgFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для управления справочником организационных форм.
 * Предоставляет методы для получения, создания, обновления и логического удаления организационных форм.
 * @author Краковцев Артём
 */
@Service
public class OrgFormService {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(OrgFormService.class);
    private final OrgFormRepository repository;

    /**
     * Конструктор сервиса с внедрением зависимости репозитория.
     *
     * @param repository репозиторий для работы с сущностью {@link OrgForm}
     */
    @Autowired
    public OrgFormService(OrgFormRepository repository) {

        this.repository = repository;

    }

    /**
     * Получает список всех активных организационных форм.
     * @return список DTO {@link OrgFormResponseDto} с данными активных организационных форм
     */
    public List<OrgFormResponseDto> findAll() {

        var orgForms = repository.findAllByIsActiveTrue();

        return orgForms.stream().map(OrgFormMapper::toDto).toList();
    }

    /**
     * Получает организационную форму по её идентификатору.
     *
     * @param id уникальный идентификатор организационной формы
     * @return DTO {@link OrgFormResponseDto} с данными организационной формы
     * @throws OrgFormNotFoundException если организационная форма с указанным ID не существует
     */
    public OrgFormResponseDto findById(int id) {
        var orgForm = repository.findById(id).orElseThrow(() -> new OrgFormNotFoundException("OrgForm with id " + id + " not found"));
        return OrgFormMapper.toDto(orgForm);
    }

    /**
     * Создаёт новую организационную форму или обновляет существующую.
     * Если указан ID и организационная форма с таким ID не существует, выбрасывается исключение.
     *
     * @param request DTO {@link OrgFormRequestDto} с данными для создания или обновления
     * @return DTO {@link OrgFormResponseDto} с данными сохранённой или обновлённой организационной формы
     * @throws OrgFormNotFoundException если указан ID, но организационная форма не найдена
     */
    public OrgFormResponseDto save(OrgFormRequestDto request) {
        OrgForm orgForm = OrgFormMapper.toOrgForm(request);

        if (orgForm.getId() != null && !repository.existsById(orgForm.getId())) {
            throw new OrgFormNotFoundException("OrgForm with id " + orgForm.getId() + " not found");
        }
        var newOrUpdatedOrgForm = repository.save(orgForm);

        return OrgFormMapper.toDto(newOrUpdatedOrgForm);
    }

    /**
     * Выполняет логическое удаление организационной формы по её идентификатору.
     * Устанавливает {@code is_active = false} для указанной организационной формы.
     *
     * @param id уникальный идентификатор организационной формы
     * @throws OrgFormNotFoundException если организационная форма с указанным ID не существует
     */
    public void delete(int id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new OrgFormNotFoundException("OrgForm with id " + id + " not found");
        }
    }

}
