package com.wanted.jaringoby.ledger.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.wanted.jaringoby.config.jpa.JpaTestConfig;
import com.wanted.jaringoby.customer.models.customer.CustomerId;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@Import(JpaTestConfig.class)
@ActiveProfiles("test")
class LedgerQueryDslRepositoryImplTest {

    @Autowired
    private LedgerQueryDslRepositoryImpl repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DisplayName("existsByCustomerIdAndPeriod")
    @Nested
    class ExistsByCustomerIdAndPeriod {

        private static final String CUSTOMER_ID = "CUSTOMER_1";

        private static final LocalDateTime DATETIME_NOW = LocalDateTime.now();
        private static final LocalDate DATE_NOW = LocalDate.now();

        @BeforeEach
        void setUp() {
            jdbcTemplate.execute("DELETE FROM ledgers");
            jdbcTemplate.execute("DELETE FROM customers");

            jdbcTemplate.update("""
                            INSERT INTO customers(id, username, password,
                            daily_expense_recommendation_push_approved,
                            daily_expense_analysis_push_approved,
                            created_at, updated_at)
                            VALUES (?, ?, ?, ?, ?, ?, ?)
                            """,
                    CUSTOMER_ID, "hsjkdss228", "Password!1", true, true,
                    DATETIME_NOW.minusHours(1), DATETIME_NOW.minusHours(1));
        }

        @DisplayName("기간이 겹치지 않는 경우")
        @Nested
        class NotOverlapped {

            @DisplayName("false 반환")
            @Test
            void notOverlapped() {
                jdbcTemplate.update("""
                            INSERT INTO ledgers(id, customer_id,
                            start_date, end_date, created_at, updated_at)
                            VALUES (?, ?, ?, ?, ?, ?),
                            (?, ?, ?, ?, ?, ?)""",

                        "LEDGER_1", CUSTOMER_ID,
                        DATE_NOW.minusMonths(1).minusDays(15), DATE_NOW.minusDays(15),
                        DATETIME_NOW, DATETIME_NOW,

                        "LEDGER_2", CUSTOMER_ID,
                        DATE_NOW.plusMonths(1).plusDays(5), DATE_NOW.plusMonths(1).plusDays(15),
                        DATETIME_NOW, DATETIME_NOW);

                assertThat(repository.existsByCustomerIdAndPeriod(
                        CustomerId.of(CUSTOMER_ID), DATE_NOW, DATE_NOW.plusMonths(1)))
                        .isFalse();
            }
        }

        @DisplayName("기간이 겹치는 경우")
        @Nested
        class Overlapped {

            @DisplayName("시작일이 시작일 이전, 종료일이 시작일-종료일 사이로 겹치는 경우 true 반환")
            @Test
            void startDateBeforeAndEndDateBetween() {
                jdbcTemplate.update("""
                            INSERT INTO ledgers(id, customer_id,
                            start_date, end_date, created_at, updated_at)
                            VALUES (?, ?, ?, ?, ?, ?)""",

                        "LEDGER_1", CUSTOMER_ID,
                        DATE_NOW.minusDays(15), DATE_NOW.plusDays(15),
                        DATETIME_NOW, DATETIME_NOW);

                assertThat(repository.existsByCustomerIdAndPeriod(
                        CustomerId.of(CUSTOMER_ID), DATE_NOW, DATE_NOW.plusMonths(1)))
                        .isTrue();
            }

            @DisplayName("시작일이 시작일-종료일 사이, 종료일이 종료일 이후로 겹치는 경우 true 반환")
            @Test
            void startDateBetweenAndEndDateAfter() {
                jdbcTemplate.update("""
                            INSERT INTO ledgers(id, customer_id,
                            start_date, end_date, created_at, updated_at)
                            VALUES (?, ?, ?, ?, ?, ?)""",

                        "LEDGER_1", CUSTOMER_ID,
                        DATE_NOW.plusDays(15), DATE_NOW.plusMonths(1).plusDays(15),
                        DATETIME_NOW, DATETIME_NOW);

                assertThat(repository.existsByCustomerIdAndPeriod(
                        CustomerId.of(CUSTOMER_ID), DATE_NOW, DATE_NOW.plusMonths(1)))
                        .isTrue();
            }

            @DisplayName("시작일이 시작일 이전, 종료일이 종료일 이후로 겹치는 경우 true 반환")
            @Test
            void startDateBeforeAndEndDateAfter() {
                jdbcTemplate.update("""
                            INSERT INTO ledgers(id, customer_id,
                            start_date, end_date, created_at, updated_at)
                            VALUES (?, ?, ?, ?, ?, ?)""",

                        "LEDGER_1", CUSTOMER_ID,
                        DATE_NOW.minusDays(15), DATE_NOW.plusMonths(1).plusDays(15),
                        DATETIME_NOW, DATETIME_NOW);

                assertThat(repository.existsByCustomerIdAndPeriod(
                        CustomerId.of(CUSTOMER_ID), DATE_NOW, DATE_NOW.plusMonths(1)))
                        .isTrue();
            }

            @DisplayName("시작일이 시작일-종료일 사이, 종료일이 시작일-종료일 사이로 겹치는 경우 true 반환")
            @Test
            void startDateBetweenAndEndDateBetween() {
                jdbcTemplate.update("""
                            INSERT INTO ledgers(id, customer_id,
                            start_date, end_date, created_at, updated_at)
                            VALUES (?, ?, ?, ?, ?, ?)""",

                        "LEDGER_1", CUSTOMER_ID,
                        DATE_NOW.plusDays(5), DATE_NOW.plusDays(25),
                        DATETIME_NOW, DATETIME_NOW);

                assertThat(repository.existsByCustomerIdAndPeriod(
                        CustomerId.of(CUSTOMER_ID), DATE_NOW, DATE_NOW.plusMonths(1)))
                        .isTrue();
            }
        }
    }
}
