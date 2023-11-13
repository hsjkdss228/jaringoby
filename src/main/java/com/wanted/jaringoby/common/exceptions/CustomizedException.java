package com.wanted.jaringoby.common.exceptions;

import com.wanted.jaringoby.common.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CustomizedException extends RuntimeException {

    private final HttpStatus statusCode;

    protected CustomizedException(HttpStatus statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public ResponseEntity<ErrorResponse> toErrorResponse() {
        return new ResponseEntity<>(ErrorResponse.of(getMessage()), statusCode);
    }
}
