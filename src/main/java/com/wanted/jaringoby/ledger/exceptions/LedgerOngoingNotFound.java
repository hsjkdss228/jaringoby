package com.wanted.jaringoby.ledger.exceptions;

import com.wanted.jaringoby.common.exceptions.CustomizedException;
import org.springframework.http.HttpStatus;

public class LedgerOngoingNotFound extends CustomizedException {

    public LedgerOngoingNotFound() {
        super(HttpStatus.NOT_FOUND, "현재 진행중인 예산 관리가 없습니다.");
    }
}
