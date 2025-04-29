package com.cloudbalance.lens.utils;

import com.cloudbalance.lens.repository.BlackListedTokenRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class TokenCleanUpJob {

    private final BlackListedTokenRepository blackListedTokenRepository;
    public TokenCleanUpJob(BlackListedTokenRepository blackListedTokenRepository) {
        this.blackListedTokenRepository = blackListedTokenRepository;
    }

    @Scheduled(cron = "0 15 10 * * ?") // every day at 10:15 AM
    @Transactional
    public void purgeExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        blackListedTokenRepository.deleteAllByTokenExpiryBefore(now);
        log.info("Expired blacklisted tokens removed at {}", now);
    }
}
