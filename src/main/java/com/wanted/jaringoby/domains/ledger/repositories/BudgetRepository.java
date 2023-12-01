package com.wanted.jaringoby.domains.ledger.repositories;

import com.wanted.jaringoby.domains.ledger.models.budget.Budget;
import com.wanted.jaringoby.domains.ledger.models.budget.BudgetId;
import com.wanted.jaringoby.domains.ledger.models.ledger.LedgerId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, BudgetId> {

    List<Budget> findByLedgerId(LedgerId ledgerId);
}
