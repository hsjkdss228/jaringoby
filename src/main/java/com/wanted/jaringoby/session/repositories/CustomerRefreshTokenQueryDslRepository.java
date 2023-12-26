package com.wanted.jaringoby.session.repositories;

import com.wanted.jaringoby.domains.customer.entities.CustomerId;

public interface CustomerRefreshTokenQueryDslRepository {

    void deleteByCustomerIdAndOldestRequestedAt(CustomerId customerId);
}
