package org.ex9.contractorservice.controller;

import org.ex9.contractorservice.exception.CountryNotFoundException;
import org.ex9.contractorservice.service.CountryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CountryController.class)
public class CountryControllerErrorIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CountryService countryService;

	@TestConfiguration
	static class MockConfig {
		@Bean
		public CountryService countryService() {
			return Mockito.mock(CountryService.class);
		}
	}

	@Test
	@DisplayName("getById() return ErrorResponse when id is not found")
	void findById_whenCountryNotFound_thenReturnError() throws Exception {
		String id = "non-existing-id";

		Mockito.when(countryService.findById(id))
				.thenThrow(new CountryNotFoundException("Country with ID " + id + " not found"));

		mockMvc.perform(get("/country/{id}", id))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Country with ID " + id + " not found"))
				.andExpect(jsonPath("$.timestamp").exists());
	}

	@Test
	@DisplayName("delete() return ErrorResponse when id is not found")
	void delete_whenCountryNotFound_shouldErrorResponse() throws Exception {
		String id = "non-existing-id";

		doThrow(new CountryNotFoundException("Country with ID " + id + " not found"))
				.when(countryService).delete(id);

		mockMvc.perform(delete("/country/delete/{id}", id))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Country with ID " + id + " not found"))
				.andExpect(jsonPath("$.timestamp").exists());
	}

}
