package com.wanted.jaringoby.customer.models.customer;

import com.wanted.jaringoby.common.utils.UlidGenerator;
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

    public static CustomerId generate(UlidGenerator ulidGenerator) {
        return new CustomerId(ulidGenerator.createRandomULID());
    }

    public String value() {
        return value;
    }
}
