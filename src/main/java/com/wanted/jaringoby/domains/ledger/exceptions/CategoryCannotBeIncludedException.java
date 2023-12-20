package com.wanted.jaringoby.domains.ledger.exceptions;

import com.wanted.jaringoby.common.exceptions.CustomizedException;
import org.springframework.http.HttpStatus;

public class CategoryCannotBeIncludedException extends CustomizedException {

    public CategoryCannotBeIncludedException() {
        super(HttpStatus.BAD_REQUEST, "포함할 수 없는 카테고리가 포함되었습니다.");
    }
}
