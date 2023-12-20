package com.wanted.jaringoby.domains.ledger.models.budget;

import com.wanted.jaringoby.common.converters.MoneyConverter;
import com.wanted.jaringoby.common.models.Money;
import com.wanted.jaringoby.common.models.Percentage;
import com.wanted.jaringoby.domains.category.models.Category;
import com.wanted.jaringoby.domains.ledger.dtos.GetBudgetResponseDto;
import com.wanted.jaringoby.domains.ledger.models.ledger.LedgerId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "budgets")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Budget {

    @EmbeddedId
    private BudgetId id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "ledger_id"))
    private LedgerId ledgerId;

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private Category category;

    @Convert(converter = MoneyConverter.class)
    @AttributeOverride(name = "value", column = @Column(name = "amount"))
    private Money amount;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Transient
    private Percentage percentage;

    @Builder
    private Budget(
            String id,
            LedgerId ledgerId,
            Category category,
            Money amount
    ) {
        this.id = BudgetId.of(id);
        this.ledgerId = ledgerId;
        this.category = category;
        this.amount = amount;
    }

    @Builder(builderMethodName = "testBuilder", buildMethodName = "testBuild")
    private Budget(
            String id,
            LedgerId ledgerId,
            Category category,
            Money amount,
            Percentage percentage
    ) {
        this.id = BudgetId.of(id);
        this.ledgerId = ledgerId;
        this.category = category;
        this.amount = amount;
        this.percentage = percentage;
    }

    public Category category() {
        return category;
    }

    public Money amount() {
        return amount;
    }

    public Percentage percentage() {
        return percentage;
    }

    public boolean categoryEquals(Category category) {
        return this.category.equals(category);
    }

    public void modifyAmount(Money amount) {
        this.amount = amount;
    }

    public void calculatePercentage(Money budgetAmountSum) {
        percentage = amount.toPercentage(budgetAmountSum);
    }

    public void addPercentageByCategory(
            Map<Category, List<Percentage>> categoriesAndPercentages,
            List<Category> targetCategories
    ) {
        if (!targetCategories.contains(category)) {
            return;
        }
        categoriesAndPercentages.putIfAbsent(category, new ArrayList<>());
        categoriesAndPercentages.get(category).add(percentage);
    }

    public GetBudgetResponseDto toGetBudgetResponseDto() {
        return GetBudgetResponseDto.builder()
                .id(id.value())
                .category(category.categoryName())
                .amount(amount.value())
                .build();
    }
}
