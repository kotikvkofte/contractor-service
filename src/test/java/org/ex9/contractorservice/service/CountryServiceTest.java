package org.ex9.contractorservice.service;

import org.ex9.contractorservice.dto.country.CountryRequestDto;
import org.ex9.contractorservice.dto.country.CountryResponseDto;
import org.ex9.contractorservice.exception.CountryNotFoundException;
import org.ex9.contractorservice.model.Country;
import org.ex9.contractorservice.repository.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @Mock
    CountryRepository countryRepository;

    @InjectMocks
    CountryService countryService;

    @Test
    @DisplayName("findAll() returns active countries mapped to DTOs")
    void findAll_ReturnsListOfActiveCountries() {
        var activeCountries1 = new Country("FR", "France", true);
        var activeCountries2 = new Country("RUS", "Russia", true);
        var inactiveCountries = new Country("US", "USA", false);

        when(countryRepository.findAllByIsActiveTrue()).thenReturn(List.of(activeCountries1, activeCountries2));

        List<CountryResponseDto> result = countryService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(activeCountries1.getId(), result.get(0).getId());
        assertEquals(activeCountries2.getId(), result.get(1).getId());
        assertEquals(activeCountries1.getName(), result.get(0).getName());
        assertEquals(activeCountries2.getName(), result.get(1).getName());

        verify(countryRepository, times(1)).findAllByIsActiveTrue();
    }

    @Test
    @DisplayName("save() inserts new country and returns DTO")
    void save_WhenIdNotExists_ShouldInsertNewCountry() {

        var request = new CountryRequestDto("DE", "Germany");
        var newCountry = new Country("DE", "Germany", true);

        when(countryRepository.existsById("DE")).thenReturn(false);
        when(countryRepository.findById("DE")).thenReturn(Optional.of(newCountry));

        CountryResponseDto result = countryService.save(request);

        assertEquals("DE", result.getId());
        assertEquals("Germany", result.getName());

        verify(countryRepository).existsById("DE");
        verify(countryRepository).insert(newCountry);
    }

    @Test
    @DisplayName("save() update country and returns DTO")
    void save_WhenIdExists_ShouldUpdateCountry() {

        var request = new CountryRequestDto("DE", "Germany update");
        var updateCountry = new Country("DE", "Germany update", true);

        when(countryRepository.existsById("DE")).thenReturn(true);
        when(countryRepository.findById("DE")).thenReturn(Optional.of(updateCountry));

        CountryResponseDto result = countryService.save(request);

        assertEquals("DE", result.getId());
        assertEquals("Germany update", result.getName());

        verify(countryRepository).existsById("DE");
        verify(countryRepository).save(updateCountry);
    }

    @Test
    @DisplayName("delete() should logical delete of country")
    void delete_ShouldPerformLogicalDelete() {
        String id = "DE";
        when(countryRepository.existsById(id)).thenReturn(true);
        doNothing().when(countryRepository).deleteById(id);

        assertDoesNotThrow(() -> countryService.delete(id));
        verify(countryRepository).deleteById(id);
    }

    @Test
    @DisplayName("delete() should logical delete of country")
    void delete_WhenCountryNotFound_ShouldThrowException() {
        String id = "DE";
        when(countryRepository.existsById(id)).thenReturn(false);

        assertThrows(CountryNotFoundException.class, () -> countryService.delete(id));
        verify(countryRepository, never()).deleteById(id);
    }

    @Test
    @DisplayName("findById() return dto when id exists")
    void findById_WhenExists_ShouldReturnCountry() {
        String id = "DE";
        Country country = new Country("DE", "Germany", true);

        when(countryRepository.findById(id)).thenReturn(Optional.of(country));

        CountryResponseDto result = countryService.findById(id);

        assertNotNull(result);
        assertEquals("DE", result.getId());
        assertEquals("Germany", result.getName());

        verify(countryRepository).findById(id);
    }

    @Test
    @DisplayName("findById() throw exception when id not exists")
    void findById_WhenNotExists_ShouldThrowException() {
        String id = "DE";

        when(countryRepository.findById(id)).thenReturn(Optional.empty());


        assertThrows(CountryNotFoundException.class, () -> countryService.findById(id));

        verify(countryRepository).findById(id);
    }

}
