package com.wanted.jaringoby.domains.ledger.models;

import com.wanted.jaringoby.common.models.Money;
import com.wanted.jaringoby.common.models.Percentage;
import com.wanted.jaringoby.domains.category.models.Category;
import com.wanted.jaringoby.domains.ledger.entities.budget.Budget;
import com.wanted.jaringoby.domains.ledger.entities.ledger.LedgerId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class BudgetAmountRatioCalculator {

    public Map<Category, Percentage> calculateByCategory(
            Map<LedgerId, List<Budget>> ledgerIdsAndBudgets,
            List<Category> categories
    ) {
        Map<LedgerId, Money> ledgerIdsAndBudgetAmountSums
                = toLedgerIdsAndBudgetAmountSums(ledgerIdsAndBudgets);

        calculatePercentages(ledgerIdsAndBudgets, ledgerIdsAndBudgetAmountSums);

        return toCategoriesAndPercentageAverages(ledgerIdsAndBudgets, categories);
    }

    public Map<LedgerId, Money> toLedgerIdsAndBudgetAmountSums(
            Map<LedgerId, List<Budget>> ledgerIdsAndBudgets) {
        return ledgerIdsAndBudgets.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> {
                            List<Budget> budgets = entry.getValue();
                            return budgets.stream()
                                    .map(Budget::amount)
                                    .reduce(Money.zero(), Money::add);
                        }
                ));
    }

    public void calculatePercentages(
            Map<LedgerId, List<Budget>> ledgerIdsAndBudgets,
            Map<LedgerId, Money> ledgerIdsAndBudgetAmountSums
    ) {
        ledgerIdsAndBudgets.forEach(((ledgerId, budgets) -> {
            Money budgetAmountSum = ledgerIdsAndBudgetAmountSums.get(ledgerId);
            budgets.forEach(budget -> budget.calculatePercentage(budgetAmountSum));
        }));
    }

    public Map<Category, Percentage> toCategoriesAndPercentageAverages(
            Map<LedgerId, List<Budget>> ledgerIdsAndBudgets,
            List<Category> targetCategories
    ) {
        Map<Category, List<Percentage>> categoriesAndPercentages = new HashMap<>();

        ledgerIdsAndBudgets.forEach((ledgerId, budgets) ->
                budgets.forEach(budget ->
                        budget.addPercentageByCategory(
                                categoriesAndPercentages, targetCategories)
                )
        );

        int ledgersCount = ledgerIdsAndBudgets.size();

        return categoriesAndPercentages.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> entry.getValue()
                                .stream()
                                .reduce(Percentage::add).get()
                                .divide(ledgersCount)
                ));
    }
}
