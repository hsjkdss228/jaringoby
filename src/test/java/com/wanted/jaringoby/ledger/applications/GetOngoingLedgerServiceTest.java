package com.wanted.jaringoby.ledger.applications;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.wanted.jaringoby.category.models.Category;
import com.wanted.jaringoby.common.models.Money;
import com.wanted.jaringoby.customer.models.customer.CustomerId;
import com.wanted.jaringoby.ledger.dtos.GetLedgerDetailResponseDto;
import com.wanted.jaringoby.ledger.exceptions.LedgerOngoingNotFound;
import com.wanted.jaringoby.ledger.models.budget.Budget;
import com.wanted.jaringoby.ledger.models.ledger.Ledger;
import com.wanted.jaringoby.ledger.models.ledger.LedgerId;
import com.wanted.jaringoby.ledger.repositories.BudgetRepository;
import com.wanted.jaringoby.ledger.repositories.LedgerRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class GetOngoingLedgerServiceTest {

    private GetOngoingLedgerService getOngoingLedgerService;
    private LedgerRepository ledgerRepository;
    private BudgetRepository budgetRepository;

    @BeforeEach
    void setUp() {
        ledgerRepository = mock(LedgerRepository.class);
        budgetRepository = mock(BudgetRepository.class);
        getOngoingLedgerService = new GetOngoingLedgerService(
                ledgerRepository,
                budgetRepository
        );
    }

    private static final String CUSTOMER_ID = "CUSTOMER_ID";

    private static final String LEDGER_ID = "LEDGER_ID";

    private static final String BUDGET_ID_1 = "BUDGET_ID_1";
    private static final String BUDGET_ID_2 = "BUDGET_ID_2";

    @DisplayName("성공")
    @Nested
    class Success {

        @DisplayName("조회된 Ledger, Budgets를 DTO로 변환해 반환")
        @Test
        void getOngoingLedger() {
            Ledger ledger = Ledger.builder()
                    .id(LEDGER_ID)
                    .startDate(LocalDate.now().minusDays(5))
                    .endDate(LocalDate.now().plusDays(25))
                    .build();

            given(ledgerRepository.findByCustomerIdAndOngoing(CustomerId.of(CUSTOMER_ID)))
                    .willReturn(Optional.of(ledger));

            List<Budget> budgets = List.of(
                    Budget.builder()
                            .id(BUDGET_ID_1)
                            .category(Category.of(Category.Living.categoryName()))
                            .amount(Money.of(1_000_000L))
                            .build(),
                    Budget.builder()
                            .id(BUDGET_ID_2)
                            .category(Category.of(Category.Meal.categoryName()))
                            .amount(Money.of(1_500_000L))
                            .build()
            );

            given(budgetRepository.findByLedgerId(LedgerId.of(LEDGER_ID)))
                    .willReturn(budgets);

            GetLedgerDetailResponseDto ledgerDetailResponseDto = getOngoingLedgerService
                    .getOngoingLedger(CUSTOMER_ID);

            assertThat(ledgerDetailResponseDto).isNotNull();
            assertThat(ledgerDetailResponseDto.id()).isEqualTo(LEDGER_ID);

            List<String> budgetIds = List.of(BUDGET_ID_1, BUDGET_ID_2);
            ledgerDetailResponseDto.budgets().forEach(getBudgetResponseDto ->
                    assertThat(budgetIds).contains(getBudgetResponseDto.id()));
        }
    }

    @DisplayName("실패")
    @Nested
    class Failure {

        @DisplayName("진행 중인 Ledger 존재하지 않는 경우, 예외 발생")
        @Test
        void ledgerOngoingNotFound() {
            given(ledgerRepository.findByCustomerIdAndOngoing(any(CustomerId.class)))
                    .willThrow(LedgerOngoingNotFound.class);

            assertThrows(LedgerOngoingNotFound.class, () -> getOngoingLedgerService
                    .getOngoingLedger(CUSTOMER_ID));

            verify(budgetRepository, never()).findByLedgerId(any(LedgerId.class));
        }
    }
}
