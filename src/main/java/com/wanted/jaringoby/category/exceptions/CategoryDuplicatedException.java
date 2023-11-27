package com.wanted.jaringoby.category.exceptions;

import com.wanted.jaringoby.common.exceptions.CustomizedException;
import org.springframework.http.HttpStatus;

public class CategoryDuplicatedException extends CustomizedException {

    public CategoryDuplicatedException() {
        super(HttpStatus.BAD_REQUEST, "중복된 카테고리입니다.");
    }
}
