package com.wanted.jaringoby.domains.customer.repositories;

import com.wanted.jaringoby.domains.customer.entities.Customer;
import com.wanted.jaringoby.domains.customer.entities.CustomerId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, CustomerId> {

    boolean existsByAccountUsername(String username);

    Optional<Customer> findByAccountUsername(String username);
}
