package com.wanted.jaringoby.domains.customer.controllers;

import com.wanted.jaringoby.common.response.Response;
import com.wanted.jaringoby.common.validations.BindingResultChecker;
import com.wanted.jaringoby.common.validations.ValidationSequence;
import com.wanted.jaringoby.domains.customer.applications.CreateCustomerService;
import com.wanted.jaringoby.domains.customer.dtos.http.CreateCustomerRequestDto;
import com.wanted.jaringoby.domains.customer.dtos.http.CreateCustomerResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1.0/customer/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CreateCustomerService createCustomerService;
    private final BindingResultChecker bindingResultChecker;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Response<CreateCustomerResponseDto> create(
            @Validated(ValidationSequence.class) @RequestBody
            CreateCustomerRequestDto createCustomerRequestDto,
            BindingResult bindingResult
    ) {
        bindingResultChecker.checkBindingErrors(bindingResult);

        return Response.of(createCustomerService.createCustomer(createCustomerRequestDto));
    }
}
