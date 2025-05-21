package sleppynavigators.studyupbackend.infrastructure.challenge.scheduler;

import com.querydsl.core.types.dsl.BooleanExpression;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.application.event.SystemEventPublisher;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.event.ChallengeCompleteEvent;
import sleppynavigators.studyupbackend.infrastructure.challenge.ChallengeQueryOptions;
import sleppynavigators.studyupbackend.infrastructure.challenge.ChallengeRepository;

@Slf4j
@Component
@RequiredArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ChallengeScheduler {

    private final ChallengeRepository challengeRepository;
    private final SystemEventPublisher systemEventPublisher;

    @Value("${scheduler.challenge.check-expiration.interval-minutes}")
    private long challengeCheckIntervalMinutes;

    @Scheduled(cron = "${scheduler.challenge.check-expiration.cron}", zone = "Asia/Seoul")
    @Transactional
    public void checkExpiredChallenges() {
        log.info("ChallengeScheduler - checkExpiredChallenges() started");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime baseTime = now.minusMinutes(challengeCheckIntervalMinutes);
        BooleanExpression predicate = ChallengeQueryOptions.getCompletedBetweenPredicate(baseTime, now);
        List<Challenge> completedChallenges = challengeRepository.findAll(predicate);

        for (Challenge challenge : completedChallenges) {
            ChallengeCompleteEvent event = new ChallengeCompleteEvent(
                    challenge.getOwner().getUserProfile().getUsername(),
                    challenge.getDetail().getTitle(),
                    challenge.getGroup().getId(),
                    challenge.getId(),
                    challenge.calcCompletionRate()
            );
            systemEventPublisher.publish(event);
        }
        log.info("ChallengeScheduler - checkExpiredChallenges() completed");
    }
}
