package com.wanted.jaringoby.common.exceptions.jwt;

import com.wanted.jaringoby.common.exceptions.CustomizedException;
import org.springframework.http.HttpStatus;

public class TokenSignatureInvalidException extends CustomizedException {

    public TokenSignatureInvalidException() {
        super(HttpStatus.UNAUTHORIZED, "토큰의 시그니쳐가 다릅니다.");
    }
}
