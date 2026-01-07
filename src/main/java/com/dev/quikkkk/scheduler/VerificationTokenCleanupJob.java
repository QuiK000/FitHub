package com.dev.quikkkk.scheduler;

import com.dev.quikkkk.repository.IVerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class VerificationTokenCleanupJob {
    private final IVerificationTokenRepository tokenRepository;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        int deleted = tokenRepository.deleteExpiredOrUsed(LocalDateTime.now());
        log.info("VerificationToken cleanup: {} tokens deleted", deleted);
    }
}
