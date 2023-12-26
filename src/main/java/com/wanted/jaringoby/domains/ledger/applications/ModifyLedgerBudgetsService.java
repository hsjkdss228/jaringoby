package com.wanted.jaringoby.domains.ledger.applications;

import com.wanted.jaringoby.common.models.Money;
import com.wanted.jaringoby.common.utils.UlidGenerator;
import com.wanted.jaringoby.domains.category.exceptions.CategoryDuplicatedException;
import com.wanted.jaringoby.domains.category.exceptions.CategoryNotFoundException;
import com.wanted.jaringoby.domains.category.models.Category;
import com.wanted.jaringoby.domains.customer.entities.CustomerId;
import com.wanted.jaringoby.domains.ledger.dtos.http.BudgetRequestDto;
import com.wanted.jaringoby.domains.ledger.dtos.http.ModifyLedgerBudgetsRequestDto;
import com.wanted.jaringoby.domains.ledger.entities.budget.Budget;
import com.wanted.jaringoby.domains.ledger.entities.ledger.Ledger;
import com.wanted.jaringoby.domains.ledger.entities.ledger.LedgerId;
import com.wanted.jaringoby.domains.ledger.exceptions.LedgerNotFoundException;
import com.wanted.jaringoby.domains.ledger.exceptions.LedgerNotOwnedException;
import com.wanted.jaringoby.domains.ledger.exceptions.LedgerPeriodEndedException;
import com.wanted.jaringoby.domains.ledger.repositories.BudgetRepository;
import com.wanted.jaringoby.domains.ledger.repositories.LedgerRepository;
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
            budgetRepository.deleteAll(notGivenBudgets);
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

            if (Category.doesNotContain(categoryName)) {
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
