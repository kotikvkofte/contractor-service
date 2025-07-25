package org.ex9.contractorservice.controller.ui;

import org.ex9.contractorservice.dto.orgform.OrgFormRequestDto;
import org.ex9.contractorservice.dto.orgform.OrgFormResponseDto;
import org.ex9.contractorservice.service.OrgFormService;
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
class UiOrgFormControllerTest {

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
    @WithMockUser(authorities = {"USER"})
    void testGetAllActiveOrgForms_whenRoleIsUser_success() throws Exception {
        List<OrgFormResponseDto> resp = List.of(
                new OrgFormResponseDto(1, "тест1"),
                new OrgFormResponseDto(2, "тест2")
        );
        when(orgFormService.findAll()).thenReturn(resp);

        mockMvc.perform(get("/ui/orgform/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllActiveOrgForms_whenUnknownUser_isForbidden() throws Exception {
        mockMvc.perform(get("/ui/orgform/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testGetAllActiveOrgForms_whenRoleAdmin_isForbidden() throws Exception {
        mockMvc.perform(get("/ui/orgform/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    void testGetOrgFormById_roleUser_success() throws Exception {
        OrgFormResponseDto orgform = new OrgFormResponseDto(1, "тест");
        when(orgFormService.findById(1)).thenReturn(orgform);

        mockMvc.perform(get("/ui/orgform/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testGetOrgFormById_roleAdmin_forbidden() throws Exception {

        mockMvc.perform(get("/ui/orgform/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"CONTRACTOR_SUPERUSER"})
    void testSaveOrgForm_roleContractorSuperuser_success() throws Exception {
        OrgFormResponseDto OrgForm = new OrgFormResponseDto(1, "USA");
        when(orgFormService.save(any(OrgFormRequestDto.class))).thenReturn(OrgForm);

        mockMvc.perform(put("/ui/orgform/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"1\",\"name\":\"USA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("USA"));
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    void testSaveOrgForm_roleUser_isForbidden() throws Exception {
        mockMvc.perform(put("/ui/orgform/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"1\",\"name\":\"Тест\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"SUPERUSER"})
    void testDeleteOrgForm_roleSuperuser_success() throws Exception {
        doNothing().when(orgFormService).delete(1);

        mockMvc.perform(delete("/ui/orgform/delete/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    void testDeleteOrgForm_roleUser_isForbidden() throws Exception {
        mockMvc.perform(delete("/ui/orgform/delete/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
    
}