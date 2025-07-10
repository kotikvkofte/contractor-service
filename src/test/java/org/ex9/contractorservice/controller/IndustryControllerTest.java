package org.ex9.contractorservice.controller;

import org.ex9.contractorservice.dto.industry.IndustryRequestDto;
import org.ex9.contractorservice.dto.industry.IndustryResponseDto;
import org.ex9.contractorservice.exception.IndustryNotFoundException;
import org.ex9.contractorservice.service.IndustryService;
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
class IndustryControllerTest {

	@Mock
	IndustryService industryService;

	@InjectMocks
	IndustryController industryController;

	@Test
	@DisplayName("getAllActive() returns 200 OK with list of industry's")
	void getAllActive_ReturnsValidResponseEntity() {
		var industrys = List.of(new IndustryResponseDto(1, "first industry"),
				new IndustryResponseDto(2, "second industry"));

		doReturn(industrys).when(this.industryService).findAll();

		var response = this.industryController.getAllActive();

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(industrys, response.getBody());
	}

	@Test
	@DisplayName("getById() returns 200 OK with found industry")
	void getById_ReturnsValidResponseEntity() {
		var industry = new IndustryResponseDto(1, "first country");

		doReturn(industry).when(this.industryService).findById(industry.getId());

		var response = this.industryController.getById(industry.getId());

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(industry, response.getBody());
	}

	@Test
	@DisplayName("getById() throw exception")
	void getById_WhenIndustryNotFound_ShouldThrowException() {
		var industry = new IndustryResponseDto(1, "first industry");

		doThrow(new IndustryNotFoundException("Industry not found with id " + industry.getId()))
				.when(this.industryService).findById(industry.getId());


		assertThrows(IndustryNotFoundException.class, () -> this.industryController.getById(industry.getId()));
		verify(industryService, times(1)).findById(industry.getId());
	}

	@Test
	@DisplayName("save() returns 200 OK with new or edited country")
	void save_ReturnsValidResponseEntity() {
		IndustryRequestDto request = new IndustryRequestDto(1, "New industry");
		IndustryResponseDto responseExpected = new IndustryResponseDto(1, "New Country");

		doReturn(responseExpected).when(this.industryService).save(request);
		var response = this.industryController.save(request);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(responseExpected, response.getBody());
	}

	@Test
	@DisplayName("save() returns 204 NO_CONTENT")
	void delete_WhenFound_ShouldReturnStatusNoContent() {
		int industryId = 13;

		doNothing().when(industryService).delete(industryId);

		var response = this.industryController.delete(industryId);

		assertNotNull(response);
		assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

		verify(industryService, times(1)).delete(industryId);
	}

	@Test
	@DisplayName("delete() throw exception")
	void delete_WhenNotFound_ShouldThrowException() {
		int industryId = 13;

		doThrow(new IndustryNotFoundException("Industry not found with id " + industryId))
				.when(industryService).delete(industryId);


		assertThrows(IndustryNotFoundException.class, () -> this.industryController.delete(industryId));
		verify(industryService, times(1)).delete(industryId);
	}

}