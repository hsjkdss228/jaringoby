package com.wanted.jaringoby.domains.ledger.models;

import com.wanted.jaringoby.common.models.Money;
import com.wanted.jaringoby.domains.category.models.Category;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class BudgetAmountTruncator {

    public Map<Category, Money> truncate(
            Map<Category, Money> categoriesAndAmounts,
            Long truncationScale,
            Money totalAmount
    ) {
        Map<Category, Money> categoriesAndTruncatedAmounts = categoriesAndAmounts.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> {
                            Money amount = entry.getValue();
                            return amount.truncate(truncationScale);
                        }
                ));

        Money truncatedAmountSum = categoriesAndTruncatedAmounts.values()
                .stream()
                .reduce(Money::add)
                .orElse(Money.zero());

        if (truncatedAmountSum.isBiggerThan(totalAmount)) {
            Map.Entry<Category, Money> biggestCategoryAndMoney = categoriesAndTruncatedAmounts
                    .entrySet()
                    .stream()
                    .max((prev, next) -> prev.getValue()
                            .compareTo(next.getValue()))
                    .get();

            Category category = biggestCategoryAndMoney.getKey();
            Money biggestMoney = biggestCategoryAndMoney.getValue();
            Money adjusted = biggestMoney.subtract(Money.of(truncationScale));

            categoriesAndTruncatedAmounts.put(category, adjusted);
        }

        return categoriesAndTruncatedAmounts;
    }
}
