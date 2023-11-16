package com.wanted.jaringoby.customer.applications;

import com.wanted.jaringoby.common.utils.UlidGenerator;
import com.wanted.jaringoby.customer.dtos.CreateCustomerRequestDto;
import com.wanted.jaringoby.customer.dtos.CreateCustomerResponseDto;
import com.wanted.jaringoby.customer.exceptions.CustomerReconfirmPasswordMismatchedException;
import com.wanted.jaringoby.customer.exceptions.CustomerUsernameDuplicatedException;
import com.wanted.jaringoby.customer.models.customer.Customer;
import com.wanted.jaringoby.customer.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateCustomerService {

    private final CustomerRepository customerRepository;
    private final UlidGenerator ulidGenerator;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CreateCustomerResponseDto createCustomer(
            CreateCustomerRequestDto createCustomerRequestDto
    ) {
        String username = createCustomerRequestDto.getUsername();
        String password = createCustomerRequestDto.getPassword();
        String reconfirmPassword = createCustomerRequestDto.getReconfirmPassword();

        validateUsernameDuplication(username);

        validateReconfirmPassword(password, reconfirmPassword);

        Customer customer = Customer.builder()
                .id(ulidGenerator.createRandomCustomerULID())
                .username(username)
                .build();

        customer.changePassword(password, passwordEncoder);

        customerRepository.save(customer);

        return customer.toCreationResponseDto();
    }

    private void validateUsernameDuplication(String username) {
        if (customerRepository.existsByAccountUsername(username)) {
            throw new CustomerUsernameDuplicatedException();
        }
    }

    private void validateReconfirmPassword(String password, String reconfirmPassword) {
        if (!password.equals(reconfirmPassword)) {
            throw new CustomerReconfirmPasswordMismatchedException();
        }
    }
}
