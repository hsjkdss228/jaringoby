package com.wanted.jaringoby.category.models;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum Category {
    Meal("식비"),
    Transportation("교통비"),
    Leisure("여가비"),
    Living("생활비"),
    PersonalDevelopment("자기계발비"),
    EtCetera("기타");

    private final String name;

    public String categoryName() {
        return name;
    }
}
