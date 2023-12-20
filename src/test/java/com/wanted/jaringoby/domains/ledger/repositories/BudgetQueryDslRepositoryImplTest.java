package com.wanted.jaringoby.domains.ledger.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.wanted.jaringoby.common.constants.DateTime;
import com.wanted.jaringoby.config.jpa.JpaTestConfig;
import com.wanted.jaringoby.domains.category.models.Category;
import com.wanted.jaringoby.domains.ledger.dtos.query.LedgerIdAndBudgetsQueryResultDto;
import com.wanted.jaringoby.domains.ledger.models.budget.Budget;
import com.wanted.jaringoby.domains.ledger.models.ledger.LedgerId;
import java.util.List;
import java.util.Map;
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
class BudgetQueryDslRepositoryImplTest {

    @Autowired
    private BudgetQueryDslRepositoryImpl repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private LedgerRepository ledgerRepository;

    @DisplayName("findAllLedgerIdAndBudgets")
    @Nested
    class FindAllLedgerIdAndBudgets {

        // 고객 1: 예산 관리 1, 예산 1-3
        private static final String CUSTOMER_ID_1 = "CUSTOMER_1";
        private static final String LEDGER_ID_1 = "LEDGER_1";
        private static final String BUDGET_ID_1 = "BUDGET_1";
        private static final String BUDGET_ID_2 = "BUDGET_2";
        private static final String BUDGET_ID_3 = "BUDGET_3";

        // 고객 2: 예산 관리 2, 예산 4-6
        private static final String CUSTOMER_ID_2 = "CUSTOMER_2";
        private static final String LEDGER_ID_2 = "LEDGER_2";
        private static final String BUDGET_ID_4 = "BUDGET_4";
        private static final String BUDGET_ID_5 = "BUDGET_5";
        private static final String BUDGET_ID_6 = "BUDGET_6";

        // 고객 3: 예산 관리 3, 예산 7-9, 예산 관리 4, 예산 10-11
        private static final String CUSTOMER_ID_3 = "CUSTOMER_3";
        private static final String LEDGER_ID_3 = "LEDGER_3";
        private static final String BUDGET_ID_7 = "BUDGET_7";
        private static final String BUDGET_ID_8 = "BUDGET_8";
        private static final String BUDGET_ID_9 = "BUDGET_9";
        private static final String LEDGER_ID_4 = "LEDGER_4";
        private static final String BUDGET_ID_10 = "BUDGET_10";
        private static final String BUDGET_ID_11 = "BUDGET_11";

