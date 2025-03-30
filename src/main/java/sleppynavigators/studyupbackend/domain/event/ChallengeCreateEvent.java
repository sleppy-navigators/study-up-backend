package sleppynavigators.studyupbackend.domain.event;

import sleppynavigators.studyupbackend.domain.chat.SystemMessageTemplate;

public record ChallengeCreateEvent(String userName, String challengeName) implements SystemEvent {

    @Override
    public SystemEventType getType() {
        return SystemEventType.CHALLENGE_CREATE;
    }

    @Override
    public String generateMessage(SystemMessageTemplate template) {
        return template.format(userName, challengeName);
    }
}
