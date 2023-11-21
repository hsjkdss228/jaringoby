package com.wanted.jaringoby.common.exceptions.http.request;

import com.wanted.jaringoby.common.exceptions.CustomizedException;
import org.springframework.http.HttpStatus;

public class MissingRequestHeaderAuthorizationException extends CustomizedException {

    public MissingRequestHeaderAuthorizationException() {
        super(HttpStatus.UNAUTHORIZED, "액세스 토큰이 없습니다.");
    }
}
