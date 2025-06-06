package sleppynavigators.studyupbackend.domain.event.group;

import sleppynavigators.studyupbackend.domain.event.EventType;
import sleppynavigators.studyupbackend.domain.event.SystemMessageEvent;

public record GroupCreateEvent(String userName, String groupName, Long groupId) implements SystemMessageEvent {
    @Override
    public EventType getType() {
        return EventType.GROUP_CREATE;
    }

    @Override
    public Long getGroupId() {
        return groupId;
    }
}
