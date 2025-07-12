package org.ex9.contractorservice.controller;

import org.ex9.contractorservice.dto.orgform.OrgFormResponseDto;
import org.ex9.contractorservice.exception.IndustryNotFoundException;
import org.ex9.contractorservice.exception.OrgFormNotFoundException;
import org.ex9.contractorservice.service.IndustryService;
import org.ex9.contractorservice.service.OrgFormService;
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

@WebMvcTest(OrgFormController.class)
public class OrgFormControllerErrorIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private OrgFormService orgFormService;

	@TestConfiguration
	static class MockConfig {
		@Bean
		public OrgFormService orgFormService() {
			return Mockito.mock(OrgFormService.class);
		}
	}

	@Test
	@DisplayName("delete() return errorResponse when id is not found")
	void delete_whenNotFound_ShouldReturnErrorResponse() throws Exception {
		int id = 123;

		doThrow(new OrgFormNotFoundException("OrgForm with ID " + id + " not found"))
				.when(orgFormService).delete(id);

		mockMvc.perform(delete("/orgform/delete/{id}", id))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("OrgForm with ID " + id + " not found"))
				.andExpect(jsonPath("$.timestamp").exists());
	}

	@Test
	@DisplayName("getById() return errorResponse when id is not found")
	void getById_whenOrgFormNotFound_shouldReturnErrorResponse() throws Exception {
		int id = 123;

		when(this.orgFormService.findById(id))
				.thenThrow(new OrgFormNotFoundException("OrgForm with ID " + id + " not found"));

		mockMvc.perform(get("/orgform/{id}", id))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("OrgForm with ID " + id + " not found"))
				.andExpect(jsonPath("$.timestamp").exists());
	}

}
