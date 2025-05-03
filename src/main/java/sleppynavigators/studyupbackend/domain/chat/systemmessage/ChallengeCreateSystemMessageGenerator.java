package sleppynavigators.studyupbackend.domain.chat.systemmessage;

import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.domain.event.ChallengeCreateEvent;
import sleppynavigators.studyupbackend.domain.event.EventType;

@Component
public class ChallengeCreateSystemMessageGenerator implements SystemMessageGenerator<ChallengeCreateEvent> {
    private static final String MESSAGE_FORMAT = "%s님이 '%s' 챌린지를 생성했습니다.";
    
    @Override
    public String generate(ChallengeCreateEvent event) {
        return String.format(MESSAGE_FORMAT, event.userName(), event.challengeName());
    }

    @Override
    public EventType supportedEventType() {
        return EventType.CHALLENGE_CREATE;
    }
}
