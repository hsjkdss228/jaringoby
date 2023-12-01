package com.wanted.jaringoby.session.applications;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.wanted.jaringoby.domains.customer.models.customer.CustomerId;
import com.wanted.jaringoby.session.repositories.CustomerRefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class LogoutServiceTest {

    private LogoutService logoutService;
    private CustomerRefreshTokenRepository customerRefreshTokenRepository;

    @BeforeEach
    void setUp() {
        customerRefreshTokenRepository = mock(CustomerRefreshTokenRepository.class);
        logoutService = new LogoutService(customerRefreshTokenRepository);
    }

    @DisplayName("성공")
    @Nested
    class Success {

        private static final String CUSTOMER_ID = "customerId";
        private static final String REFRESH_TOKEN = "customerId";

        @DisplayName("전달된 고객 식별자와 리프레시 토큰 값에 해당하는 Entity 제거 메서드 호출")
        @Test
        void logout() {
            assertDoesNotThrow(() -> logoutService.logout(CUSTOMER_ID, REFRESH_TOKEN));

            verify(customerRefreshTokenRepository)
                    .deleteByCustomerIdAndValue(CustomerId.of(CUSTOMER_ID), REFRESH_TOKEN);
        }
    }
}
