package com.wanted.jaringoby.common.exceptions.jwt;

import com.wanted.jaringoby.common.exceptions.CustomizedException;
import org.springframework.http.HttpStatus;

public class TokenNotRefreshTokenException extends CustomizedException {

    public TokenNotRefreshTokenException() {
        super(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 아닙니다.");
    }
}
