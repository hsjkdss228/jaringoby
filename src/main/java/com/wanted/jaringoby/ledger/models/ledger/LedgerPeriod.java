package com.wanted.jaringoby.ledger.models.ledger;

import com.wanted.jaringoby.ledger.exceptions.LedgerEndDateBeforeStartDateException;
import com.wanted.jaringoby.ledger.exceptions.LedgerStartDateBeforeNowException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import java.time.ZoneId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class LedgerPeriod {

    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    public static LedgerPeriod of(LocalDate startDate, LocalDate endDate) {
        if (startDate.isBefore(LocalDate.now(ZONE_ID))) {
            throw new LedgerStartDateBeforeNowException();
        }

        if (endDate.isBefore(startDate)) {
            throw new LedgerEndDateBeforeStartDateException();
        }

        return new LedgerPeriod(startDate, endDate);
    }
}
