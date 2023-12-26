package com.wanted.jaringoby.common.models;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MoneyTest {

    @DisplayName("isBiggerThan")
    @Nested
    class IsBiggerThan {

        @DisplayName("비교 대상보다 크면 true")
        @Test
        void isTrue() {
            assertThat(Money.of(10_000L).isBiggerThan(Money.of(9_999L))).isTrue();
        }

        @DisplayName("비교 대상보다 작거나 같으면 false")
        @Test
        void isFalse() {
            assertThat(Money.of(10_000L).isBiggerThan(Money.of(10_000L))).isFalse();
            assertThat(Money.of(10_000L).isBiggerThan(Money.of(999_999L))).isFalse();
        }
    }

    @DisplayName("add")
    @Test
    void add() {
        Money money1 = Money.of(50_000L);
        Money money2 = Money.of(3_000L);
        assertThat(money1.add(money2)).isEqualTo(Money.of(53_000L));
    }

    @DisplayName("subtract")
    @Test
    void subtract() {
        Money money1 = Money.of(50_000L);
        Money money2 = Money.of(3_000L);
        assertThat(money1.subtract(money2)).isEqualTo(Money.of(47_000L));
    }

    @DisplayName("toPercentage")
    @Test
    void toPercentage() {
        Money dividend = Money.of(3_000L);
        Money divisor = Money.of(50_000L);
        assertThat(dividend.toPercentage(divisor)).isEqualTo(Percentage.of(0.06D));
    }

    @DisplayName("truncate")
    @Nested
    class Truncate {

        @DisplayName("절사 단위까지의 금액이 절사 단위 / 2 미만인 경우 반내림")
        @Test
        void floor() {
            Money money = Money.of(1_131_247L);
            Long truncationScale = 1_000L;
            assertThat(money.truncate(truncationScale)).isEqualTo(Money.of(1_131_000L));
        }

        @DisplayName("절사 단위까지의 금액이 절사 단위 / 2 이상인 경우 반올림")
        @Test
        void ceil() {
            Money money = Money.of(1_131_500L);
            Long truncationScale = 1_000L;
            assertThat(money.truncate(truncationScale)).isEqualTo(Money.of(1_132_000L));
        }
    }

    @DisplayName("floor")
    @Test
    void floor() {
        Money money = Money.of(311_835L);
        Long truncationScale = 1_000L;
        assertThat(money.floor(truncationScale)).isEqualTo(Money.of(311_000L));
    }
}
