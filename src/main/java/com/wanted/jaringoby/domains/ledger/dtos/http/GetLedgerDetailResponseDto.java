package com.wanted.jaringoby.domains.ledger.dtos.http;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record GetLedgerDetailResponseDto(
        String id,
        LocalDate startDate,
        LocalDate endDate,
        List<GetBudgetResponseDto> budgets
) {

}
