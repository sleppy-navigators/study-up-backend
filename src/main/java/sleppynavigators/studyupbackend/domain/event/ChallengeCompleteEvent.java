package sleppynavigators.studyupbackend.domain.event;

import sleppynavigators.studyupbackend.domain.chat.SystemMessageTemplate;

public record ChallengeCompleteEvent(String userName, String challengeName) implements SystemEvent {

    @Override
    public SystemEventType getType() {
        return SystemEventType.CHALLENGE_COMPLETE;
    }

    @Override
    public String generateMessage(SystemMessageTemplate template) {
        return template.format(userName, challengeName);
    }
}
