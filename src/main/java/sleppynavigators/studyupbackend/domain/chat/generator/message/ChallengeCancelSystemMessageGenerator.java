package sleppynavigators.studyupbackend.domain.chat.generator.message;

import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.domain.event.challenge.ChallengeCancelEvent;
import sleppynavigators.studyupbackend.domain.event.EventType;

@Component
public class ChallengeCancelSystemMessageGenerator implements SystemMessageGenerator<ChallengeCancelEvent> {
    private static final String MESSAGE_FORMAT = "%s님이 '%s' 챌린지를 취소했습니다.";

    @Override
    public String generate(ChallengeCancelEvent event) {
        return String.format(MESSAGE_FORMAT, event.userName(), event.challengeName());
    }

    @Override
    public EventType supportedEventType() {
        return EventType.CHALLENGE_CANCEL;
    }
}
