package com.wanted.jaringoby.domains.ledger.dtos.http;

import lombok.Builder;

@Builder
public record BudgetRecommendationDto(
        String name,
        Long amount
) {

}
