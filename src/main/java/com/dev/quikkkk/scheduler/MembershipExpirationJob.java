package com.dev.quikkkk.scheduler;

import com.dev.quikkkk.entity.Membership;
import com.dev.quikkkk.enums.MembershipStatus;
import com.dev.quikkkk.repository.IMembershipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MembershipExpirationJob {
    private final IMembershipRepository repository;

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void expireMemberships() {
        List<Membership> expiredMemberships = repository.findByStatusAndEndDateBefore(
                MembershipStatus.ACTIVE,
                LocalDateTime.now()
        );

        expiredMemberships.forEach(membership -> {
            membership.setStatus(MembershipStatus.EXPIRED);
            log.info("Membership {} expired", membership.getId());
        });

        repository.saveAll(expiredMemberships);
    }
}
