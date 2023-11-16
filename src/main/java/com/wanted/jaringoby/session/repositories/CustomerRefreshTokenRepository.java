package com.wanted.jaringoby.session.repositories;

import com.wanted.jaringoby.customer.models.customer.CustomerId;
import com.wanted.jaringoby.session.entities.CustomerRefreshToken;
import com.wanted.jaringoby.session.entities.CustomerRefreshTokenId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRefreshTokenRepository extends
        JpaRepository<CustomerRefreshToken, CustomerRefreshTokenId>,
        CustomerRefreshTokenQueryDslRepository {

    Long countByCustomerId(CustomerId customerId);

    Optional<CustomerRefreshToken> findByCustomerId(CustomerId customerId);
}