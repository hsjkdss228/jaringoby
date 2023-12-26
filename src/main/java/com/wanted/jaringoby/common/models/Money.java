package com.wanted.jaringoby.common.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
public class Money {

    private final Long amount;

    public static Money of(Long amount) {
        return new Money(amount);
    }

    public static Money zero() {
        return new Money(0L);
    }

    public Long value() {
        return amount;
    }

    public boolean isBiggerThan(Money other) {
        return amount > other.amount;
    }

    public int compareTo(Money other) {
        return (int) (amount - other.amount);
    }

    public Money add(Money amount) {
        return new Money(this.amount + amount.amount);
    }

    public Money subtract(Money amount) {
        return new Money(this.amount - amount.amount);
    }

    public Percentage toPercentage(Money other) {
        return Percentage.of((double) amount / other.amount);
    }

    public Money percentageOf(Percentage percentage) {
        return percentage.ofMoney(amount);
    }

    public Money truncate(Long truncationScale) {
        Long remainder = amount % truncationScale;
        Long truncatedAmount = remainder >= truncationScale / 2
                ? amount - remainder + truncationScale
                : amount - remainder;
        return Money.of(truncatedAmount);
    }

    public Money floor(Long truncationScale) {
        Long remainder = amount % truncationScale;
        return Money.of(amount - remainder);
    }
}
