package com.wanted.jaringoby.common.exceptions.jwt;

import com.wanted.jaringoby.common.exceptions.CustomizedException;
import org.springframework.http.HttpStatus;

public class TokenDecodingFailedException extends CustomizedException {

    public TokenDecodingFailedException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "토큰 디코딩에 실패했습니다.");
    }
}
