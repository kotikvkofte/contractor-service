package org.ex9.contractorservice.mapper;

import org.ex9.contractorservice.dto.contractor.ContractorRequestDto;
import org.ex9.contractorservice.dto.contractor.ContractorResponseDto;
import org.ex9.contractorservice.model.Contractor;
import org.ex9.contractorservice.model.Country;
import org.ex9.contractorservice.model.Industry;
import org.ex9.contractorservice.model.OrgForm;

public final class ContractorMapper {

	private ContractorMapper() {
	}

	public static ContractorResponseDto toDto(Contractor contractor) {

		var builder = ContractorResponseDto.builder();

		builder.id(contractor.getId());
		builder.parentId(contractor.getParent() != null ? contractor.getParent().getId() : null);
		builder.name(contractor.getName());
		builder.nameFull(contractor.getNameFull());
		builder.inn(contractor.getInn());
		builder.ogrn(contractor.getOgrn());
		builder.country(contractor.getCountry().getName());
		builder.industry(contractor.getIndustry().getName());
		builder.orgForm(contractor.getOrgForm().getName());

		return builder.build();
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

}
