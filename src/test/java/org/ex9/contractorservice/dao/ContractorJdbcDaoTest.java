package org.ex9.contractorservice.dao;

import org.ex9.contractorservice.dto.contractor.SearchContractorRequestDto;
import org.ex9.contractorservice.exception.ContractorNotFoundException;
import org.ex9.contractorservice.model.Contractor;
import org.ex9.contractorservice.model.Country;
import org.ex9.contractorservice.model.Industry;
import org.ex9.contractorservice.model.OrgForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class ContractorJdbcDaoTest {
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
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Autowired
	private ContractorJdbcDao contractorJdbcDao;

	@BeforeEach
	void setUp() throws Exception {
		jdbcTemplate.update("TRUNCATE TABLE contractor, country, industry, org_form", Map.of());

		// Вставляем тестовые данные через SQL
		jdbcTemplate.update("""
            INSERT INTO country (id, name, is_active) VALUES (:id, :name, :isActive)
            """, Map.of("id", "RU", "name", "Россия", "isActive", true));
		jdbcTemplate.update("""
            INSERT INTO industry (id, name, is_active) VALUES (:id, :name, :isActive)
            """, Map.of("id", 1, "name", "IT", "isActive", true));
		jdbcTemplate.update("""
            INSERT INTO org_form (id, name, is_active) VALUES (:id, :name, :isActive)
            """, Map.of("id", 1, "name", "ООО", "isActive", true));

		jdbcTemplate.update("""
            INSERT INTO contractor (
                id, name, name_full, inn, ogrn, country, industry, org_form
            ) VALUES (
                :id, :name, :nameFull, :inn, :ogrn, :country, :industry, :orgForm
            )
            """, Map.of("id", "CTR001",
				"name", "ООО Ромашка",
				"nameFull", "Общество с ограниченной ответственностью Ромашка",
				"inn", "123456789012",
				"ogrn", "1234567890123",
				"country", "RU",
				"industry", 1,
				"orgForm", 1));
		jdbcTemplate.update("""
            INSERT INTO contractor (
                id, parent_id, name, name_full, inn, ogrn, country, industry, org_form
            ) VALUES (
                :id, :parentId, :name, :nameFull, :inn, :ogrn, :country, :industry, :orgForm
            )
            """, Map.of(
				"id", "CTR002",
				"parentId", "CTR001",
				"name", "ООО Роза",
				"nameFull", "Общество с ограниченной ответственностью Роза",
				"inn", "987654321098",
				"ogrn", "9876543210987",
				"country", "RU",
				"industry", 1,
				"orgForm", 1
		));
	}

	@Test
	@DisplayName("findById() return exist contractor")
	void findById_existingContractor_shouldReturnContractor() {
		Optional<Contractor> contractorOpt = contractorJdbcDao.findById("CTR001");
		assertTrue(contractorOpt.isPresent(), "Contractor should be found");

		Contractor contractor = contractorOpt.get();
		assertEquals("CTR001", contractor.getId());
		assertEquals("ООО Ромашка", contractor.getName());
		assertEquals("Общество с ограниченной ответственностью Ромашка", contractor.getNameFull());
		assertEquals("123456789012", contractor.getInn());
		assertEquals("1234567890123", contractor.getOgrn());
		assertEquals("RU", contractor.getCountry().getId());
		assertEquals("Россия", contractor.getCountry().getName());
		assertEquals(1, contractor.getIndustry().getId());
		assertEquals("IT", contractor.getIndustry().getName());
		assertEquals(1, contractor.getOrgForm().getId());
		assertEquals("ООО", contractor.getOrgForm().getName());
		assertNull(contractor.getParent().getId());
		assertTrue(contractor.getIsActive());
	}

	@Test
	@DisplayName("findById() return null")
	void findById_nonExistingContractor_shouldReturnEmpty() {
		Optional<Contractor> contractorOpt = contractorJdbcDao.findById("NON_EXISTENT");
		assertFalse(contractorOpt.isPresent(), "Contractor should not be found");
	}

	@Test
	@DisplayName("findById() return new contractor")
	void insert_newContractor_shouldCreateAndReturnContractor() {
		Contractor newContractor = Contractor.builder()
				.id("CTR004")
				.name("ООО Новый")
				.nameFull("Общество с ограниченной ответственностью Новый")
				.inn("555666777888")
				.ogrn("5556667778889")
				.country(Country.builder().id("RU").name("Россия").build())
				.industry(Industry.builder().id(1).name("IT").build())
				.orgForm(OrgForm.builder().id(1).name("ООО").build())
				.parent(Contractor.builder().id("CTR001").name("ООО Ромашка").build())
				.build();

		Contractor savedContractor = contractorJdbcDao.insert(newContractor);

		assertEquals("CTR004", savedContractor.getId());
		assertEquals("ООО Новый", savedContractor.getName());
		assertEquals("CTR001", savedContractor.getParent().getId());
		assertNotNull(savedContractor.getCreateDate());

		Optional<Contractor> foundContractor = contractorJdbcDao.findById("CTR004");
		assertTrue(foundContractor.isPresent());
	}

	@Test
	@DisplayName("findById() return updated contractor")
	void update_existingContractor_shouldUpdateContractor() {
		Contractor contractor = contractorJdbcDao.findById("CTR001").orElseThrow();
		contractor.setName("ООО Ромашка Обновлённая");
		contractor.setInn("999888777666");

		Contractor updatedContractor = contractorJdbcDao.update(contractor);

		assertEquals("CTR001", updatedContractor.getId());
		assertEquals("ООО Ромашка Обновлённая", updatedContractor.getName());
		assertEquals("999888777666", updatedContractor.getInn());
		assertNotNull(updatedContractor.getModifyDate());

		Contractor foundContractor = contractorJdbcDao.findById("CTR001").orElseThrow();
		assertEquals("ООО Ромашка Обновлённая", foundContractor.getName());
		assertEquals("999888777666", foundContractor.getInn());
	}

	@Test
	@DisplayName("save() throw exception")
	void update_nonExistingContractor_shouldThrowException() {
		Contractor contractor = Contractor.builder()
				.id("NON_EXISTENT")
				.name("ООО Несуществующий")
				.build();

		assertThrows(ContractorNotFoundException.class, () -> contractorJdbcDao.update(contractor));
	}

	@Test
	@DisplayName("search() return found contractor by id")
	void search_byContractorId_shouldReturnSingleContractor() {
		SearchContractorRequestDto request = new SearchContractorRequestDto();
		request.setContractorId("CTR001");
		request.setPage(0);
		request.setSize(10);

		List<Contractor> contractors = contractorJdbcDao.search(request);
		assertEquals(1, contractors.size());
		assertEquals("CTR001", contractors.get(0).getId());
	}

	@Test
	@DisplayName("search() return found contractor by parent id")
	void search_byParentId_shouldReturnContractors() {
		SearchContractorRequestDto request = new SearchContractorRequestDto();
		request.setParentId("CTR001");
		request.setPage(0);
		request.setSize(10);

		List<Contractor> contractors = contractorJdbcDao.search(request);
		assertEquals(1, contractors.size());
		assertEquals("CTR002", contractors.get(0).getId());
		assertEquals("CTR001", contractors.get(0).getParent().getId());
	}

	@Test
	@DisplayName("search() return found contractor by multi-search string")
	void search_byContractorSearch_shouldReturnMatchingContractors() {
		SearchContractorRequestDto request = new SearchContractorRequestDto();
		request.setContractorSearch("Ромашка");
		request.setPage(0);
		request.setSize(10);

		List<Contractor> contractors = contractorJdbcDao.search(request);
		assertEquals(1, contractors.size());
		assertEquals("CTR001", contractors.get(0).getId());

		request.setContractorSearch("987654321098");
		contractors = contractorJdbcDao.search(request);
		assertEquals(1, contractors.size());
		assertEquals("CTR002", contractors.get(0).getId());
	}

	@Test
	@DisplayName("search() return found contractor by contry name")
	void search_byCountry_shouldReturnContractors() {
		SearchContractorRequestDto request = new SearchContractorRequestDto();
		request.setCountry("Рос");
		request.setPage(0);
		request.setSize(10);

		List<Contractor> contractors = contractorJdbcDao.search(request);
		assertEquals(2, contractors.size());
		assertTrue(contractors.stream().anyMatch(c -> c.getId().equals("CTR001")));
		assertTrue(contractors.stream().anyMatch(c -> c.getId().equals("CTR002")));
	}

	@Test
	@DisplayName("search() return found contractor by industry id")
	void search_byIndustry_shouldReturnContractors() {
		SearchContractorRequestDto request = new SearchContractorRequestDto();
		request.setIndustry(1);
		request.setPage(0);
		request.setSize(10);

		List<Contractor> contractors = contractorJdbcDao.search(request);
		assertEquals(2, contractors.size());
		assertTrue(contractors.stream().anyMatch(c -> c.getId().equals("CTR001")));
		assertTrue(contractors.stream().anyMatch(c -> c.getId().equals("CTR002")));
	}

	@Test
	@DisplayName("search() return found contractor by orgForm name")
	void search_byOrgForm_shouldReturnContractors() {
		SearchContractorRequestDto request = new SearchContractorRequestDto();
		request.setOrgForm("ООО");
		request.setPage(0);
		request.setSize(10);

		List<Contractor> contractors = contractorJdbcDao.search(request);
		assertEquals(2, contractors.size());
		assertTrue(contractors.stream().anyMatch(c -> c.getId().equals("CTR001")));
		assertTrue(contractors.stream().anyMatch(c -> c.getId().equals("CTR002")));
	}

	@Test
	@DisplayName("search() return found contractors by page size")
	void search_withPagination_shouldReturnCorrectPage() {
		SearchContractorRequestDto request = new SearchContractorRequestDto();
		request.setPage(0);
		request.setSize(1);

		List<Contractor> contractors = contractorJdbcDao.search(request);
		assertEquals(1, contractors.size());
		assertEquals("CTR001", contractors.get(0).getId());

		request.setPage(1);
		contractors = contractorJdbcDao.search(request);
		assertEquals(1, contractors.size());
		assertEquals("CTR002", contractors.get(0).getId());
	}

	@Test
	@DisplayName("search() return all contractors")
	void search_withEmptyFilters_shouldReturnAllContractors() {
		SearchContractorRequestDto request = new SearchContractorRequestDto();
		request.setPage(0);
		request.setSize(10);

		List<Contractor> contractors = contractorJdbcDao.search(request);
		assertEquals(2, contractors.size());
		assertTrue(contractors.stream().anyMatch(c -> c.getId().equals("CTR001")));
		assertTrue(contractors.stream().anyMatch(c -> c.getId().equals("CTR002")));
	}
}