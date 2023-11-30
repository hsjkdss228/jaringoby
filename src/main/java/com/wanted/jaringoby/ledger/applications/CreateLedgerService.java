package com.wanted.jaringoby.ledger.applications;

import static com.wanted.jaringoby.common.constants.Date.TODAY;

import com.wanted.jaringoby.category.exceptions.CategoryDuplicatedException;
import com.wanted.jaringoby.category.exceptions.CategoryNotFoundException;
import com.wanted.jaringoby.category.models.Category;
import com.wanted.jaringoby.common.utils.UlidGenerator;
import com.wanted.jaringoby.customer.models.customer.CustomerId;
import com.wanted.jaringoby.ledger.dtos.BudgetRequestDto;
import com.wanted.jaringoby.ledger.dtos.CreateLedgerRequestDto;
import com.wanted.jaringoby.ledger.dtos.CreateLedgerResponseDto;
import com.wanted.jaringoby.ledger.exceptions.LedgerPeriodInvalidException;
import com.wanted.jaringoby.ledger.exceptions.LedgerPeriodOverlappedException;
import com.wanted.jaringoby.ledger.models.budget.Budget;
import com.wanted.jaringoby.ledger.models.ledger.Ledger;
import com.wanted.jaringoby.ledger.repositories.BudgetRepository;
import com.wanted.jaringoby.ledger.repositories.LedgerRepository;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateLedgerService {

    private final LedgerRepository ledgerRepository;
    private final BudgetRepository budgetRepository;
    private final UlidGenerator ulidGenerator;

    @Transactional
    public CreateLedgerResponseDto createLedger(
            String customerId,
            CreateLedgerRequestDto createLedgerRequestDto
    ) {
        LocalDate startDate = createLedgerRequestDto.getStartDate();
        LocalDate endDate = createLedgerRequestDto.getEndDate();
        List<BudgetRequestDto> budgetRequestDtos = createLedgerRequestDto
                .getBudgets();

        validateLedgerPeriod(startDate, endDate, CustomerId.of(customerId));

        validateBudgetCategories(budgetRequestDtos);

        Ledger ledger = Ledger.builder()
                .id(ulidGenerator.createRandomLedgerULID())
                .customerId(customerId)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        ledgerRepository.save(ledger);

        List<Budget> budgets = budgetRequestDtos.stream()
                .map(budgetRequestDto -> Budget.builder()
                        .id(ulidGenerator.createRandomBudgetULID())
                        .ledgerId(ledger.id())
                        .category(budgetRequestDto.getCategory())
                        .amount(budgetRequestDto.getAmount())
                        .build())
                .toList();

        budgetRepository.saveAll(budgets);

        return CreateLedgerResponseDto.builder()
                .ledgerId(ledger.id().value())
                .build();
    }

    private void validateLedgerPeriod(LocalDate startDate, LocalDate endDate, CustomerId customerId) {
        if (startDate.isBefore(TODAY) || endDate.isBefore(startDate)) {
            throw new LedgerPeriodInvalidException();
        }

        if (ledgerRepository.existsByCustomerIdAndPeriod(customerId, startDate, endDate)) {
            throw new LedgerPeriodOverlappedException();
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
}
