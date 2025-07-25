package org.ex9.contractorservice.controller.ui;

import org.ex9.contractorservice.dto.industry.IndustryRequestDto;
import org.ex9.contractorservice.dto.industry.IndustryResponseDto;
import org.ex9.contractorservice.service.IndustryService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UiIndustryControllerTest {

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
    @WithMockUser(authorities = {"USER"})
    void testGetAllActiveIndustries_whenRoleIsUser_success() throws Exception {
        List<IndustryResponseDto> resp = List.of(
                new IndustryResponseDto(1, "тест1"),
                new IndustryResponseDto(2, "тест2")
        );
        when(industryService.findAll()).thenReturn(resp);

        mockMvc.perform(get("/ui/industry/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllActiveIndustries_whenUnknownUser_isForbidden() throws Exception {
        mockMvc.perform(get("/ui/industry/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testGetAllActiveIndustries_whenRoleAdmin_isForbidden() throws Exception {
        mockMvc.perform(get("/ui/industry/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    void testGetIndustryById_roleUser_success() throws Exception {
        IndustryResponseDto industry = new IndustryResponseDto(1, "тест");
        when(industryService.findById(1)).thenReturn(industry);

        mockMvc.perform(get("/ui/industry/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testGetIndustryById_roleAdmin_forbidden() throws Exception {

        mockMvc.perform(get("/ui/industry/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"CONTRACTOR_SUPERUSER"})
    void testSaveIndustry_roleContractorSuperuser_success() throws Exception {
        IndustryResponseDto Industry = new IndustryResponseDto(1, "тест");
        when(industryService.save(any(IndustryRequestDto.class))).thenReturn(Industry);

        mockMvc.perform(put("/ui/industry/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"1\",\"name\":\"USA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("тест"));
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    void testSaveIndustry_roleUser_isForbidden() throws Exception {
        mockMvc.perform(put("/ui/industry/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"1\",\"name\":\"Test\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"SUPERUSER"})
    void testDeleteIndustry_roleSuperuser_success() throws Exception {
        doNothing().when(industryService).delete(1);

        mockMvc.perform(delete("/ui/industry/delete/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    void testDeleteIndustry_roleUser_isForbidden() throws Exception {
        mockMvc.perform(delete("/ui/industry/delete/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

}