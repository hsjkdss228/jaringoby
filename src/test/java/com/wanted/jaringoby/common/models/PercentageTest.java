package com.wanted.jaringoby.common.models;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PercentageTest {

    @DisplayName("divide")
    @Test
    void divide() {
        assertThat(Percentage.of(0.337D + 0.52D + 0.481D).divide(3))
                .isEqualTo(Percentage.of(.446D));
    }

    @DisplayName("approximate")
    @Nested
    class Approximate {

        @DisplayName("완전히 같은 경우 true")
        @Test
        void equals() {
            assertThat(Percentage.of(.5D).approximate(.5D)).isTrue();
        }

        @DisplayName("소수점 셋째 자리 수까지 같은 경우 true")
        @Test
        void equalTo3DecimalPlaces() {
            assertThat(Percentage.of(.3339868957D).approximate(.333D)).isTrue();
        }

        @DisplayName("소수점 셋째 자리 수 이내에서 달라질 경우 false")
        @Test
        void unequalTo3DecimalPlaces() {
            assertThat(Percentage.of(.333D).approximate(.332D)).isFalse();
        }
    }

    @DisplayName("ofMoney")
    @Test
    void ofMoney() {
        assertThat(Percentage.of(.3D).ofMoney(10_000L)).isEqualTo(Money.of(3_000L));
    }
}
