package com.wanted.jaringoby.domains.customer.exceptions;

import com.wanted.jaringoby.common.exceptions.CustomizedException;
import org.springframework.http.HttpStatus;

public class CustomerReconfirmPasswordMismatchedException extends CustomizedException {

    public CustomerReconfirmPasswordMismatchedException() {
        super(HttpStatus.BAD_REQUEST, "비밀번호 확인이 일치하지 않습니다.");
    }
}
