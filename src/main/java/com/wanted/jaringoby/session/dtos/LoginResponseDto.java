package com.wanted.jaringoby.session.dtos;

import lombok.Builder;

@Builder
public record LoginResponseDto(
        String accessToken,
        String refreshToken
) {

}
