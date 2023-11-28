package com.wanted.jaringoby.ledger.applications;

import com.wanted.jaringoby.customer.models.customer.CustomerId;
import com.wanted.jaringoby.ledger.dtos.GetLedgerDetailResponseDto;
import com.wanted.jaringoby.ledger.exceptions.LedgerOngoingNotFound;
import com.wanted.jaringoby.ledger.models.budget.Budget;
import com.wanted.jaringoby.ledger.models.ledger.Ledger;
import com.wanted.jaringoby.ledger.repositories.BudgetRepository;
import com.wanted.jaringoby.ledger.repositories.LedgerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetOngoingLedgerService {

    private final LedgerRepository ledgerRepository;
    private final BudgetRepository budgetRepository;

    @Transactional(readOnly = true)
    public GetLedgerDetailResponseDto getOngoingLedger(String customerId) {
        Ledger ledger = ledgerRepository
                .findByCustomerIdAndOngoing(CustomerId.of(customerId))
                .orElseThrow(LedgerOngoingNotFound::new);

        List<Budget> budgets = budgetRepository.findByLedgerId(ledger.id());

        return ledger.toLedgerDetailResponseDto(budgets);
    }
}
