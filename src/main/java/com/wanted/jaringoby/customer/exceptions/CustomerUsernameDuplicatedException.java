package com.wanted.jaringoby.customer.exceptions;

import com.wanted.jaringoby.common.exceptions.CustomizedException;
import org.springframework.http.HttpStatus;

public class CustomerUsernameDuplicatedException extends CustomizedException {

    public CustomerUsernameDuplicatedException() {
        super(HttpStatus.CONFLICT, "이미 존재하는 계정명입니다.");
    }
}
