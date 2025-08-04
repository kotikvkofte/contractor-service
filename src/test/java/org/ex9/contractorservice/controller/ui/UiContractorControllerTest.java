package org.ex9.contractorservice.controller.ui;

import org.ex9.contractorservice.dto.contractor.ContractorRequestDto;
import org.ex9.contractorservice.dto.contractor.ContractorResponseDto;
import org.ex9.contractorservice.dto.contractor.SearchContractorRequestDto;
import org.ex9.contractorservice.exception.ContractorNotFoundException;
import org.ex9.contractorservice.service.ContractorService;
import org.ex9.contractorservice.utils.AuthInfo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UiContractorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContractorService contractorService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public ContractorService contractorService() {
            return Mockito.mock(ContractorService.class);
        }
    }

    @Test
    @WithMockUser(authorities = {"CONTRACTOR_SUPERUSER"})
    void testGetContractorById_success() throws Exception {
        when(contractorService.findById("123")).thenReturn(any(ContractorResponseDto.class));

        mockMvc.perform(get("/ui/contractor/contractor/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    void testGetContractorById_roleAdmin_isForbidden() throws Exception {
        mockMvc.perform(get("/ui/contractor/contractor/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"CONTRACTOR_SUPERUSER"})
    void testGetContractorById_notFound() throws Exception {
        when(contractorService.findById(any(String.class)))
                .thenThrow(new ContractorNotFoundException("Contractor not found"));

        mockMvc.perform(get("/ui/contractor/contractor/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Contractor not found"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void testGetContractorById_unauthenticated_isForbidden() throws Exception {
        mockMvc.perform(get("/ui/contractor/contractor/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"SUPERUSER"})
    void testDeleteContractor_success() throws Exception {
        doNothing().when(contractorService).delete("1");

        mockMvc.perform(delete("/ui/contractor/contractor/delete/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());
    }

    @Test
    @WithMockUser(authorities = {"SUPERUSER"})
    void testDeleteContractor_notFound() throws Exception {
        doThrow(new ContractorNotFoundException("Contractor not found")).when(contractorService).delete("1");

        mockMvc.perform(delete("/ui/contractor/contractor/delete/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Contractor not found"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    void testDeleteContractor_roleUser_isForbidden() throws Exception {
        mockMvc.perform(delete("/ui/contractor/contractor/delete/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "superuser", authorities = {"CONTRACTOR_SUPERUSER"})
    void testSaveContractor_success() throws Exception {
        ContractorResponseDto contractor = ContractorResponseDto.builder()
                .id("123")
                .name("Test")
                .country("РФ")
                .build();
        when(contractorService.save(any(ContractorRequestDto.class), eq("superuser"))).thenReturn(contractor);

        mockMvc.perform(put("/ui/contractor/contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"1\",\"name\":\"Contractor1\",\"country\":\"RUS\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    void testSaveContractor_roleUser_isForbidden() throws Exception {
        mockMvc.perform(put("/ui/contractor/contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"1\",\"name\":\"Contractor1\",\"country\":\"RUS\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"CONTRACTOR_SUPERUSER"})
    void testSearchContractors_Success() throws Exception {
        ContractorResponseDto responseDto = new ContractorResponseDto();
        responseDto.setId("CTR001");
        responseDto.setName("ООО Ромашка");
        responseDto.setNameFull("Общество с ограниченной ответственностью Ромашка");
        responseDto.setInn("123456789012");
        responseDto.setOgrn("1234567890123");
        responseDto.setCountry("Россия");
        responseDto.setIndustry("IT");
        responseDto.setOrgForm("ООО");
        when(contractorService.search(any(SearchContractorRequestDto.class))).thenReturn(List.of(responseDto));

        mockMvc.perform(post("/ui/contractor/contractor/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"country\":\"Российская Федерация\",\"page\":\"0\",\"size\":\"5\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"CONTRACTOR_RUS"})
    void testSearchContractors_roleContractorRus_success() throws Exception {
        ContractorResponseDto responseDto = new ContractorResponseDto();
        responseDto.setId("CTR001");
        responseDto.setName("ООО Ромашка");
        responseDto.setNameFull("Общество с ограниченной ответственностью Ромашка");
        responseDto.setInn("123456789012");
        responseDto.setOgrn("1234567890123");
        responseDto.setCountry("Российская федерация");
        responseDto.setIndustry("IT");
        responseDto.setOrgForm("ООО");

        when(contractorService.search(any(SearchContractorRequestDto.class))).thenReturn(List.of(responseDto));

        mockMvc.perform(post("/ui/contractor/contractor/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"country\":\"Российская Федерация\",\"page\":\"0\",\"size\":\"5\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"CONTRACTOR_RUS"})
    void testSearchContractors_roleContractorRus_isForbidden() throws Exception {
        ContractorResponseDto responseDto = new ContractorResponseDto();
        responseDto.setId("CTR001");
        responseDto.setName("ООО Ромашка");
        responseDto.setNameFull("Общество с ограниченной ответственностью Ромашка");
        responseDto.setInn("123456789012");
        responseDto.setOgrn("1234567890123");
        responseDto.setCountry("Соединенный штаты америки");
        responseDto.setIndustry("IT");
        responseDto.setOrgForm("ООО");

        when(contractorService.search(any(SearchContractorRequestDto.class))).thenReturn(List.of(responseDto));

        mockMvc.perform(post("/ui/contractor/contractor/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"page\":\"0\",\"size\":\"5\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    void testSearchContractors_roleUser_isForbidden() throws Exception {
        mockMvc.perform(post("/ui/contractor/contractor/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"page\":\"0\",\"size\":\"5\"}"))
                .andExpect(status().isForbidden());
    }


}