package com.wanted.jaringoby.domains.ledger.repositories;

import com.wanted.jaringoby.domains.ledger.dtos.query.LedgerIdAndBudgetsQueryResultDto;

public interface BudgetQueryDslRepository {

    LedgerIdAndBudgetsQueryResultDto findAllLedgerIdAndBudgets();
}
