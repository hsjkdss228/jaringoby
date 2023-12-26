package com.wanted.jaringoby.domains.expense.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class ExpenseProperty {

    @Column(name = "included_expense_sum")
    private Boolean includedExpenseSum;
}
