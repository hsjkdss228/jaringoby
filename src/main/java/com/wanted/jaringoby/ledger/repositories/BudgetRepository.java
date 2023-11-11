package com.wanted.jaringoby.ledger.repositories;

import com.wanted.jaringoby.ledger.models.budget.Budget;
import com.wanted.jaringoby.ledger.models.budget.BudgetId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, BudgetId> {

}
