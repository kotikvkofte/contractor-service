package org.ex9.contractorservice.controller;

import org.ex9.contractorservice.dto.orgform.OrgFormRequestDto;
import org.ex9.contractorservice.dto.orgform.OrgFormResponseDto;
import org.ex9.contractorservice.exception.OrgFormNotFoundException;
import org.ex9.contractorservice.service.OrgFormService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrgFormControllerTest {

	@Mock
	OrgFormService orgFormService;

	@InjectMocks
	OrgFormController orgFormController;

	@Test
	@DisplayName("getAllActive() returns 200 OK with list of orgForms")
	void getAllActive_ReturnsValidResponseEntity() {
		var orgForms = List.of(new OrgFormResponseDto(1, "first orgForm"),
				new OrgFormResponseDto(2, "second orgForm"));

		doReturn(orgForms).when(this.orgFormService).findAll();

		var response = this.orgFormController.getAllActive();

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(orgForms, response.getBody());
	}

	@Test
	@DisplayName("getById() returns 200 OK with found orgForm")
	void getById_ReturnsValidResponseEntity() {
		var orgForm = new OrgFormResponseDto(1, "first country");

		doReturn(orgForm).when(this.orgFormService).findById(orgForm.getId());

		var response = this.orgFormController.getById(orgForm.getId());

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(orgForm, response.getBody());
	}

	@Test
	@DisplayName("getById() throw exception")
	void getById_WhenOrgFormNotFound_ShouldThrowException() {
		var orgForm = new OrgFormResponseDto(1, "first industry");

		doThrow(new OrgFormNotFoundException("OrgForm not found with id " + orgForm.getId()))
				.when(this.orgFormService).findById(orgForm.getId());

		assertThrows(OrgFormNotFoundException.class, () -> this.orgFormController.getById(orgForm.getId()));
		verify(orgFormService, times(1)).findById(orgForm.getId());
	}

	@Test
	@DisplayName("save() returns 200 OK with new or edited country")
	void save_ReturnsValidResponseEntity() {
		OrgFormRequestDto request = new OrgFormRequestDto(1, "New orgForm");
		OrgFormResponseDto responseExpected = new OrgFormResponseDto(1, "New orgForm");

		doReturn(responseExpected).when(this.orgFormService).save(request);
		var response = this.orgFormController.save(request);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(responseExpected, response.getBody());
	}

	@Test
	@DisplayName("save() returns 204 NO_CONTENT")
	void delete_WhenFound_ShouldReturnStatusNoContent() {
		int orgFormId = 13;

		doNothing().when(orgFormService).delete(orgFormId);

		var response = this.orgFormController.delete(orgFormId);

		assertNotNull(response);
		assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

		verify(orgFormService, times(1)).delete(orgFormId);
	}

	@Test
	@DisplayName("delete() throw exception")
	void delete_WhenNotFound_ShouldReturnStatusNotFound() {
		int orgFormId = 13;

		doThrow(new OrgFormNotFoundException("OrgForm not found with id " + orgFormId))
				.when(orgFormService).delete(orgFormId);

		assertThrows(OrgFormNotFoundException.class, () -> this.orgFormController.delete(orgFormId));
		verify(orgFormService, times(1)).delete(orgFormId);
	}

}