        @BeforeEach
        void setUp() {
            jdbcTemplate.execute("DELETE FROM budgets");
            jdbcTemplate.execute("DELETE FROM ledgers");
            jdbcTemplate.execute("DELETE FROM customers");

            jdbcTemplate.update("""
                            INSERT INTO customers(id, username, password,
                            daily_expense_recommendation_push_approved,
                            daily_expense_analysis_push_approved,
                            created_at, updated_at)
                            VALUES (?, ?, ?, ?, ?, ?, ?),
                            (?, ?, ?, ?, ?, ?, ?),
                            (?, ?, ?, ?, ?, ?, ?)
                            """,

                    CUSTOMER_ID_1, "customer1", "Password!1", true, true,
                    DateTime.now().minusMonths(1), DateTime.now().minusMonths(1),

                    CUSTOMER_ID_2, "customer2", "Password@2", true, true,
                    DateTime.now().minusMonths(1), DateTime.now().minusMonths(1),

                    CUSTOMER_ID_3, "customer3", "Password#3", true, true,
                    DateTime.now().minusMonths(1), DateTime.now().minusMonths(1));

            jdbcTemplate.update("""
                            INSERT INTO ledgers(id, customer_id, start_date, end_date,
                            created_at, updated_at)
                            VALUES (?, ?, ?, ?, ?, ?),
                            (?, ?, ?, ?, ?, ?),
                            (?, ?, ?, ?, ?, ?),
                            (?, ?, ?, ?, ?, ?)
                            """,

                    LEDGER_ID_1, CUSTOMER_ID_1, DateTime.now().minusDays(21),
                    DateTime.now().minusDays(14),
                    DateTime.now().minusDays(21), DateTime.now().minusDays(21),

                    LEDGER_ID_2, CUSTOMER_ID_2, DateTime.now().minusDays(3),
                    DateTime.now().plusDays(4),
                    DateTime.now().minusDays(3), DateTime.now().minusDays(3),

                    LEDGER_ID_3, CUSTOMER_ID_3, DateTime.now().minusWeeks(2),
                    DateTime.now().plusWeeks(2),
                    DateTime.now().minusWeeks(2), DateTime.now().minusWeeks(2),

                    LEDGER_ID_4, CUSTOMER_ID_3, DateTime.now().plusDays(7),
                    DateTime.now().plusDays(14),
                    DateTime.now(), DateTime.now());

            jdbcTemplate.update("""
                            INSERT INTO budgets(id, ledger_id, category, amount,
                            created_at, updated_at)
                            VALUES (?, ?, ?, ?, ?, ?),
                            (?, ?, ?, ?, ?, ?),
                            (?, ?, ?, ?, ?, ?),

                            (?, ?, ?, ?, ?, ?),
                            (?, ?, ?, ?, ?, ?),
                            (?, ?, ?, ?, ?, ?),

                            (?, ?, ?, ?, ?, ?),
                            (?, ?, ?, ?, ?, ?),
                            (?, ?, ?, ?, ?, ?),

                            (?, ?, ?, ?, ?, ?),
                            (?, ?, ?, ?, ?, ?)
                            """,

                    BUDGET_ID_1, LEDGER_ID_1, Category.Meal.name(), 10_000L,
                    DateTime.now().minusDays(21), DateTime.now().minusDays(21),
                    BUDGET_ID_2, LEDGER_ID_1, Category.Leisure.name(), 5_000L,
                    DateTime.now().minusDays(21), DateTime.now().minusDays(21),
                    BUDGET_ID_3, LEDGER_ID_1, Category.Living.name(), 3_000L,
                    DateTime.now().minusDays(21), DateTime.now().minusDays(21),

                    BUDGET_ID_4, LEDGER_ID_2, Category.Meal.name(), 120_000L,
                    DateTime.now().minusDays(3), DateTime.now().minusDays(3),
                    BUDGET_ID_5, LEDGER_ID_2, Category.Leisure.name(), 50_000L,
                    DateTime.now().minusDays(3), DateTime.now().minusDays(3),
                    BUDGET_ID_6, LEDGER_ID_2, Category.Living.name(), 5_000L,
                    DateTime.now().minusDays(3), DateTime.now().minusDays(3),

                    BUDGET_ID_7, LEDGER_ID_3, Category.Meal.name(), 100_000L,
                    DateTime.now().minusWeeks(2), DateTime.now().minusWeeks(2),
                    BUDGET_ID_8, LEDGER_ID_3, Category.Leisure.name(), 60_000L,
                    DateTime.now().minusWeeks(2), DateTime.now().minusWeeks(2),
                    BUDGET_ID_9, LEDGER_ID_3, Category.Living.name(), 2_000L,
                    DateTime.now().minusWeeks(2), DateTime.now().minusWeeks(2),

                    BUDGET_ID_10, LEDGER_ID_4, Category.Transportation.name(), 300_000L,
                    DateTime.now(), DateTime.now(),
                    BUDGET_ID_11, LEDGER_ID_4, Category.PersonalDevelopment.name(), 200_000L,
                    DateTime.now(), DateTime.now());
        }

        @DisplayName("모든 Ledger들과 연관된 모든 Budget들을 쿼리해 LedgerId 별로 집계")
        @Test
        void findAllLedgerIdAndBudgets() {
            LedgerIdAndBudgetsQueryResultDto ledgerIdAndBudgetsQueryResultDto = repository
                    .findAllLedgerIdAndBudgets();

            assertThat(ledgerIdAndBudgetsQueryResultDto).isNotNull();

            Map<LedgerId, List<Budget>> ledgerIdAndBudgets = ledgerIdAndBudgetsQueryResultDto
                    .getLedgerIdsAndBudgets();

            assertThat(ledgerIdAndBudgets).containsKeys(
                    LedgerId.of(LEDGER_ID_1),
                    LedgerId.of(LEDGER_ID_2),
                    LedgerId.of(LEDGER_ID_3),
                    LedgerId.of(LEDGER_ID_4)
            );

            assertThat(ledgerIdAndBudgets.get(LedgerId.of(LEDGER_ID_1))).hasSize(3);
            assertThat(ledgerIdAndBudgets.get(LedgerId.of(LEDGER_ID_2))).hasSize(3);
            assertThat(ledgerIdAndBudgets.get(LedgerId.of(LEDGER_ID_3))).hasSize(3);
            assertThat(ledgerIdAndBudgets.get(LedgerId.of(LEDGER_ID_4))).hasSize(2);
        }
    }
}
