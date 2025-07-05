package org.ex9.contractorservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.ex9.contractorservice.dto.industry.IndustryRequestDto;
import org.ex9.contractorservice.dto.industry.IndustryResponseDto;
import org.ex9.contractorservice.exception.IndustryNotFoundException;
import org.ex9.contractorservice.service.IndustryService;
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
@RequestMapping("industry")
public class IndustryController {

    private final IndustryService industryService;

    @Autowired
    public IndustryController(IndustryService industryService) {
        this.industryService = industryService;
    }

    @GetMapping("/all")
    @Operation(summary = "Get industry's list")
    public ResponseEntity<List<IndustryResponseDto>> getAllActive() {
        return ResponseEntity.ok(industryService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Receiving by id")
    public ResponseEntity<IndustryResponseDto> getById(@PathVariable @NotNull int id) {
        try {
            return ResponseEntity.ok(industryService.findById(id));
        } catch (IndustryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/save")
    @Operation(summary = "Creating a new or updating an existing industry")
    public ResponseEntity<IndustryResponseDto> save(@RequestBody IndustryRequestDto industryRequestDto) {
        try {
            return ResponseEntity.ok(industryService.save(industryRequestDto));
        } catch (IndustryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Logical delete a country")
    public ResponseEntity<Void> delete(@PathVariable @NotNull int id) {
        try {
            industryService.delete(id);
            return ResponseEntity.accepted().build();
        } catch (IndustryNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
