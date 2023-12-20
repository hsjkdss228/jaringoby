package com.wanted.jaringoby.domains.ledger.exceptions;

import com.wanted.jaringoby.common.exceptions.CustomizedException;
import org.springframework.http.HttpStatus;

public class TruncationScaleIndivisibleException extends CustomizedException {

    public TruncationScaleIndivisibleException() {
        super(HttpStatus.BAD_REQUEST, "0으로 나누어떨어지는 자리수로 표현된 절사 단위가 아닙니다.");
    }
}
