package com.wanted.jaringoby.domains.ledger.applications;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.wanted.jaringoby.common.constants.Date;
import com.wanted.jaringoby.domains.customer.entities.CustomerId;
import com.wanted.jaringoby.domains.ledger.dtos.http.ModifyLedgerPeriodRequestDto;
import com.wanted.jaringoby.domains.ledger.entities.ledger.Ledger;
import com.wanted.jaringoby.domains.ledger.entities.ledger.LedgerId;
import com.wanted.jaringoby.domains.ledger.exceptions.LedgerNotFoundException;
import com.wanted.jaringoby.domains.ledger.exceptions.LedgerNotOwnedException;
import com.wanted.jaringoby.domains.ledger.exceptions.LedgerPeriodEndedException;
import com.wanted.jaringoby.domains.ledger.exceptions.LedgerPeriodInvalidException;
import com.wanted.jaringoby.domains.ledger.exceptions.LedgerPeriodOverlappedException;
import com.wanted.jaringoby.domains.ledger.repositories.LedgerRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ModifyLedgerPeriodServiceTest {

    private ModifyLedgerPeriodService modifyLedgerPeriodService;
    private LedgerRepository ledgerRepository;

    @BeforeEach
    void setUp() {
        ledgerRepository = mock(LedgerRepository.class);
        modifyLedgerPeriodService = new ModifyLedgerPeriodService(ledgerRepository);
    }

    private static final String CUSTOMER_ID = "CUSTOMER_ID";
    private static final String LEDGER_ID = "LEDGER_ID";

    private LocalDate previousStartDate;
    private LocalDate previousEndDate;
    private LocalDate startDate;
    private LocalDate endDate;

    private Ledger ledger;

    @DisplayName("성공")
    @Nested
    class Success {

        @DisplayName("대상 Ledger가 현재 진행 중이면서 전달된 날짜에 오류가 없는 경우")
        @Nested
        class LedgerIsInProgress {

            @BeforeEach
            void setUp() {
                previousStartDate = Date.today().minusDays(15);
                previousEndDate = previousStartDate.plusMonths(1);

                ledger = spy(Ledger.builder()
                        .id(LEDGER_ID)
                        .customerId(CUSTOMER_ID)
                        .startDate(previousStartDate)
                        .endDate(previousEndDate)
                        .build()
                );

                given(ledgerRepository.findById(LedgerId.of(LEDGER_ID)))
                        .willReturn(Optional.of(ledger));

                startDate = previousStartDate;
                endDate = previousEndDate.minusDays(7);

                given(ledgerRepository.existsByCustomerIdAndLedgerIdNotAndPeriod(
                        CustomerId.of(CUSTOMER_ID),
                        LedgerId.of(LEDGER_ID),
                        startDate,
                        endDate
                )).willReturn(false);
            }

            @DisplayName("Ledger 기간을 전달된 날짜로 변경")
            @Test
            void modifyProgressingLedgerPeriod() {
                ModifyLedgerPeriodRequestDto modifyLedgerPeriodRequestDto
                        = ModifyLedgerPeriodRequestDto.builder()
                        .startDate(startDate)
                        .endDate(endDate)
                        .build();

                assertDoesNotThrow(() -> modifyLedgerPeriodService
                        .modifyLedgerPeriod(CUSTOMER_ID, LEDGER_ID, modifyLedgerPeriodRequestDto));

                verify(ledger).modifyPeriod(startDate, endDate);
            }
        }

        @DisplayName("대상 Ledger가 시작 전이면서 전달된 날짜에 오류가 없는 경우")
        @Nested
        class LedgerHasNotStarted {

            @BeforeEach
            void setUp() {
                previousStartDate = Date.today().plusMonths(1).minusDays(5);
                previousEndDate = previousStartDate.plusMonths(1);

                ledger = spy(Ledger.builder()
                        .id(LEDGER_ID)
                        .customerId(CUSTOMER_ID)
                        .startDate(previousStartDate)
                        .endDate(previousEndDate)
                        .build()
                );

                given(ledgerRepository.findById(LedgerId.of(LEDGER_ID)))
                        .willReturn(Optional.of(ledger));

                startDate = previousStartDate.plusMonths(1);
                endDate = previousEndDate.plusMonths(1);

                given(ledgerRepository.existsByCustomerIdAndLedgerIdNotAndPeriod(
                        CustomerId.of(CUSTOMER_ID),
                        LedgerId.of(LEDGER_ID),
                        startDate,
                        endDate
                )).willReturn(false);
            }

            @DisplayName("Ledger 기간을 전달된 날짜로 변경")
            @Test
            void modifyNotStartedLedgerPeriod() {
                ModifyLedgerPeriodRequestDto modifyLedgerPeriodRequestDto
                        = ModifyLedgerPeriodRequestDto.builder()
                        .startDate(startDate)
                        .endDate(endDate)
                        .build();

                assertDoesNotThrow(() -> modifyLedgerPeriodService
                        .modifyLedgerPeriod(CUSTOMER_ID, LEDGER_ID, modifyLedgerPeriodRequestDto));

                verify(ledger).modifyPeriod(startDate, endDate);
            }
        }
    }

    @DisplayName("실패")
    @Nested
    class Failure {

        private static final String OTHER_CUSTOMER_ID = "OTHER_CUSTOMER_ID";

        @DisplayName("대상 예산 관리가 종료된 경우")
        @Nested
        class LedgerHasEnded {

            @BeforeEach
            void setUp() {
                previousStartDate = Date.today().minusMonths(1).minusDays(7);
                previousEndDate = previousStartDate.plusMonths(1);

                ledger = Ledger.builder()
                        .id(LEDGER_ID)
                        .customerId(CUSTOMER_ID)
                        .startDate(previousStartDate)
                        .endDate(previousEndDate)
                        .build();

                given(ledgerRepository.findById(LedgerId.of(LEDGER_ID)))
                        .willReturn(Optional.of(ledger));
            }

            @DisplayName("예외 발생")
            @Test
            void ledgerPeriodEnded() {
                ModifyLedgerPeriodRequestDto modifyLedgerPeriodRequestDto
                        = ModifyLedgerPeriodRequestDto.builder()
                        .startDate(previousStartDate.plusMonths(5))
                        .endDate(previousEndDate.plusMonths(5))
                        .build();

                assertThrows(LedgerPeriodEndedException.class, () ->
                        modifyLedgerPeriodService.modifyLedgerPeriod(
                                CUSTOMER_ID, LEDGER_ID, modifyLedgerPeriodRequestDto));
            }
        }

        @DisplayName("대상 예산 관리가 진행 중인 경우")
        @Nested
        class LedgerIsInProgress {

            @BeforeEach
            void setUp() {
                previousStartDate = Date.today().minusDays(14);
                previousEndDate = previousStartDate.plusMonths(1);

                ledger = Ledger.builder()
                        .id(LEDGER_ID)
                        .customerId(CUSTOMER_ID)
                        .startDate(previousStartDate)
                        .endDate(previousEndDate)
                        .build();

                given(ledgerRepository.findById(LedgerId.of(LEDGER_ID)))
                        .willReturn(Optional.of(ledger));
            }

            @DisplayName("대상 예산 관리 시작일이 전달된 시작일과 다른 경우 예외 발생")
            @Test
            void ledgerStartDateIsDifferentFromStartDate() {
                ModifyLedgerPeriodRequestDto modifyLedgerPeriodRequestDto
                        = ModifyLedgerPeriodRequestDto.builder()
                        .startDate(previousStartDate.plusDays(5))
                        .endDate(previousEndDate.minusDays(5))
                        .build();

                assertThrows(LedgerPeriodInvalidException.class, () ->
                        modifyLedgerPeriodService.modifyLedgerPeriod(
                                CUSTOMER_ID, LEDGER_ID, modifyLedgerPeriodRequestDto));
            }

            @DisplayName("전달된 종료일이 당일 이전인 경우 예외 발생")
            @Test
            void endDateIsBeforeToday() {
                ModifyLedgerPeriodRequestDto modifyLedgerPeriodRequestDto
                        = ModifyLedgerPeriodRequestDto.builder()
                        .startDate(previousStartDate)
                        .endDate(Date.today().minusDays(2))
                        .build();

                assertThrows(LedgerPeriodInvalidException.class, () ->
                        modifyLedgerPeriodService.modifyLedgerPeriod(
                                CUSTOMER_ID, LEDGER_ID, modifyLedgerPeriodRequestDto));
            }
        }

        @DisplayName("대상 예산 관리가 진행 예정인 경우")
        @Nested
        class LedgerHasNotStarted {

            @BeforeEach
            void setUp() {
                previousStartDate = Date.today().plusDays(14);
                previousEndDate = previousStartDate.plusWeeks(1);

                Ledger ledger = Ledger.builder()
                        .id(LEDGER_ID)
                        .customerId(CUSTOMER_ID)
                        .startDate(previousStartDate)
                        .endDate(previousEndDate)
                        .build();

                given(ledgerRepository.findById(LedgerId.of(LEDGER_ID)))
                        .willReturn(Optional.of(ledger));
            }

            @DisplayName("전달된 시작일이 당일 이전인 경우 예외 발생")
            @Test
            void startDateIsBeforeToday() {
                ModifyLedgerPeriodRequestDto modifyLedgerPeriodRequestDto
                        = ModifyLedgerPeriodRequestDto.builder()
                        .startDate(Date.today().minusDays(2))
                        .endDate(previousEndDate)
                        .build();

                assertThrows(LedgerPeriodInvalidException.class, () ->
                        modifyLedgerPeriodService.modifyLedgerPeriod(
                                CUSTOMER_ID, LEDGER_ID, modifyLedgerPeriodRequestDto));
            }

            @DisplayName("전달된 종료일이 전달된 시작일 이전인 경우 예외 발생")
            @Test
            void endDateIsBeforeStartDate() {
                ModifyLedgerPeriodRequestDto modifyLedgerPeriodRequestDto
                        = ModifyLedgerPeriodRequestDto.builder()
                        .startDate(previousStartDate.plusWeeks(2))
                        .endDate(previousEndDate)
                        .build();

                assertThrows(LedgerPeriodInvalidException.class, () ->
                        modifyLedgerPeriodService.modifyLedgerPeriod(
                                CUSTOMER_ID, LEDGER_ID, modifyLedgerPeriodRequestDto));
            }
        }

        @DisplayName("대상 예산 관리가 존재하지 않는 경우")
        @Nested
        class LedgerNotFound {

            @BeforeEach
            void setUp() {
                given(ledgerRepository.findById(LedgerId.of(LEDGER_ID)))
                        .willThrow(LedgerNotFoundException.class);
            }

            @DisplayName("예외 발생")
            @Test
            void ledgerNotFound() {
                ModifyLedgerPeriodRequestDto modifyLedgerPeriodRequestDto
                        = ModifyLedgerPeriodRequestDto.builder()
                        .startDate(Date.today().plusWeeks(2))
                        .endDate(Date.today().plusWeeks(4))
                        .build();

                assertThrows(LedgerNotFoundException.class, () ->
                        modifyLedgerPeriodService.modifyLedgerPeriod(
                                CUSTOMER_ID, LEDGER_ID, modifyLedgerPeriodRequestDto));
            }
        }

        @DisplayName("다른 고객의 예산 관리인 경우")
        @Nested
        class LedgerNotOwned {

            @BeforeEach
            void setUp() {
                previousStartDate = Date.today().plusDays(15);
                previousEndDate = previousStartDate.plusMonths(1);

                ledger = Ledger.builder()
                        .id(LEDGER_ID)
                        .customerId(OTHER_CUSTOMER_ID)
                        .startDate(previousStartDate)
                        .endDate(previousEndDate)
                        .build();

                given(ledgerRepository.findById(LedgerId.of(LEDGER_ID)))
                        .willReturn(Optional.of(ledger));
            }

            @DisplayName("예외 발생")
            @Test
            void ledgerNotOwned() {
                ModifyLedgerPeriodRequestDto modifyLedgerPeriodRequestDto
                        = ModifyLedgerPeriodRequestDto.builder()
                        .startDate(Date.today().plusWeeks(2))
                        .endDate(Date.today().plusWeeks(4))
                        .build();

                assertThrows(LedgerNotOwnedException.class, () ->
                        modifyLedgerPeriodService.modifyLedgerPeriod(
                                CUSTOMER_ID, LEDGER_ID, modifyLedgerPeriodRequestDto));
            }
        }

        @DisplayName("기간 내 다른 예산 관리가 이미 존재하는 경우")
        @Nested
        class LedgerPeriodOverlapped {

            @BeforeEach
            void setUp() {
                previousStartDate = Date.today().minusDays(15);
                previousEndDate = previousStartDate.plusMonths(1);

                ledger = Ledger.builder()
                        .id(LEDGER_ID)
                        .customerId(CUSTOMER_ID)
                        .startDate(previousStartDate)
                        .endDate(previousEndDate)
                        .build();

                given(ledgerRepository.findById(LedgerId.of(LEDGER_ID)))
                        .willReturn(Optional.of(ledger));

                startDate = previousStartDate;
                endDate = previousEndDate.minusDays(7);

                given(ledgerRepository.existsByCustomerIdAndLedgerIdNotAndPeriod(
                        CustomerId.of(CUSTOMER_ID),
                        LedgerId.of(LEDGER_ID),
                        startDate,
                        endDate
                )).willReturn(true);
            }

            @DisplayName("예외 발생")
            @Test
            void ledgerPeriodOverlapped() {
                ModifyLedgerPeriodRequestDto modifyLedgerPeriodRequestDto
                        = ModifyLedgerPeriodRequestDto.builder()
                        .startDate(startDate)
                        .endDate(endDate)
                        .build();

                assertThrows(LedgerPeriodOverlappedException.class, () ->
                        modifyLedgerPeriodService.modifyLedgerPeriod(
                                CUSTOMER_ID, LEDGER_ID, modifyLedgerPeriodRequestDto
                        ));
            }
        }
    }
}
