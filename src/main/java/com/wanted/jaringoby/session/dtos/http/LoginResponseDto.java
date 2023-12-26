package com.wanted.jaringoby.session.dtos.http;

import lombok.Builder;

@Builder
public record LoginResponseDto(
        String accessToken,
        String refreshToken
) {

}
