package com.wanted.jaringoby.customer.models.customer;

import com.wanted.jaringoby.common.utils.UlidGenerator;
import com.wanted.jaringoby.customer.dtos.CreateCustomerResponseDto;
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
    public Customer(String username) {
        this.account = CustomerAccount.ofUsername(username);
        this.pushConfiguration = CustomerPushConfiguration.defaultStatus();
    }

    public void generateId(UlidGenerator ulidGenerator) {
        this.id = CustomerId.generate(ulidGenerator);
    }

    public void changePassword(String password, PasswordEncoder passwordEncoder) {
        this.account.changePassword(password, passwordEncoder);
    }

    public CreateCustomerResponseDto toCreationResponseDto() {
        return CreateCustomerResponseDto.builder()
                .customerId(id.value())
                .build();
    }
}
