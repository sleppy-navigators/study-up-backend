package sleppynavigators.studyupbackend.infrastructure.challenge.scheduler;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.application.event.SystemEventPublisher;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.event.ChallengeCompleteEvent;
import sleppynavigators.studyupbackend.infrastructure.challenge.ChallengeRepository;

@Component
@RequiredArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ChallengeScheduler {

    private final ChallengeRepository challengeRepository;
    private final SystemEventPublisher systemEventPublisher;

    @Scheduled(cron = "${scheduler.challenge.check-expiration}", zone = "Asia/Seoul")
    @Transactional
    public void checkExpiredChallenges() {
        LocalDateTime now = LocalDateTime.now();
        List<Challenge> completedChallenges = challengeRepository.findAllByDetailDeadlineBefore(now).stream()
                .filter(Challenge::isAllTasksCompleted)
                .toList();

        for (Challenge challenge : completedChallenges) {
            ChallengeCompleteEvent event = new ChallengeCompleteEvent(
                    challenge.getOwner().getUserProfile().getUsername(),
                    challenge.getDetail().getTitle(),
                    challenge.getGroup().getId()
            );
            systemEventPublisher.publish(event);
        }
    }
}
