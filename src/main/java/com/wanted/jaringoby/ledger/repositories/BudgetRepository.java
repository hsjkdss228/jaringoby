package com.wanted.jaringoby.ledger.repositories;

import com.wanted.jaringoby.ledger.models.budget.Budget;
import com.wanted.jaringoby.ledger.models.budget.BudgetId;
import com.wanted.jaringoby.ledger.models.ledger.LedgerId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, BudgetId> {

    List<Budget> findByLedgerId(LedgerId ledgerId);
}
