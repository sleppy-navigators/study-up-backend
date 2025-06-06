package sleppynavigators.studyupbackend.domain.event.group;

import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.SystemMessageEvent;

public record UserJoinEvent(String userName, Long groupId, Long userId) implements SystemMessageEvent {

    @Override
    public EventType getType() {
        return EventType.USER_JOIN;
    }

    @Override
    public Long getGroupId() {
        return groupId;
    }
}
