package org.ex9.contractorservice.service;

import org.ex9.contractorservice.dto.industry.IndustryRequestDto;
import org.ex9.contractorservice.dto.industry.IndustryResponseDto;
import org.ex9.contractorservice.exception.IndustryNotFoundException;
import org.ex9.contractorservice.model.Industry;
import org.ex9.contractorservice.repository.IndustryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IndustryServiceTest {

	@Mock
	IndustryRepository industryRepository;

	@InjectMocks
	IndustryService industryService;

	@Test
	@DisplayName("findAll() returns active industries mapped to DTOs")
	void findAll_ReturnsListOfActiveIndustries() {
		var activeIndustries1 = new Industry(1, "industry1", true);
		var activeIndustries2 = new Industry(2, "industry2", true);
		var activeIndustries3 = new Industry(3, "industry3", false);

		when(industryRepository.findAllByIsActiveTrue()).thenReturn(List.of(activeIndustries1, activeIndustries2));

		List<IndustryResponseDto> result = industryService.findAll();

		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(activeIndustries1.getId(), result.get(0).getId());
		assertEquals(activeIndustries2.getId(), result.get(1).getId());
		assertEquals(activeIndustries1.getName(), result.get(0).getName());
		assertEquals(activeIndustries2.getName(), result.get(1).getName());

		verify(industryRepository, times(1)).findAllByIsActiveTrue();
	}

	@Test
	@DisplayName("save() throw exception when id not found")
	void save_WhenIdNotExists_ShouldThrowException() {

		var request = new IndustryRequestDto(1, "industry1");
		Industry industry = new Industry(1, "industry1", true);

		when(industryRepository.existsById(1)).thenReturn(false);

		assertThrows(IndustryNotFoundException.class, () -> industryService.save(request));
		verify(industryRepository, times(1)).existsById(1);
		verify(industryRepository, never()).save(industry);
	}

	@Test
	@DisplayName("save() add new industry when id is null")
	void save_WhenIdIsNull_ShouldInsertNewIndustry() {

		var request = new IndustryRequestDto(null, "industry1");
		Industry industry = new Industry(null, "industry1", true);
		Industry industrySaved = new Industry(1, "industry1", true);

		when(industryRepository.save(industry)).thenReturn(industrySaved);

		IndustryResponseDto result = industryService.save(request);

		assertNotNull(result);
		assertEquals(industrySaved.getId(), result.getId());
		assertEquals(industrySaved.getName(), result.getName());

		verify(industryRepository, times(1)).save(industry);
		verify(industryRepository, never()).existsById(1);
	}

	@Test
	@DisplayName("save() update industry when id is exist")
	void save_WhenIdExists_ShouldUpdateIndustry() {
		var request = new IndustryRequestDto(1, "industry123");
		Industry industry = new Industry(1, "industry123", true);

		when(industryRepository.existsById(1)).thenReturn(true);
		when(industryRepository.save(industry)).thenReturn(industry);

		IndustryResponseDto result = industryService.save(request);

		assertNotNull(result);
		assertEquals(industry.getId(), result.getId());
		assertEquals(industry.getName(), result.getName());

		verify(industryRepository, times(1)).existsById(1);
		verify(industryRepository, times(1)).save(industry);
	}

	@Test
	@DisplayName("findById() return dto when id exists")
	void findById_WhenIdExist_ShouldReturnIndustry() {
		int id = 1;
		Industry industry = new Industry(id, "industry1", true);

		when(industryRepository.findById(id)).thenReturn(Optional.of(industry));

		IndustryResponseDto result = industryService.findById(id);

		assertNotNull(result);
		assertEquals(industry.getId(), result.getId());
		assertEquals(industry.getName(), result.getName());

		verify(industryRepository, times(1)).findById(id);
	}

	@Test
	@DisplayName("findById() throw exception when id not exists")
	void findById_WhenIdNotExists_ShouldThrowException() {
		int id = 1;

		when(industryRepository.findById(id)).thenReturn(Optional.empty());

		assertThrows(IndustryNotFoundException.class, () -> industryService.findById(id));

		verify(industryRepository, times(1)).findById(id);
	}

	@Test
	@DisplayName("delete() should logical delete when id exist")
	void delete_WhenIdExists_ShouldLogicalDeleteIndustry() {
		int id = anyInt();

		when(industryRepository.existsById(id)).thenReturn(true);
		doNothing().when(industryRepository).deleteById(id);

		assertDoesNotThrow(() -> industryService.delete(id));

		verify(industryRepository, times(1)).deleteById(id);
	}

	@Test
	@DisplayName("delete() should throw exception when id is not exist")
	void delete_WhenIdNotExists_ShouldThrowException() {
		int id = anyInt();

		when(industryRepository.existsById(id)).thenReturn(false);

		assertThrows(IndustryNotFoundException.class, () -> industryService.delete(id));

		verify(industryRepository, never()).deleteById(id);
		verify(industryRepository, times(1)).existsById(id);
	}

}
