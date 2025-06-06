package sleppynavigators.studyupbackend.application.event;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import sleppynavigators.studyupbackend.application.challenge.ChallengeService;
import sleppynavigators.studyupbackend.domain.event.challenge.ChallengeCompleteEvent;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ChallengeEventListener {

    private final ChallengeService challengeService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleChallengeCompleteEvent(ChallengeCompleteEvent event) {
        try {
            challengeService.settlementReward(event.challengerId(), event.challengeId());
        } catch (Exception e) {
            log.error("Error handling ChallengeCompleteEvent: {}", e.getMessage(), e);
        }
    }
}
