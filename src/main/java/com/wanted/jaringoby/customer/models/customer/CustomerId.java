package com.wanted.jaringoby.customer.models.customer;

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
public class CustomerId implements Serializable {

    @Column(name = "id")
    private String value;

    public static CustomerId of(String value) {
        return new CustomerId(value);
    }

    public String value() {
        return value;
    }
}
