package com.wanted.jaringoby.ledger.dtos;

import lombok.Builder;

@Builder
public record GetBudgetResponseDto(
        String id,
        String category,
        Long amount
) {

}
