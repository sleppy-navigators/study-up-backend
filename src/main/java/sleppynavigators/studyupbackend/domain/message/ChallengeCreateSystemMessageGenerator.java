package sleppynavigators.studyupbackend.domain.message;

import sleppynavigators.studyupbackend.domain.event.ChallengeCreateEvent;
import sleppynavigators.studyupbackend.domain.event.EventType;

public class ChallengeCreateSystemMessageGenerator implements SystemMessageGenerator<ChallengeCreateEvent> {
    private static final String MESSAGE_FORMAT = "%s님이 '%s' 챌린지를 생성했습니다.";
    
    @Override
    public String generate(ChallengeCreateEvent event) {
        return String.format(MESSAGE_FORMAT, event.userName(), event.challengeName());
    }
    
    @Override
    public boolean supports(ChallengeCreateEvent event) {
        return event.getType() == EventType.CHALLENGE_CREATE;
    }
}
