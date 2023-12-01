package com.wanted.jaringoby.domains.ledger.dtos;

import com.wanted.jaringoby.common.validations.groups.RangeGroup;
import com.wanted.jaringoby.common.validations.groups.MissingValueGroup;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Getter
public class BudgetRequestDto {

    @NotBlank(groups = MissingValueGroup.class)
    private String category;

    @NotNull(groups = MissingValueGroup.class)
    @Min(groups = RangeGroup.class, value = 1)
    private Long amount;
}
