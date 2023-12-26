package com.wanted.jaringoby.domains.expense.repositories;

import com.wanted.jaringoby.domains.expense.entities.Expense;
import com.wanted.jaringoby.domains.expense.entities.ExpenseId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, ExpenseId> {

}
