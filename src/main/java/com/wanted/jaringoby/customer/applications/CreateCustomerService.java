package com.wanted.jaringoby.customer.applications;

import com.wanted.jaringoby.customer.dtos.CreateCustomerRequestDto;
import com.wanted.jaringoby.customer.dtos.CreateCustomerResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateCustomerService {

    @Transactional
    public CreateCustomerResponseDto createCustomer(
            CreateCustomerRequestDto createCustomerRequestDto
    ) {
        return null;
    }
}
