package com.wanted.jaringoby.domains.ledger.exceptions;

import com.wanted.jaringoby.common.exceptions.CustomizedException;
import org.springframework.http.HttpStatus;

public class LedgerPeriodEndedException extends CustomizedException {

    public LedgerPeriodEndedException() {
        super(HttpStatus.BAD_REQUEST, "종료된 예산 관리입니다.");
    }
}
