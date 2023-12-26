package com.wanted.jaringoby.domains.ledger.dtos.http;

import com.wanted.jaringoby.common.validations.groups.MissingValueGroup;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Getter
public class ModifyLedgerPeriodRequestDto {

    @NotNull(groups = MissingValueGroup.class)
    private LocalDate startDate;

    @NotNull(groups = MissingValueGroup.class)
    private LocalDate endDate;
}
