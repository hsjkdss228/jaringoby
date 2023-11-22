package com.wanted.jaringoby.session.applications;

import com.wanted.jaringoby.common.utils.JwtUtil;
import com.wanted.jaringoby.customer.models.customer.CustomerId;
import com.wanted.jaringoby.session.dtos.ReissueAccessTokenResultDto;
import com.wanted.jaringoby.session.exceptions.CustomerRefreshTokenNotFoundException;
import com.wanted.jaringoby.session.exceptions.CustomerRefreshTokenNullException;
import com.wanted.jaringoby.session.repositories.CustomerRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReissueAccessTokenService {

    private final CustomerRefreshTokenRepository customerRefreshTokenRepository;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public ReissueAccessTokenResultDto reissueAccessToken(
            String customerId,
            String refreshToken
    ) {
        if (refreshToken == null) {
            throw new CustomerRefreshTokenNullException();
        }

        if (!customerRefreshTokenRepository.existsByCustomerIdAndValue(
                CustomerId.of(customerId), refreshToken)) {
            throw new CustomerRefreshTokenNotFoundException();
        }

        return ReissueAccessTokenResultDto.builder()
                .accessToken(issueAccessToken(customerId))
                .build();
    }

    private String issueAccessToken(String customerId) {
        return jwtUtil.issueAccessToken(CustomerId.of(customerId));
    }
}
