package com.wanted.jaringoby.domains.ledger.dtos.query;

import com.wanted.jaringoby.domains.ledger.models.budget.Budget;
import com.wanted.jaringoby.domains.ledger.models.ledger.LedgerId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LedgerIdAndBudgetsQueryResultDto {

    private final Map<LedgerId, List<Budget>> ledgerIdsAndBudgets = new HashMap<>();

    @Builder
    public LedgerIdAndBudgetsQueryResultDto(
            Map<LedgerId, List<Budget>> ledgerIdsAndBudgets
    ) {
        this.ledgerIdsAndBudgets.putAll(ledgerIdsAndBudgets);
    }
}
