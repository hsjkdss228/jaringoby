package com.wanted.jaringoby.session.applications;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.wanted.jaringoby.common.utils.JwtUtil;
import com.wanted.jaringoby.customer.models.customer.CustomerId;
import com.wanted.jaringoby.session.dtos.ReissueAccessTokenResultDto;
import com.wanted.jaringoby.session.exceptions.CustomerRefreshTokenNotFoundException;
import com.wanted.jaringoby.session.repositories.CustomerRefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ReissueAccessTokenServiceTest {

    private ReissueAccessTokenService reissueAccessTokenService;
    private CustomerRefreshTokenRepository customerRefreshTokenRepository;
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        customerRefreshTokenRepository = mock(CustomerRefreshTokenRepository.class);
        jwtUtil = mock(JwtUtil.class);
        reissueAccessTokenService = new ReissueAccessTokenService(
                customerRefreshTokenRepository,
                jwtUtil
        );
    }

    private static final String CUSTOMER_ID = "CUSTOMER_ID";
    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";

    @DisplayName("성공")
    @Nested
    class Success {

        private static final String ACCESS_TOKEN = "ACCESS_TOKEN";

        @DisplayName("리프레시 토큰이 존재하는 경우, 액세스 토큰을 재발행해 반환")
        @Test
        void reissueAccessToken() {
            given(customerRefreshTokenRepository
                    .existsByCustomerIdAndValue(CustomerId.of(CUSTOMER_ID), REFRESH_TOKEN))
                    .willReturn(true);

            given(jwtUtil.issueAccessToken(CustomerId.of(CUSTOMER_ID)))
                    .willReturn(ACCESS_TOKEN);

            ReissueAccessTokenResultDto reissueAccessTokenResultDto
                    = reissueAccessTokenService.reissueAccessToken(CUSTOMER_ID, REFRESH_TOKEN);

            assertThat(reissueAccessTokenResultDto).isNotNull();
        }
    }

    @DisplayName("실패")
    @Nested
    class Failure {

        @DisplayName("리프레시 토큰이 존재하지 않는 경우 예외처리")
        @Test
        void customerRefreshTokenNotFound() {
            given(customerRefreshTokenRepository
                    .existsByCustomerIdAndValue(CustomerId.of(CUSTOMER_ID), REFRESH_TOKEN))
                    .willReturn(false);

            assertThrows(CustomerRefreshTokenNotFoundException.class, () ->
                    reissueAccessTokenService.reissueAccessToken(CUSTOMER_ID, REFRESH_TOKEN));
        }
    }
}
