package com.wanted.jaringoby.session.repositories;

import com.wanted.jaringoby.domains.customer.models.customer.CustomerId;

public interface CustomerRefreshTokenQueryDslRepository {

    void deleteByCustomerIdAndOldestRequestedAt(CustomerId customerId);
}
