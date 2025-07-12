package org.ex9.contractorservice.repository;

import org.ex9.contractorservice.model.Country;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJdbcTest
@Testcontainers
@ActiveProfiles("test")
class CountryRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("contractor-service-test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(false);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private CountryRepository countryRepository;

    @BeforeEach
    void setup() {
        countryRepository.insert(new Country("KZ", "Kazakhstan", true));
        countryRepository.insert(new Country("RUS", "Russia", true));
        countryRepository.insert(new Country("US", "USA", false));
    }

    @Test
    @DisplayName("findAllByIsActiveTrue() should return list of active country's")
    void findAllByIsActiveTrue_ShouldReturnOnlyActiveCountries() {
        List<Country> countries = countryRepository.findAllByIsActiveTrue();
        assertEquals(2, countries.size());

        assertEquals("KZ", countries.get(0).getId());
        assertEquals("RUS", countries.get(1).getId());
        assertEquals("Kazakhstan", countries.get(0).getName());
        assertEquals("Russia", countries.get(1).getName());
    }

    @Test
    @DisplayName("insert() should add new country to database")
    void insert_ShouldAddNewCountry() {
        Country newCountry = new Country("UK", "United Kingdom", true);

        countryRepository.insert(newCountry);
        List<Country> countries = countryRepository.findAllByIsActiveTrue();

        assertEquals(3, countries.size());
        assertTrue(countries.stream().anyMatch(c -> c.getId().equals("UK")));
    }

    @Test
    @DisplayName("deleteById() should deactivate country by id")
    void deleteById_ShouldDeactivateCountry() {
        countryRepository.deleteById("KZ");

        List<Country> activeCountries = countryRepository.findAllByIsActiveTrue();
        assertEquals(1, activeCountries.size());
        assertEquals("RUS", activeCountries.get(0).getId());

        assertTrue(countryRepository.existsById("KZ"));
        Optional<Country> deactivatedCountry = countryRepository.findById("KZ");
        assertTrue(deactivatedCountry.isEmpty());
    }
}