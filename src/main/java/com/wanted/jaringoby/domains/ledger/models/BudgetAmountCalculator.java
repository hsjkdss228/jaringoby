package com.wanted.jaringoby.domains.ledger.models;

import com.wanted.jaringoby.common.models.Money;
import com.wanted.jaringoby.common.models.Percentage;
import com.wanted.jaringoby.domains.category.models.Category;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class BudgetAmountCalculator {

    public Map<Category, Money> calculateAmount(
            Money totalAmount,
            Map<Category, Percentage> categoriesAndPercentageAverages
    ) {
        return categoriesAndPercentageAverages.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> {
                            Percentage percentage = entry.getValue();
                            return totalAmount.percentageOf(percentage);
                        }
                ));
    }
}
