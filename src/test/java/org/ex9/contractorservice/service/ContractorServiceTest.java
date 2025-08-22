package org.ex9.contractorservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ex9.contractorservice.dao.ContractorJdbcDao;
import org.ex9.contractorservice.dto.contractor.ContractorRequestDto;
import org.ex9.contractorservice.dto.contractor.ContractorResponseDto;
import org.ex9.contractorservice.dto.contractor.SearchContractorRequestDto;
import org.ex9.contractorservice.dto.rabbit.ContractorDto;
import org.ex9.contractorservice.enums.EventType;
import org.ex9.contractorservice.exception.ContractorNotFoundException;
import org.ex9.contractorservice.exception.CountryNotFoundException;
import org.ex9.contractorservice.exception.IndustryNotFoundException;
import org.ex9.contractorservice.exception.OrgFormNotFoundException;
import org.ex9.contractorservice.mapper.ContractorMapper;
import org.ex9.contractorservice.model.*;
import org.ex9.contractorservice.repository.ContractorRepository;
import org.ex9.contractorservice.repository.CountryRepository;
import org.ex9.contractorservice.repository.IndustryRepository;
import org.ex9.contractorservice.repository.OrgFormRepository;
import org.ex9.contractorservice.service.outbox.OutboxPublisher;
import org.ex9.contractorservice.service.outbox.OutboxService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractorServiceTest {
	@Mock
	private OutboxService outboxService;

	@Mock
	private OutboxPublisher outboxPublisher;

	@Mock
	private ObjectMapper objectMapper;

	@Mock
	private ContractorJdbcDao contractorJdbcDao;

	@Mock
	private ContractorRepository contractorRepository;

	@Mock
	private CountryRepository countryRepository;

	@Mock
	private IndustryRepository industryRepository;

	@Mock
	private OrgFormRepository orgFormRepository;

	@InjectMocks
	private ContractorService contractorService;

	private Contractor contractor;
	private ContractorRequestDto requestDto;
	private ContractorResponseDto responseDto;

	@BeforeEach
	void setUp() {

		requestDto = new ContractorRequestDto();
		requestDto.setId("CTR001");
		requestDto.setName("ООО Ромашка");
		requestDto.setNameFull("Общество с ограниченной ответственностью Ромашка");
		requestDto.setInn("123456789012");
		requestDto.setOgrn("1234567890123");
		requestDto.setCountryId("RU");
		requestDto.setIndustryId(1);
		requestDto.setOrgFormId(1);

		responseDto = new ContractorResponseDto();
		responseDto.setId("CTR001");
		responseDto.setName("ООО Ромашка");
		responseDto.setNameFull("Общество с ограниченной ответственностью Ромашка");
		responseDto.setInn("123456789012");
		responseDto.setOgrn("1234567890123");
		responseDto.setCountry("Российская Федерация");
		responseDto.setIndustry("IT");
		responseDto.setOrgForm("ООО");

		contractor = Contractor.builder()
				.id("CTR001")
				.name("ООО Ромашка")
				.nameFull("Общество с ограниченной ответственностью Ромашка")
				.inn("123456789012")
				.ogrn("1234567890123")
				.country(Country.builder().id("RU").name("Российская Федерация").isActive(true).build())
				.industry(Industry.builder().id(1).name("IT").isActive(true).build())
				.orgForm(OrgForm.builder().id(1).name("ООО").isActive(true).build())
				.isActive(true)
				.build();;
	}

	@Test
	@DisplayName("findById() return dto of found contractor")
	void findById_existingContractor_shouldReturnContractor() {
		when(contractorJdbcDao.findById("CTR001")).thenReturn(Optional.of(contractor));

		ContractorResponseDto result = contractorService.findById("CTR001");

		assertNotNull(result);
		assertEquals(responseDto, result);
		verify(contractorJdbcDao).findById("CTR001");
	}

	@Test
	@DisplayName("findById() throw exception when id not exist")
	void findById_nonExistingContractor_shouldThrowException() {
		when(contractorJdbcDao.findById("NON_EXISTENT")).thenReturn(Optional.empty());

		assertThrows(ContractorNotFoundException.class, () -> contractorService.findById("NON_EXISTENT"),
				"Contractor not found");
		verify(contractorJdbcDao).findById("NON_EXISTENT");
	}

	@Test
	@DisplayName("save() return new contractor")
	void save_newContractor_shouldInsertAndReturnContractor() throws JsonProcessingException {
		this.contractor = ContractorMapper.toContractor(requestDto);
		when(countryRepository.findById(requestDto.getCountryId())).thenReturn(Optional.of(Country.builder().id("RU").isActive(true).build()));
		when(industryRepository.findById(requestDto.getIndustryId())).thenReturn(Optional.of(Industry.builder().id(1).isActive(true).build()));
		when(orgFormRepository.findById(requestDto.getOrgFormId())).thenReturn(Optional.of(OrgForm.builder().id(1).isActive(true).build()));
		when(contractorRepository.existsById(requestDto.getId())).thenReturn(false);
		when(contractorJdbcDao.insert(contractor)).thenReturn(contractor);
		when(contractorJdbcDao.findById(contractor.getId())).thenReturn(Optional.of(contractor));

		ContractorDto contractorDto = ContractorDto.builder()
				.id("CTR001")
				.name("Test Contractor")
				.inn("1234567890")
				.modifyDateTime(LocalDateTime.now())
				.build();

		OutboxEvent outboxEvent = new OutboxEvent();
		outboxEvent.setPayload("{\"id\":\"CTR001\",\"name\":\"Test Contractor\",\"inn\":\"1234567890\",\"modifyDateTime\":\"2025-08-19T16:24:00\"}");

		when(outboxService.saveEvent(any(Contractor.class))).thenReturn(outboxEvent);
		when(objectMapper.readValue(outboxEvent.getPayload(), ContractorDto.class)).thenReturn(contractorDto);
		doNothing().when(outboxPublisher).publish(any(ContractorDto.class));
		doNothing().when(outboxService).markAsPublished(any(OutboxEvent.class));

		ContractorResponseDto result = contractorService.save(requestDto);

		assertNotNull(result);
		assertEquals("CTR001", result.getId());
		verify(contractorRepository).existsById("CTR001");
		verify(countryRepository).findById("RU");
		verify(industryRepository).findById(1);
		verify(orgFormRepository).findById(1);
		verify(contractorJdbcDao).insert(contractor);
		verify(contractorJdbcDao).findById("CTR001");
	}

	@Test
	@DisplayName("save() return updated contractor")
	void save_existingContractor_shouldUpdateAndReturnContractor() throws JsonProcessingException {
		this.contractor = ContractorMapper.toContractor(requestDto);
		when(countryRepository.findById(requestDto.getCountryId())).thenReturn(Optional.of(Country.builder().id("RU").isActive(true).build()));
		when(industryRepository.findById(requestDto.getIndustryId())).thenReturn(Optional.of(Industry.builder().id(1).isActive(true).build()));
		when(orgFormRepository.findById(requestDto.getOrgFormId())).thenReturn(Optional.of(OrgForm.builder().id(1).isActive(true).build()));
		when(contractorRepository.existsById(requestDto.getId())).thenReturn(true);
		when(contractorJdbcDao.update(contractor)).thenReturn(contractor);
		when(contractorJdbcDao.findById(contractor.getId())).thenReturn(Optional.of(contractor));

		ContractorDto contractorDto = ContractorDto.builder()
				.id("CTR001")
				.name("Test Contractor")
				.inn("1234567890")
				.modifyDateTime(LocalDateTime.now())
				.build();

		OutboxEvent outboxEvent = new OutboxEvent();
		outboxEvent.setPayload("{\"id\":\"CTR001\",\"name\":\"Test Contractor\",\"inn\":\"1234567890\",\"modifyDateTime\":\"2025-08-19T16:24:00\"}");

		when(outboxService.saveEvent(any(Contractor.class))).thenReturn(outboxEvent);
		when(objectMapper.readValue(outboxEvent.getPayload(), ContractorDto.class)).thenReturn(contractorDto);
		doNothing().when(outboxPublisher).publish(any(ContractorDto.class));
		doNothing().when(outboxService).markAsPublished(any(OutboxEvent.class));

		ContractorResponseDto result = contractorService.save(requestDto);

		assertNotNull(result);
		assertEquals("CTR001", result.getId());
		verify(contractorRepository).existsById("CTR001");
		verify(countryRepository).findById("RU");
		verify(industryRepository).findById(1);
		verify(orgFormRepository).findById(1);
		verify(contractorJdbcDao).update(contractor);
		verify(contractorJdbcDao).findById("CTR001");
	}

	@Test
	@DisplayName("save() throw exception when parent no exist")
	void save_nonExistParent_shouldThrowContractorNotFoundException() {
		requestDto.setParentId("NON_EXISTENT");
		when(contractorJdbcDao.findById("NON_EXISTENT")).thenReturn(Optional.empty());

		assertThrows(ContractorNotFoundException.class, () -> contractorService.save(requestDto),
				"Expected ContractorNotFoundException for non-existing parent");
		verify(contractorJdbcDao).findById("NON_EXISTENT");
		verifyNoInteractions(contractorRepository, countryRepository, industryRepository, orgFormRepository);
	}

	@Test
	@DisplayName("save() throw exception when country no exist")
	void save_nonExistCountry_shouldThrowCountryNotFoundException() {
		when(countryRepository.findById("RU")).thenReturn(Optional.empty());

		assertThrows(CountryNotFoundException.class, () -> contractorService.save(requestDto),
				"Expected CountryNotFoundException");
		verify(countryRepository).findById("RU");
		verifyNoInteractions(contractorJdbcDao, contractorRepository, industryRepository, orgFormRepository);
	}

	@Test
	@DisplayName("save() throw exception when industry no exist")
	void save_nonExistIndustry_shouldThrowIndustryNotFoundException() {
		when(countryRepository.findById("RU")).thenReturn(Optional.of(Country.builder().id("RU").isActive(true).build()));
		when(industryRepository.findById(1)).thenReturn(Optional.empty());

		assertThrows(IndustryNotFoundException.class, () -> contractorService.save(requestDto),
				"Expected IndustryNotFoundException");
		verify(countryRepository).findById("RU");
		verify(industryRepository).findById(1);
		verifyNoInteractions(contractorJdbcDao, contractorRepository, orgFormRepository);
	}

	@Test
	@DisplayName("save() throw exception when orgForm no exist")
	void save_nonExistOrgForm_shouldThrowOrgFormNotFoundException() {
		when(countryRepository.findById("RU")).thenReturn(Optional.of(Country.builder().id("RU").isActive(true).build()));
		when(industryRepository.findById(1)).thenReturn(Optional.of(Industry.builder().id(1).isActive(true).build()));
		when(orgFormRepository.findById(1)).thenReturn(Optional.empty());

		assertThrows(OrgFormNotFoundException.class, () -> contractorService.save(requestDto),
				"Expected OrgFormNotFoundException");
		verify(countryRepository).findById("RU");
		verify(industryRepository).findById(1);
		verify(orgFormRepository).findById(1);
		verifyNoInteractions(contractorJdbcDao, contractorRepository);
	}

	@Test
	@DisplayName("search() return filtered contractors")
	void search_withFilters_shouldReturnContractors() {
		SearchContractorRequestDto request = new SearchContractorRequestDto();
		request.setContractorId("CTR001");
		when(contractorJdbcDao.search(request)).thenReturn(List.of(contractor));

		List<ContractorResponseDto> result = contractorService.search(request);

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("CTR001", result.get(0).getId());
		verify(contractorJdbcDao).search(request);
	}

	@Test
	@DisplayName("delete() delete contractor")
	void delete_existContractor_shouldDeleteSuccessfully() {
		when(contractorRepository.existsById("CTR001")).thenReturn(true);

		contractorService.delete("CTR001");

		verify(contractorRepository).existsById("CTR001");
		verify(contractorRepository).deleteById("CTR001");
		verifyNoInteractions(contractorJdbcDao, countryRepository, industryRepository, orgFormRepository);
	}

	@Test
	@DisplayName("delete() throw exception when id not exist")
	void delete_nonExistContractor_shouldThrowException() {
		when(contractorRepository.existsById("NON_EXISTENT")).thenReturn(false);

		assertThrows(ContractorNotFoundException.class, () -> contractorService.delete("NON_EXISTENT"),
				"Expected ContractorNotFoundException");
		verify(contractorRepository).existsById("NON_EXISTENT");
		verifyNoMoreInteractions(contractorRepository);
		verifyNoInteractions(contractorJdbcDao, countryRepository, industryRepository, orgFormRepository);
	}

}