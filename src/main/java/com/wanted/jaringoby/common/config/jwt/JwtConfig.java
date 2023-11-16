package com.wanted.jaringoby.common.config.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import com.wanted.jaringoby.common.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    private final String secret;
    private final Long validTimeAccessToken;
    private final Long validTimeRefreshToken;

    public JwtConfig(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.valid-time.access-token}") Long validTimeAccessToken,
            @Value("${jwt.valid-time.refresh-token}") Long validTimeRefreshToken
    ) {
        this.secret = secret;
        this.validTimeAccessToken = validTimeAccessToken;
        this.validTimeRefreshToken = validTimeRefreshToken;
    }

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(
                Algorithm.HMAC256(secret),
                validTimeAccessToken, validTimeRefreshToken
        );
    }
}
