package sleppynavigators.studyupbackend.domain.notification;

import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.domain.event.ChallengeCompleteEvent;
import sleppynavigators.studyupbackend.domain.event.EventType;

@Component
public class ChallengeCompleteNotificationMessageGenerator implements
    NotificationMessageGenerator<ChallengeCompleteEvent> {
    private static final String MESSAGE_FORMAT = "%s님이 '%s' 챌린지를 완료했습니다. (%.2f%% 달성)";

    @Override
    public String generate(ChallengeCompleteEvent event) {
        return String.format(MESSAGE_FORMAT, event.userName(), event.challengeName(), event.percentage());
    }

    @Override
    public EventType supportedEventType() {
        return EventType.CHALLENGE_COMPLETE;
    }
}
