package com.example.signup.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    // This method is executed once per request to filter and validate JWT
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        // Get the Authorization header
        String authHeader = request.getHeader("Authorization");
        // Check if the header is not null and starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            // Validate the token
            if (jwtUtil.validateToken(token)) {
            	// Extract email from token
                String email = jwtUtil.extractEmail(token);
                // Create authentication token with extracted email
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
                // Attach request details to the authentication object
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Set authentication to Spring Security context
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}
