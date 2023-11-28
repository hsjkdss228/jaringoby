package com.wanted.jaringoby.ledger.models.ledger;

import com.wanted.jaringoby.customer.models.customer.CustomerId;
import com.wanted.jaringoby.ledger.dtos.GetBudgetResponseDto;
import com.wanted.jaringoby.ledger.dtos.GetLedgerDetailResponseDto;
import com.wanted.jaringoby.ledger.models.budget.Budget;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "ledgers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ledger {

    @EmbeddedId
    private LedgerId id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "customer_id"))
    private CustomerId customerId;

    @Embedded
    private LedgerPeriod period;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder
    public Ledger(
            String id,
            CustomerId customerId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        this.id = LedgerId.of(id);
        this.customerId = customerId;
        this.period = LedgerPeriod.of(startDate, endDate);
    }

    public LedgerId id() {
        return id;
    }

    public GetLedgerDetailResponseDto toLedgerDetailResponseDto(List<Budget> budgets) {
        List<GetBudgetResponseDto> budgetResponseDtos = budgets.stream()
                .map(Budget::toGetBudgetResponseDto)
                .toList();

        return GetLedgerDetailResponseDto.builder()
                .id(id.value())
                .startDate(period.startDate())
                .endDate(period.endDate())
                .budgets(budgetResponseDtos)
                .build();
    }
}
