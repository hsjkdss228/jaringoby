package com.wanted.jaringoby.session.applications;

import com.wanted.jaringoby.common.utils.JwtUtil;
import com.wanted.jaringoby.domains.customer.entities.CustomerId;
import com.wanted.jaringoby.session.dtos.http.ReissueAccessTokenResponseDto;
import com.wanted.jaringoby.session.exceptions.CustomerRefreshTokenNotFoundException;
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
    public ReissueAccessTokenResponseDto reissueAccessToken(
            String customerId,
            String refreshToken
    ) {
        if (!customerRefreshTokenRepository.existsByCustomerIdAndValue(
                CustomerId.of(customerId), refreshToken)) {
            throw new CustomerRefreshTokenNotFoundException();
        }

        return ReissueAccessTokenResponseDto.builder()
                .accessToken(issueAccessToken(customerId))
                .build();
    }

    private String issueAccessToken(String customerId) {
        return jwtUtil.issueAccessToken(CustomerId.of(customerId));
    }
}
