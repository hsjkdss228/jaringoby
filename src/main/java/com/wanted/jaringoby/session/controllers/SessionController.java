package com.wanted.jaringoby.session.controllers;

import com.wanted.jaringoby.common.response.Response;
import com.wanted.jaringoby.common.validations.BindingResultChecker;
import com.wanted.jaringoby.common.validations.ValidationSequence;
import com.wanted.jaringoby.session.applications.LoginService;
import com.wanted.jaringoby.session.applications.LogoutService;
import com.wanted.jaringoby.session.dtos.http.LoginRequestDto;
import com.wanted.jaringoby.session.dtos.http.LoginResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1.0/customer/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final LoginService loginService;
    private final LogoutService logoutService;
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

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(
            @RequestAttribute("customerId") String customerId,
            @RequestAttribute("refreshToken") String refreshToken
    ) {
        logoutService.logout(customerId, refreshToken);
    }
}
