package com.wanted.jaringoby.session.exceptions;

import com.wanted.jaringoby.common.exceptions.CustomizedException;
import org.springframework.http.HttpStatus;

public class CustomerRefreshTokenNullException extends CustomizedException {

    public CustomerRefreshTokenNullException() {
        super(HttpStatus.BAD_REQUEST, "전달된 리프레시 토큰이 없습니다.");
    }
}
