package com.wanted.jaringoby.session.controllers;

import com.wanted.jaringoby.common.response.Response;
import com.wanted.jaringoby.common.validations.BindingResultChecker;
import com.wanted.jaringoby.common.validations.ValidationSequence;
import com.wanted.jaringoby.session.applications.LoginService;
import com.wanted.jaringoby.session.dtos.LoginRequestDto;
import com.wanted.jaringoby.session.dtos.LoginResponseDto;
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
@RequestMapping("/customer/v1.0/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final LoginService loginService;
    private final BindingResultChecker bindingResultChecker;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Response<LoginResponseDto> login(
            @Validated(ValidationSequence.class) @RequestBody LoginRequestDto loginRequestDto,
            BindingResult bindingResult
    ) {
        bindingResultChecker.checkBindingErrors(bindingResult);

        return Response.of(loginService.login(loginRequestDto));
    }
}