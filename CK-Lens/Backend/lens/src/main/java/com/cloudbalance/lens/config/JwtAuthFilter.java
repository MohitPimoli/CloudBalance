package com.cloudbalance.lens.config;

import com.cloudbalance.lens.dto.auth.CustomUserDetails;
import com.cloudbalance.lens.repository.BlackListedTokenRepository;
import com.cloudbalance.lens.service.auth.impl.CustomUserDetailsServiceImpl;
import com.cloudbalance.lens.utils.TokenBlacklistUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsServiceImpl userDetailsService;
    private final BlackListedTokenRepository blackListedTokenRepository;
    private final TokenBlacklistUtil tokenBlacklisterUtil;

    public JwtAuthFilter(JwtUtil jwtUtil,
                         CustomUserDetailsServiceImpl userDetailsService,
                         BlackListedTokenRepository blackListedTokenRepository,
                         TokenBlacklistUtil tokenBlacklisterUtil) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.blackListedTokenRepository = blackListedTokenRepository;
        this.tokenBlacklisterUtil = tokenBlacklisterUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header");
            jwtUtil.writeCustomErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, 1001,
                    "InvalidAuthorizationHeader", "Missing or invalid Authorization header.");
            return;
        }

        String token = authHeader.substring(7).trim();

        try {
            if (blackListedTokenRepository.existsByToken(token)) {
                log.warn("Blacklisted token used: {}", token);
                jwtUtil.writeCustomErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, 4002,
                        "BlacklistedToken", "Token is blacklisted.");
                return;
            }
            String username = jwtUtil.extractUsername(token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                CustomUserDetails customUserDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(token, customUserDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(customUserDetails, null,
                                    customUserDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (ExpiredJwtException e) {
            log.warn("Expired token: {}", token);

            if (!blackListedTokenRepository.existsByToken(token)) {
                tokenBlacklisterUtil.blacklistToken(token);
                log.info("Expired token blacklisted: {}", token);
            }
            jwtUtil.writeCustomErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, 1001,
                    "TokenExpired", "Your access token has expired. Please refresh.");
            return;
        } catch (Exception e) {
            log.error("Token validation failed", e);
            jwtUtil.writeCustomErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, 4003,
                    "InvalidToken", "Your access token was invalid or tampered. Please login again.");
            return;
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/auth");
    }
}
