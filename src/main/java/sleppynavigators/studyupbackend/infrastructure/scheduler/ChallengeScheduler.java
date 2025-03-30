package sleppynavigators.studyupbackend.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.event.ChallengeCompleteEvent;
import sleppynavigators.studyupbackend.application.event.SystemEventPublisher;
import sleppynavigators.studyupbackend.infrastructure.challenge.ChallengeRepository;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChallengeScheduler {

    private final ChallengeRepository challengeRepository;
    private final SystemEventPublisher systemEventPublisher;

    @Scheduled(cron = "0 0 9 * * *") // 매일 오전 9시에 실행
    @Transactional
    public void checkExpiredChallenges() {
        List<Challenge> completedChallenges = challengeRepository.findAll().stream()
                .filter(challenge -> challenge.getDetail().isPast())
                .filter(Challenge::isAllTasksCompleted)
                .toList();

        for (Challenge challenge : completedChallenges) {
            ChallengeCompleteEvent event = new ChallengeCompleteEvent(
                challenge.getOwner().getUserProfile().username(),
                challenge.getDetail().title(),
                challenge.getGroup().getId()
            );
            systemEventPublisher.publish(event);
        }
    }
}
