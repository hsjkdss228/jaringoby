package com.wanted.jaringoby.common.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.jaringoby.domains.customer.entities.CustomerId;
import com.wanted.jaringoby.domains.customer.exceptions.CustomerNotFoundException;
import com.wanted.jaringoby.domains.customer.repositories.CustomerRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.transaction.annotation.Transactional;

public class AuthorizationFilter extends CustomFilter {

    private final CustomerRepository customerRepository;
    private final ObjectMapper objectMapper;

    public AuthorizationFilter(
            CustomerRepository customerRepository,
            ObjectMapper objectMapper) {
        this.customerRepository = customerRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain chain
    ) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        try {
            CustomerId customerId = CustomerId.of(
                    (String) request.getAttribute("customerId"));

            if (!customerRepository.existsById(customerId)) {
                throw new CustomerNotFoundException();
            }

            chain.doFilter(request, response);

        } catch (CustomerNotFoundException exception) {
            writeErrorResponse(response, objectMapper, exception);
        }
    }
}
