package com.wanted.jaringoby.customer.exceptions;

import com.wanted.jaringoby.common.exceptions.CustomizedException;
import org.springframework.http.HttpStatus;

public class CustomerPasswordMismatchedException extends CustomizedException {

    public CustomerPasswordMismatchedException() {
        super(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.");
    }
}
