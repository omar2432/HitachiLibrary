package com.hitachi.library.config;

import com.hitachi.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This filter intercepts HTTP requests to validate JWT tokens in the Authorization header.
 * It sets the authentication context if the token is valid.
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userService; // Service to load user details from the database

    @Autowired
    private JwtUtil jwtUtil; // Utility class for JWT operations

    /*
      This method is invoked for every HTTP request. It checks for JWT tokens,
      validates them, and sets the authentication context if the token is valid.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Retrieve the Authorization header from the request
        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        // Check if the Authorization header is present and starts with "Bearer "
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            // Extract the JWT token from the header
            jwtToken = requestTokenHeader.substring(7);
            try {
                // Extract the username from the JWT token
                username = jwtUtil.extractUsername(jwtToken);
            } catch (IllegalArgumentException e) {
                // Log and handle exception if JWT token extraction fails
                System.out.println("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                // Log and handle exception if JWT token has expired
                System.out.println("JWT Token has expired");
            }
        } else {
            // Log a warning if the Authorization header does not start with "Bearer "
            logger.warn("JWT Token does not begin with Bearer String");
        }

        // If the username is extracted and no authentication exists in the context
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load user details from the database
            UserDetails userDetails = this.userService.loadUserByUsername(username);

            // Validate the JWT token
            if (jwtUtil.validateToken(jwtToken, userDetails.getUsername())) {
                // Extract roles from the JWT token and convert them to authorities
                List<String> roles = jwtUtil.extractClaim(jwtToken, claims -> claims.get("roles", List.class));
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                // Create an authentication token with user details and authorities
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, authorities);
                // Set additional details for the authentication token
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Set the authentication context
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        // Continue the filter chain
        chain.doFilter(request, response);
    }
}
