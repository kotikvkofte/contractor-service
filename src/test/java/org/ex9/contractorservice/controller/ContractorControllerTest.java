package org.ex9.contractorservice.controller;

import org.ex9.contractorservice.dto.contractor.ContractorRequestDto;
import org.ex9.contractorservice.dto.contractor.ContractorResponseDto;
import org.ex9.contractorservice.dto.contractor.SearchContractorRequestDto;
import org.ex9.contractorservice.exception.ContractorNotFoundException;
import org.ex9.contractorservice.exception.CountryNotFoundException;
import org.ex9.contractorservice.exception.IndustryNotFoundException;
import org.ex9.contractorservice.exception.OrgFormNotFoundException;
import org.ex9.contractorservice.service.ContractorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractorControllerTest {

	@Mock
	ContractorService contractorService;

	@InjectMocks
	ContractorController contractorController;

	private ContractorResponseDto responseDto;
	private ContractorRequestDto requestDto;
	private SearchContractorRequestDto searchRequestDto;

	@BeforeEach
	void setUp() {
		responseDto = new ContractorResponseDto();
		responseDto.setId("CTR001");
		responseDto.setName("ООО Ромашка");
		responseDto.setNameFull("Общество с ограниченной ответственностью Ромашка");
		responseDto.setInn("123456789012");
		responseDto.setOgrn("1234567890123");
		responseDto.setCountry("Россия");
		responseDto.setIndustry("IT");
		responseDto.setOrgForm("ООО");

		requestDto = new ContractorRequestDto();
		requestDto.setId("CTR001");
		requestDto.setName("ООО Ромашка");
		requestDto.setNameFull("Общество с ограниченной ответственностью Ромашка");
		requestDto.setInn("123456789012");
		requestDto.setOgrn("1234567890123");
		requestDto.setCountryId("RU");
		requestDto.setIndustryId(1);
		requestDto.setOrgFormId(1);

		searchRequestDto = new SearchContractorRequestDto();
		searchRequestDto.setContractorId("CTR001");
	}

	@Test
	@DisplayName("getById() return contractor by id")
	void getById_existingContractor_shouldReturnContractor() {
		when(contractorService.findById("CTR001")).thenReturn(responseDto);

		ResponseEntity<ContractorResponseDto> response = contractorController.getById("CTR001");

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("CTR001", response.getBody().getId());
		assertEquals("ООО Ромашка", response.getBody().getName());
		verify(contractorService).findById("CTR001");
	}

	@Test
	@DisplayName("delete() return accepted status and delete contractor")
	void delete_existingContractor_shouldReturnAcceptedStatus() {
		doNothing().when(contractorService).delete("CTR001");

		ResponseEntity<Void> response = contractorController.delete("CTR001");

		assertNotNull(response);
		assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		verify(contractorService).delete("CTR001");
	}

	@Test
	@DisplayName("save() return saved contractor")
	void save_validContractor_shouldReturnContractor() {
		when(contractorService.save(any(ContractorRequestDto.class))).thenReturn(responseDto);

		ResponseEntity<ContractorResponseDto> response = contractorController.save(requestDto);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("CTR001", response.getBody().getId());
		assertEquals("ООО Ромашка", response.getBody().getName());
		verify(contractorService).save(requestDto);
	}

	@Test
	@DisplayName("save() throw exception when parent not exist")
	void save_nonParentExist_shouldThrowException() {
		when(contractorService.save(any(ContractorRequestDto.class))).thenThrow(new ContractorNotFoundException("Parent contractor not found"));

		assertThrows(ContractorNotFoundException.class, () -> contractorController.save(requestDto));
	}

	@Test
	@DisplayName("save() throw exception when industry not exist")
	void save_nonIndustryExist_shouldThrowException() {
		when(contractorService.save(any(ContractorRequestDto.class))).thenThrow(new IndustryNotFoundException("Industry contractor not found"));

		assertThrows(IndustryNotFoundException.class, () -> contractorController.save(requestDto));
	}

	@Test
	@DisplayName("save() throw exception when orgForm not exist")
	void save_nonOrgFormNotExist_shouldThrowException() {
		when(contractorService.save(any(ContractorRequestDto.class))).thenThrow(new OrgFormNotFoundException("OrgForm contractor not found"));

		assertThrows(OrgFormNotFoundException.class, () -> contractorController.save(requestDto));
	}

	@Test
	@DisplayName("save() throw exception when contractor id not exist")
	void save_nonCountryExist_shouldThrowException() {
		when(contractorService.save(any(ContractorRequestDto.class))).thenThrow(new CountryNotFoundException("Country contractor not found"));

		assertThrows(CountryNotFoundException.class, () -> contractorController.save(requestDto));
	}

	@Test
	@DisplayName("search() return dto")
	void search_validRequest_shouldReturnContractors() {
		when(contractorService.search(any(SearchContractorRequestDto.class))).thenReturn(List.of(responseDto));

		ResponseEntity<List<ContractorResponseDto>> response = contractorController.search(searchRequestDto);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(1, response.getBody().size());
		assertEquals("CTR001", response.getBody().get(0).getId());
		assertEquals("ООО Ромашка", response.getBody().get(0).getName());
		verify(contractorService).search(searchRequestDto);
	}

}