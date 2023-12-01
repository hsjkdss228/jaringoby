package com.wanted.jaringoby.domains.ledger.exceptions;

import com.wanted.jaringoby.common.exceptions.CustomizedException;
import org.springframework.http.HttpStatus;

public class LedgerPeriodOverlappedException extends CustomizedException {

    public LedgerPeriodOverlappedException() {
        super(HttpStatus.CONFLICT, "주어진 기간 내 예산 관리가 이미 존재합니다.");
    }
}
