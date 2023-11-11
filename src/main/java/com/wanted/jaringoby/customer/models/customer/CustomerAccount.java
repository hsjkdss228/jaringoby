package com.wanted.jaringoby.customer.models.customer;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class CustomerAccount {

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String encodedPassword;
}
