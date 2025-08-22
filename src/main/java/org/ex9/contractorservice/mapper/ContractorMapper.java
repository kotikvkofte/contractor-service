package org.ex9.contractorservice.mapper;

import org.ex9.contractorservice.dto.contractor.ContractorRequestDto;
import org.ex9.contractorservice.dto.contractor.ContractorResponseDto;
import org.ex9.contractorservice.dto.rabbit.ContractorDto;
import org.ex9.contractorservice.model.Contractor;
import org.ex9.contractorservice.model.Country;
import org.ex9.contractorservice.model.Industry;
import org.ex9.contractorservice.model.OrgForm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public final class ContractorMapper {

    private ContractorMapper() {
    }

    public static ContractorResponseDto toDto(Contractor contractor) {

        return ContractorResponseDto.builder()
                .id(contractor.getId())
                .parentId(contractor.getParent() != null ? contractor.getParent().getId() : null)
                .name(contractor.getName())
                .nameFull(contractor.getNameFull())
                .inn(contractor.getInn())
                .ogrn(contractor.getOgrn())
                .country(contractor.getCountry().getName())
                .industry(contractor.getIndustry().getName())
                .orgForm(contractor.getOrgForm().getName())
                .build();
    }

    public static Contractor toContractor(ContractorRequestDto request) {
        Contractor parent = request.getParentId() != null ? Contractor.builder().id(request.getParentId()).build() : null;
        Country country = Country.builder().id(request.getCountryId()).build();
        Industry industry = Industry.builder().id(request.getIndustryId()).build();
        OrgForm orgForm = OrgForm.builder().id(request.getOrgFormId()).build();

        return Contractor.builder()
                .id(request.getId())
                .parent(parent)
                .name(request.getName())
                .nameFull(request.getNameFull())
                .inn(request.getInn())
                .ogrn(request.getOgrn())
                .country(country)
                .industry(industry)
                .orgForm(orgForm)
                .build();

    }

    public static Contractor toContractor(ResultSet rs) throws SQLException {
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

    public static Contractor toContractor(ContractorRequestDto request,
                                          Country country,
                                          Industry industry,
                                          OrgForm orgForm) {
        Contractor parent = request.getParentId() != null ? Contractor.builder().id(request.getParentId()).build() : null;

        return Contractor.builder()
                .id(request.getId())
                .parent(parent)
                .name(request.getName())
                .nameFull(request.getNameFull())
                .inn(request.getInn())
                .ogrn(request.getOgrn())
                .country(country)
                .industry(industry)
                .orgForm(orgForm)
                .build();

    }

    public static Contractor toContractor(ContractorRequestDto request,
                                          Contractor parent,
                                          Country country,
                                          Industry industry,
                                          OrgForm orgForm) {

        return Contractor.builder()
                .id(request.getId())
                .parent(parent)
                .name(request.getName())
                .nameFull(request.getNameFull())
                .inn(request.getInn())
                .ogrn(request.getOgrn())
                .country(country)
                .industry(industry)
                .orgForm(orgForm)
                .build();

    }

    public static ContractorDto toRabbitDto(Contractor contractor) {
        return ContractorDto.builder()
                .id(contractor.getId())
                .name(contractor.getName())
                .inn(contractor.getInn())
                .modifyDateTime(LocalDateTime.now())
                .build();
    }

}
