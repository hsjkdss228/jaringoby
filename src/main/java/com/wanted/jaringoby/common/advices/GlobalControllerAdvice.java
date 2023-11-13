package com.wanted.jaringoby.common.advices;

import com.wanted.jaringoby.common.exceptions.CustomizedException;
import com.wanted.jaringoby.common.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(CustomizedException.class)
    public ResponseEntity<ErrorResponse> customizedException(CustomizedException exception) {
        return exception.toErrorResponse();
    }
}
