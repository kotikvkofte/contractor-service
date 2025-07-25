package org.ex9.contractorservice.controller.ui;

import org.ex9.contractorservice.dto.country.CountryRequestDto;
import org.ex9.contractorservice.dto.country.CountryResponseDto;
import org.ex9.contractorservice.service.CountryService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UiCountryControllerTest {

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
    @WithMockUser(authorities = {"USER"})
    void testGetAllActiveCountries_whenRoleIsUser_success() throws Exception {
        List<CountryResponseDto> resp = List.of(
                new CountryResponseDto("USA", "Соединенный штаты америки"),
                new CountryResponseDto("RUS", "Российская федерация")
        );
        when(countryService.findAll()).thenReturn(resp);

        mockMvc.perform(get("/ui/country/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllActiveCountries_whenUnknownUser_isForbidden() throws Exception {
        mockMvc.perform(get("/ui/country/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testGetAllActiveCountries_whenRoleAdmin_isForbidden() throws Exception {
        mockMvc.perform(get("/ui/country/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    void testGetCountryById_roleUser_success() throws Exception {
        CountryResponseDto country = new CountryResponseDto("1", "USA");
        when(countryService.findById("1")).thenReturn(country);

        mockMvc.perform(get("/ui/country/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    void testGetCountryById_roleAdmin_forbidden() throws Exception {

        mockMvc.perform(get("/ui/country/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"CONTRACTOR_SUPERUSER"})
    void testSaveCountry_roleContractorSuperuser_success() throws Exception {
        CountryResponseDto country = new CountryResponseDto("1", "USA");
        when(countryService.save(any(CountryRequestDto.class))).thenReturn(country);

        mockMvc.perform(put("/ui/country/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"1\",\"name\":\"USA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("USA"));
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    void testSaveCountry_roleUser_isForbidden() throws Exception {
        mockMvc.perform(put("/ui/country/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"1\",\"name\":\"USA\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"SUPERUSER"})
    void testDeleteCountry_roleSuperuser_success() throws Exception {
        doNothing().when(countryService).delete("1");

        mockMvc.perform(delete("/ui/country/delete/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    void testDeleteCountry_roleUser_isForbidden() throws Exception {
        mockMvc.perform(delete("/ui/country/delete/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

}