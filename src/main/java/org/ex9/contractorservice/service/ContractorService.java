package org.ex9.contractorservice.service;

import org.ex9.contractorservice.dao.ContractorJdbcDao;
import org.ex9.contractorservice.dto.contractor.ContractorResponseDto;
import org.ex9.contractorservice.dto.contractor.ContractorRequestDto;
import org.ex9.contractorservice.dto.country.CountryResponseDto;
import org.ex9.contractorservice.dto.contractor.SearchContractorRequestDto;
import org.ex9.contractorservice.exception.ContractorNotFoundException;
import org.ex9.contractorservice.exception.CountryNotFoundException;
import org.ex9.contractorservice.exception.IndustryNotFoundException;
import org.ex9.contractorservice.exception.OrgFormNotFoundException;
import org.ex9.contractorservice.mapper.ContractorMapper;
import org.ex9.contractorservice.repository.ContractorRepository;
import org.ex9.contractorservice.repository.CountryRepository;
import org.ex9.contractorservice.repository.IndustryRepository;
import org.ex9.contractorservice.repository.OrgFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContractorService {

	private ContractorJdbcDao contractorJdbcDao;
	private ContractorRepository contractorRepository;
	private CountryRepository countryRepository;
	private IndustryRepository industryRepository;
	private OrgFormRepository orgFormRepository;

	@Autowired
	public ContractorService(ContractorJdbcDao contractorJdbcDao,
							 ContractorRepository contractorRepository,
							 CountryRepository countryRepository,
							 IndustryRepository industryRepository,
							 OrgFormRepository orgFormRepository) {
		this.contractorJdbcDao = contractorJdbcDao;
		this.contractorRepository = contractorRepository;
		this.countryRepository = countryRepository;
		this.industryRepository = industryRepository;
		this.orgFormRepository = orgFormRepository;
	}

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
			var parent = contractorJdbcDao.findById(request.getParentId());
			if (parent.isEmpty() || !parent.get().getIsActive()) {
				throw new ContractorNotFoundException("Parent contractor with ID " + request.getParentId() + " not found");
			}
		}
		var country = countryRepository.findById(request.getCountryId());
		if (country.isEmpty() || !country.get().getIsActive()) {
			throw new CountryNotFoundException("Country with ID " + request.getCountryId() + " not found");
		}
		var industry = industryRepository.findById(request.getIndustryId());
		if (industry.isEmpty() || !industry.get().getIsActive()) {
			throw new IndustryNotFoundException("Industry with ID " + request.getIndustryId() + " not found");
		}
		var orgForm = orgFormRepository.findById(request.getOrgFormId());
		if (orgForm.isEmpty() || !orgForm.get().getIsActive()) {
			throw new OrgFormNotFoundException("OrgForm with ID " + request.getOrgFormId() + " not found");
		}

		var c = ContractorMapper.toContractor(request);
		if (contractorRepository.existsById(c.getId())) {
			contractorJdbcDao.update(c);
		} else {
			contractorJdbcDao.insert(c);
		}

		var contractor = contractorJdbcDao.findById(c.getId()).orElseThrow(() -> new ContractorNotFoundException("Contractor not found with id " + c.getId()));
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
	public void delete(String id) {
		if (contractorRepository.existsById(id)) {
			contractorRepository.deleteById(id);
		} else {
			throw new ContractorNotFoundException("Contractor not found with id " + id);
		}
	}

}
