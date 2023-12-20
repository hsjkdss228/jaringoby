package com.wanted.jaringoby.domains.ledger.applications;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.wanted.jaringoby.common.models.Money;
import com.wanted.jaringoby.domains.category.models.Category;
import com.wanted.jaringoby.domains.ledger.dtos.BudgetRecommendationDto;
import com.wanted.jaringoby.domains.ledger.dtos.GetBudgetRecommendationQueryParamsDto;
import com.wanted.jaringoby.domains.ledger.dtos.GetBudgetRecommendationResponseDto;
import com.wanted.jaringoby.domains.ledger.dtos.query.LedgerIdAndBudgetsQueryResultDto;
import com.wanted.jaringoby.domains.ledger.exceptions.CategoryCannotBeIncludedException;
import com.wanted.jaringoby.domains.ledger.exceptions.TruncationScaleGreaterThanTotalAmountException;
import com.wanted.jaringoby.domains.ledger.exceptions.TruncationScaleIndivisibleException;
import com.wanted.jaringoby.domains.ledger.exceptions.TruncationScaleRangeNotAllowedException;
import com.wanted.jaringoby.domains.ledger.models.BudgetAmountCalculator;
import com.wanted.jaringoby.domains.ledger.models.BudgetAmountRatioCalculator;
import com.wanted.jaringoby.domains.ledger.models.BudgetAmountTruncator;
import com.wanted.jaringoby.domains.ledger.models.budget.Budget;
import com.wanted.jaringoby.domains.ledger.models.ledger.LedgerId;
import com.wanted.jaringoby.domains.ledger.repositories.BudgetRepository;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BudgetRecommendationServiceTest {

    private BudgetRecommendationService budgetRecommendationService;
    private BudgetRepository budgetRepository;

    @BeforeEach
    void setUp() {
        budgetRepository = mock(BudgetRepository.class);
        budgetRecommendationService = new BudgetRecommendationService(
                budgetRepository,
                new BudgetAmountRatioCalculator(),
                new BudgetAmountCalculator(),
                new BudgetAmountTruncator()
        );
    }

    @DisplayName("recommendBudget")
    @Nested
    class RecommendBudget {

        private static final Long AMOUNT = 1_600_000L;
        private static final List<String> CATEGORY_NAMES = List.of(
                "식비",
                "교통비",
                "여가비"
        );
        private static final String TRUNCATION_SCALE = "10000";

        @DisplayName("성공")
        @Nested
        class Success {

            private static final LedgerId LEDGER_ID_1 = LedgerId.of("LEDGER_1");
            private static final LedgerId LEDGER_ID_2 = LedgerId.of("LEDGER_2");
            private static final LedgerId LEDGER_ID_3 = LedgerId.of("LEDGER_3");

            @BeforeEach
            void setUp() {
                LedgerIdAndBudgetsQueryResultDto ledgerIdAndBudgetsQueryResultDto
                        = LedgerIdAndBudgetsQueryResultDto.builder()
                        .ledgerIdsAndBudgets(Map.of(
                                LEDGER_ID_1, List.of(
                                        Budget.testBuilder()
                                                .ledgerId(LEDGER_ID_1)
                                                .category(Category.Meal)
                                                .amount(Money.of(1_000_000L))
                                                .testBuild(),
                                        Budget.testBuilder()
                                                .ledgerId(LEDGER_ID_1)
                                                .category(Category.Transportation)
                                                .amount(Money.of(700_000L))
                                                .testBuild(),
                                        Budget.testBuilder()
                                                .ledgerId(LEDGER_ID_1)
                                                .category(Category.Leisure)
                                                .amount(Money.of(200_000L))
                                                .testBuild(),
                                        Budget.testBuilder()
                                                .ledgerId(LEDGER_ID_1)
                                                .category(Category.EtCetera)
                                                .amount(Money.of(100_000L))
                                                .testBuild()
                                ),
                                LEDGER_ID_2, List.of(
                                        Budget.testBuilder()
                                                .ledgerId(LEDGER_ID_2)
                                                .category(Category.Meal)
                                                .amount(Money.of(500_000L))
                                                .testBuild(),
                                        Budget.testBuilder()
                                                .ledgerId(LEDGER_ID_2)
                                                .category(Category.Transportation)
                                                .amount(Money.of(200_000L))
                                                .testBuild(),
                                        Budget.testBuilder()
                                                .ledgerId(LEDGER_ID_2)
                                                .category(Category.Leisure)
                                                .amount(Money.of(50_000L))
                                                .testBuild()
                                ),
                                LEDGER_ID_3, List.of(
                                        Budget.testBuilder()
                                                .ledgerId(LEDGER_ID_2)
                                                .category(Category.Meal)
                                                .amount(Money.of(700_000L))
                                                .testBuild(),
                                        Budget.testBuilder()
                                                .ledgerId(LEDGER_ID_2)
                                                .category(Category.Transportation)
                                                .amount(Money.of(500_000L))
                                                .testBuild(),
                                        Budget.testBuilder()
                                                .ledgerId(LEDGER_ID_2)
                                                .category(Category.Leisure)
                                                .amount(Money.of(100_000L))
                                                .testBuild(),
                                        Budget.testBuilder()
                                                .ledgerId(LEDGER_ID_2)
                                                .category(Category.Living)
                                                .amount(Money.of(100_000L))
                                                .testBuild()
                                )
                        ))
                        .build();

                given(budgetRepository.findAllLedgerIdAndBudgets())
                        .willReturn(ledgerIdAndBudgetsQueryResultDto);
            }

            @DisplayName("총액, 선택한 카테고리 목록, 절사 범위를 바탕으로 "
                    + "각 카테고리 별 총액 대비 평균 비중에 해당하는 금액을 환산하여 반환")
            @Test
            void recommendBudget() {
                GetBudgetRecommendationQueryParamsDto getBudgetRecommendationQueryParamsDto
                        = GetBudgetRecommendationQueryParamsDto.builder()
                        .amount(AMOUNT)
                        .categories(CATEGORY_NAMES)
                        .truncationScale(TRUNCATION_SCALE)
                        .build();

                GetBudgetRecommendationResponseDto getBudgetRecommendationResponseDto
                        = budgetRecommendationService.recommendBudget(
                        getBudgetRecommendationQueryParamsDto);

                assertThat(getBudgetRecommendationResponseDto).isNotNull();

                List<BudgetRecommendationDto> budgetRecommendations
                        = getBudgetRecommendationResponseDto.getBudgetRecommendations();
                assertThat(budgetRecommendations).hasSize(CATEGORY_NAMES.size());

                List<String> categoryNamesOfBudgetRecommendations
                        = toCategoryNames(budgetRecommendations);
                assertThat(categoryNamesOfBudgetRecommendations).containsAll(CATEGORY_NAMES);

                List<Long> amountsOfBudgeRecommendations = toAmounts(budgetRecommendations);
                assertThat(amountsOfBudgeRecommendations).containsAll(List.of(
                        890_000L,
                        520_000L,
                        130_000L
                ));
            }

            @DisplayName("카테고리 목록으로 null 전달된 경우 없는 카테고리를 제외한 "
                    + "모든 카테고리 별 총액 대비 평균 비중에 해당하는 금액을 환산하여 반환")
            @Test
            void nullCategories() {
                GetBudgetRecommendationQueryParamsDto getBudgetRecommendationQueryParamsDto
                        = GetBudgetRecommendationQueryParamsDto.builder()
                        .amount(AMOUNT)
                        .categories(null)
                        .truncationScale(TRUNCATION_SCALE)
                        .build();

                GetBudgetRecommendationResponseDto getBudgetRecommendationResponseDto
                        = budgetRecommendationService.recommendBudget(
                        getBudgetRecommendationQueryParamsDto);

                assertThat(getBudgetRecommendationResponseDto).isNotNull();

                List<String> allCategoryNamesExceptNonExistent = List.of(
                        "식비",
                        "교통비",
                        "여가비",
                        "생활비",
                        "기타"
                );

                List<BudgetRecommendationDto> budgetRecommendations
                        = getBudgetRecommendationResponseDto.getBudgetRecommendations();
                assertThat(budgetRecommendations).hasSize(
                        allCategoryNamesExceptNonExistent.size());

                List<String> categoryNamesOfBudgetRecommendations
                        = toCategoryNames(budgetRecommendations);
                assertThat(categoryNamesOfBudgetRecommendations).containsAll(CATEGORY_NAMES);

                List<Long> amountsOfBudgeRecommendations = toAmounts(budgetRecommendations);
                assertThat(amountsOfBudgeRecommendations).containsAll(List.of(
                        880_000L,
                        520_000L,
                        130_000L,
                        40_000L,
                        30_000L
                ));
            }

            private List<String> toCategoryNames(
                    List<BudgetRecommendationDto> budgetRecommendations
            ) {
                return budgetRecommendations
                        .stream()
                        .map(BudgetRecommendationDto::name)
                        .toList();
            }

            private List<Long> toAmounts(
                    List<BudgetRecommendationDto> budgetRecommendations
            ) {
                return budgetRecommendations
                        .stream()
                        .map(BudgetRecommendationDto::amount)
                        .toList();
            }
        }

        @DisplayName("실패")
        @Nested
        class Failure {

            @DisplayName("카테고리 목록에 기타가 포함된 경우 예외처리")
            @Test
            void containsEtCetera() {
                List<String> categoryNamesWithEtCetera = List.of(
                        "자기계발비",
                        "기타"
                );

                GetBudgetRecommendationQueryParamsDto getBudgetRecommendationQueryParamsDto
                        = GetBudgetRecommendationQueryParamsDto.builder()
                        .amount(AMOUNT)
                        .categories(categoryNamesWithEtCetera)
                        .truncationScale(TRUNCATION_SCALE)
                        .build();

                assertThrows(CategoryCannotBeIncludedException.class, () ->
                        budgetRecommendationService
                                .recommendBudget(getBudgetRecommendationQueryParamsDto));
            }

            @DisplayName("절사 단위가 총액보다 큰 경우 예외처리")
            @Test
            void truncationScaleGreaterThanTotalAmount() {
                Long amount = 1_600_000L;
                String invalidTruncationScale = "10000000";

                GetBudgetRecommendationQueryParamsDto getBudgetRecommendationQueryParamsDto
                        = GetBudgetRecommendationQueryParamsDto.builder()
                        .amount(amount)
                        .categories(CATEGORY_NAMES)
                        .truncationScale(invalidTruncationScale)
                        .build();

                assertThrows(TruncationScaleGreaterThanTotalAmountException.class, () ->
                        budgetRecommendationService
                                .recommendBudget(getBudgetRecommendationQueryParamsDto));
            }

            @DisplayName("절사 단위가 0으로 나누어떨어지는 수가 아닌 경우 예외처리")
            @Test
            void truncationScaleIndivisible() {
                String invalidTruncationScale = "1234";

                GetBudgetRecommendationQueryParamsDto getBudgetRecommendationQueryParamsDto
                        = GetBudgetRecommendationQueryParamsDto.builder()
                        .amount(AMOUNT)
                        .categories(CATEGORY_NAMES)
                        .truncationScale(invalidTruncationScale)
                        .build();

                assertThrows(TruncationScaleIndivisibleException.class, () ->
                        budgetRecommendationService
                                .recommendBudget(getBudgetRecommendationQueryParamsDto));
            }

            @DisplayName("총액이 1_000_000 미만이면서 "
                    + "절사 단위가 총액보다 최소 1자릿수 이하로 작지 않은 경우 에외처리")
            @Test
            void truncationScaleNotAtLeastOneDigit() {
                Long amount = 750_000L;
                String truncationScale = "100000";

                GetBudgetRecommendationQueryParamsDto getBudgetRecommendationQueryParamsDto
                        = GetBudgetRecommendationQueryParamsDto.builder()
                        .amount(amount)
                        .categories(CATEGORY_NAMES)
                        .truncationScale(truncationScale)
                        .build();

                assertThrows(TruncationScaleRangeNotAllowedException.class, () ->
                        budgetRecommendationService
                                .recommendBudget(getBudgetRecommendationQueryParamsDto));
            }

            @DisplayName("총액이 1_000_000 이상이면서 "
                    + "절사 단위가 총액보다 최소 2자릿수 이하로 작지 않은 경우 에외처리")
            @Test
            void truncationScaleNotAtLeastTwoDigits() {
                Long amount = 12_500_000L;
                String truncationScale = "1000000";

                GetBudgetRecommendationQueryParamsDto getBudgetRecommendationQueryParamsDto
                        = GetBudgetRecommendationQueryParamsDto.builder()
                        .amount(amount)
                        .categories(CATEGORY_NAMES)
                        .truncationScale(truncationScale)
                        .build();

                assertThrows(TruncationScaleRangeNotAllowedException.class, () ->
                        budgetRecommendationService
                                .recommendBudget(getBudgetRecommendationQueryParamsDto));
            }
        }
    }
}
