package com.cloudbalance.lens.config;

import com.cloudbalance.lens.dto.auth.CustomUserDetails;
import com.cloudbalance.lens.utils.RateLimitProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RoleBasedRateLimiterFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> userBuckets = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    private final RateLimitProperties properties;

    public RoleBasedRateLimiterFilter(ObjectMapper objectMapper, RateLimitProperties properties) {
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    /**
     * Filters incoming requests and applies rate limiting based on user roles.
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            String username = userDetails.getUsername();

            Bucket bucket = userBuckets.computeIfAbsent(username, key -> createNewBucket(userDetails));

            if (bucket.tryConsume(1)) {
                filterChain.doFilter(request, response);
                return;
            } else {
                writeRateLimitExceededResponse(response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Creates a new bucket with a rate limit based on the user's role.
     * @param userDetails
     * @return
     */

    private Bucket createNewBucket(CustomUserDetails userDetails) {
        int capacity = properties.getCapacity();

        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            capacity = properties.getLimitForRole("ADMIN");
            log.debug("Setting ADMIN rate limit capacity: {}", capacity);
        } else if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_READONLY"))) {
            capacity = properties.getLimitForRole("READONLY");
            log.debug("Setting READONLY rate limit capacity: {}", capacity);
        } else if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CUSTOMER"))) {
            capacity = properties.getLimitForRole("CUSTOMER");
            log.debug("Setting CUSTOMER rate limit capacity: {}", capacity);
        }

        return Bucket.builder()
                .addLimit(Bandwidth.classic(
                        capacity,
                        Refill.intervally(properties.getRefillTokens(),
                                Duration.ofSeconds(properties.getRefillDuration()))
                ))
                .build();
    }

    /**
     * Writes a response indicating that the rate limit has been exceeded.
     * @param response
     * @throws IOException
     */

    private void writeRateLimitExceededResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("X-Rate-Limit-Retry-After", String.valueOf(properties.getRefillDuration()));

        Map<String, Object> errorResponse = Map.of(
                "status", HttpStatus.TOO_MANY_REQUESTS.value(),
                "code", "TOO_MANY_REQUESTS",
                "message", "Rate limit exceeded. Please try again later.",
                "retryAfterSeconds", properties.getRefillDuration(),
                "timestamp", System.currentTimeMillis()
        );

        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/auth");
    }
}