package sleppynavigators.studyupbackend.domain.event.challenge;

import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.SystemMessageEvent;

public record ChallengeCancelEvent(String userName, String challengeName, Long groupId) implements SystemMessageEvent {
    @Override
    public EventType getType() {
        return EventType.CHALLENGE_CANCEL;
    }

    @Override
    public Long getGroupId() {
        return groupId;
    }
}
