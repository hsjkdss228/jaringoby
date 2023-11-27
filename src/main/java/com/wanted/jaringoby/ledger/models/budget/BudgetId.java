package com.wanted.jaringoby.ledger.models.budget;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class BudgetId implements Serializable {

    @Column(name = "id")
    private String value;

    public static BudgetId of(String value) {
        return new BudgetId(value);
    }
}
