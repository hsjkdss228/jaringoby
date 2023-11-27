package com.wanted.jaringoby.ledger.exceptions;

import com.wanted.jaringoby.common.exceptions.CustomizedException;
import org.springframework.http.HttpStatus;

public class LedgerEndDateBeforeStartDateException extends CustomizedException {

    public LedgerEndDateBeforeStartDateException() {
        super(HttpStatus.BAD_REQUEST, "종료일이 시작일 이전입니다.");
    }
}
