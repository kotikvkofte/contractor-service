package org.ex9.contractorservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.ex9.contractorservice.dto.country.CountryRequestDto;
import org.ex9.contractorservice.dto.country.CountryResponseDto;
import org.ex9.contractorservice.exception.CountryNotFoundException;
import org.ex9.contractorservice.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("country")
public class CountryController {

    private final CountryService countryService;

    @Autowired
    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping("/all")
    @Operation(summary = "Get country's list")
    public ResponseEntity<List<CountryResponseDto>> getAllActive() {
        return ResponseEntity.ok(countryService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Receiving by id")
    public ResponseEntity<CountryResponseDto> getById(@PathVariable @NotNull String id) {
        try {
            return ResponseEntity.ok(countryService.findById(id));
        } catch (CountryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/save")
    @Operation(summary = "Creating a new or updating an existing country")
    public ResponseEntity<CountryResponseDto> save(@RequestBody CountryRequestDto countryRequest) {
        return ResponseEntity.ok(countryService.save(countryRequest));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Logical delete a country")
    public ResponseEntity<Void> delete(@PathVariable @NotNull String id) {
        try {
            countryService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (CountryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
