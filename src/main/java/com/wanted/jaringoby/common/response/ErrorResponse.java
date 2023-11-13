package com.wanted.jaringoby.common.response;

public record ErrorResponse(String error) {

    public static ErrorResponse of(String error) {
        return new ErrorResponse(error);
    }
}
