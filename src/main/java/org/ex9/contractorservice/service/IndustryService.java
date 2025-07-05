package org.ex9.contractorservice.service;

import org.ex9.contractorservice.dto.industry.IndustryRequestDto;
import org.ex9.contractorservice.dto.industry.IndustryResponseDto;
import org.ex9.contractorservice.exception.IndustryNotFoundException;
import org.ex9.contractorservice.mapper.IndustryMapper;
import org.ex9.contractorservice.model.Industry;
import org.ex9.contractorservice.repository.IndustryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndustryService {

    private final IndustryRepository repository;

    @Autowired
    public IndustryService(IndustryRepository industryRepository) {
        this.repository = industryRepository;
    }

    public List<IndustryResponseDto> findAll() {
        var industryList = repository.findAllByIsActiveTrue();
        return industryList.stream().map(IndustryMapper::toDto).toList();
    }

    public IndustryResponseDto findById(int id) {
        var industry = repository.findById(id).orElseThrow(() -> new IndustryNotFoundException("Industry with id " + id + " not found"));
        return IndustryMapper.toDto(industry);
    }

    public IndustryResponseDto save(IndustryRequestDto request) {
        Industry industry = IndustryMapper.toIndustry(request);
        if (industry.getId() != null && !repository.existsById(industry.getId())) {
            throw new IndustryNotFoundException("Industry with id " + industry.getId() + " not found");
        }
        var newOrUpdatedIndustry = repository.save(industry);
        return IndustryMapper.toDto(newOrUpdatedIndustry);
    }

    public void delete(int id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new IndustryNotFoundException("Industry with id " + id + " not found");
        }
    }

}
