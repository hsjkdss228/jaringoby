package com.wanted.jaringoby.domains.ledger.entities.ledger;

import com.wanted.jaringoby.common.constants.Date;
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
        return endDate.isBefore(Date.today());
    }

    public boolean isInProgress() {
        return (startDate.isBefore(Date.today()) || startDate.isEqual(Date.today()))
                && (endDate.isEqual(Date.today()) || endDate.isAfter(Date.today()));
    }

    public boolean hasNotStarted() {
        return startDate.isAfter(Date.today());
    }

    public boolean startDateNotEquals(LocalDate startDate) {
        return !this.startDate.isEqual(startDate);
    }

    public void modify(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
