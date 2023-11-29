package com.wanted.jaringoby.ledger.repositories;

import static com.wanted.jaringoby.common.constants.Date.NOW;
import static com.wanted.jaringoby.ledger.models.ledger.QLedger.ledger;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wanted.jaringoby.customer.models.customer.CustomerId;
import com.wanted.jaringoby.ledger.models.ledger.Ledger;
import com.wanted.jaringoby.ledger.models.ledger.LedgerId;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LedgerQueryDslRepositoryImpl implements LedgerQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Ledger> findByCustomerIdAndOngoing(CustomerId customerId) {
        Ledger fetch = jpaQueryFactory
                .select(ledger)
                .from(ledger)
                .where(ledger.customerId.eq(customerId)
                        .and(ledger.period.startDate.before(NOW))
                        .and(ledger.period.endDate.after(NOW)))
                .orderBy(ledger.period.startDate.desc())
                .limit(1)
                .fetchFirst();

        System.out.println(fetch);

        return fetch == null ? Optional.empty() : Optional.of(fetch);
    }

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
                        .and(ledgerExistsBetween(startDate, endDate)))
                .fetch();

        return !queryResult.isEmpty();
    }

    @Override
    public boolean existsByCustomerIdAndLedgerIdNotAndPeriod(
            CustomerId customerId,
            LedgerId ledgerId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        List<Ledger> queryResult = jpaQueryFactory
                .select(ledger)
                .from(ledger)
                .where(ledger.customerId.eq(customerId)
                        .and(ledger.id.eq(ledgerId).not())
                        .and(ledgerExistsBetween(startDate, endDate)))
                .fetch();

        return !queryResult.isEmpty();
    }

    private BooleanExpression ledgerExistsBetween(
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
