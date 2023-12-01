package com.wanted.jaringoby.domains.customer.exceptions;

import com.wanted.jaringoby.common.exceptions.CustomizedException;
import org.springframework.http.HttpStatus;

public class CustomerNotFoundException extends CustomizedException {

    public CustomerNotFoundException() {
        super(HttpStatus.NOT_FOUND, "존재하지 않는 고객입니다.");
    }
}
