package com.wanted.jaringoby.domains.ledger.dtos;

import com.wanted.jaringoby.common.validations.groups.MissingValueGroup;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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
public class ModifyLedgerBudgetsRequestDto {

    @Valid
    @NotEmpty(groups = MissingValueGroup.class)
    private List<BudgetRequestDto> budgets;
}
