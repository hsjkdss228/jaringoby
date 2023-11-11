package com.wanted.jaringoby.common.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class Money {

    private final Long value;

    public static Money of(Long value) {
        return new Money(value);
    }

    public Long value() {
        return value;
    }
}
