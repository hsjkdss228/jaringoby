package com.wanted.jaringoby.ledger.repositories;

import static com.wanted.jaringoby.ledger.models.ledger.QLedger.ledger;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wanted.jaringoby.customer.models.customer.CustomerId;
import com.wanted.jaringoby.ledger.models.ledger.Ledger;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LedgerQueryDslRepositoryImpl implements LedgerQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsByCustomerIdAndPeriod(
            CustomerId customerId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        List<Ledger> queryResult = jpaQueryFactory
                .select(ledger)
                .from(ledger)
                .where(ledger.customerId.eq(customerId)
                        .and(ledgerPeriodOverlapsBetween(startDate, endDate)))
                .fetch();

        return !queryResult.isEmpty();
    }

    private BooleanExpression ledgerPeriodOverlapsBetween(
            LocalDate startDate,
            LocalDate endDate
    ) {
        return (
                ledger.period.startDate.before(startDate)
                        .and(ledger.period.endDate.between(startDate, endDate)))
                .or(ledger.period.startDate.between(startDate, endDate)
                        .and(ledger.period.endDate.after(endDate)))
                .or(ledger.period.startDate.before(startDate)
                        .and(ledger.period.endDate.after(endDate)))
                .or(ledger.period.startDate.between(startDate, endDate)
                        .and(ledger.period.endDate.between(startDate, endDate)));
    }
}
