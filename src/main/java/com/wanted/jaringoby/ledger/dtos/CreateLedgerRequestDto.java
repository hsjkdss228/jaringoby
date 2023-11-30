package com.wanted.jaringoby.ledger.dtos;

import com.wanted.jaringoby.common.validations.groups.MissingValueGroup;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Getter
public class CreateLedgerRequestDto {

    @NotNull(groups = MissingValueGroup.class)
    private LocalDate startDate;

    @NotNull(groups = MissingValueGroup.class)
    private LocalDate endDate;

    @Valid
    @NotEmpty(groups = MissingValueGroup.class)
    private List<BudgetRequestDto> budgets;
}
