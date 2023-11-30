package com.wanted.jaringoby.ledger.applications;

import com.wanted.jaringoby.category.exceptions.CategoryDuplicatedException;
import com.wanted.jaringoby.category.exceptions.CategoryNotFoundException;
import com.wanted.jaringoby.category.models.Category;
import com.wanted.jaringoby.common.models.Money;
import com.wanted.jaringoby.common.utils.UlidGenerator;
import com.wanted.jaringoby.customer.models.customer.CustomerId;
import com.wanted.jaringoby.ledger.dtos.BudgetRequestDto;
import com.wanted.jaringoby.ledger.dtos.ModifyLedgerBudgetsRequestDto;
import com.wanted.jaringoby.ledger.exceptions.LedgerNotFoundException;
import com.wanted.jaringoby.ledger.exceptions.LedgerNotOwnedException;
import com.wanted.jaringoby.ledger.exceptions.LedgerPeriodEndedException;
import com.wanted.jaringoby.ledger.models.budget.Budget;
import com.wanted.jaringoby.ledger.models.ledger.Ledger;
import com.wanted.jaringoby.ledger.models.ledger.LedgerId;
import com.wanted.jaringoby.ledger.repositories.BudgetRepository;
import com.wanted.jaringoby.ledger.repositories.LedgerRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ModifyLedgerBudgetsService {

    private final LedgerRepository ledgerRepository;
    private final BudgetRepository budgetRepository;
    private final UlidGenerator ulidGenerator;

    @Transactional
    public void modifyLedgerBudgets(
            String customerId,
            String ledgerId,
            ModifyLedgerBudgetsRequestDto modifyLedgerBudgetsRequestDto
    ) {
        Ledger ledger = ledgerRepository.findById(LedgerId.of(ledgerId))
                .orElseThrow(LedgerNotFoundException::new);

        validateLedger(ledger, CustomerId.of(customerId));

        List<BudgetRequestDto> budgetRequestDtos = modifyLedgerBudgetsRequestDto.getBudgets();

        validateBudgetCategories(budgetRequestDtos);

        List<Budget> budgets = budgetRepository.findByLedgerId(LedgerId.of(ledgerId));
        List<Budget> newBudgets = new ArrayList<>();

        budgetRequestDtos.forEach(budgetRequestDto -> {
            Category category = Category.of(budgetRequestDto.getCategory());
            Money amount = Money.of(budgetRequestDto.getAmount());

            Budget modified = modifyBudgetIfCategoryExists(budgets, category, amount);

            if (modified != null) {
                budgets.remove(modified);
                return;
            }

            Budget budget = Budget.builder()
                    .id(ulidGenerator.createRandomBudgetULID())
                    .ledgerId(ledger.id())
                    .category(category)
                    .amount(amount)
                    .build();

            newBudgets.add(budget);
        });

        if (budgetExists(newBudgets)) {
            budgetRepository.saveAll(newBudgets);
        }

        List<Budget> notGivenBudgets = new ArrayList<>(budgets);

        if (budgetExists(notGivenBudgets)) {
            budgetRepository.deleteAll(budgets);
        }
    }

    private void validateLedger(Ledger ledger, CustomerId customerId) {
        if (!ledger.ownedBy(customerId)) {
            throw new LedgerNotOwnedException();
        }

        if (ledger.hasEnded()) {
            throw new LedgerPeriodEndedException();
        }
    }

    private void validateBudgetCategories(List<BudgetRequestDto> budgetRequestDtos) {
        Set<String> categoryNames = new HashSet<>();

        budgetRequestDtos.forEach(budget -> {
            String categoryName = budget.getCategory();

            if (!Category.contains(categoryName)) {
                throw new CategoryNotFoundException();
            }

            if (categoryNames.contains(categoryName)) {
                throw new CategoryDuplicatedException();
            }

            categoryNames.add(categoryName);
        });
    }

    private Budget modifyBudgetIfCategoryExists(
            List<Budget> budgets,
            Category category,
            Money amount
    ) {
        for (Budget budget : budgets) {
            if (budget.categoryEquals(category)) {
                budget.modifyAmount(amount);
                return budget;
            }
        }

        return null;
    }

    private boolean budgetExists(List<Budget> budgets) {
        return !budgets.isEmpty();
    }
}
