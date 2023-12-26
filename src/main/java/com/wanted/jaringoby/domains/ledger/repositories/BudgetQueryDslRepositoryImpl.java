package com.wanted.jaringoby.domains.ledger.repositories;

import static com.wanted.jaringoby.domains.ledger.entities.budget.QBudget.budget;
import static com.wanted.jaringoby.domains.ledger.entities.ledger.QLedger.ledger;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wanted.jaringoby.domains.ledger.dtos.query.LedgerIdAndBudgetsQueryResultDto;
import com.wanted.jaringoby.domains.ledger.entities.budget.Budget;
import com.wanted.jaringoby.domains.ledger.entities.ledger.LedgerId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BudgetQueryDslRepositoryImpl implements BudgetQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public LedgerIdAndBudgetsQueryResultDto findAllLedgerIdAndBudgets() {

        // TODO: Soft Delete 비즈니스 로직 추가될 경우
        //       deleted = false 인 것만 쿼리
        List<Tuple> tuples = jpaQueryFactory
                .select(ledger.id, budget)
                .from(ledger)
                .innerJoin(budget).on(budget.ledgerId.eq(ledger.id))
                .fetch();

        Map<LedgerId, List<Budget>> ledgerIdsAndBudgets = new HashMap<>();

        tuples.forEach(tuple -> {
            LedgerId ledgerId = tuple.get(ledger.id);
            Budget budgetQueried = tuple.get(budget);
            ledgerIdsAndBudgets.putIfAbsent(ledgerId, new ArrayList<>());
            ledgerIdsAndBudgets.get(ledgerId)
                    .add(budgetQueried);
        });

        return LedgerIdAndBudgetsQueryResultDto.builder()
                .ledgerIdsAndBudgets(ledgerIdsAndBudgets)
                .build();
    }
}
