package com.wanted.jaringoby.domains.ledger.applications;

import com.wanted.jaringoby.common.models.Money;
import com.wanted.jaringoby.common.models.Percentage;
import com.wanted.jaringoby.domains.category.models.Category;
import com.wanted.jaringoby.domains.ledger.dtos.http.GetBudgetRecommendationQueryParamsDto;
import com.wanted.jaringoby.domains.ledger.dtos.http.GetBudgetRecommendationResponseDto;
import com.wanted.jaringoby.domains.ledger.dtos.query.LedgerIdAndBudgetsQueryResultDto;
import com.wanted.jaringoby.domains.ledger.entities.budget.Budget;
import com.wanted.jaringoby.domains.ledger.entities.ledger.LedgerId;
import com.wanted.jaringoby.domains.ledger.exceptions.CategoryCannotBeIncludedException;
import com.wanted.jaringoby.domains.ledger.exceptions.TruncationScaleGreaterThanTotalAmountException;
import com.wanted.jaringoby.domains.ledger.exceptions.TruncationScaleIndivisibleException;
import com.wanted.jaringoby.domains.ledger.exceptions.TruncationScaleRangeNotAllowedException;
import com.wanted.jaringoby.domains.ledger.models.BudgetAmountCalculator;
import com.wanted.jaringoby.domains.ledger.models.BudgetAmountRatioCalculator;
import com.wanted.jaringoby.domains.ledger.models.BudgetAmountTruncator;
import com.wanted.jaringoby.domains.ledger.repositories.BudgetRepository;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BudgetRecommendationService {

    private final BudgetRepository budgetRepository;
    private final BudgetAmountRatioCalculator budgetAmountRatioCalculator;
    private final BudgetAmountCalculator budgetAmountCalculator;
    private final BudgetAmountTruncator budgetAmountTruncator;

    @Transactional(readOnly = true)
    public GetBudgetRecommendationResponseDto recommendBudget(
            GetBudgetRecommendationQueryParamsDto getBudgetRecommendationQueryParamsDto
    ) {
        Long totalAmount = getBudgetRecommendationQueryParamsDto.getAmount();
        List<String> categoryNames = getBudgetRecommendationQueryParamsDto.getCategories();
        Long truncationScale = getBudgetRecommendationQueryParamsDto.getTruncationScale();

        validateContainsCategoryCannotIncluded(categoryNames);
        validateAmountAndTruncationScale(totalAmount, truncationScale);

        List<Category> categories = categoryNames == null
                ? Category.allValues()
                : Category.toCategories(categoryNames);

        LedgerIdAndBudgetsQueryResultDto ledgerIdAndBudgetsQueryResultDto
                = budgetRepository.findAllLedgerIdAndBudgets();

        Map<LedgerId, List<Budget>> ledgerIdsAndBudgets = ledgerIdAndBudgetsQueryResultDto
                .getLedgerIdsAndBudgets();
        Map<Category, Percentage> categoriesAndPercentageAverages = budgetAmountRatioCalculator
                .calculateByCategory(ledgerIdsAndBudgets, categories);

        Map<Category, Money> categoriesAndAmounts = budgetAmountCalculator
                .calculateAmount(Money.of(totalAmount), categoriesAndPercentageAverages);

        Map<Category, Money> categoriesAndTruncatedAmounts = budgetAmountTruncator
                .truncate(categoriesAndAmounts, truncationScale, Money.of(totalAmount));

        return GetBudgetRecommendationResponseDto.builder()
                .categoriesAndAmounts(categoriesAndTruncatedAmounts)
                .build();
    }

    private void validateContainsCategoryCannotIncluded(List<String> categoryNames) {
        if (categoryNames != null
                && categoryNames.contains(Category.EtCetera.categoryName())) {
            throw new CategoryCannotBeIncludedException();
        }
    }

    private void validateAmountAndTruncationScale(Long totalAmount, Long truncationScale) {
        if (truncationScale > totalAmount) {
            throw new TruncationScaleGreaterThanTotalAmountException();
        }

        if (truncationScale % 10 != 0) {
            throw new TruncationScaleIndivisibleException();
        }

        if (totalAmount <= 999_999L
                && notAtLeastOneDigit(truncationScale, totalAmount)) {
            throw new TruncationScaleRangeNotAllowedException();
        }

        if (totalAmount >= 1_000_000L
                && notAtLeastTwoDigits(truncationScale, totalAmount)) {
            throw new TruncationScaleRangeNotAllowedException();
        }
    }

    private boolean notAtLeastOneDigit(Long truncationScale, Long totalAmount) {
        return digitCount(totalAmount) - digitCount(truncationScale) < 1;
    }

    private boolean notAtLeastTwoDigits(Long truncationScale, Long totalAmount) {
        return digitCount(totalAmount) - digitCount(truncationScale) < 2;
    }

    private int digitCount(Long truncationScale) {
        return Long.toString(Math.abs(truncationScale)).length();
    }
}
