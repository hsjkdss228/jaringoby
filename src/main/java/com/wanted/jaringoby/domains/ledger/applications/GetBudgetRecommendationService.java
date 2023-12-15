package com.wanted.jaringoby.domains.ledger.applications;

import com.wanted.jaringoby.domains.ledger.dtos.GetBudgetRecommendationQueryParamsDto;
import com.wanted.jaringoby.domains.ledger.dtos.GetBudgetRecommendationResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetBudgetRecommendationService {

    @Transactional(readOnly = true)
    public GetBudgetRecommendationResponseDto getBudgetRecommendation(
            GetBudgetRecommendationQueryParamsDto getBudgetRecommendationQueryParamsDto
    ) {
        return null;
    }
}
