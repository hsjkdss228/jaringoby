package com.wanted.jaringoby.domains.ledger.entities.budget;

import static org.assertj.core.api.Assertions.assertThat;

import com.wanted.jaringoby.common.models.Money;
import com.wanted.jaringoby.common.models.Percentage;
import com.wanted.jaringoby.domains.category.models.Category;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BudgetTest {

    @DisplayName("calculatePercentage")
    @Test
    void calculatePercentage() {
        Budget budget = Budget.testBuilder()
                .amount(Money.of(1_000L))
                .testBuild();

        budget.calculatePercentage(Money.of(50_000L));

        assertThat(budget.percentage()).isEqualTo(Percentage.of(.02D));
    }

    @DisplayName("addPercentageByCategory")
    @Nested
    class ReflectPercentage {

        private static final List<Category> TARGET_CATEGORIES = List.of(
                Category.Living,
                Category.Transportation
        );

        private Map<Category, List<Percentage>> categoriesAndPercentages;

        @BeforeEach
        void setUp() {
            categoriesAndPercentages = new HashMap<>();
        }

        @DisplayName("대상 카테고리로 지정된 경우 비중 평균 집계")
        @Test
        void reflectPercentage() {
            List<Budget> budgets = List.of(
                    Budget.testBuilder()
                            .category(Category.Living)
                            .amount(Money.of(1_000L))
                            .testBuild(),
                    Budget.testBuilder()
                            .category(Category.Transportation)
                            .amount(Money.of(5_000L))
                            .testBuild(),
                    Budget.testBuilder()
                            .category(Category.Transportation)
                            .amount(Money.of(10_000L))
                            .testBuild());

            budgets.forEach(budget -> budget.addPercentageByCategory(
                    categoriesAndPercentages,
                    TARGET_CATEGORIES
            ));

            assertThat(categoriesAndPercentages).containsKeys(
                    Category.Living,
                    Category.Transportation
            );
            assertThat(categoriesAndPercentages.get(Category.Living)).hasSize(1);
            assertThat(categoriesAndPercentages.get(Category.Transportation)).hasSize(2);
        }

        @DisplayName("대상 카테고리로 지정되지 않은 경우 비중 평균을 집계하지 않음")
        @Test
        void doesNotReflectPercentage() {
            Budget budget = Budget.testBuilder()
                    .category(Category.Meal)
                    .amount(Money.of(500_000L))
                    .testBuild();

            budget.addPercentageByCategory(categoriesAndPercentages, TARGET_CATEGORIES);

            assertThat(categoriesAndPercentages).doesNotContainKey(Category.Meal);
        }
    }
}
