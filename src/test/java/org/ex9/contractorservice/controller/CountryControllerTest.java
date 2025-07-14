package org.ex9.contractorservice.controller;

import org.ex9.contractorservice.dto.country.CountryRequestDto;
import org.ex9.contractorservice.dto.country.CountryResponseDto;
import org.ex9.contractorservice.exception.CountryNotFoundException;
import org.ex9.contractorservice.service.CountryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryControllerTest {

    @Mock
    CountryService countryService;

    @InjectMocks
    CountryController countryController;

    @Test
    @DisplayName("getAllActive() returns 200 OK with list of countries")
    void getAllActive_ReturnsValidResponseEntity() {
        var countrys = List.of(new CountryResponseDto("FRT", "first country"),
                new CountryResponseDto("SCD", "second country"));

        doReturn(countrys).when(this.countryService).findAll();

        var response = this.countryController.getAllActive();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(countrys, response.getBody());
    }

    @Test
    @DisplayName("getById() returns 200 OK with found country")
    void getById_ReturnsValidResponseEntity() {
        var country = new CountryResponseDto("FRT", "first country");

        doReturn(country).when(this.countryService).findById(country.getId());

        var response = this.countryController.getById(country.getId());

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(country, response.getBody());
    }

    @Test
    @DisplayName("getById() throw exception")
    void getById_WhenCountryNotFound_ShouldThrowException() {
        var country = new CountryResponseDto("FRT", "first country");

       doThrow(new CountryNotFoundException("Country not found with id " + country.getId()))
               .when(this.countryService).findById(country.getId());

       assertThrows(CountryNotFoundException.class, () -> this.countryController.getById(country.getId()));
        verify(countryService).findById(country.getId());
    }

    @Test
    @DisplayName("save() returns 200 OK with new or edited country")
    void save_ReturnsValidResponseEntity() {
        CountryRequestDto request = new CountryRequestDto("NEW", "New Country");
        CountryResponseDto responseExpected = new CountryResponseDto("NEW", "New Country");

        doReturn(responseExpected).when(this.countryService).save(request);
        var response = this.countryController.save(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseExpected, response.getBody());
    }

    @Test
    @DisplayName("save() returns 204 NO_CONTENT")
    void delete_WhenCountryFound_ShouldReturnStatusNoContent() {
        String countryId = "FRT";

        doNothing().when(countryService).delete(countryId);

        var response = this.countryController.delete(countryId);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(countryService, times(1)).delete(countryId);
    }

    @Test
    @DisplayName("delete() throw exception")
    void delete_WhenCountryNotFound_ShouldThrowException() {
        String countryId = "FRT";

        doThrow(new CountryNotFoundException("Country not found with id " + countryId))
                .when(countryService).delete(countryId);

        assertThrows(CountryNotFoundException.class, () -> this.countryController.delete(countryId));
        verify(countryService, times(1)).delete(countryId);
    }

}
