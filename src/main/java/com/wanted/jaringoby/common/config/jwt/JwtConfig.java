package com.wanted.jaringoby.common.config.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import com.wanted.jaringoby.common.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String SECRET;

    @Value("${jwt.valid-time.access-token}")
    private Long VALID_TIME_ACCESS_TOKEN;

    @Value("${jwt.valid-time.access-token}")
    private Long VALID_TIME_REFRESH_TOKEN;

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(
                Algorithm.HMAC256(SECRET),
                VALID_TIME_ACCESS_TOKEN, VALID_TIME_REFRESH_TOKEN
        );
    }
}
