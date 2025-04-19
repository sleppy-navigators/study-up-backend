package sleppynavigators.studyupbackend.infrastructure.challenge.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sleppynavigators.studyupbackend.domain.challenge.Challenge;
import sleppynavigators.studyupbackend.domain.event.ChallengeCompleteEvent;
import sleppynavigators.studyupbackend.application.event.SystemEventPublisher;
import sleppynavigators.studyupbackend.infrastructure.challenge.ChallengeRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ChallengeScheduler {

    private final ChallengeRepository challengeRepository;
    private final SystemEventPublisher systemEventPublisher;

    // TODO(@Jayon): properties 분리 필요성이 생기면 빼기
    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul") // 매일 오전 9시에 실행
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
