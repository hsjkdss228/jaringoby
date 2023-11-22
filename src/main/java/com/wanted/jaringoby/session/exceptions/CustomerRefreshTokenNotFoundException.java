package com.wanted.jaringoby.session.exceptions;

import com.wanted.jaringoby.common.exceptions.CustomizedException;
import org.springframework.http.HttpStatus;

public class CustomerRefreshTokenNotFoundException extends CustomizedException {

    public CustomerRefreshTokenNotFoundException() {
        super(HttpStatus.NOT_FOUND, "존재하지 않는 리프레시 토큰입니다.");
    }
}
