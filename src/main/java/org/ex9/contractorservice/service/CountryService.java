package org.ex9.contractorservice.service;

import org.ex9.contractorservice.dto.country.CountryRequestDto;
import org.ex9.contractorservice.dto.country.CountryResponseDto;
import org.ex9.contractorservice.exception.CountryNotFoundException;
import org.ex9.contractorservice.mapper.CountryMapper;
import org.ex9.contractorservice.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CountryService {

    private final CountryRepository countryRepository;

    @Autowired
    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public List<CountryResponseDto> findAll() {

        var countryList = countryRepository.findAllByIsActiveTrue();
        return countryList.stream().map(CountryMapper::toDto).toList();

    }

    @Transactional
    public CountryResponseDto save(CountryRequestDto request) {
        var c = CountryMapper.toCountry(request);
        if (countryRepository.existsById(c.getId())) {
            countryRepository.save(c);
        } else {
            countryRepository.insert(c);
        }
        var country = countryRepository.findById(c.getId()).orElseThrow(() -> new CountryNotFoundException("Country not found with id " + c.getId()));
        return CountryMapper.toDto(country);
    }

    public void delete(String id) {
        if (countryRepository.existsById(id)) {
            countryRepository.deleteById(id);
        } else {
            throw new CountryNotFoundException("Country not found with id " + id);
        }
    }

    public CountryResponseDto findById(String id) {
        var country = countryRepository.findById(id).orElseThrow(() -> new CountryNotFoundException("Country not found with id " + id));
        return CountryMapper.toDto(country);
    }

}
