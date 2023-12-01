package com.wanted.jaringoby.domains.customer.models.customer;

import com.wanted.jaringoby.domains.customer.dtos.CreateCustomerResponseDto;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "customers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer {

    @EmbeddedId
    private CustomerId id;

    @Embedded
    private CustomerAccount account;

    @Embedded
    private CustomerPushConfiguration pushConfiguration;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder
    public Customer(String id, String username) {
        this.id = CustomerId.of(id);
        this.account = CustomerAccount.ofUsername(username);
        this.pushConfiguration = CustomerPushConfiguration.defaultStatus();
    }

    public CustomerId id() {
        return id;
    }

    public void changePassword(String password, PasswordEncoder passwordEncoder) {
        this.account.changePassword(password, passwordEncoder);
    }

    public boolean passwordMatches(String password, PasswordEncoder passwordEncoder) {
        return account.passwordMatches(password, passwordEncoder);
    }

    public CreateCustomerResponseDto toCreationResponseDto() {
        return CreateCustomerResponseDto.builder()
                .customerId(id.value())
                .build();
    }
}
