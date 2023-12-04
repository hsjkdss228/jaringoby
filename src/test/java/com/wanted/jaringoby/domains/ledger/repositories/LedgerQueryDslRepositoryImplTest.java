package com.wanted.jaringoby.domains.ledger.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.wanted.jaringoby.config.jpa.JpaTestConfig;
import com.wanted.jaringoby.domains.customer.models.customer.CustomerId;
import com.wanted.jaringoby.domains.ledger.models.ledger.Ledger;
import com.wanted.jaringoby.domains.ledger.models.ledger.LedgerId;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
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

    private static final String CUSTOMER_ID = "CUSTOMER_1";
    private static final String OTHER_CUSTOMER_ID = "CUSTOMER_222222";

    private static final LocalDateTime DATETIME_NOW = LocalDateTime.now();
    private static final LocalDate DATE_NOW = LocalDate.now();

    @Autowired
    private LedgerRepository ledgerRepository;

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

    @DisplayName("findByCustomerIdAndOngoing")
    @Nested
    class FindByCustomerIdAndOngoing {

        @DisplayName("현재 진행 중인 Ledger 존재하는 경우")
        @Nested
        class Exists {

            private static final String TARGET_LEDGER_ID = "LEDGER_1";

            @DisplayName("대상 Ledger 반환")
            @Test
            void getLedger() {
                jdbcTemplate.update("""
                                INSERT INTO ledgers(id, customer_id,
                                start_date, end_date, created_at, updated_at)
                                VALUES (?, ?, ?, ?, ?, ?),
                                (?, ?, ?, ?, ?, ?),
                                (?, ?, ?, ?, ?, ?),
                                (?, ?, ?, ?, ?, ?)""",

                        // 현재 진행 중인 Ledger
                        TARGET_LEDGER_ID, CUSTOMER_ID,
                        DATE_NOW.minusDays(15), DATE_NOW.plusDays(14),
                        DATETIME_NOW, DATETIME_NOW,

                        // 다른 고객의 현재 진행 중인 Ledger
                        "LEDGER_2", OTHER_CUSTOMER_ID,
                        DATE_NOW.minusDays(15), DATE_NOW.plusDays(14),
                        DATETIME_NOW, DATETIME_NOW,

                        // 당일 이전에 종료된 Ledger
                        "LEDGER_3", OTHER_CUSTOMER_ID,
                        DATE_NOW.minusMonths(1).minusDays(15), DATE_NOW.minusMonths(1).plusDays(14),
                        DATETIME_NOW, DATETIME_NOW,

                        // 당일 이후 진행 예정인 Ledger
                        "LEDGER_4", OTHER_CUSTOMER_ID,
                        DATE_NOW.plusMonths(1).minusDays(15), DATE_NOW.plusMonths(1).plusDays(14),
                        DATETIME_NOW, DATETIME_NOW);

                Optional<Ledger> found = repository
                        .findByCustomerIdAndOngoing(CustomerId.of(CUSTOMER_ID));
                assertThat(found).isNotEmpty();

                Ledger ledger = found.get();
                assertThat(ledger.id().value()).isEqualTo(TARGET_LEDGER_ID);
            }
        }

        @DisplayName("현재 진행 중인 Ledger 존재하지 않는 경우")
        @Nested
        class NotExists {

            @DisplayName("Optional.empty() 반환")
            @Test
            void getLedger() {
                jdbcTemplate.update("""
                                INSERT INTO ledgers(id, customer_id,
                                start_date, end_date, created_at, updated_at)
                                VALUES (?, ?, ?, ?, ?, ?),
                                (?, ?, ?, ?, ?, ?),
                                (?, ?, ?, ?, ?, ?)""",

                        // 다른 고객의 현재 진행 중인 Ledger
                        "LEDGER_2", OTHER_CUSTOMER_ID,
                        DATE_NOW.minusDays(15), DATE_NOW.plusDays(14),
                        DATETIME_NOW, DATETIME_NOW,

                        // 당일 이전에 종료된 Ledger
                        "LEDGER_3", OTHER_CUSTOMER_ID,
                        DATE_NOW.minusMonths(1).minusDays(15), DATE_NOW.minusMonths(1).plusDays(14),
                        DATETIME_NOW, DATETIME_NOW,

                        // 당일 이후 진행 예정인 Ledger
                        "LEDGER_4", OTHER_CUSTOMER_ID,
                        DATE_NOW.plusMonths(1).minusDays(15), DATE_NOW.plusMonths(1).plusDays(14),
                        DATETIME_NOW, DATETIME_NOW);

                assertThat(repository
                        .findByCustomerIdAndOngoing(CustomerId.of(CUSTOMER_ID)))
                        .isEmpty();
            }
        }
    }

    @DisplayName("existsByCustomerIdAndPeriod")
    @Nested
    class ExistsByCustomerIdAndPeriod {

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

    @DisplayName("existsByCustomerIdAndLedgerIdNotAndPeriod")
    @Nested
    class ExistsByCustomerIdAndLedgerIdNotAndPeriod {

        private static final String TARGET_LEDGER_ID = "LEDGER_1";

        @DisplayName("대상 Ledger를 제외했을 때 기간이 겹치지 않는 경우, false 반환")
        @Test
        void notOverlapped() {
            jdbcTemplate.update("""
                            INSERT INTO ledgers(id, customer_id,
                            start_date, end_date, created_at, updated_at)
                            VALUES (?, ?, ?, ?, ?, ?),
                            (?, ?, ?, ?, ?, ?),
                            (?, ?, ?, ?, ?, ?),
                            (?, ?, ?, ?, ?, ?)""",

                    // 현재 진행 중인 Ledger
                    TARGET_LEDGER_ID, CUSTOMER_ID,
                    DATE_NOW.minusDays(15), DATE_NOW.plusDays(14),
                    DATETIME_NOW, DATETIME_NOW,

                    // 다른 고객의 현재 진행 중인 Ledger
                    "LEDGER_2", OTHER_CUSTOMER_ID,
                    DATE_NOW.minusDays(15), DATE_NOW.plusDays(14),
                    DATETIME_NOW, DATETIME_NOW,

                    // 당일 이전에 종료된 Ledger
                    "LEDGER_3", OTHER_CUSTOMER_ID,
                    DATE_NOW.minusMonths(1).minusDays(15), DATE_NOW.minusMonths(1).plusDays(14),
                    DATETIME_NOW, DATETIME_NOW,

                    // 당일 이후 진행 예정인 Ledger
                    "LEDGER_4", OTHER_CUSTOMER_ID,
                    DATE_NOW.plusMonths(1).minusDays(15), DATE_NOW.plusMonths(1).plusDays(14),
                    DATETIME_NOW, DATETIME_NOW);

            assertThat(repository.existsByCustomerIdAndLedgerIdNotAndPeriod(
                    CustomerId.of(CUSTOMER_ID),
                    LedgerId.of(TARGET_LEDGER_ID),
                    DATE_NOW.plusDays(5),
                    DATE_NOW.plusMonths(1).plusDays(4))
            ).isFalse();
        }
    }
}
