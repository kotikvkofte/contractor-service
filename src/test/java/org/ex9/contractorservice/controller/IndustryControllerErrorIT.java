package org.ex9.contractorservice.controller;

import org.ex9.contractorservice.dto.industry.IndustryResponseDto;
import org.ex9.contractorservice.exception.CountryNotFoundException;
import org.ex9.contractorservice.exception.IndustryNotFoundException;
import org.ex9.contractorservice.service.CountryService;
import org.ex9.contractorservice.service.IndustryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IndustryController.class)
public class IndustryControllerErrorIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private IndustryService industryService;

	@TestConfiguration
	static class MockConfig {
		@Bean
		public IndustryService industryService() {
			return Mockito.mock(IndustryService.class);
		}
	}

	@Test
	@DisplayName("getById() return ErrorResponse when id is not found")
	void getById_WhenIndustryNotFound_ShouldReturnErrorResponse() throws Exception {
		int id = 123;

		Mockito.when(industryService.findById(id))
				.thenThrow(new IndustryNotFoundException("Industry with ID " + id + " not found"));

		mockMvc.perform(get("/industry/{id}", id))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Industry with ID " + id + " not found"))
				.andExpect(jsonPath("$.timestamp").exists());
	}

	@Test
	@DisplayName("delete() return ErrorResponse when id is not found")
	void delete_WhenNotFound_ShouldReturnErrorResponse() throws Exception {
		int id = 123;

		doThrow(new IndustryNotFoundException("Industry with ID " + id + " not found"))
				.when(industryService).delete(id);

		mockMvc.perform(delete("/industry/delete/{id}", id))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Industry with ID " + id + " not found"))
				.andExpect(jsonPath("$.timestamp").exists());
	}
}
