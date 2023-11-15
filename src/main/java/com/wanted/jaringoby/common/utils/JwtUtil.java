package com.wanted.jaringoby.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.wanted.jaringoby.customer.models.customer.Customer;
import com.wanted.jaringoby.customer.models.customer.CustomerId;
import java.util.Date;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtUtil {

    private static final String CLAIM_TYPE = "type";
    private static final String CLAIM_CUSTOMER_ID = "customerId";

    private final Algorithm algorithm;
    private final Long validTimeAccessToken;
    private final Long validTimeRefreshToken;

    public String issueAccessToken(Customer customer) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + validTimeAccessToken);

        return JWT.create()
                .withClaim(CLAIM_TYPE, "accessToken")
                .withClaim(CLAIM_CUSTOMER_ID, customer.id().value())
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }

    public String issueRefreshToken(CustomerId customerId) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + validTimeRefreshToken);

        return JWT.create()
                .withClaim(CLAIM_TYPE, "refreshToken")
                .withClaim(CLAIM_CUSTOMER_ID, customerId.value())
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }
}
