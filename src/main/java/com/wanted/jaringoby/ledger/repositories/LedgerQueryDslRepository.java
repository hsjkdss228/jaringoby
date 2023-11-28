package com.wanted.jaringoby.ledger.repositories;

import com.wanted.jaringoby.customer.models.customer.CustomerId;
import com.wanted.jaringoby.ledger.models.ledger.Ledger;
import java.time.LocalDate;
import java.util.Optional;

public interface LedgerQueryDslRepository {

    Optional<Ledger> findByCustomerIdAndOngoing(CustomerId customerId);

    boolean existsByCustomerIdAndPeriod(
            CustomerId customerId,
            LocalDate startDate,
            LocalDate endDate
    );
}
