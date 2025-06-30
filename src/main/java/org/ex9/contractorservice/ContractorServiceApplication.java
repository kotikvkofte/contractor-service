package org.ex9.contractorservice;

import org.ex9.contractorservice.model.Country;
import org.ex9.contractorservice.model.Industry;
import org.ex9.contractorservice.model.OrgForm;
import org.ex9.contractorservice.repository.CountryRepository;
import org.ex9.contractorservice.repository.IndustryRepository;
import org.ex9.contractorservice.repository.OrgFormRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ContractorServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContractorServiceApplication.class, args);
	}

	@Bean
	public ApplicationRunner init(IndustryRepository repository) {
		return args -> {


			Industry newIndustry = Industry.builder()
					.name("тест industry")
					.build();

//			repository.save(country);

			repository.insert(newIndustry);
		};
	}
}
