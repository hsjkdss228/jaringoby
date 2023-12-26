package com.wanted.jaringoby.domains.customer.applications;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.wanted.jaringoby.common.utils.UlidGenerator;
import com.wanted.jaringoby.domains.customer.dtos.http.CreateCustomerRequestDto;
import com.wanted.jaringoby.domains.customer.dtos.http.CreateCustomerResponseDto;
import com.wanted.jaringoby.domains.customer.entities.Customer;
import com.wanted.jaringoby.domains.customer.exceptions.CustomerReconfirmPasswordMismatchedException;
import com.wanted.jaringoby.domains.customer.exceptions.CustomerUsernameDuplicatedException;
import com.wanted.jaringoby.domains.customer.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class CreateCustomerServiceTest {

    private CreateCustomerService createCustomerService;
    private CustomerRepository customerRepository;
    private UlidGenerator ulidGenerator;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        ulidGenerator = mock(UlidGenerator.class);
        passwordEncoder = mock(Argon2PasswordEncoder.class);

        createCustomerService = new CreateCustomerService(
                customerRepository,
                ulidGenerator,
                passwordEncoder
        );
    }

    private static final String USERNAME = "hsjkdss228";
    private static final String PASSWORD = "Password!1";
    private static final String RECONFIRM_PASSWORD = "Password!1";

    @DisplayName("성공")
    @Nested
    class Success {

        private static final String ULID = "CUSTOMER_ULID";

        @DisplayName("Customer 생성 후 영속화, 생성된 Customer 식별자 반환")
        @Test
        void createCustomer() {
            given(customerRepository.existsByAccountUsername(USERNAME))
                    .willReturn(false);
            given(ulidGenerator.createRandomCustomerULID()).willReturn(ULID);

            CreateCustomerRequestDto createCustomerRequestDto = CreateCustomerRequestDto.builder()
                    .username(USERNAME)
                    .password(PASSWORD)
                    .reconfirmPassword(RECONFIRM_PASSWORD)
                    .build();

            CreateCustomerResponseDto createCustomerResponseDto = createCustomerService
                    .createCustomer(createCustomerRequestDto);

            assertThat(createCustomerResponseDto).isNotNull();
            assertThat(createCustomerResponseDto.customerId()).isEqualTo(ULID);

            verify(ulidGenerator).createRandomCustomerULID();
            verify(passwordEncoder).encode(PASSWORD);
            verify(customerRepository).save(any(Customer.class));
        }
    }

    @DisplayName("실패")
    @Nested
    class Failure {

        private static final String USERNAME_DUPLICATED = "hsjkdss228";
        private static final String RECONFIRM_PASSWORD_MISMATCHED = "Abcdefgh&7";

        @DisplayName("계정명이 이미 존재할 경우 예외 발생")
        @Test
        void customerUsernameDuplicated() {
            given(customerRepository.existsByAccountUsername(USERNAME_DUPLICATED))
                    .willReturn(true);

            CreateCustomerRequestDto createCustomerRequestDto = CreateCustomerRequestDto.builder()
                    .username(USERNAME_DUPLICATED)
                    .password(PASSWORD)
                    .reconfirmPassword(RECONFIRM_PASSWORD)
                    .build();

            assertThrows(CustomerUsernameDuplicatedException.class, () -> createCustomerService
                    .createCustomer(createCustomerRequestDto));

            verify(ulidGenerator, never()).createRandomCustomerULID();
            verify(passwordEncoder, never()).encode(any(String.class));
            verify(customerRepository, never()).save(any(Customer.class));
        }

        @DisplayName("비밀번호 재확인이 일치하지 않을 경우 예외 발생")
        @Test
        void customerReconfirmPasswordMismatched() {
            given(customerRepository.existsByAccountUsername(USERNAME_DUPLICATED))
                    .willReturn(false);

            CreateCustomerRequestDto createCustomerRequestDto = CreateCustomerRequestDto.builder()
                    .username(USERNAME_DUPLICATED)
                    .password(PASSWORD)
                    .reconfirmPassword(RECONFIRM_PASSWORD_MISMATCHED)
                    .build();

            assertThrows(CustomerReconfirmPasswordMismatchedException.class,
                    () -> createCustomerService.createCustomer(createCustomerRequestDto));

            verify(ulidGenerator, never()).createRandomCustomerULID();
            verify(passwordEncoder, never()).encode(any(String.class));
            verify(customerRepository, never()).save(any(Customer.class));
        }
    }
}
