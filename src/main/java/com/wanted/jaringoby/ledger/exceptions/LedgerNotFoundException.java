package com.wanted.jaringoby.ledger.exceptions;

import com.wanted.jaringoby.common.exceptions.CustomizedException;
import org.springframework.http.HttpStatus;

public class LedgerNotFoundException extends CustomizedException {

    public LedgerNotFoundException() {
        super(HttpStatus.NOT_FOUND, "존재하지 않는 예산 관리입니다.");
    }
}
