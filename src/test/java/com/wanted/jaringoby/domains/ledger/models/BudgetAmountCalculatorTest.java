package com.wanted.jaringoby.domains.ledger.models;

import static org.assertj.core.api.Assertions.assertThat;

import com.wanted.jaringoby.common.models.Money;
import com.wanted.jaringoby.common.models.Percentage;
import com.wanted.jaringoby.domains.category.models.Category;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BudgetAmountCalculatorTest {

    private BudgetAmountCalculator budgetAmountCalculator;

    @BeforeEach
    void setUp() {
        budgetAmountCalculator = new BudgetAmountCalculator();
    }

    @DisplayName("calculateAmount")
    @Nested
    class CalculateAmount {

        @DisplayName("주어진 총액을 바탕으로 카테고리 별 비중 평균을 "
                + "카테고리 별 총액 대비 비중에 맞는 액수로 환산")
        @Test
        void calculateAmount() {
            Money totalAmount = Money.of(2_000_000L);
            Map<Category, Percentage> categoriesAndPercentageAverages = Map.of(
                    Category.Meal, Percentage.of(.65D),
                    Category.Transportation, Percentage.of(.15D),
                    Category.PersonalDevelopment, Percentage.of(.12D),
                    Category.EtCetera, Percentage.of(.08D)
            );

            Map<Category, Money> categoriesAndAmounts = budgetAmountCalculator
                    .calculateAmount(totalAmount, categoriesAndPercentageAverages);

            assertThat(categoriesAndAmounts).hasSize(categoriesAndPercentageAverages.size());
            assertThat(categoriesAndAmounts.get(Category.Meal))
                    .isEqualTo(Money.of(1_300_000L));
            assertThat(categoriesAndAmounts.get(Category.Transportation))
                    .isEqualTo(Money.of(300_000L));
            assertThat(categoriesAndAmounts.get(Category.PersonalDevelopment))
                    .isEqualTo(Money.of(240_000L));
            assertThat(categoriesAndAmounts.get(Category.EtCetera))
                    .isEqualTo(Money.of(160_000L));
        }
    }
}
