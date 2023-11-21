package com.wanted.jaringoby.common.filters;

import static com.wanted.jaringoby.common.utils.JwtUtil.CLAIM_CUSTOMER_ID;
import static com.wanted.jaringoby.common.utils.JwtUtil.CLAIM_TYPE;
import static com.wanted.jaringoby.common.utils.JwtUtil.REFRESH_TOKEN;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.jaringoby.common.exceptions.http.request.InvalidRequestHeaderAuthorizationBearerException;
import com.wanted.jaringoby.common.exceptions.http.request.MissingRequestHeaderAuthorizationException;
import com.wanted.jaringoby.common.exceptions.jwt.TokenDecodingFailedException;
import com.wanted.jaringoby.common.exceptions.jwt.TokenExpiredException;
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
import org.springframework.web.filter.GenericFilterBean;

@RequiredArgsConstructor
public class AuthenticationFilter extends GenericFilterBean
        implements ErrorResponseWriter {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";

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

        try {
            String authorization = request.getHeader(AUTHORIZATION);

            if (authorization == null) {
                throw new MissingRequestHeaderAuthorizationException();
            }

            if (!authorization.startsWith(BEARER)) {
                throw new InvalidRequestHeaderAuthorizationBearerException();
            }

            String token = authorization.substring(BEARER.length());

            Map<String, String> claimAndValues = jwtUtil.decodeToken(token);

            if (claimAndValues.get(CLAIM_TYPE).equals(REFRESH_TOKEN)) {
                request.setAttribute("refreshToken", token);
            }

            request.setAttribute("customerId", claimAndValues.get(CLAIM_CUSTOMER_ID));

            chain.doFilter(request, response);

        } catch (
                MissingRequestHeaderAuthorizationException
                | InvalidRequestHeaderAuthorizationBearerException
                | TokenDecodingFailedException
                | TokenExpiredException exception
        ) {
            writeErrorResponse(response, objectMapper, exception);
        }
    }
}
