package com.wanted.jaringoby.domains.ledger.exceptions;

import com.wanted.jaringoby.common.exceptions.CustomizedException;
import org.springframework.http.HttpStatus;

public class TruncationScaleRangeNotAllowedException extends CustomizedException {

    public TruncationScaleRangeNotAllowedException() {
        super(HttpStatus.BAD_REQUEST, "해당 총액에 허용되지 않는 절사 단위 범위입니다.");
    }
}
