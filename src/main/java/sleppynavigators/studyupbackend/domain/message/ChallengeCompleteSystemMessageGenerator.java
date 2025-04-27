package sleppynavigators.studyupbackend.domain.message;

import sleppynavigators.studyupbackend.domain.event.ChallengeCompleteEvent;
import sleppynavigators.studyupbackend.domain.event.EventType;

public class ChallengeCompleteSystemMessageGenerator implements SystemMessageGenerator<ChallengeCompleteEvent> {
    private static final String MESSAGE_FORMAT = "%s님이 '%s' 챌린지를 완료했습니다.";
    
    @Override
    public String generate(ChallengeCompleteEvent event) {
        return String.format(MESSAGE_FORMAT, event.userName(), event.challengeName());
    }
    
    @Override
    public boolean supports(ChallengeCompleteEvent event) {
        return event.getType() == EventType.CHALLENGE_COMPLETE;
    }
}
