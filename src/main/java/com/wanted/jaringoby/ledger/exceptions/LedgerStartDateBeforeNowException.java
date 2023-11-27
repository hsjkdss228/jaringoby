package com.wanted.jaringoby.ledger.exceptions;

import com.wanted.jaringoby.common.exceptions.CustomizedException;
import org.springframework.http.HttpStatus;

public class LedgerStartDateBeforeNowException extends CustomizedException {

    public LedgerStartDateBeforeNowException() {
        super(HttpStatus.BAD_REQUEST, "시작일이 당일 이전입니다.");
    }
}
