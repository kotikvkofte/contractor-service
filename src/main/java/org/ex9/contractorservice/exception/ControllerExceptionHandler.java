package org.ex9.contractorservice.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.ex9.contractorservice.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    @ApiResponse(
            responseCode = "404",
            description = "Contractor not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    public ErrorResponse handleContractorNotFoundException(ContractorNotFoundException e) {
            return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(CountryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    @ApiResponse(
            responseCode = "404",
            description = "Country not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    public ErrorResponse handleCountryNotFoundException(CountryNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(IndustryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    @ApiResponse(
            responseCode = "404",
            description = "Industry not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    public ErrorResponse handleIndustryNotFoundException(IndustryNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(OrgFormNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    @ApiResponse(
            responseCode = "404",
            description = "Organizational form not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    public ErrorResponse handleOrgFormNotFoundException(OrgFormNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

}
