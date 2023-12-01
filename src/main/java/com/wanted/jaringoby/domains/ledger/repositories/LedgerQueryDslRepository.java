package com.wanted.jaringoby.domains.ledger.repositories;

import com.wanted.jaringoby.domains.customer.models.customer.CustomerId;
import com.wanted.jaringoby.domains.ledger.models.ledger.Ledger;
import com.wanted.jaringoby.domains.ledger.models.ledger.LedgerId;
import java.time.LocalDate;
import java.util.Optional;

public interface LedgerQueryDslRepository {

    Optional<Ledger> findByCustomerIdAndOngoing(CustomerId customerId);

    boolean existsByCustomerIdAndPeriod(
            CustomerId customerId,
            LocalDate startDate,
            LocalDate endDate
    );

    boolean existsByCustomerIdAndLedgerIdNotAndPeriod(
            CustomerId customerId,
            LedgerId ledgerId,
            LocalDate startDate,
            LocalDate endDate
    );
}
