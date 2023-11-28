package com.wanted.jaringoby.ledger.applications;

import static com.wanted.jaringoby.common.constants.Date.NOW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.wanted.jaringoby.category.exceptions.CategoryDuplicatedException;
import com.wanted.jaringoby.category.exceptions.CategoryNotFoundException;
import com.wanted.jaringoby.category.models.Category;
import com.wanted.jaringoby.common.utils.UlidGenerator;
import com.wanted.jaringoby.customer.models.customer.CustomerId;
import com.wanted.jaringoby.ledger.dtos.CreateBudgetRequestDto;
import com.wanted.jaringoby.ledger.dtos.CreateLedgerRequestDto;
import com.wanted.jaringoby.ledger.dtos.CreateLedgerResponseDto;
import com.wanted.jaringoby.ledger.exceptions.LedgerPeriodInvalidException;
import com.wanted.jaringoby.ledger.exceptions.LedgerPeriodOverlappedException;
import com.wanted.jaringoby.ledger.models.ledger.Ledger;
import com.wanted.jaringoby.ledger.repositories.BudgetRepository;
import com.wanted.jaringoby.ledger.repositories.LedgerRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CreateLedgerServiceTest {

    private CreateLedgerService createLedgerService;
    private LedgerRepository ledgerRepository;
    private BudgetRepository budgetRepository;
    private UlidGenerator ulidGenerator;

    @BeforeEach
    void setUp() {
        ledgerRepository = mock(LedgerRepository.class);
        budgetRepository = mock(BudgetRepository.class);
        ulidGenerator = mock(UlidGenerator.class);
        createLedgerService = new CreateLedgerService(
                ledgerRepository,
                budgetRepository,
                ulidGenerator
        );
    }

    private static final String CUSTOMER_ID = "CUSTOMER_ID";

    private static final String LEDGER_ID = "LEDGER_ID";
    private static final LocalDate START_DATE = NOW;
    private static final LocalDate END_DATE = START_DATE.plusMonths(1);

    private static final String BUDGET_ID = "BUDGET_ID";

    @DisplayName("성공")
    @Nested
    class Success {

        @DisplayName("Ledger 및 연관된 모든 Budget Entity 생성 후 영속화, 생성된 Ledger 식별자 반환")
        @Test
        void createLedger() {
            given(ledgerRepository
                    .existsByCustomerIdAndPeriod(CustomerId.of(CUSTOMER_ID), START_DATE, END_DATE))
                    .willReturn(false);

            given(ulidGenerator.createRandomLedgerULID())
                    .willReturn(LEDGER_ID);

            given(ulidGenerator.createRandomBudgetULID())
                    .willReturn(BUDGET_ID);

            CreateLedgerRequestDto createLedgerRequestDto = CreateLedgerRequestDto.builder()
                    .startDate(START_DATE)
                    .endDate(END_DATE)
                    .budgets(List.of(
                            CreateBudgetRequestDto.builder()
                                    .category(Category.Leisure.categoryName())
                                    .amount(200_000L)
                                    .build()
                    ))
                    .build();

            CreateLedgerResponseDto createLedgerResponseDto = createLedgerService
                    .createLedger(CUSTOMER_ID, createLedgerRequestDto);

            assertThat(createLedgerResponseDto).isNotNull();

            verify(ledgerRepository).save(any(Ledger.class));
            verify(budgetRepository).saveAll(any());
        }
    }

    @DisplayName("실패")
    @Nested
    class Failure {

        private static final LocalDate START_DATE_BEFORE_NOW = LocalDate.now().minusDays(1);
        private static final LocalDate END_DATE_BEFORE_START_DATE = START_DATE.minusDays(1);
        private static final String INVALID_CATEGORY_NAME = "INVALID_CATEGORY_NAME";

        @DisplayName("시작일이 당일보다 이전일인 경우 예외 발생")
        @Test
        void ledgerStartDateBeforeNow() {
            CreateLedgerRequestDto createLedgerRequestDto = CreateLedgerRequestDto.builder()
                    .startDate(START_DATE_BEFORE_NOW)
                    .endDate(END_DATE)
                    .budgets(List.of(
                            CreateBudgetRequestDto.builder()
                                    .category(Category.Leisure.categoryName())
                                    .amount(200_000L)
                                    .build()
                    ))
                    .build();

            assertThrows(LedgerPeriodInvalidException.class, () ->
                    createLedgerService.createLedger(CUSTOMER_ID, createLedgerRequestDto));

            verify(ledgerRepository, never()).save(any(Ledger.class));
            verify(budgetRepository, never()).saveAll(any());
        }

        @DisplayName("종료일이 시작일보다 이른 경우 예외 발생")
        @Test
        void ledgerEndDateBeforeStartDate() {
            CreateLedgerRequestDto createLedgerRequestDto = CreateLedgerRequestDto.builder()
                    .startDate(START_DATE)
                    .endDate(END_DATE_BEFORE_START_DATE)
                    .budgets(List.of(
                            CreateBudgetRequestDto.builder()
                                    .category(Category.EtCetera.categoryName())
                                    .amount(200_000L)
                                    .build()
                    ))
                    .build();

            assertThrows(LedgerPeriodInvalidException.class, () ->
                    createLedgerService.createLedger(CUSTOMER_ID, createLedgerRequestDto));

            verify(ledgerRepository, never()).save(any(Ledger.class));
            verify(budgetRepository, never()).saveAll(any());
        }

        @DisplayName("존재하지 않는 카테고리인 경우 예외 발생")
        @Test
        void categoryNotFound() {
            CreateLedgerRequestDto createLedgerRequestDto = CreateLedgerRequestDto.builder()
                    .startDate(START_DATE)
                    .endDate(END_DATE)
                    .budgets(List.of(
                            CreateBudgetRequestDto.builder()
                                    .category(INVALID_CATEGORY_NAME)
                                    .amount(50_000_000L)
                                    .build()
                    ))
                    .build();

            assertThrows(CategoryNotFoundException.class, () ->
                    createLedgerService.createLedger(CUSTOMER_ID, createLedgerRequestDto));

            verify(ledgerRepository, never()).save(any(Ledger.class));
            verify(budgetRepository, never()).saveAll(any());
        }

        @DisplayName("카테고리가 중복해서 주어지는 경우 예외 발생")
        @Test
        void categoryDuplicated() {
            CreateLedgerRequestDto createLedgerRequestDto = CreateLedgerRequestDto.builder()
                    .startDate(START_DATE)
                    .endDate(END_DATE)
                    .budgets(List.of(
                            CreateBudgetRequestDto.builder()
                                    .category(Category.Leisure.categoryName())
                                    .amount(50_000_000L)
                                    .build(),
                            CreateBudgetRequestDto.builder()
                                    .category(Category.Leisure.categoryName())
                                    .amount(100_000_000L)
                                    .build()
                    ))
                    .build();

            assertThrows(CategoryDuplicatedException.class, () ->
                    createLedgerService.createLedger(CUSTOMER_ID, createLedgerRequestDto));

            verify(ledgerRepository, never()).save(any(Ledger.class));
            verify(budgetRepository, never()).saveAll(any());
        }

        @DisplayName("기간 내 예산 관리가 이미 존재하는 경우 예외 발생")
        @Test
        void ledgerPeriodOverlapped() {
            given(ledgerRepository.existsByCustomerIdAndPeriod(
                    CustomerId.of(CUSTOMER_ID), START_DATE, END_DATE))
                    .willReturn(true);

            CreateLedgerRequestDto createLedgerRequestDto = CreateLedgerRequestDto.builder()
                    .startDate(START_DATE)
                    .endDate(END_DATE)
                    .budgets(List.of(
                            CreateBudgetRequestDto.builder()
                                    .category(Category.PersonalDevelopment.categoryName())
                                    .amount(100_000_000L)
                                    .build()
                    ))
                    .build();

            assertThrows(LedgerPeriodOverlappedException.class, () ->
                    createLedgerService.createLedger(CUSTOMER_ID, createLedgerRequestDto));

            verify(ledgerRepository, never()).save(any(Ledger.class));
            verify(budgetRepository, never()).saveAll(any());
        }
    }
}
