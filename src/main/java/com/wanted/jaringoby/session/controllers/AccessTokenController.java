package com.wanted.jaringoby.session.controllers;

import com.wanted.jaringoby.common.response.Response;
import com.wanted.jaringoby.session.applications.ReissueAccessTokenService;
import com.wanted.jaringoby.session.dtos.http.ReissueAccessTokenResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1.0/customer/access-tokens")
@RequiredArgsConstructor
public class AccessTokenController {

    private final ReissueAccessTokenService reissueAccessTokenService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Response<ReissueAccessTokenResponseDto> reissueAccessToken(
            @RequestAttribute("customerId") String customerId,
            @RequestAttribute("refreshToken") String refreshToken
    ) {
        return Response.of(reissueAccessTokenService
                .reissueAccessToken(customerId, refreshToken));
    }
}
