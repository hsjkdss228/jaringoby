package com.wanted.jaringoby.domains.ledger.config;

import com.wanted.jaringoby.domains.ledger.models.BudgetAmountCalculator;
import com.wanted.jaringoby.domains.ledger.models.BudgetAmountRatioCalculator;
import com.wanted.jaringoby.domains.ledger.models.BudgetAmountTruncator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BudgetRecommendationConfig {

    @Bean
    public BudgetAmountRatioCalculator budgetAmountRatioCalculator() {
        return new BudgetAmountRatioCalculator();
    }

    @Bean
    public BudgetAmountCalculator budgetAmountCalculator() {
        return new BudgetAmountCalculator();
    }

    @Bean
    public BudgetAmountTruncator budgetAmountTruncator() {
        return new BudgetAmountTruncator();
    }
}
