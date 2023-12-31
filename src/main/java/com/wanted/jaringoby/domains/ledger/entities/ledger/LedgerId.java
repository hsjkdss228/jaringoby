package com.wanted.jaringoby.domains.ledger.entities.ledger;

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
public class LedgerId implements Serializable {

    @Column(name = "id")
    private String value;

    public static LedgerId of(String value) {
        return new LedgerId(value);
    }

    public String value() {
        return value;
    }
}
