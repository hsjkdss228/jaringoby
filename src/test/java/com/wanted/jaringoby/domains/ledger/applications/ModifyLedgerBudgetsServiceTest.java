package com.wanted.jaringoby.domains.ledger.applications;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.wanted.jaringoby.common.constants.Date;
import com.wanted.jaringoby.common.models.Money;
import com.wanted.jaringoby.common.utils.UlidGenerator;
import com.wanted.jaringoby.domains.category.exceptions.CategoryDuplicatedException;
import com.wanted.jaringoby.domains.category.exceptions.CategoryNotFoundException;
import com.wanted.jaringoby.domains.category.models.Category;
import com.wanted.jaringoby.domains.ledger.dtos.BudgetRequestDto;
import com.wanted.jaringoby.domains.ledger.dtos.ModifyLedgerBudgetsRequestDto;
import com.wanted.jaringoby.domains.ledger.exceptions.LedgerNotFoundException;
import com.wanted.jaringoby.domains.ledger.exceptions.LedgerNotOwnedException;
import com.wanted.jaringoby.domains.ledger.exceptions.LedgerPeriodEndedException;
import com.wanted.jaringoby.domains.ledger.models.budget.Budget;
import com.wanted.jaringoby.domains.ledger.models.ledger.Ledger;
import com.wanted.jaringoby.domains.ledger.models.ledger.LedgerId;
import com.wanted.jaringoby.domains.ledger.repositories.BudgetRepository;
import com.wanted.jaringoby.domains.ledger.repositories.LedgerRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ModifyLedgerBudgetsServiceTest {

    private ModifyLedgerBudgetsService modifyLedgerBudgetsService;
    private LedgerRepository ledgerRepository;
    private BudgetRepository budgetRepository;
    private UlidGenerator ulidGenerator;

    @BeforeEach
    void setUp() {
        ledgerRepository = mock(LedgerRepository.class);
        budgetRepository = mock(BudgetRepository.class);
        ulidGenerator = spy(new UlidGenerator());
        modifyLedgerBudgetsService = new ModifyLedgerBudgetsService(
                ledgerRepository,
                budgetRepository,
                ulidGenerator
        );
    }

    private static final String CUSTOMER_ID = "CUSTOMER_ID";
    private static final String LEDGER_ID = "LEDGER_ID";

    private static final LocalDate START_DATE = Date.today();
    private static final LocalDate END_DATE = Date.today().plusWeeks(2);

    private ModifyLedgerBudgetsRequestDto modifyLedgerBudgetsRequestDto;

    @DisplayName("성공")
    @Nested
    class Success {

        private Budget existingAndGivenBudget1;
        private Budget existingButNotGivenBudget2;

        @BeforeEach
        void setUp() {
            Ledger ledger = Ledger.builder()
                    .id(LEDGER_ID)
                    .customerId(CUSTOMER_ID)
                    .startDate(START_DATE)
                    .endDate(END_DATE)
                    .build();

            given(ledgerRepository.findById(LedgerId.of(LEDGER_ID)))
                    .willReturn(Optional.of(ledger));

            existingAndGivenBudget1 = spy(Budget.testBuilder()
                    .category(Category.Meal)
                    .amount(Money.of(1_000_000L))
                    .testBuild()
            );
            existingButNotGivenBudget2 = spy(Budget.testBuilder()
                    .category(Category.Transportation)
                    .amount(Money.of(200_000L))
                    .testBuild()
            );

            given(budgetRepository.findByLedgerId(LedgerId.of(LEDGER_ID)))
                    .willReturn(new ArrayList<>(List.of(
                            existingAndGivenBudget1,
                            existingButNotGivenBudget2
                    )));

            modifyLedgerBudgetsRequestDto = ModifyLedgerBudgetsRequestDto.builder()
                    .budgets(List.of(
                            BudgetRequestDto.builder()
                                    .category(Category.Meal.categoryName())
                                    .amount(10_000L)
                                    .build(),
                            BudgetRequestDto.builder()
                                    .category(Category.Leisure.categoryName())
                                    .amount(100_000L)
                                    .build()
                    ))
                    .build();
        }

        @DisplayName("전달된 예산들 중 존재하는 카테고리의 예산은 수정")
        @Test
        void modifyBudgetsIfCategoryExists() {
            assertDoesNotThrow(() -> modifyLedgerBudgetsService
                    .modifyLedgerBudgets(CUSTOMER_ID, LEDGER_ID, modifyLedgerBudgetsRequestDto));

            verify(existingAndGivenBudget1).modifyAmount(any(Money.class));
        }

        @DisplayName("전달된 예산들 중 존재하지 않는 카테고리의 예산은 새로 생성")
        @Test
        void saveNewBudgets() {
            assertDoesNotThrow(() -> modifyLedgerBudgetsService
                    .modifyLedgerBudgets(CUSTOMER_ID, LEDGER_ID, modifyLedgerBudgetsRequestDto));

            verify(ulidGenerator, times(1)).createRandomBudgetULID();
            verify(budgetRepository).saveAll(any());
        }

        @DisplayName("존재하는 카테고리의 예산이 전달되지 않은 경우 삭제")
        @Test
        void deleteNotGivenBudgets() {
            assertDoesNotThrow(() -> modifyLedgerBudgetsService
                    .modifyLedgerBudgets(CUSTOMER_ID, LEDGER_ID, modifyLedgerBudgetsRequestDto));

            verify(existingButNotGivenBudget2, never()).modifyAmount(any(Money.class));
            verify(budgetRepository).deleteAll(any());
        }
    }

    @DisplayName("실패")
    @Nested
    class Failure {

        private static final String OTHER_CUSTOMER_ID = "OTHER_CUSTOMER_ID";
        private static final String NOT_EXISTING_CATEGORY = "NOT_EXISTING_CATEGORY";

        private static final LocalDate ENDED_START_DATE = Date.today().minusWeeks(2);
        private static final LocalDate ENDED_END_DATE = Date.today().minusWeeks(1);

        @DisplayName("존재하지 않는 예산 관리인 경우 예외처리")
        @Test
        void ledgerNotFound() {
            given(ledgerRepository.findById(LedgerId.of(LEDGER_ID)))
                    .willThrow(LedgerNotFoundException.class);

            modifyLedgerBudgetsRequestDto = ModifyLedgerBudgetsRequestDto.builder()
                    .budgets(List.of(
                            BudgetRequestDto.builder()
                                    .category(Category.Meal.categoryName())
                                    .amount(10_000L)
                                    .build()
                    ))
                    .build();

            assertThrows(LedgerNotFoundException.class, () -> modifyLedgerBudgetsService
                    .modifyLedgerBudgets(CUSTOMER_ID, LEDGER_ID, modifyLedgerBudgetsRequestDto));
        }

        @DisplayName("대상 고객의 예산 관리가 아닌 경우 예외처리")
        @Test
        void ledgerNotOwned() {
            Ledger ledger = Ledger.builder()
                    .id(LEDGER_ID)
                    .customerId(OTHER_CUSTOMER_ID)
                    .startDate(START_DATE)
                    .endDate(END_DATE)
                    .build();

            given(ledgerRepository.findById(LedgerId.of(LEDGER_ID)))
                    .willReturn(Optional.of(ledger));

            modifyLedgerBudgetsRequestDto = ModifyLedgerBudgetsRequestDto.builder()
                    .budgets(List.of(
                            BudgetRequestDto.builder()
                                    .category(Category.Meal.categoryName())
                                    .amount(10_000L)
                                    .build()
                    ))
                    .build();

            assertThrows(LedgerNotOwnedException.class, () -> modifyLedgerBudgetsService
                    .modifyLedgerBudgets(CUSTOMER_ID, LEDGER_ID, modifyLedgerBudgetsRequestDto));
        }

        @DisplayName("종료된 예산 관리인 경우 예외처리")
        @Test
        void ledgerPeriodEnded() {
            Ledger ledger = Ledger.builder()
                    .id(LEDGER_ID)
                    .customerId(CUSTOMER_ID)
                    .startDate(ENDED_START_DATE)
                    .endDate(ENDED_END_DATE)
                    .build();

            given(ledgerRepository.findById(LedgerId.of(LEDGER_ID)))
                    .willReturn(Optional.of(ledger));

            modifyLedgerBudgetsRequestDto = ModifyLedgerBudgetsRequestDto.builder()
                    .budgets(List.of(
                            BudgetRequestDto.builder()
                                    .category(NOT_EXISTING_CATEGORY)
                                    .amount(10_000L)
                                    .build()
                    ))
                    .build();

            assertThrows(LedgerPeriodEndedException.class, () -> modifyLedgerBudgetsService
                    .modifyLedgerBudgets(CUSTOMER_ID, LEDGER_ID, modifyLedgerBudgetsRequestDto));
        }

        @DisplayName("존재하지 않는 카테고리인 경우 예외처리")
        @Test
        void categoryNotFound() {
            Ledger ledger = Ledger.builder()
                    .id(LEDGER_ID)
                    .customerId(CUSTOMER_ID)
                    .startDate(START_DATE)
                    .endDate(END_DATE)
                    .build();

            given(ledgerRepository.findById(LedgerId.of(LEDGER_ID)))
                    .willReturn(Optional.of(ledger));

            modifyLedgerBudgetsRequestDto = ModifyLedgerBudgetsRequestDto.builder()
                    .budgets(List.of(
                            BudgetRequestDto.builder()
                                    .category(NOT_EXISTING_CATEGORY)
                                    .amount(10_000L)
                                    .build()
                    ))
                    .build();

            assertThrows(CategoryNotFoundException.class, () -> modifyLedgerBudgetsService
                    .modifyLedgerBudgets(CUSTOMER_ID, LEDGER_ID, modifyLedgerBudgetsRequestDto));
        }

        @DisplayName("카테고리가 중복해서 주어지는 경우 예외처리")
        @Test
        void categoryDuplicated() {
            Ledger ledger = Ledger.builder()
                    .id(LEDGER_ID)
                    .customerId(CUSTOMER_ID)
                    .startDate(START_DATE)
                    .endDate(END_DATE)
                    .build();

            given(ledgerRepository.findById(LedgerId.of(LEDGER_ID)))
                    .willReturn(Optional.of(ledger));

            modifyLedgerBudgetsRequestDto = ModifyLedgerBudgetsRequestDto.builder()
                    .budgets(List.of(
                            BudgetRequestDto.builder()
                                    .category(Category.Meal.categoryName())
                                    .amount(10_000L)
                                    .build(),
                            BudgetRequestDto.builder()
                                    .category(Category.Meal.categoryName())
                                    .amount(500_000L)
                                    .build()
                    ))
                    .build();

            assertThrows(CategoryDuplicatedException.class, () -> modifyLedgerBudgetsService
                    .modifyLedgerBudgets(CUSTOMER_ID, LEDGER_ID, modifyLedgerBudgetsRequestDto));
        }
    }
}
