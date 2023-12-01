package com.wanted.jaringoby.session.applications;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.wanted.jaringoby.common.utils.JwtUtil;
import com.wanted.jaringoby.common.utils.UlidGenerator;
import com.wanted.jaringoby.domains.customer.exceptions.CustomerNotFoundException;
import com.wanted.jaringoby.session.exceptions.CustomerPasswordMismatchedException;
import com.wanted.jaringoby.domains.customer.models.customer.Customer;
import com.wanted.jaringoby.domains.customer.repositories.CustomerRepository;
import com.wanted.jaringoby.session.dtos.LoginRequestDto;
import com.wanted.jaringoby.session.dtos.LoginResponseDto;
import com.wanted.jaringoby.session.entities.CustomerRefreshToken;
import com.wanted.jaringoby.session.repositories.CustomerRefreshTokenRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class LoginServiceTest {

    private LoginService loginService;
    private CustomerRepository customerRepository;
    private CustomerRefreshTokenRepository customerRefreshTokenRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private UlidGenerator ulidGenerator;

    private static final Long MAX_SESSIONS_COUNT = 2L;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        customerRefreshTokenRepository = mock(CustomerRefreshTokenRepository.class);
        passwordEncoder = mock(Argon2PasswordEncoder.class);
        ulidGenerator = mock(UlidGenerator.class);
        jwtUtil = mock(JwtUtil.class);

        loginService = new LoginService(
                customerRepository,
                customerRefreshTokenRepository,
                passwordEncoder,
                ulidGenerator,
                jwtUtil,
                MAX_SESSIONS_COUNT
        );
    }

    private static final String CUSTOMER_ID = "CUSTOMER_1";
    private static final String USERNAME = "hsjkdss228";
    private static final String PASSWORD = "Password!1";

    private final LoginRequestDto loginRequestDto = LoginRequestDto.builder()
            .username(USERNAME)
            .password(PASSWORD)
            .build();

    private final Customer customer = Customer.builder()
            .id(CUSTOMER_ID)
            .username(USERNAME)
            .build();

    private static final String CUSTOMER_REFRESH_TOKEN_ID = "CUSTOMER_REFRESH_TOKEN_ID_1";

    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";

    @DisplayName("성공")
    @Nested
    class Success {

        @BeforeEach
        void setUp() {
            given(customerRepository.findByAccountUsername(USERNAME))
                    .willReturn(Optional.of(customer));

            given(customer.passwordMatches(PASSWORD, passwordEncoder)).willReturn(true);

            given(customerRefreshTokenRepository.countByCustomerId(customer.id()))
                    .willReturn(0L);

            given(ulidGenerator.createRandomCustomerRefreshTokenULID())
                    .willReturn(CUSTOMER_REFRESH_TOKEN_ID);

            given(jwtUtil.issueAccessToken(customer.id()))
                    .willReturn(ACCESS_TOKEN);
            given(jwtUtil.issueRefreshToken(customer.id()))
                    .willReturn(REFRESH_TOKEN);
        }

        @DisplayName("액세스 토큰, 리프레시 토큰 발행 후 반환")
        @Test
        void login() {
            LoginResponseDto loginResponseDto = loginService.login(loginRequestDto);

            assertThat(loginResponseDto).isNotNull();
            assertThat(loginResponseDto.accessToken()).isEqualTo(ACCESS_TOKEN);
            assertThat(loginResponseDto.refreshToken()).isEqualTo(REFRESH_TOKEN);

            verify(jwtUtil).issueAccessToken(customer.id());
            verify(jwtUtil).issueRefreshToken(customer.id());
            verify(customerRefreshTokenRepository).save(any(CustomerRefreshToken.class));
        }

        // TODO: 이벤트 발행 방식 비동기 처리로 리팩토링하고, 해당 테스트도 분리
        @DisplayName("허용 세션 수 이내인 경우")
        @Nested
        class WithinMaxSessions {

            @DisplayName("요청 시점이 가장 오래된 세션을 파기하지 않음")
            @Test
            void doNotDestroyOldestRequestedSession() {
                given(customerRefreshTokenRepository.countByCustomerId(customer.id()))
                        .willReturn(MAX_SESSIONS_COUNT - 1);

                loginService.login(loginRequestDto);

                verify(customerRefreshTokenRepository, never())
                        .deleteByCustomerIdAndOldestRequestedAt(customer.id());
            }
        }

        @DisplayName("허용 세션 수에 도달한 상태인 경우")
        @Nested
        class ReachedMaxSessions {

            @DisplayName("요청 시점이 가장 오래된 세션을 파기")
            @Test
            void destroyOldestRequestedSession() {
                given(customerRefreshTokenRepository.countByCustomerId(customer.id()))
                        .willReturn(MAX_SESSIONS_COUNT);

                loginService.login(loginRequestDto);

                verify(customerRefreshTokenRepository)
                        .deleteByCustomerIdAndOldestRequestedAt(customer.id());
            }
        }
    }

    @DisplayName("실패")
    @Nested
    class Failure {

        @DisplayName("고객 계정이 존재하지 않는 경우 예외 발생")
        @Test
        void customerNotFound() {
            given(customerRepository.findByAccountUsername(USERNAME))
                    .willThrow(CustomerNotFoundException.class);

            assertThrows(CustomerNotFoundException.class,
                    () -> loginService.login(loginRequestDto));
        }

        @DisplayName("비밀번호가 일치하지 않는 경우 예외 발생")
        @Test
        void customerPasswordMismatches() {
            given(customerRepository.findByAccountUsername(USERNAME))
                    .willReturn(Optional.of(customer));

            given(customer.passwordMatches(PASSWORD, passwordEncoder)).willReturn(false);

            assertThrows(CustomerPasswordMismatchedException.class,
                    () -> loginService.login(loginRequestDto));
        }
    }
}
