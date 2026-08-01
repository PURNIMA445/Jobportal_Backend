package com.example.Jobportal.config;

import com.example.Jobportal.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final com.example.Jobportal.repository.UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Read the Authorization header
        String authHeader = request.getHeader("Authorization");
        String debugMsg = "URI: " + request.getRequestURI() + " | AuthHeader: " + authHeader + "\n";
        try {
            java.nio.file.Files.write(java.nio.file.Paths.get("debug.txt"), debugMsg.getBytes(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch(Exception e) {}

        // 2. If no token, skip
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            try {
                java.nio.file.Files.write(java.nio.file.Paths.get("debug.txt"), "No valid Bearer token. Skipping.\n".getBytes(), java.nio.file.StandardOpenOption.APPEND);
            } catch(Exception e) {}
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract the token
        String token = authHeader.substring(7);

        // 4. Validate token
        boolean isValid = jwtUtils.validateToken(token);
        try {
            java.nio.file.Files.write(java.nio.file.Paths.get("debug.txt"), ("Token isValid: " + isValid + "\n").getBytes(), java.nio.file.StandardOpenOption.APPEND);
        } catch(Exception e) {}

        if (isValid) {
            try {
                String email = jwtUtils.getEmailFromToken(token);
                String role = jwtUtils.getRoleFromToken(token);
                Long userId = jwtUtils.getUserIdFromToken(token);

                // Fallback for older tokens that lack the userId claim
                if (userId == null && email != null) {
                    userId = userRepository.findByEmail(email)
                            .map(com.example.Jobportal.entity.UserEntity::getId)
                            .orElse(null);
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userId,        // ← store userId as principal, not email
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
                java.nio.file.Files.write(java.nio.file.Paths.get("debug.txt"), "Authentication SET successfully.\n".getBytes(), java.nio.file.StandardOpenOption.APPEND);
            } catch (Exception ex) {
                try {
                    java.nio.file.Files.write(java.nio.file.Paths.get("debug.txt"), ("EXCEPTION IN FILTER: " + ex.toString() + "\n").getBytes(), java.nio.file.StandardOpenOption.APPEND);
                } catch(Exception ignored) {}
            }
        }

        filterChain.doFilter(request, response);
    }
}