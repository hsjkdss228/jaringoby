package com.wanted.jaringoby.session.entities;

import com.wanted.jaringoby.common.utils.JwtUtil;
import com.wanted.jaringoby.customer.models.customer.Customer;
import com.wanted.jaringoby.customer.models.customer.CustomerId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer_refresh_tokens")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerRefreshToken {

    @EmbeddedId
    private CustomerRefreshTokenId id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "customer_id"))
    private CustomerId customerId;

    @Column(name = "token_value")
    private String value;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Builder
    private CustomerRefreshToken(String id, Customer customer) {
        this.id = CustomerRefreshTokenId.of(id);
        this.customerId = customer.id();
        this.requestedAt = LocalDateTime.now();
    }

    public String value() {
        return value;
    }

    public LocalDateTime requestedAt() {
        return requestedAt;
    }

    public void issue(JwtUtil jwtUtil) {
        value = jwtUtil.issueRefreshToken(customerId);
    }
}
