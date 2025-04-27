package sleppynavigators.studyupbackend.domain.chat.systemmessage;

import org.springframework.stereotype.Component;
import sleppynavigators.studyupbackend.domain.event.ChallengeCompleteEvent;
import sleppynavigators.studyupbackend.domain.event.EventType;

@Component
public class ChallengeCompleteSystemMessageGenerator implements SystemMessageGenerator<ChallengeCompleteEvent> {
    private static final String MESSAGE_FORMAT = "%s님이 '%s' 챌린지를 완료했습니다.";
    
    @Override
    public String generate(ChallengeCompleteEvent event) {
        return String.format(MESSAGE_FORMAT, event.userName(), event.challengeName());
    }

    @Override
    public EventType getEventType() {
        return EventType.CHALLENGE_COMPLETE;
    }
}
