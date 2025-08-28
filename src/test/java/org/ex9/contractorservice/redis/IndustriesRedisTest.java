package org.ex9.contractorservice.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.testcontainers.RedisContainer;
import org.ex9.contractorservice.dto.industry.IndustryRequestDto;
import org.ex9.contractorservice.model.Industry;
import org.ex9.contractorservice.repository.IndustryRepository;
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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class IndustriesRedisTest {

    private static final String INDUSTRIES_CACHE_PREFIX = "industries";

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
    IndustryRepository industryRepository;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        IndustryRepository mockIndustryRepository() {
            return Mockito.mock(IndustryRepository.class);
        }
    }

    @BeforeEach
    void setup() {
        cacheManager.getCache(INDUSTRIES_CACHE_PREFIX).clear();

        reset(industryRepository);

        Industry industry1 = Industry.builder()
                .id(1)
                .name("test1")
                .isActive(true)
                .build();
        Industry industry2 = Industry.builder()
                .id(2)
                .name("test2")
                .isActive(true)
                .build();

        when(industryRepository.findAllByIsActiveTrue())
                .thenReturn(List.of(industry1, industry2));
    }

    @Test
    void getAll_shouldUseRedisCache_betweenCalls() throws Exception {
        mockMvc.perform(get("/industry/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));

        mockMvc.perform(get("/industry/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(industryRepository, times(1)).findAllByIsActiveTrue();
    }

    @Test
    void save_shouldUpdateRedisCache() throws Exception {
        Industry industry = Industry.builder()
                .id(1)
                .name("test1")
                .isActive(true)
                .build();
        when(industryRepository.existsById(any())).thenReturn(true);
        when(industryRepository.save(any())).thenReturn(industry);

        mockMvc.perform(get("/industry/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));

        var request = new ObjectMapper().writeValueAsString(new IndustryRequestDto(1, "test1"));

        mockMvc.perform(put("/industry/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk());

        mockMvc.perform(get("/industry/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(industryRepository, times(2)).findAllByIsActiveTrue();
    }

    @Test
    void delete_shouldUpdateRedisCache() throws Exception {
        Integer id = 1;
        when(industryRepository.existsById(any())).thenReturn(true);
        doNothing().when(industryRepository).deleteById(any());

        mockMvc.perform(get("/industry/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));


        mockMvc.perform(delete("/industry/delete/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());

        mockMvc.perform(get("/industry/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(industryRepository, times(2)).findAllByIsActiveTrue();
    }

}
