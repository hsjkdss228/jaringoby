package com.wanted.jaringoby.common.exceptions.jwt;

import com.wanted.jaringoby.common.exceptions.CustomizedException;
import org.springframework.http.HttpStatus;

public class TokenNotAccessTokenException extends CustomizedException {

    public TokenNotAccessTokenException() {
        super(HttpStatus.UNAUTHORIZED, "액세스 토큰이 아닙니다.");
    }
}
