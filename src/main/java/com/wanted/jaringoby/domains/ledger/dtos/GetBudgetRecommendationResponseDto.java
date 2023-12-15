package com.wanted.jaringoby.domains.ledger.dtos;

import java.util.List;
import lombok.Builder;

@Builder
public record GetBudgetRecommendationResponseDto(
        List<BudgetRecommendationDto> budgetRecommendations,
        Boolean sumMatches
) {

}
