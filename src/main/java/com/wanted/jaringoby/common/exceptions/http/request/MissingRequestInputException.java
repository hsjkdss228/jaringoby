package com.wanted.jaringoby.common.exceptions.http.request;

import com.wanted.jaringoby.common.exceptions.CustomizedException;
import org.springframework.http.HttpStatus;

public class MissingRequestInputException extends CustomizedException {

    public MissingRequestInputException() {
        super(HttpStatus.BAD_REQUEST, "미입력 필드가 존재합니다.");
    }
}
