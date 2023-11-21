package com.wanted.jaringoby.common.exceptions.jwt;

import com.wanted.jaringoby.common.exceptions.CustomizedException;
import org.springframework.http.HttpStatus;

public class TokenExpiredException extends CustomizedException {

    public TokenExpiredException() {
        super(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다.");
    }
}
