package com.wanted.jaringoby.session.repositories;

import static com.wanted.jaringoby.session.entities.QCustomerRefreshToken.customerRefreshToken;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wanted.jaringoby.domains.customer.models.customer.CustomerId;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomerRefreshTokenQueryDslRepositoryImpl
        implements CustomerRefreshTokenQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public void deleteByCustomerIdAndOldestRequestedAt(CustomerId customerId) {
        LocalDateTime oldestRequestedAt = jpaQueryFactory
                .select(customerRefreshToken.requestedAt.min())
                .from(customerRefreshToken)
                .where(customerRefreshToken.customerId.eq(customerId))
                .fetchOne();

        jpaQueryFactory.delete(customerRefreshToken)
                .where(customerRefreshToken.customerId.eq(customerId)
                        .and(customerRefreshToken.requestedAt.eq(oldestRequestedAt)))
                .execute();
    }
}
