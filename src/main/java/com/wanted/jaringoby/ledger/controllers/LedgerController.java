package com.wanted.jaringoby.ledger.controllers;

import com.wanted.jaringoby.common.response.Response;
import com.wanted.jaringoby.common.validations.BindingResultChecker;
import com.wanted.jaringoby.common.validations.ValidationSequence;
import com.wanted.jaringoby.ledger.applications.CreateLedgerService;
import com.wanted.jaringoby.ledger.applications.GetOngoingLedgerService;
import com.wanted.jaringoby.ledger.dtos.CreateLedgerRequestDto;
import com.wanted.jaringoby.ledger.dtos.CreateLedgerResponseDto;
import com.wanted.jaringoby.ledger.dtos.GetLedgerDetailResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer/v1.0/ledgers")
@RequiredArgsConstructor
public class LedgerController {

    private final GetOngoingLedgerService getOngoingLedgerService;
    private final CreateLedgerService createLedgerService;
    private final BindingResultChecker bindingResultChecker;

    @GetMapping("/now")
    @ResponseStatus(HttpStatus.OK)
    public Response<GetLedgerDetailResponseDto> now(
            @RequestAttribute("customerId") String customerId
    ) {
        return Response.of(getOngoingLedgerService.getOngoingLedger(customerId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Response<CreateLedgerResponseDto> create(
            @RequestAttribute("customerId") String customerId,
            @Validated(ValidationSequence.class) @RequestBody
            CreateLedgerRequestDto createCustomerRequestDto,
            BindingResult bindingResult
    ) {
        bindingResultChecker.checkBindingErrors(bindingResult);

        return Response.of(createLedgerService
                .createLedger(customerId, createCustomerRequestDto));
    }
}
