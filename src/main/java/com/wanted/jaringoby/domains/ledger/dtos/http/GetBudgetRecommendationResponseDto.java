package com.wanted.jaringoby.domains.ledger.dtos.http;

import com.wanted.jaringoby.common.models.Money;
import com.wanted.jaringoby.domains.category.models.Category;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Builder;

public class GetBudgetRecommendationResponseDto {

    private final List<BudgetRecommendationDto> budgetRecommendations = new ArrayList<>();

    @Builder
    private GetBudgetRecommendationResponseDto(Map<Category, Money> categoriesAndAmounts) {
        categoriesAndAmounts.forEach(((category, money) ->
                budgetRecommendations.add(BudgetRecommendationDto.builder()
                        .name(category.categoryName())
                        .amount(money.value())
                        .build())
        ));
    }

    @Builder(builderMethodName = "testBuilder", buildMethodName = "testBuild")
    private GetBudgetRecommendationResponseDto(
            List<BudgetRecommendationDto> budgetRecommendations
    ) {
        this.budgetRecommendations.addAll(budgetRecommendations);
    }

    public List<BudgetRecommendationDto> getBudgetRecommendations() {
        return budgetRecommendations;
    }
}
