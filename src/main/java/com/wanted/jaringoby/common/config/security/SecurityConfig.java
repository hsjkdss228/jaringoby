package com.wanted.jaringoby.common.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.jaringoby.common.filters.AccessTokenBasedAuthenticationFilter;
import com.wanted.jaringoby.common.filters.AuthorizationFilter;
import com.wanted.jaringoby.common.filters.RefreshTokenBasedAuthenticationFilter;
import com.wanted.jaringoby.common.utils.JwtUtil;
import com.wanted.jaringoby.customer.repositories.CustomerRepository;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final CustomerRepository customerRepository;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(accessTokenBasedAuthenticationFilter(),
                        BasicAuthenticationFilter.class)
                .addFilterAfter(refreshTokenBasedAuthenticationFilter(),
                        AccessTokenBasedAuthenticationFilter.class)
                .addFilterAfter(authorizationFilter(),
                        RefreshTokenBasedAuthenticationFilter.class);

        return http.build();
    }

    private Filter accessTokenBasedAuthenticationFilter() {
        return new AccessTokenBasedAuthenticationFilter(jwtUtil, objectMapper);
    }

    private Filter refreshTokenBasedAuthenticationFilter() {
        return new RefreshTokenBasedAuthenticationFilter(jwtUtil, objectMapper);
    }

    private Filter authorizationFilter() {
        return new AuthorizationFilter(customerRepository, objectMapper);
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST,
                        "/v1.0/customer/customers"))
                .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST,
                        "/v1.0/customer/sessions"));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }
}
