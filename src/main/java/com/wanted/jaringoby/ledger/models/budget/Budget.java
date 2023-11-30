package com.wanted.jaringoby.ledger.models.budget;

import com.wanted.jaringoby.category.models.Category;
import com.wanted.jaringoby.common.converters.MoneyConverter;
import com.wanted.jaringoby.common.models.Money;
import com.wanted.jaringoby.ledger.dtos.GetBudgetResponseDto;
import com.wanted.jaringoby.ledger.models.ledger.LedgerId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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

    @Builder
    public Budget(
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

    public Category category() {
        return category;
    }

    public boolean categoryEquals(Category category) {
        return this.category.equals(category);
    }

    public void modifyAmount(Money amount) {
        this.amount = amount;
    }

    public GetBudgetResponseDto toGetBudgetResponseDto() {
        return GetBudgetResponseDto.builder()
                .id(id.value())
                .category(category.categoryName())
                .amount(amount.value())
                .build();
    }
}
