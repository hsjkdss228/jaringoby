package com.wanted.jaringoby.domains.ledger.dtos.http;

import lombok.Builder;

@Builder
public record GetBudgetResponseDto(
        String id,
        String category,
        Long amount
) {

}
