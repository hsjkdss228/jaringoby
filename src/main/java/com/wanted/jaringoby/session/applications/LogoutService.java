package com.wanted.jaringoby.session.applications;

import com.wanted.jaringoby.domains.customer.entities.CustomerId;
import com.wanted.jaringoby.session.repositories.CustomerRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final CustomerRefreshTokenRepository customerRefreshTokenRepository;

    @Transactional
    public void logout(String customerId, String refreshToken) {
        customerRefreshTokenRepository.deleteByCustomerIdAndValue(
                CustomerId.of(customerId), refreshToken);
    }
}
