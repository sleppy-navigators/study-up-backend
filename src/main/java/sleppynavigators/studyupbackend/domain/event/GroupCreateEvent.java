package sleppynavigators.studyupbackend.domain.event;

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
