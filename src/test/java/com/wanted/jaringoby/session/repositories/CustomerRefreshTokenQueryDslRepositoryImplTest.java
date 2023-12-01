package com.wanted.jaringoby.session.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.wanted.jaringoby.config.jpa.JpaTestConfig;
import com.wanted.jaringoby.domains.customer.models.customer.Customer;
import com.wanted.jaringoby.session.entities.CustomerRefreshToken;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@Import(JpaTestConfig.class)
@ActiveProfiles("test")
class CustomerRefreshTokenQueryDslRepositoryImplTest {

    @Autowired
    private CustomerRefreshTokenQueryDslRepositoryImpl repositoryImpl;

    @Autowired
    private CustomerRefreshTokenRepository customerRefreshTokenRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // TODO: Testcontainer를 활용해 실제 MySQL 환경의 데이터베이스에서 테스트 수행
    //       작성한 쿼리문이 특정 데이터베이스에서 제한되는 형식인지도 확인할 수 있어야 함.

    private static final LocalDateTime NOW = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM customer_refresh_tokens");
        jdbcTemplate.execute("DELETE FROM customers");

        jdbcTemplate.update("""
                        INSERT INTO customers(id, username, password,
                        daily_expense_recommendation_push_approved,
                        daily_expense_analysis_push_approved,
                        created_at, updated_at)
                        VALUES (?, ?, ?, ?, ?, ?, ?)
                        """,

                "CUSTOMER_1", "hsjkdss228", "Password!1", true, true,
                NOW.minusHours(1), NOW.minusHours(1));

        jdbcTemplate.update("""
                        INSERT INTO customer_refresh_tokens(id, customer_id, token_value, requested_at)
                        VALUES (?, ?, ?, ?),
                        (?, ?, ?, ?)
                        """,

                "CUSTOMER_REFRESH_TOKEN_1", "CUSTOMER_1", "REFRESH_TOKEN_1", NOW.minusMinutes(30),
                "CUSTOMER_REFRESH_TOKEN_2", "CUSTOMER_1", "REFRESH_TOKEN_2", NOW.minusMinutes(15));
    }

    @DisplayName("requestedAt이 가장 이전 시간대인 CustomerRefreshToken을 데이터베이스에서 제거")
    @Test
    void deleteByCustomerIdAndOldestRequestedAt() {
        Customer customer = Customer.builder()
                .id("CUSTOMER_1")
                .build();

        repositoryImpl.deleteByCustomerIdAndOldestRequestedAt(customer.id());

        assertDoesNotThrow(() -> {
            CustomerRefreshToken customerRefreshToken = customerRefreshTokenRepository
                    .findByCustomerId(customer.id())
                    .orElseThrow(RuntimeException::new);

            assertThat(customerRefreshToken.requestedAt()).isEqualTo(NOW.minusMinutes(15));
        });
    }
}
