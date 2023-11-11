package com.wanted.jaringoby.customer.repositories;

import com.wanted.jaringoby.customer.models.customer.Customer;
import com.wanted.jaringoby.customer.models.customer.CustomerId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, CustomerId> {

}
