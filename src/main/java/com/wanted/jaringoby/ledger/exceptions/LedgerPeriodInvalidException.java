package com.wanted.jaringoby.ledger.exceptions;

import com.wanted.jaringoby.common.exceptions.CustomizedException;
import org.springframework.http.HttpStatus;

public class LedgerPeriodInvalidException extends CustomizedException {

    public LedgerPeriodInvalidException() {
        super(HttpStatus.BAD_REQUEST, "잘못된 예산 관리 기간입니다.");
    }
}
