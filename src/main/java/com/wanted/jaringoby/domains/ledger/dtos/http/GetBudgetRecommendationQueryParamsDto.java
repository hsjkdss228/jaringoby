package com.wanted.jaringoby.domains.ledger.dtos.http;

import com.wanted.jaringoby.common.validations.groups.ElementCountGroup;
import com.wanted.jaringoby.common.validations.groups.MissingValueGroup;
import com.wanted.jaringoby.common.validations.groups.PatternMatchesGroup;
import com.wanted.jaringoby.common.validations.groups.RangeGroup;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class GetBudgetRecommendationQueryParamsDto {

    @NotNull(groups = MissingValueGroup.class)
    @Min(groups = RangeGroup.class, value = 1000)
    private Long amount;

    @Size(groups = ElementCountGroup.class, min = 2)
    private List<String> categories;

    @Pattern(groups = PatternMatchesGroup.class, regexp = "^10+$")
    private String truncationScale;

    public Long getAmount() {
        return amount;
    }

    public List<String> getCategories() {
        return categories;
    }

    public Long getTruncationScale() {
        return truncationScale == null ? 10L : Long.parseLong(truncationScale);
    }
}
