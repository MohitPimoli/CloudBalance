package com.cloudbalance.lens.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@PropertySource(value = "classpath:ratelimit.yml", factory = YamlPropertySourceFactory.class)
public class RateLimitProperties {

    @Value("${rate.limit.capacity}")
    private int capacity;

    @Value("${rate.limit.refill-tokens}")
    private int refillTokens;

    @Value("${rate.limit.refill-duration}")
    private int refillDuration;

    @Value("${rate.limit.role.ADMIN}")
    private int adminLimit;

    @Value("${rate.limit.role.READONLY}")
    private int readonlyLimit;

    @Value("${rate.limit.role.CUSTOMER}")
    private int customerLimit;

    /**
     * Returns the rate limit for a given role.
     * If the role is not recognized, it returns the default capacity.
     *
     * @param role The role for which to get the rate limit.
     * @return The rate limit for the specified role.
     */

    public int getLimitForRole(String role) {
        return switch (role.toUpperCase()) {
            case "ADMIN" -> adminLimit;
            case "READONLY" -> readonlyLimit;
            case "CUSTOMER" -> customerLimit;
            default -> capacity;
        };
    }
}
