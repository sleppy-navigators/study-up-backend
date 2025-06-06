package sleppynavigators.studyupbackend.domain.notification.generator;

import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.domain.event.challenge.ChallengeCompleteEvent;
import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.notification.NotificationMessage;

@Component
public class ChallengeCompleteNotificationMessageGenerator implements
        NotificationMessageGenerator<ChallengeCompleteEvent> {

    private static final String TITLE = "챌린지 완료 알림";
    private static final String MESSAGE_FORMAT = "%s님이 '%s' 챌린지를 완료했습니다. (%.2f%% 달성)";

    @Override
    public NotificationMessage generate(ChallengeCompleteEvent event) {
        String body = String.format(MESSAGE_FORMAT, event.userName(), event.challengeName(), event.percentage());
        return new NotificationMessage(TITLE, body, null, null);
    }

    @Override
    public EventType supportedEventType() {
        return EventType.CHALLENGE_COMPLETE;
    }
}
