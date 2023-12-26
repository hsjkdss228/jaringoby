package com.wanted.jaringoby.domains.ledger.repositories;

import static com.wanted.jaringoby.domains.ledger.entities.ledger.QLedger.ledger;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wanted.jaringoby.common.constants.Date;
import com.wanted.jaringoby.domains.customer.entities.CustomerId;
import com.wanted.jaringoby.domains.ledger.entities.ledger.Ledger;
import com.wanted.jaringoby.domains.ledger.entities.ledger.LedgerId;
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
        Ledger fetched = jpaQueryFactory
                .select(ledger)
                .from(ledger)
                .where(ledger.customerId.eq(customerId)
                        .and(ledger.period.startDate.before(Date.today()))
                        .and(ledger.period.endDate.after(Date.today())))
                .orderBy(ledger.period.startDate.desc())
                .limit(1)
                .fetchFirst();

        return fetched == null ? Optional.empty() : Optional.of(fetched);
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
