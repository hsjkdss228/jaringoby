package com.wanted.jaringoby.common.exceptions.http.request;

import com.wanted.jaringoby.common.exceptions.CustomizedException;
import org.springframework.http.HttpStatus;

public class InvalidRequestHeaderAuthorizationBearerException extends CustomizedException {

    public InvalidRequestHeaderAuthorizationBearerException() {
        super(HttpStatus.UNAUTHORIZED, "잘못된 액세스 토큰 전달자 형식입니다.");
    }
}
