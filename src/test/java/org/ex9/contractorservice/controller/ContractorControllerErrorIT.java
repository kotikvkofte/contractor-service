package org.ex9.contractorservice.controller;

import org.ex9.contractorservice.dto.contractor.ContractorRequestDto;
import org.ex9.contractorservice.exception.ContractorNotFoundException;
import org.ex9.contractorservice.exception.CountryNotFoundException;
import org.ex9.contractorservice.exception.IndustryNotFoundException;
import org.ex9.contractorservice.exception.OrgFormNotFoundException;
import org.ex9.contractorservice.service.ContractorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ContractorControllerErrorIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ContractorService contractorService;

	@Autowired
	private ObjectMapper objectMapper;

	@TestConfiguration
	static class MockConfig {
		@Bean
		public ContractorService contractorService() {
			return Mockito.mock(ContractorService.class);
		}
	}

	@Test
	@DisplayName("getById() return ErrorResponse when id is not found")
	void findById_whenContractorNotFound_shouldReturnErrorResponse() throws Exception {
		String id = "non-existing-id";

		Mockito.when(contractorService.findById(id))
				.thenThrow(new ContractorNotFoundException("Contractor with ID " + id + " not found"));

		mockMvc.perform(get("/contractor/{id}", id))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Contractor with ID " + id + " not found"))
				.andExpect(jsonPath("$.timestamp").exists());
	}

	@Test
	@DisplayName("delete() return ErrorResponse when id is not found")
	void delete_whenContractorNotFound_shouldReturnErrorResponse() throws Exception {
		String id = "non-existing-id";

		Mockito.doThrow(new ContractorNotFoundException("Contractor with ID " + id + " not found"))
						.when(contractorService).delete(id);

		mockMvc.perform(delete("/contractor/delete/{id}", id))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Contractor with ID " + id + " not found"))
				.andExpect(jsonPath("$.timestamp").exists());
	}

	@Test
	@DisplayName("save() return ErrorResponse when parent id not found")
	void save_whenContractorParentNotFound_shouldReturnErrorResponse() throws Exception {
		ContractorRequestDto requestDto = new ContractorRequestDto();
		requestDto.setId("CTR001");
		requestDto.setParentId("CTR002");
		requestDto.setName("ООО Ромашка");
		requestDto.setNameFull("Общество с ограниченной ответственностью Ромашка");
		requestDto.setInn("123456789012");
		requestDto.setOgrn("1234567890123");
		requestDto.setCountryId("RU");
		requestDto.setIndustryId(1);
		requestDto.setOrgFormId(1);

		Mockito.doThrow(new ContractorNotFoundException("Contractor parent with ID " + requestDto.getParentId() + " not found"))
				.when(contractorService).save(requestDto);



		mockMvc.perform(put("/contractor/save")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(requestDto))
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Contractor parent with ID " + requestDto.getParentId() + " not found"))
				.andExpect(jsonPath("$.timestamp").exists());
	}

	@Test
	@DisplayName("save() return ErrorResponse when country id not found")
	void save_whenCountryNotFound_shouldReturnErrorResponse() throws Exception {
		ContractorRequestDto requestDto = new ContractorRequestDto();
		requestDto.setId("CTR001");
		requestDto.setParentId("CTR002");
		requestDto.setName("ООО Ромашка");
		requestDto.setNameFull("Общество с ограниченной ответственностью Ромашка");
		requestDto.setInn("123456789012");
		requestDto.setOgrn("1234567890123");
		requestDto.setCountryId("RU");
		requestDto.setIndustryId(1);
		requestDto.setOrgFormId(1);

		Mockito.doThrow(new CountryNotFoundException("Country with ID " + requestDto.getCountryId() + " not found"))
				.when(contractorService).save(requestDto);



		mockMvc.perform(put("/contractor/save")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(requestDto))
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Country with ID " + requestDto.getCountryId() + " not found"))
				.andExpect(jsonPath("$.timestamp").exists());
	}

	@Test
	@DisplayName("save() return ErrorResponse when industry id not found")
	void save_whenIndustryNotFound_shouldReturnErrorResponse() throws Exception {
		ContractorRequestDto requestDto = new ContractorRequestDto();
		requestDto.setId("CTR001");
		requestDto.setParentId("CTR002");
		requestDto.setName("ООО Ромашка");
		requestDto.setNameFull("Общество с ограниченной ответственностью Ромашка");
		requestDto.setInn("123456789012");
		requestDto.setOgrn("1234567890123");
		requestDto.setCountryId("RU");
		requestDto.setIndustryId(1);
		requestDto.setOrgFormId(1);

		Mockito.doThrow(new IndustryNotFoundException("Industry with ID " + requestDto.getIndustryId() + " not found"))
				.when(contractorService).save(requestDto);



		mockMvc.perform(put("/contractor/save")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(requestDto))
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Industry with ID " + requestDto.getIndustryId() + " not found"))
				.andExpect(jsonPath("$.timestamp").exists());
	}

	@Test
	@DisplayName("save() return ErrorResponse when OrgForm id not found")
	void save_whenOrgFormNotFound_shouldReturnErrorResponse() throws Exception {
		ContractorRequestDto requestDto = new ContractorRequestDto();
		requestDto.setId("CTR001");
		requestDto.setParentId("CTR002");
		requestDto.setName("ООО Ромашка");
		requestDto.setNameFull("Общество с ограниченной ответственностью Ромашка");
		requestDto.setInn("123456789012");
		requestDto.setOgrn("1234567890123");
		requestDto.setCountryId("RU");
		requestDto.setIndustryId(1);
		requestDto.setOrgFormId(1);

		Mockito.doThrow(new OrgFormNotFoundException("OrgForm with ID " + requestDto.getOrgFormId() + " not found"))
				.when(contractorService).save(requestDto);



		mockMvc.perform(put("/contractor/save")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(requestDto))
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("OrgForm with ID " + requestDto.getOrgFormId() + " not found"))
				.andExpect(jsonPath("$.timestamp").exists());
	}

	@Test
	@DisplayName("save() return ErrorResponse when contractor id not found after insert or update")
	void save_whenContractorNotFound_shouldReturnErrorResponse() throws Exception {
		ContractorRequestDto requestDto = new ContractorRequestDto();
		requestDto.setId("CTR001");
		requestDto.setParentId("CTR002");
		requestDto.setName("ООО Ромашка");
		requestDto.setNameFull("Общество с ограниченной ответственностью Ромашка");
		requestDto.setInn("123456789012");
		requestDto.setOgrn("1234567890123");
		requestDto.setCountryId("RU");
		requestDto.setIndustryId(1);
		requestDto.setOrgFormId(1);

		Mockito.doThrow(new ContractorNotFoundException("Contractor with ID " + requestDto.getId() + " not found"))
				.when(contractorService).save(requestDto);



		mockMvc.perform(put("/contractor/save")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(requestDto))
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Contractor with ID " + requestDto.getId() + " not found"))
				.andExpect(jsonPath("$.timestamp").exists());
	}

}
