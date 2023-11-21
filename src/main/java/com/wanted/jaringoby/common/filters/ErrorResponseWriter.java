package com.wanted.jaringoby.common.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.jaringoby.common.exceptions.CustomizedException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ErrorResponseWriter {

    default void writeErrorResponse(
            HttpServletResponse response,
            ObjectMapper objectMapper,
            CustomizedException exception
    ) throws IOException {
        int status = exception.statusCode().value();
        String body = objectMapper.writeValueAsString(exception.toErrorResponse());

        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(status);
        response.getWriter().write(body);
    }
}
