package org.ex9.contractorservice.repository;

import org.ex9.contractorservice.model.Country;
import org.ex9.contractorservice.model.Industry;
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
class IndustryRepositoryTest {
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

	@BeforeEach
	void setup() {
		industryRepository.save(new Industry(null, "industry1", true));
		industryRepository.save(new Industry(null, "industry2", true));
		industryRepository.save(new Industry(null, "industry3", false));
	}

	@Autowired
	private IndustryRepository industryRepository;

	@Test
	@DisplayName("findAllByIsActiveTrue() should return list of active country's")
	void findAllByIsActiveTrue_ShouldReturnOnlyActiveIndustries() {
		List<Industry> industries = industryRepository.findAllByIsActiveTrue();

		assertEquals(2, industries.size());
		assertEquals("industry1", industries.get(0).getName());
		assertEquals("industry2", industries.get(1).getName());
	}

	@Test
	@DisplayName("deleteById() should deactivate industry by id")
	void deleteById_ShouldDeactivateIndustry() {
		var deleteIndustry = industryRepository.findAllByIsActiveTrue().get(0);
		industryRepository.deleteById(deleteIndustry.getId());

		List<Industry> activeIndustry = industryRepository.findAllByIsActiveTrue();
		assertEquals(1, activeIndustry.size());
		assertEquals("industry2", activeIndustry.get(0).getName());

	}
}