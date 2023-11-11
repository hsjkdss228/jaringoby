package com.wanted.jaringoby.category.models;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum Category {
    Meal("Meal"),
    Transportation("Transportation"),
    Leisure("Leisure"),
    Living("Living"),
    PersonalDevelopment("PersonalDevelopment");

    private final String name;
}
