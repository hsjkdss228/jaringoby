package com.wanted.jaringoby.category.exceptions;

import com.wanted.jaringoby.common.exceptions.CustomizedException;
import org.springframework.http.HttpStatus;

public class CategoryNotFoundException extends CustomizedException {

    public CategoryNotFoundException() {
        super(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다.");
    }
}
