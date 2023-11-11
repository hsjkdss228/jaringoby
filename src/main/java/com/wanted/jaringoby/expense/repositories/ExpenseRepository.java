package com.wanted.jaringoby.expense.repositories;

import com.wanted.jaringoby.expense.models.Expense;
import com.wanted.jaringoby.expense.models.ExpenseId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, ExpenseId> {

}
