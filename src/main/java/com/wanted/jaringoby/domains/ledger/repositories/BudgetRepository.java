package com.wanted.jaringoby.domains.ledger.repositories;

import com.wanted.jaringoby.domains.ledger.entities.budget.Budget;
import com.wanted.jaringoby.domains.ledger.entities.budget.BudgetId;
import com.wanted.jaringoby.domains.ledger.entities.ledger.LedgerId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, BudgetId>,
        BudgetQueryDslRepository {

    List<Budget> findByLedgerId(LedgerId ledgerId);
}
