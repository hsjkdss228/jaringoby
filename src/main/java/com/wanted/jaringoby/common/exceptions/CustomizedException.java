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

    public String message() {
        return getMessage();
    }

    public HttpStatus statusCode() {
        return statusCode;
    }

    public ErrorResponse toErrorResponse() {
        return new ErrorResponse(message());
    }

    // TODO: toErrorResponseEntity()로 명칭 변경
    public ResponseEntity<ErrorResponse> toResponseEntity() {
        return new ResponseEntity<>(ErrorResponse.of(message()), statusCode());
    }
}
