package sleppynavigators.studyupbackend.domain.event.challenge;

import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.SystemMessageEvent;

public record ChallengeCreateEvent(String userName, String challengeName, Long groupId, Long challengeId)
        implements SystemMessageEvent {

    @Override
    public EventType getType() {
        return EventType.CHALLENGE_CREATE;
    }

    @Override
    public Long getGroupId() {
        return groupId;
    }
}
