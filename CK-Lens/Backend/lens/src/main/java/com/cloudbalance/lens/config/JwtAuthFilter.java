package com.cloudbalance.lens.config;

import com.cloudbalance.lens.dto.auth.CustomUserDetails;
import com.cloudbalance.lens.entity.BlackListedToken;
import com.cloudbalance.lens.repository.BlackListedTokenRepository;
import com.cloudbalance.lens.service.auth.impl.CustomUserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CustomUserDetailsServiceImpl userDetailsService;
    @Autowired
    private BlackListedTokenRepository blackListedTokenRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header");
            writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header.");
            return;
        }

        String token = authHeader.substring(7).trim();

        try {
            if (blackListedTokenRepository.existsByToken(token)) {
                log.warn("Blacklisted token used: {}", token);
                writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token is blacklisted.");
                return;
            }
            String username = jwtUtil.extractUsername(token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                CustomUserDetails customUserDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(token, customUserDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (ExpiredJwtException e) {
            log.warn("Expired token: {}", token);
            if (!blackListedTokenRepository.existsByToken(token)) {
                BlackListedToken blackListedToken = BlackListedToken.builder()
                        .token(token)
                        .blacklistedAt((LocalDateTime.parse(LocalDateTime.now().format(FORMATTER), FORMATTER)))
                        .tokenExpiry((LocalDateTime.parse(LocalDateTime.now().format(FORMATTER), FORMATTER)))
                        .build();
                blackListedTokenRepository.save(blackListedToken);
                log.info("Expired token blacklisted: {}", token);
            }
            writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token has expired.");
            return;
        } catch (Exception e) {
            log.error("Token validation failed", e);
            writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token.");
            return;
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/auth");
    }

    private void writeErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }

}

