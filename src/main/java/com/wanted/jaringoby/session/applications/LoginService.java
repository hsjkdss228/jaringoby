package com.wanted.jaringoby.session.applications;

import com.wanted.jaringoby.common.utils.JwtUtil;
import com.wanted.jaringoby.common.utils.UlidGenerator;
import com.wanted.jaringoby.domains.customer.exceptions.CustomerNotFoundException;
import com.wanted.jaringoby.session.repositories.CustomerRefreshTokenRepository;
import com.wanted.jaringoby.session.exceptions.CustomerPasswordMismatchedException;
import com.wanted.jaringoby.domains.customer.models.customer.Customer;
import com.wanted.jaringoby.domains.customer.repositories.CustomerRepository;
import com.wanted.jaringoby.session.dtos.LoginRequestDto;
import com.wanted.jaringoby.session.dtos.LoginResponseDto;
import com.wanted.jaringoby.session.entities.CustomerRefreshToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginService {

    private final CustomerRepository customerRepository;
    private final CustomerRefreshTokenRepository customerRefreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UlidGenerator ulidGenerator;
    private final JwtUtil jwtUtil;

    private final Long maxSessionsCount;

    public LoginService(
            CustomerRepository customerRepository,
            CustomerRefreshTokenRepository customerRefreshTokenRepository,
            PasswordEncoder passwordEncoder,
            UlidGenerator ulidGenerator,
            JwtUtil jwtUtil,
            @Value("${session.max-count}") Long maxSessionsCount
    ) {
        this.customerRepository = customerRepository;
        this.customerRefreshTokenRepository = customerRefreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.ulidGenerator = ulidGenerator;
        this.jwtUtil = jwtUtil;
        this.maxSessionsCount = maxSessionsCount;
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        Customer customer = validateUsername(username);

        validatePassword(password, customer);

        // TODO: 이벤트 발행 방식 비동기 처리시키기
        if (exceededMaxSessionCount(customer)) {
            destroyOldestRequestedSession(customer);
        }

        return LoginResponseDto.builder()
                .accessToken(issueAccessToken(customer))
                .refreshToken(issueRefreshToken(customer))
                .build();
    }

    private Customer validateUsername(String username) {
        return customerRepository
                .findByAccountUsername(username)
                .orElseThrow(CustomerNotFoundException::new);
    }

    private void validatePassword(String password, Customer customer) {
        if (!customer.passwordMatches(password, passwordEncoder)) {
            throw new CustomerPasswordMismatchedException();
        }
    }

    private boolean exceededMaxSessionCount(Customer customer) {
        return customerRefreshTokenRepository
                .countByCustomerId(customer.id()) >= maxSessionsCount;
    }

    private void destroyOldestRequestedSession(Customer customer) {
        customerRefreshTokenRepository
                .deleteByCustomerIdAndOldestRequestedAt(customer.id());
    }

    private String issueAccessToken(Customer customer) {
        return jwtUtil.issueAccessToken(customer.id());
    }

    private String issueRefreshToken(Customer customer) {
        CustomerRefreshToken customerRefreshToken = CustomerRefreshToken.builder()
                .id(ulidGenerator.createRandomCustomerRefreshTokenULID())
                .customer(customer)
                .build();
        customerRefreshToken.issue(jwtUtil);

        customerRefreshTokenRepository.save(customerRefreshToken);

        return customerRefreshToken.value();
    }
}
