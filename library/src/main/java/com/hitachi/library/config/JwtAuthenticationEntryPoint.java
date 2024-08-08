package com.hitachi.library.config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
  This class handles authentication errors in the application.
  It implements AuthenticationEntryPoint to handle unauthorized access.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /*
       This method is called when an unauthenticated user tries to access a protected resource.
       It sends an HTTP 401 Unauthorized response to the client.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // Send an HTTP 401 Unauthorized error to the client
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}
