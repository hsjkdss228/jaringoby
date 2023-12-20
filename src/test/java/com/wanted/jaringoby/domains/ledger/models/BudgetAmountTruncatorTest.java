package com.wanted.jaringoby.domains.ledger.models;

import static org.assertj.core.api.Assertions.assertThat;

import com.wanted.jaringoby.common.models.Money;
import com.wanted.jaringoby.domains.category.models.Category;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BudgetAmountTruncatorTest {

    private BudgetAmountTruncator budgetAmountTruncator;

    @BeforeEach
    void setUp() {
        budgetAmountTruncator = new BudgetAmountTruncator();
    }

    @DisplayName("truncate")
    @Nested
    class Truncate {

        @DisplayName("주어진 절사 단위에 맞게 금액을 반올림하거나 반내림")
        @Test
        void truncate() {
            Money mealAmount = Money.of(1_131_247L);
            Money transportationAmount = Money.of(866_235L);
            Money leisureAmount = Money.of(42_235L);
            Money livingAmount = Money.of(310_497L);
            Money etCeteraAmount = Money.of(7_630L);

            Map<Category, Money> categoriesAndAmounts = Map.of(
                Category.Meal, mealAmount,
                Category.Transportation, transportationAmount,
                Category.Leisure, leisureAmount,
                Category.Living, livingAmount,
                Category.EtCetera, etCeteraAmount
            );
            Long truncationScale = 1_000L;
            Money totalAmount = mealAmount.add(transportationAmount)
                    .add(leisureAmount)
                    .add(livingAmount)
                    .add(etCeteraAmount);

            Map<Category, Money> categoriesAndTruncatedAmounts = budgetAmountTruncator
                    .truncate(categoriesAndAmounts, truncationScale, totalAmount);

            assertThat(categoriesAndTruncatedAmounts).containsKeys(
                    Category.Meal,
                    Category.Transportation,
                    Category.Leisure,
                    Category.Living,
                    Category.EtCetera
            );

            assertThat(categoriesAndTruncatedAmounts.get(Category.Meal))
                    .isEqualTo(Money.of(1_131_000L));
            assertThat(categoriesAndTruncatedAmounts.get(Category.Transportation))
                    .isEqualTo(Money.of(866_000L));
            assertThat(categoriesAndTruncatedAmounts.get(Category.Leisure))
                    .isEqualTo(Money.of(42_000L));
            assertThat(categoriesAndTruncatedAmounts.get(Category.Living))
                    .isEqualTo(Money.of(310_000L));
            assertThat(categoriesAndTruncatedAmounts.get(Category.EtCetera))
                    .isEqualTo(Money.of(8_000L));
        }

        @DisplayName("절사 후 금액 합계가 총액보다 작거나 같은 경우")
        @Nested
        class TruncatedAmountSumEqualsOrLessThanTotalAmount {

            @DisplayName("카테고리 별 절사 후 금액 목록을 그대로 반환")
            @Test
            void doesNotAdjustMarginOfError() {
                Money mealAmount = Money.of(889_600L);
                Money transportationAmount = Money.of(520_000L);

                Map<Category, Money> categoriesAndAmounts = Map.of(
                        Category.Meal, mealAmount,
                        Category.Transportation, transportationAmount
                );
                Long truncationScale = 10_000L;
                Money totalAmount = Money.of(2_000_000L);

                Map<Category, Money> categoriesAndTruncatedAmounts = budgetAmountTruncator
                        .truncate(categoriesAndAmounts, truncationScale, totalAmount);

                Money truncatedAmountSum = categoriesAndTruncatedAmounts.values()
                        .stream()
                        .reduce(Money::add)
                        .get();

                assertThat(truncatedAmountSum.isBiggerThan(totalAmount)).isFalse();
            }
        }

        @DisplayName("절사 후 금액 합계가 총액보다 큰 경우")
        @Nested
        class TruncatedAmountSumGreaterThanTotalAmount {

            @DisplayName("카테고리 별 절사 후 금액 목록 중 "
                    + "가장 큰 금액에 대해 절사 단위만큼 빼 오차범위 조정 후 반환")
            @Test
            void adjustMarginOfError() {
                Money mealAmount = Money.of(889_600L);
                Money transportationAmount = Money.of(520_000L);
                Money leisureAmount = Money.of(128_000L);
                Money livingAmount = Money.of(38_400L);
                Money etCeteraAmount = Money.of(27_200L);

                Map<Category, Money> categoriesAndAmounts = Map.of(
                        Category.Meal, mealAmount,
                        Category.Transportation, transportationAmount,
                        Category.Leisure, leisureAmount,
                        Category.Living, livingAmount,
                        Category.EtCetera, etCeteraAmount
                );
                Long truncationScale = 10_000L;
                Money totalAmount = mealAmount.add(transportationAmount)
                        .add(leisureAmount)
                        .add(livingAmount)
                        .add(etCeteraAmount);

                Map<Category, Money> categoriesAndTruncatedAmounts = budgetAmountTruncator
                        .truncate(categoriesAndAmounts, truncationScale, totalAmount);

                Money truncatedAmountSum = categoriesAndTruncatedAmounts.values()
                        .stream()
                        .reduce(Money::add)
                        .get();

                assertThat(truncatedAmountSum.isBiggerThan(totalAmount)).isFalse();
            }
        }
    }
}
