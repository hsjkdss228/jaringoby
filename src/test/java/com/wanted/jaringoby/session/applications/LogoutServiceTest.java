package com.wanted.jaringoby.session.applications;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.wanted.jaringoby.customer.models.customer.CustomerId;
import com.wanted.jaringoby.session.exceptions.CustomerRefreshTokenNullException;
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

    private static final String CUSTOMER_ID = "customerId";

    @DisplayName("성공")
    @Nested
    class Success {

        private static final String REFRESH_TOKEN = "customerId";

        @DisplayName("전달된 고객 식별자와 리프레시 토큰 값에 해당하는 Entity 제거 메서드 호출")
        @Test
        void logout() {
            assertDoesNotThrow(() -> logoutService.logout(CUSTOMER_ID, REFRESH_TOKEN));

            verify(customerRefreshTokenRepository)
                    .deleteByCustomerIdAndValue(CustomerId.of(CUSTOMER_ID), REFRESH_TOKEN);
        }
    }

    @DisplayName("실패")
    @Nested
    class Failure {

        @DisplayName("리프레시 토큰이 null로 전달된 경우 예외 발생")
        @Test
        void logout() {
            assertThrows(CustomerRefreshTokenNullException.class, () ->
                    logoutService.logout(CUSTOMER_ID, null));

            verify(customerRefreshTokenRepository, never())
                    .deleteByCustomerIdAndValue(any(CustomerId.class), any(String.class));
        }
    }
}
