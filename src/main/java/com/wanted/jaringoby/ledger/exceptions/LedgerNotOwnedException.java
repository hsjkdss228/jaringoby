package com.wanted.jaringoby.ledger.exceptions;

import com.wanted.jaringoby.common.exceptions.CustomizedException;
import org.springframework.http.HttpStatus;

public class LedgerNotOwnedException extends CustomizedException {

    public LedgerNotOwnedException() {
        super(HttpStatus.BAD_REQUEST, "다른 고객의 예산 관리입니다.");
    }
}
