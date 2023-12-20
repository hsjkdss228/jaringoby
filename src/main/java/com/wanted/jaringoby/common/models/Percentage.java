package com.wanted.jaringoby.common.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@ToString
public class Percentage {

    private static final Double PRECISION = 0.001D;

    private final Double value;

    public static Percentage of(Double value) {
        return new Percentage(value);
    }

    public Percentage add(Percentage other) {
        return new Percentage(value + other.value);
    }

    public Percentage divide(int divisor) {
        return new Percentage(value / divisor);
    }

    public boolean approximate(Double other) {
        return Math.abs(value - other) < PRECISION;
    }

    public Money ofMoney(Long amount) {
        return Money.of((long) (amount * value));
    }
}
