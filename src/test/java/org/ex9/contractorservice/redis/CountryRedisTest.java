package org.ex9.contractorservice.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.testcontainers.RedisContainer;
import org.ex9.contractorservice.dto.country.CountryRequestDto;
import org.ex9.contractorservice.model.Country;
import org.ex9.contractorservice.repository.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class CountryRedisTest {

    private static final String COUNTRIES_CACHE_PREFIX = "countries";

    @Container
    static final RedisContainer REDIS = new RedisContainer("redis:latest");

    @DynamicPropertySource
    static void redisProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", REDIS::getFirstMappedPort);
    }

    @Autowired
    MockMvc mockMvc;
    @Autowired
    CacheManager cacheManager;
    @Autowired
    CountryRepository countryRepository;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        CountryRepository mockCountryRepository() {
            return Mockito.mock(CountryRepository.class);
        }
    }

    @BeforeEach
    void setup() {
        cacheManager.getCache(COUNTRIES_CACHE_PREFIX).clear();

        reset(countryRepository);

        Country country1 = Country.builder()
                .id("USA")
                .name("United States")
                .isActive(true)
                .build();
        Country country2 = Country.builder()
                .id("RU")
                .name("Russian")
                .isActive(true)
                .build();

        when(countryRepository.findAllByIsActiveTrue())
                .thenReturn(List.of(country1, country2));
    }

    @Test
    void getAll_shouldUseRedisCache_betweenCalls() throws Exception {
        mockMvc.perform(get("/country/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));

        mockMvc.perform(get("/country/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(countryRepository, times(1)).findAllByIsActiveTrue();
    }

    @Test
    void save_shouldUpdateRedisCache() throws Exception {
        Country country = Country.builder()
                .id("USA")
                .name("United States")
                .isActive(true)
                .build();
        when(countryRepository.findById(any())).thenReturn(Optional.of(country));
        when(countryRepository.existsById(any())).thenReturn(true);
        when(countryRepository.save(any())).thenReturn(country);

        mockMvc.perform(get("/country/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));

        var request = new ObjectMapper().writeValueAsString(new CountryRequestDto("RU", "Russia"));

        mockMvc.perform(put("/country/save")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isOk());

        mockMvc.perform(get("/country/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(countryRepository, times(2)).findAllByIsActiveTrue();
    }

    @Test
    void delete_shouldUpdateRedisCache() throws Exception {
        String id = "USA";
        when(countryRepository.existsById(any())).thenReturn(true);
        doNothing().when(countryRepository).deleteById(any());

        mockMvc.perform(get("/country/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));


        mockMvc.perform(delete("/country/delete/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/country/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(countryRepository, times(2)).findAllByIsActiveTrue();
    }

}
