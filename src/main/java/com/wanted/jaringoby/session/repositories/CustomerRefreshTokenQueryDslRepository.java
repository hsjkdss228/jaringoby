package com.wanted.jaringoby.session.repositories;

import com.wanted.jaringoby.customer.models.customer.CustomerId;

public interface CustomerRefreshTokenQueryDslRepository {

    void deleteByCustomerIdAndOldestRequestedAt(CustomerId customerId);
}
