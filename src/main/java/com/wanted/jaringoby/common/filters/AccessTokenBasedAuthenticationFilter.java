package com.wanted.jaringoby.common.filters;

import static com.wanted.jaringoby.common.utils.JwtUtil.CLAIM_CUSTOMER_ID;
import static com.wanted.jaringoby.common.utils.JwtUtil.CLAIM_TYPE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.jaringoby.common.exceptions.http.request.InvalidRequestHeaderAuthorizationBearerException;
import com.wanted.jaringoby.common.exceptions.http.request.MissingRequestHeaderAuthorizationException;
import com.wanted.jaringoby.common.exceptions.jwt.TokenDecodingFailedException;
import com.wanted.jaringoby.common.exceptions.jwt.TokenExpiredException;
import com.wanted.jaringoby.common.exceptions.jwt.TokenNotAccessTokenException;
import com.wanted.jaringoby.common.exceptions.jwt.TokenSignatureInvalidException;
import com.wanted.jaringoby.common.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccessTokenBasedAuthenticationFilter extends AuthenticationFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain chain
    ) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (requestForRefreshTokenBasedAuthentication(request)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String token = parseToken(request);

            Map<String, String> claimAndValues = jwtUtil.decodeToken(token);

            if (!claimAndValues.get(CLAIM_TYPE).equals("accessToken")) {
                throw new TokenNotAccessTokenException();
            }

            request.setAttribute("customerId", claimAndValues.get(CLAIM_CUSTOMER_ID));

            chain.doFilter(request, response);

        } catch (
                MissingRequestHeaderAuthorizationException
                | InvalidRequestHeaderAuthorizationBearerException
                | TokenSignatureInvalidException
                | TokenExpiredException
                | TokenDecodingFailedException
                | TokenNotAccessTokenException exception
        ) {
            writeErrorResponse(response, objectMapper, exception);
        }
    }
}
