package com.wanted.jaringoby.ledger.repositories;

import com.wanted.jaringoby.customer.models.customer.CustomerId;
import java.time.LocalDate;

public interface LedgerQueryDslRepository {

    boolean existsByCustomerIdAndPeriod(
            CustomerId customerId,
            LocalDate startDate,
            LocalDate endDate
    );
}
