package org.ex9.contractorservice.service;

import org.ex9.contractorservice.dto.orgform.OrgFormRequestDto;
import org.ex9.contractorservice.dto.orgform.OrgFormResponseDto;
import org.ex9.contractorservice.exception.OrgFormNotFoundException;
import org.ex9.contractorservice.model.OrgForm;
import org.ex9.contractorservice.repository.OrgFormRepository;
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
class OrgFormServiceTest {
	@Mock
	OrgFormRepository orgFormRepository;

	@InjectMocks
	OrgFormService orgFormService;

	@Test
	@DisplayName("findAll() returns active orgForms mapped to DTOs")
	void findAll_ReturnsListOfActiveOrgForms() {
		var activeOrgForm1 = new OrgForm(1, "orgform 1", true);
		var activeOrgForm2 = new OrgForm(2, "orgform 2", true);
		var activeOrgForm3 = new OrgForm(3, "orgform 3", false);

		when(orgFormRepository.findAllByIsActiveTrue()).thenReturn(List.of(activeOrgForm1, activeOrgForm2));

		List<OrgFormResponseDto> result = orgFormService.findAll();

		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(activeOrgForm1.getId(), result.get(0).getId());
		assertEquals(activeOrgForm2.getId(), result.get(1).getId());
		assertEquals(activeOrgForm1.getName(), result.get(0).getName());
		assertEquals(activeOrgForm2.getName(), result.get(1).getName());

		verify(orgFormRepository, times(1)).findAllByIsActiveTrue();
	}

	@Test
	@DisplayName("save() throw exception when id not found")
	void save_WhenIdNotExists_ShouldThrowException() {

		var request = new OrgFormRequestDto(1, "orgform 1");
		OrgForm orgForm = new OrgForm(1, "orgform 1", true);

		when(orgFormRepository.existsById(1)).thenReturn(false);

		assertThrows(OrgFormNotFoundException.class, () -> orgFormService.save(request));
		verify(orgFormRepository, times(1)).existsById(1);
		verify(orgFormRepository, never()).save(orgForm);
	}

	@Test
	@DisplayName("save() add new orgForm when id is null")
	void save_WhenIdIsNull_ShouldInsertNewOrgForm() {

		var request = new OrgFormRequestDto(null, "orgForm1");
		OrgForm orgForm = new OrgForm(null, "orgForm1", true);
		OrgForm orgFormSaved = new OrgForm(1, "orgForm1", true);

		when(orgFormRepository.save(orgForm)).thenReturn(orgFormSaved);

		OrgFormResponseDto result = orgFormService.save(request);

		assertNotNull(result);
		assertEquals(orgFormSaved.getId(), result.getId());
		assertEquals(orgFormSaved.getName(), result.getName());

		verify(orgFormRepository, times(1)).save(orgForm);
		verify(orgFormRepository, never()).existsById(1);
	}

	@Test
	@DisplayName("save() update orgForm when id is exist")
	void save_WhenIdExists_ShouldUpdateOrgForm() {
		var request = new OrgFormRequestDto(1, "orgForm123");
		OrgForm industry = new OrgForm(1, "orgForm123", true);

		when(orgFormRepository.existsById(1)).thenReturn(true);
		when(orgFormRepository.save(industry)).thenReturn(industry);

		OrgFormResponseDto result = orgFormService.save(request);

		assertNotNull(result);
		assertEquals(industry.getId(), result.getId());
		assertEquals(industry.getName(), result.getName());

		verify(orgFormRepository, times(1)).existsById(1);
		verify(orgFormRepository, times(1)).save(industry);
	}

	@Test
	@DisplayName("findById() return dto when id exists")
	void findById_WhenIdExist_ShouldReturnOrgForm() {
		int id = 1;
		OrgForm industry = new OrgForm(id, "OrgForm1", true);

		when(orgFormRepository.findById(id)).thenReturn(Optional.of(industry));

		OrgFormResponseDto result = orgFormService.findById(id);

		assertNotNull(result);
		assertEquals(industry.getId(), result.getId());
		assertEquals(industry.getName(), result.getName());

		verify(orgFormRepository, times(1)).findById(id);
	}

	@Test
	@DisplayName("findById() throw exception when id not exists")
	void findById_WhenIdNotExists_ShouldThrowException() {
		int id = 1;

		when(orgFormRepository.findById(id)).thenReturn(Optional.empty());

		assertThrows(OrgFormNotFoundException.class, () -> orgFormService.findById(id));

		verify(orgFormRepository, times(1)).findById(id);
	}

	@Test
	@DisplayName("delete() should logical delete when id exist")
	void delete_WhenIdExists_ShouldLogicalDeleteOrgForm() {
		int id = anyInt();

		when(orgFormRepository.existsById(id)).thenReturn(true);
		doNothing().when(orgFormRepository).deleteById(id);

		assertDoesNotThrow(() -> orgFormService.delete(id));

		verify(orgFormRepository, times(1)).deleteById(id);
	}

	@Test
	@DisplayName("delete() should throw exception when id is not exist")
	void delete_WhenIdNotExists_ShouldThrowException() {
		int id = anyInt();

		when(orgFormRepository.existsById(id)).thenReturn(false);

		assertThrows(OrgFormNotFoundException.class, () -> orgFormService.delete(id));

		verify(orgFormRepository, never()).deleteById(id);
		verify(orgFormRepository, times(1)).existsById(id);
	}
}