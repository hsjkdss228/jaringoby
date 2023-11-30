package com.wanted.jaringoby.ledger.models.ledger;

import static com.wanted.jaringoby.common.constants.Date.TODAY;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class LedgerPeriod {

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    public static LedgerPeriod of(LocalDate startDate, LocalDate endDate) {
        return new LedgerPeriod(startDate, endDate);
    }

    public LocalDate startDate() {
        return startDate;
    }

    public LocalDate endDate() {
        return endDate;
    }

    public boolean hasEnded() {
        return endDate.isBefore(TODAY);
    }

    public boolean isInProgress() {
        return (startDate.isBefore(TODAY) || startDate.isEqual(TODAY))
                && (endDate.isEqual(TODAY) || endDate.isAfter(TODAY));
    }

    public boolean hasNotStarted() {
        return startDate.isAfter(TODAY);
    }

    public boolean startDateNotEquals(LocalDate startDate) {
        return !this.startDate.isEqual(startDate);
    }

    public void modify(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
