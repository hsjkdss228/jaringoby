package com.wanted.jaringoby.common.exceptions.http.request;

import com.wanted.jaringoby.common.exceptions.CustomizedException;
import org.springframework.http.HttpStatus;

public class InvalidRequestInputException extends CustomizedException {

    public InvalidRequestInputException() {
        super(HttpStatus.BAD_REQUEST, "입력 조건을 만족하지 못하는 필드가 존재합니다.");
    }
}
