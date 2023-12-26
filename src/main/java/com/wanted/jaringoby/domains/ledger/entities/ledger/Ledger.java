package com.wanted.jaringoby.domains.ledger.entities.ledger;

import com.wanted.jaringoby.domains.customer.entities.CustomerId;
import com.wanted.jaringoby.domains.ledger.dtos.http.GetBudgetResponseDto;
import com.wanted.jaringoby.domains.ledger.dtos.http.GetLedgerDetailResponseDto;
import com.wanted.jaringoby.domains.ledger.entities.budget.Budget;
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
            String customerId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        this.id = LedgerId.of(id);
        this.customerId = CustomerId.of(customerId);
        this.period = LedgerPeriod.of(startDate, endDate);
    }

    public LedgerId id() {
        return id;
    }

    public boolean ownedBy(CustomerId customerId) {
        return this.customerId.equals(customerId);
    }

    public boolean hasEnded() {
        return period.hasEnded();
    }

    public boolean isInProgress() {
        return period.isInProgress();
    }

    public boolean hasNotStarted() {
        return period.hasNotStarted();
    }

    public boolean startDateIsDifferentFrom(LocalDate startDate) {
        return period.startDateNotEquals(startDate);
    }

    public void modifyPeriod(LocalDate startDate, LocalDate endDate) {
        period.modify(startDate, endDate);
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
