package com.wanted.jaringoby.customer.models.customer;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class CustomerPushConfiguration {

    @Column(name = "daily_expense_recommendation_push_approved")
    private Boolean dailyExpenseRecommendation;

    @Column(name = "daily_expense_analysis_push_approved")
    private Boolean dailyExpenseAnalysis;
}
