package com.wanted.jaringoby.common.filters;

import com.wanted.jaringoby.common.exceptions.http.request.InvalidRequestHeaderAuthorizationBearerException;
import com.wanted.jaringoby.common.exceptions.http.request.MissingRequestHeaderAuthorizationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public abstract class AuthenticationFilter extends CustomFilter {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";

    private final List<AntPathRequestMatcher> requestMatchersForRefreshTokenBasedAuthentication
            = List.of(
            AntPathRequestMatcher.antMatcher(HttpMethod.DELETE, "/customer/v1.0/sessions"),
            AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/customer/v1.0/access-tokens")
    );

    public abstract void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain chain
    ) throws IOException, ServletException;

    protected boolean requestForRefreshTokenBasedAuthentication(HttpServletRequest request) {
        return requestMatchersForRefreshTokenBasedAuthentication.stream()
                .anyMatch(requestMatcher -> requestMatcher.matches(request));
    }

    protected String parseToken(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION);

        if (authorization == null) {
            throw new MissingRequestHeaderAuthorizationException();
        }

        if (!authorization.startsWith(BEARER)) {
            throw new InvalidRequestHeaderAuthorizationBearerException();
        }

        return authorization.substring(BEARER.length());
    }
}
