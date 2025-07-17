package org.ex9.contractorservice.dao;

import org.ex9.contractorservice.dto.contractor.SearchContractorRequestDto;
import org.ex9.contractorservice.exception.ContractorNotFoundException;
import org.ex9.contractorservice.model.Contractor;
import org.ex9.contractorservice.model.Country;
import org.ex9.contractorservice.model.Industry;
import org.ex9.contractorservice.model.OrgForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ContractorJdbcDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String TEMPLATE = """
            SELECT c.id,
                   p.id as parentId,
                   p.name as parentName,
                   c.name,
                   c.name_full,
                   c.inn,
                   c.ogrn,
                   co.id as countryId,
                   co.name as countryName,
                   ind.id as industryId,
                   ind.name as industryName,
                   of.id as orgFormId,
                   of.name as orgFormName,
                   c.create_date,
                   c.modify_date,
                   c.create_user_id,
                   c.modify_user_id,
                   c.is_active
            FROM contractor c
                LEFT JOIN contractor p ON c.parent_id = p.id AND p.is_active = true
                LEFT JOIN country co ON c.country = co.id AND co.is_active = true
                LEFT JOIN industry ind ON c.industry = ind.id AND ind.is_active = true
                LEFT JOIN org_form of ON c.org_form = of.id AND of.is_active = true
            WHERE c.is_active = true
            """;

    @Autowired
    public ContractorJdbcDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public Optional<Contractor> findById(String id) {

        StringBuilder sql = new StringBuilder(template);
        sql.append(" AND c.id = :id");
        try {
            SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
            var contractor = namedParameterJdbcTemplate.queryForObject(sql.toString(), namedParameters, (rs, rowId) -> toContractor(rs));
            return Optional.of(contractor);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

    }

    /**
     * Обновляет существующего контрагента в базе данных.
     * Обновляет все поля, кроме {@code create_date}.
     * Поле {@code modify_date} устанавливается в текущую дату.
     *
     * @param contractor сущность {@link Contractor} для обновления
     * @return обновлённая сущность {@link Contractor}
     * @throws ContractorNotFoundException если контрагент не найден после обновления
     */
    public Contractor update(Contractor contractor) {
        String sql = """
            UPDATE contractor
            SET parent_id = :parentId,
                name = :name,
                name_full = :nameFull,
                inn = :inn,
                ogrn = :ogrn,
                country = :countryId,
                industry = :industryId,
                org_form = :orgFormId,
                modify_date = :modifyDate
            WHERE id = :id
            """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", contractor.getId());
        params.addValue("parentId", contractor.getParent() != null ? contractor.getParent().getId() : null);
        params.addValue("name", contractor.getName());
        params.addValue("nameFull", contractor.getNameFull());
        params.addValue("inn", contractor.getInn());
        params.addValue("ogrn", contractor.getOgrn());
        params.addValue("countryId", contractor.getCountry() != null ? contractor.getCountry().getId() : null);
        params.addValue("industryId", contractor.getIndustry() != null ? contractor.getIndustry().getId() : null);
        params.addValue("orgFormId", contractor.getOrgForm() != null ? contractor.getOrgForm().getId() : null);
        params.addValue("modifyDate", LocalDateTime.now());

        namedParameterJdbcTemplate.update(sql, params);
        return findById(contractor.getId()).orElseThrow(() ->
                new ContractorNotFoundException("Contractor with ID " + contractor.getId() + " not found after update"));
    }

    /**
     * Создаёт нового контрагента в базе данных.
     * Устанавливает служебные поля {@code create_date}, {@code modify_date}.
     * Поле {@code is_active} устанавливается в {@code true}.
     *
     * @param contractor сущность {@link Contractor} для создания
     * @return созданная сущность {@link Contractor}
     * @throws ContractorNotFoundException если контрагент не найден после создания
     */
    public Contractor insert(Contractor contractor) {
        String sql = """
            INSERT INTO contractor (
                id, parent_id, name, name_full, inn, ogrn, country, industry, org_form
            )
            VALUES (
                :id, :parentId, :name, :nameFull, :inn, :ogrn, :countryId, :industryId, :orgFormId
            )
            """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", contractor.getId());
        params.addValue("parentId", contractor.getParent() != null ? contractor.getParent().getId() : null);
        params.addValue("name", contractor.getName());
        params.addValue("nameFull", contractor.getNameFull());
        params.addValue("inn", contractor.getInn());
        params.addValue("ogrn", contractor.getOgrn());
        params.addValue("countryId", contractor.getCountry() != null ? contractor.getCountry().getId() : null);
        params.addValue("industryId", contractor.getIndustry() != null ? contractor.getIndustry().getId() : null);
        params.addValue("orgFormId", contractor.getOrgForm() != null ? contractor.getOrgForm().getId() : null);

        namedParameterJdbcTemplate.update(sql, params);
        return findById(contractor.getId()).orElseThrow(() ->
                new ContractorNotFoundException("Контрагент с ID " + contractor.getId() + " не найден после создания"));
    }

    /**
     * Выполняет поиск активных контрагентов с фильтрацией и пагинацией.
     *
     * @param request параметры фильтрации
     * @return {@link Page} с найденными контрагентами
     */
    public List<Contractor> search(SearchContractorRequestDto request) {
        StringBuilder sql = new StringBuilder(template);

        MapSqlParameterSource params = new MapSqlParameterSource();
        List<String> conditions = new ArrayList<>();

        if (request.getContractorId() != null && !request.getContractorId().isBlank()) {
            conditions.add("c.id = :contractorId");
            params.addValue("contractorId", request.getContractorId());
        }
        if (request.getParentId() != null && !request.getParentId().isBlank()) {
            conditions.add("c.parent_id = :parentId");
            params.addValue("parentId", request.getParentId());
        }
        if (request.getContractorSearch() != null && !request.getContractorSearch().isBlank()) {
            String search = "%" + request.getContractorSearch().toLowerCase() + "%";
            conditions.add("""
                (LOWER(c.name) LIKE :contractorSearch OR
                 LOWER(c.name_full) LIKE :contractorSearch OR
                 LOWER(c.inn) LIKE :contractorSearch OR
                 LOWER(c.ogrn) LIKE :contractorSearch)
                """);
            params.addValue("contractorSearch", search);
        }
        if (request.getCountry() != null && !request.getCountry().isBlank()) {
            conditions.add("LOWER(co.name) LIKE :country");
            params.addValue("country", "%" + request.getCountry().toLowerCase() + "%");
        }
        if (request.getIndustry() != null) {
            conditions.add("c.industry = :industry");
            params.addValue("industry", request.getIndustry());
        }
        if (request.getOrgForm() != null && !request.getOrgForm().isBlank()) {
            conditions.add("LOWER(of.name) LIKE :orgForm");
            params.addValue("orgForm", "%" + request.getOrgForm().toLowerCase() + "%");
        }

        if (!conditions.isEmpty()) {
            sql.append(" AND ").append(String.join(" AND ", conditions));
        }

        sql.append(" ORDER BY c.id OFFSET :offset LIMIT :limit");
        params.addValue("offset", request.getPage() * request.getSize());
        params.addValue("limit", request.getSize());

        return namedParameterJdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> toContractor(rs));
    }

    private Contractor toContractor(ResultSet rs) throws SQLException {
        var contractorBuilder = Contractor.builder();
        var id = rs.getString("id");
        contractorBuilder.id(id);
        contractorBuilder.name(rs.getString("name"));
        contractorBuilder.nameFull(rs.getString("name_full"));
        contractorBuilder.inn(rs.getString("inn"));
        contractorBuilder.ogrn(rs.getString("ogrn"));
        contractorBuilder.createDate(rs.getDate("create_date"));
        contractorBuilder.modifyDate(rs.getDate("modify_date"));
        contractorBuilder.createUserId(rs.getString("create_user_id"));
        contractorBuilder.modifyUserId(rs.getString("modify_user_id"));

        Country country = Country.builder()
                .id(rs.getString("countryid"))
                .name(rs.getString("countryname"))
                .build();
        Industry industry = Industry.builder()
                .id(rs.getInt("industryid"))
                .name(rs.getString("industryname"))
                .build();
        OrgForm orgForm = OrgForm.builder()
                .id(rs.getInt("orgformid"))
                .name(rs.getString("orgformname"))
                .build();

        Contractor parent = new Contractor();
        parent.setId(rs.getString("parentId"));
        parent.setName(rs.getString("parentName"));

        contractorBuilder.country(country);
        contractorBuilder.industry(industry);
        contractorBuilder.orgForm(orgForm);
        contractorBuilder.parent(parent);

        return contractorBuilder.build();
    }

}
