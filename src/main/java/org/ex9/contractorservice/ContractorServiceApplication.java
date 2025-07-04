package org.ex9.contractorservice;

import org.ex9.contractorservice.model.Industry;
import org.ex9.contractorservice.repository.IndustryRepository;
import org.ex9.contractorservice.service.CountryService;
import org.ex9.contractorservice.service.IndustryService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ContractorServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContractorServiceApplication.class, args);
	}
}
