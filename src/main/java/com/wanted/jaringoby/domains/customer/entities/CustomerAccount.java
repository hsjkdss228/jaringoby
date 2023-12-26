package com.wanted.jaringoby.domains.customer.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class CustomerAccount {

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String encodedPassword;

    public CustomerAccount(String username) {
        this.username = username;
    }

    public static CustomerAccount ofUsername(String username) {
        return new CustomerAccount(username);
    }

    public void changePassword(String password, PasswordEncoder passwordEncoder) {
        this.encodedPassword = passwordEncoder.encode(password);
    }

    public boolean passwordMatches(String password, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(password, encodedPassword);
    }
}
