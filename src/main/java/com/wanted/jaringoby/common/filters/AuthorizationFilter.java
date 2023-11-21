package com.wanted.jaringoby.common.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.jaringoby.customer.exceptions.CustomerNotFoundException;
import com.wanted.jaringoby.customer.models.customer.CustomerId;
import com.wanted.jaringoby.customer.repositories.CustomerRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.GenericFilterBean;

@RequiredArgsConstructor
public class AuthorizationFilter extends GenericFilterBean
        implements ErrorResponseWriter {

    private final CustomerRepository customerRepository;
    private final ObjectMapper objectMapper;

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
