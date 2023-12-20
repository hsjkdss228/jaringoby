package com.wanted.jaringoby.domains.ledger.exceptions;

import com.wanted.jaringoby.common.exceptions.CustomizedException;
import org.springframework.http.HttpStatus;

public class TruncationScaleGreaterThanTotalAmountException extends CustomizedException {

    public TruncationScaleGreaterThanTotalAmountException() {
        super(HttpStatus.BAD_REQUEST, "절사 단위가 총액보다 큽니다.");
    }
}
