package sleppynavigators.studyupbackend.domain.event.group;

import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.SystemMessageEvent;

public record UserLeaveEvent(String userName, Long groupId) implements SystemMessageEvent {

    @Override
    public EventType getType() {
        return EventType.USER_LEAVE;
    }

    @Override
    public Long getGroupId() {
        return groupId;
    }
}
