package com.wanted.jaringoby.domains.expense.repositories;

import com.wanted.jaringoby.domains.expense.models.Expense;
import com.wanted.jaringoby.domains.expense.models.ExpenseId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, ExpenseId> {

}
