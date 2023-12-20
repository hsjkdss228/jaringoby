package com.wanted.jaringoby.domains.ledger.models;

import static org.assertj.core.api.Assertions.assertThat;

import com.wanted.jaringoby.common.models.Money;
import com.wanted.jaringoby.common.models.Percentage;
import com.wanted.jaringoby.domains.category.models.Category;
import com.wanted.jaringoby.domains.ledger.models.budget.Budget;
import com.wanted.jaringoby.domains.ledger.models.ledger.LedgerId;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BudgetAmountRatioCalculatorTest {

    private BudgetAmountRatioCalculator budgetAmountRatioCalculator;

    @BeforeEach
    void setUp() {
        budgetAmountRatioCalculator = new BudgetAmountRatioCalculator();
    }

    @DisplayName("calculateByCategory")
    @Nested
    class CalculateByCategory {

        @DisplayName("toLedgerIdsAndBudgetAmountSums")
        @Nested
        class ToLedgerIdsAndBudgetAmountSums {

            @DisplayName("Map<LedgerId, List<Budget>>을 Map<LedgerId, BudgetAmountSum>으로 변환")
            @Test
            void toLedgerIdsAndBudgetAmountSums() {
                Map<LedgerId, List<Budget>> ledgerIdsAndBudgets = Map.of(
                        LedgerId.of("LEDGER_1"), List.of(
                                Budget.builder()
                                        .amount(Money.of(100_000L))
                                        .build(),
                                Budget.builder()
                                        .amount(Money.of(30_000L))
                                        .build(),
                                Budget.builder()
                                        .amount(Money.of(50_000L))
                                        .build()
                        ),
                        LedgerId.of("LEDGER_2"), List.of(
                                Budget.builder()
                                        .amount(Money.of(5_000L))
                                        .build(),
                                Budget.builder()
                                        .amount(Money.of(12_000L))
                                        .build()
                        )
                );

                Map<LedgerId, Money> ledgerIdAndBudgetAmountSums = budgetAmountRatioCalculator
                        .toLedgerIdsAndBudgetAmountSums(ledgerIdsAndBudgets);

                assertThat(ledgerIdAndBudgetAmountSums).isEqualTo(Map.of(
                        LedgerId.of("LEDGER_1"), Money.of(180_000L),
                        LedgerId.of("LEDGER_2"), Money.of(17_000L)
                ));
            }

        }

        @DisplayName("calculatePercentages")
        @Nested
        class CalculatePercentages {

            @DisplayName("Map<LedgerId, List<Budget>>의 각 Budget에 대해 "
                    + "Map<LedgerId, BudgetAmountSum>에 대응하는 Percentage를 도출")
            @Test
            void calculatePercentages() {
                LedgerId ledgerId1 = LedgerId.of("LEDGER_1");

                Budget budget1 = Budget.builder()
                        .amount(Money.of(60_000L))
                        .build();
                Budget budget2 = Budget.builder()
                        .amount(Money.of(40_000L))
                        .build();

                LedgerId ledgerId2 = LedgerId.of("LEDGER_2");

                Budget budget3 = Budget.builder()
                        .amount(Money.of(1_000L))
                        .build();
                Budget budget4 = Budget.builder()
                        .amount(Money.of(2_000L))
                        .build();

                Map<LedgerId, List<Budget>> ledgerIdsAndBudgets = Map.of(
                        ledgerId1, List.of(budget1, budget2),
                        ledgerId2, List.of(budget3, budget4)
                );

                Map<LedgerId, Money> ledgerIdsAndBudgetAmountSums = Map.of(
                        ledgerId1, budget1.amount().add(budget2.amount()),
                        ledgerId2, budget3.amount().add(budget4.amount())
                );

                budgetAmountRatioCalculator.calculatePercentages(
                        ledgerIdsAndBudgets, ledgerIdsAndBudgetAmountSums);

                assertThat(budget1.percentage().approximate(0.6D)).isTrue();
                assertThat(budget2.percentage().approximate(0.4D)).isTrue();
                assertThat(budget3.percentage().approximate(0.333D)).isTrue();
                assertThat(budget4.percentage().approximate(0.667D)).isTrue();
            }
        }

        @DisplayName("toCategoriesAndPercentageAverages")
        @Nested
        class ToCategoriesAndPercentageAverages {

            @DisplayName("Map<LedgerId, List<Budget>>의 각 Budget에 대해 계산된 Percentage를 반영해 "
                    + "List<Category>에 지정된 Category 별 Percentage 평균 도출")
            @Test
            void toCategoriesAndPercentageAverages() {
                LedgerId ledgerId1 = LedgerId.of("LEDGER_1");

                Budget budget1 = Budget.builder()
                        .category(Category.Living)
                        .percentage(Percentage.of(.6D))
                        .build();
                Budget budget2 = Budget.builder()
                        .category(Category.Leisure)
                        .percentage(Percentage.of(.4D))
                        .build();

                LedgerId ledgerId2 = LedgerId.of("LEDGER_2");

                Budget budget3 = Budget.builder()
                        .category(Category.Living)
                        .percentage(Percentage.of(.75D))
                        .build();
                Budget budget4 = Budget.builder()
                        .category(Category.Transportation)
                        .percentage(Percentage.of(.25D))
                        .build();

                Map<LedgerId, List<Budget>> ledgerIdsAndBudgets = Map.of(
                        ledgerId1, List.of(budget1, budget2),
                        ledgerId2, List.of(budget3, budget4)
                );

                List<Category> categories = List.of(
                        Category.Living,
                        Category.Leisure
                );

                Map<Category, Percentage> categoriesAndPercentageAverages
                        = budgetAmountRatioCalculator.toCategoriesAndPercentageAverages(
                        ledgerIdsAndBudgets, categories);

                assertThat(categoriesAndPercentageAverages).containsKeys(
                        Category.Living,
                        Category.Leisure
                );
                assertThat(categoriesAndPercentageAverages).doesNotContainKey(
                        Category.Transportation
                );
                assertThat(categoriesAndPercentageAverages.get(Category.Living)
                        .approximate(.675D))
                        .isTrue();
                assertThat(categoriesAndPercentageAverages.get(Category.Leisure)
                        .approximate(.2D))
                        .isTrue();
            }
        }

        @DisplayName("모든 각 예산들의 금액에 대해 고객이 지정한 카테고리 별로 "
                + "각 예산 관리에서 차지하는 비중 평균을 도출")
        @Test
        void calculateByCategory() {
            LedgerId ledgerId1 = LedgerId.of("LEDGER_1");

            Budget budget1 = Budget.builder()
                    .category(Category.Meal)
                    .amount(Money.of(300_000L))
                    .build();
            Budget budget2 = Budget.builder()
                    .category(Category.Transportation)
                    .amount(Money.of(150_000L))
                    .build();
            Budget budget3 = Budget.builder()
                    .category(Category.Leisure)
                    .amount(Money.of(1_000_000L))
                    .build();

            LedgerId ledgerId2 = LedgerId.of("LEDGER_2");

            Budget budget4 = Budget.builder()
                    .category(Category.Meal)
                    .amount(Money.of(800_000L))
                    .build();
            Budget budget5 = Budget.builder()
                    .category(Category.Transportation)
                    .amount(Money.of(600_000L))
                    .build();
            Budget budget6 = Budget.builder()
                    .category(Category.Living)
                    .amount(Money.of(500_000L))
                    .build();

            LedgerId ledgerId3 = LedgerId.of("LEDGER_3");

            Budget budget7 = Budget.builder()
                    .category(Category.Meal)
                    .amount(Money.of(500_000L))
                    .build();
            Budget budget8 = Budget.builder()
                    .category(Category.Living)
                    .amount(Money.of(500_000L))
                    .build();
            Budget budget9 = Budget.builder()
                    .category(Category.PersonalDevelopment)
                    .amount(Money.of(150_000L))
                    .build();

            Map<LedgerId, List<Budget>> ledgerIdsAndBudgets = Map.of(
                    ledgerId1, List.of(budget1, budget2, budget3),
                    ledgerId2, List.of(budget4, budget5, budget6),
                    ledgerId3, List.of(budget7, budget8, budget9)
            );

            List<Category> categories = List.of(
                    Category.Meal,
                    Category.Transportation,
                    Category.Leisure,
                    Category.Living
            );

            Map<Category, Percentage> categoriesAndPercentageAverages
                    = budgetAmountRatioCalculator.calculateByCategory(
                    ledgerIdsAndBudgets, categories);

            assertThat(categoriesAndPercentageAverages).containsKeys(
                    Category.Meal,
                    Category.Transportation,
                    Category.Leisure,
                    Category.Living
            );
            assertThat(categoriesAndPercentageAverages).doesNotContainKey(
                    Category.PersonalDevelopment
            );
            assertThat(categoriesAndPercentageAverages.get(Category.Meal)
                    .approximate(.354D))
                    .isTrue();
            assertThat(categoriesAndPercentageAverages.get(Category.Transportation)
                    .approximate(.14D))
                    .isTrue();
            assertThat(categoriesAndPercentageAverages.get(Category.Leisure)
                    .approximate(.23D))
                    .isTrue();
            assertThat(categoriesAndPercentageAverages.get(Category.Living)
                    .approximate(.233D))
                    .isTrue();
        }
    }
}
