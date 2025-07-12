package org.ex9.contractorservice.repository;

import org.ex9.contractorservice.model.Industry;
import org.ex9.contractorservice.model.OrgForm;
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

import static org.junit.jupiter.api.Assertions.*;

@DataJdbcTest
@Testcontainers
@ActiveProfiles("test")
class OrgFormRepositoryTest {
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
	private OrgFormRepository orgFormRepository;

	@BeforeEach
	void setup() {
		orgFormRepository.save(new OrgForm(null, "OrgForm1", true));
		orgFormRepository.save(new OrgForm(null, "OrgForm2", true));
		orgFormRepository.save(new OrgForm(null, "OrgForm3", false));
	}

	@Test
	@DisplayName("findAllByIsActiveTrue() should return list of active orgForms")
	void findAllByIsActiveTrue_ShouldReturnOnlyActiveOrgForms() {
		List<OrgForm> orgForms = orgFormRepository.findAllByIsActiveTrue();

		assertEquals(2, orgForms.size());
		assertEquals("OrgForm1", orgForms.get(0).getName());
		assertEquals("OrgForm2", orgForms.get(1).getName());
	}

	@Test
	@DisplayName("deleteById() should deactivate orgForm by id")
	void deleteById_ShouldDeactivateOrgForm() {
		var deleteOrgForm = orgFormRepository.findAllByIsActiveTrue().get(0);
		orgFormRepository.deleteById(deleteOrgForm.getId());

		List<OrgForm> activeIndustry = orgFormRepository.findAllByIsActiveTrue();
		assertEquals(1, activeIndustry.size());
		assertEquals("OrgForm2", activeIndustry.get(0).getName());

	}

}