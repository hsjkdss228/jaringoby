package com.wanted.jaringoby.domains.ledger.applications;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ModifyLedgerPeriodService {

    private final LedgerRepository ledgerRepository;

    public ModifyLedgerPeriodService(LedgerRepository ledgerRepository) {
        this.ledgerRepository = ledgerRepository;
    }

    @Transactional
    public void modifyLedgerPeriod(
            String customerId,
            String ledgerId,
            ModifyLedgerPeriodRequestDto modifyLedgerPeriodRequestDto
    ) {
        LocalDate startDate = modifyLedgerPeriodRequestDto.getStartDate();
        LocalDate endDate = modifyLedgerPeriodRequestDto.getEndDate();

        Ledger ledger = ledgerRepository.findById(LedgerId.of(ledgerId))
                .orElseThrow(LedgerNotFoundException::new);

        validateLedgerCustomer(ledger, CustomerId.of(customerId));

        validateLedgerPeriod(ledger, startDate, endDate, CustomerId.of(customerId));

        ledger.modifyPeriod(startDate, endDate);
    }

    private void validateLedgerCustomer(Ledger ledger, CustomerId customerId) {
        if (!ledger.ownedBy(customerId)) {
            throw new LedgerNotOwnedException();
        }
    }

    private void validateLedgerPeriod(
            Ledger ledger,
            LocalDate startDate,
            LocalDate endDate,
            CustomerId customerId
    ) {
        if (ledger.hasEnded()) {
            throw new LedgerPeriodEndedException();
        }

        if (ledger.isInProgress()
                && (ledger.startDateIsDifferentFrom(startDate)
                || endDate.isBefore(Date.today()))
        ) {
            throw new LedgerPeriodInvalidException();
        }

        if (ledger.hasNotStarted()
                && (startDate.isBefore(Date.today())
                || endDate.isBefore(startDate))) {
            throw new LedgerPeriodInvalidException();
        }

        if (ledgerRepository.existsByCustomerIdAndLedgerIdNotAndPeriod(
                customerId, ledger.id(), startDate, endDate)) {
            throw new LedgerPeriodOverlappedException();
        }
    }
}
